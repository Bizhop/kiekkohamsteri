import * as pulumi from "@pulumi/pulumi"
import * as aws from "@pulumi/aws"

const config = new pulumi.Config("kiekkohamsteri")
const awsConfig = new pulumi.Config("aws")
const env = config.require("environment")
const defaultResourceName = env == "prod" ? "kiekkohamsteri" : `kiekkohamsteri-${env}`
const subnets = ["subnet-e3947c8a", "subnet-bc3b30c4", "subnet-737d4839"]
const vpcId = config.require("vpcId")
const zoneId = config.require("zoneId")

//US provider is needed for frontend certificate
const usProvider = new aws.Provider("aws-east-1", { region: "us-east-1" })

const s3BucketName = defaultResourceName
const bucket = new aws.s3.Bucket(defaultResourceName + "-s3", {
  bucket: s3BucketName,
  policy: JSON.stringify({
    Version: "2012-10-17",
    Id: defaultResourceName + "-bucketPolicy",
    Statement: [
      {
        Sid: "AllowCloudFrontServicePrincipalReadOnly-" + env,
        Effect: "Allow",
        Principal: {
          Service: "cloudfront.amazonaws.com",
        },
        Action: "s3:GetObject",
        Resource: `arn:aws:s3:::${s3BucketName}/*`,
      },
    ],
  }),
})

const dbParameterGroup = new aws.rds.ParameterGroup(defaultResourceName + "-db-params", {
  family: "postgres13",
})

const kiekko_sg = new aws.ec2.SecurityGroup(defaultResourceName + "-sg", {
  description: `Kiekkohamsteri ${env} security group`,
  egress: [
    {
      cidrBlocks: ["0.0.0.0/0"],
      fromPort: 0,
      protocol: "-1",
      toPort: 0,
    },
  ],
  ingress: [
    {
      cidrBlocks: ["0.0.0.0/0"],
      fromPort: 5432,
      protocol: "tcp",
      toPort: 5432,
    },
    {
      cidrBlocks: ["0.0.0.0/0"],
      fromPort: 80,
      protocol: "tcp",
      toPort: 80,
    },
    {
      cidrBlocks: ["0.0.0.0/0"],
      fromPort: 443,
      protocol: "tcp",
      toPort: 443,
    },
  ],
  vpcId: vpcId,
})

const dbInstance = new aws.rds.Instance(defaultResourceName + "-db-instance", {
  identifier: defaultResourceName + "-db",
  allocatedStorage: 10,
  engine: "postgres",
  engineVersion: "13.7",
  instanceClass: "db.t3.micro",
  dbName: "kiekkohamsteri",
  parameterGroupName: dbParameterGroup.name,
  password: config.requireSecret("dbPassword"),
  skipFinalSnapshot: true,
  username: "hamsteri",
  publiclyAccessible: true,
  vpcSecurityGroupIds: [kiekko_sg.id],
})

const kiekkohamsteriEcsCluster = new aws.ecs.Cluster(defaultResourceName + "-cluster", {
  name: defaultResourceName,
  settings: [
    {
      name: "containerInsights",
      value: "disabled",
    },
  ],
})

const backendDomainName = defaultResourceName + "-backend.valuemotive.net"
const backendCert = new aws.acm.Certificate(
  defaultResourceName + "-backendCert",
  {
    domainName: backendDomainName,
    subjectAlternativeNames: [backendDomainName],
    validationMethod: "DNS",
  },
  {
    protect: true,
  },
)

const lb = new aws.lb.LoadBalancer(
  defaultResourceName + "-lb",
  {
    internal: false,
    loadBalancerType: "application",
    securityGroups: [kiekko_sg.id],
    subnets: subnets,
    enableDeletionProtection: true,
  },
  {
    dependsOn: [kiekko_sg],
  },
)

const targetGroup = new aws.lb.TargetGroup(defaultResourceName + "-tg", {
  port: 80,
  protocol: "HTTP",
  targetType: "ip",
  vpcId: vpcId,
  healthCheck: {
    enabled: true,
    path: "/actuator/health",
  },
})

new aws.lb.Listener(
  defaultResourceName + "-listener",
  {
    loadBalancerArn: lb.arn,
    port: 443,
    protocol: "HTTPS",
    sslPolicy: "ELBSecurityPolicy-2016-08",
    certificateArn: backendCert.arn,
    defaultActions: [
      {
        type: "forward",
        targetGroupArn: targetGroup.arn,
      },
    ],
  },
  {
    dependsOn: [lb, backendCert, targetGroup],
  },
)

const logGroupName = `/ecs/${defaultResourceName}-task`
new aws.cloudwatch.LogGroup(defaultResourceName + "_lg", {name: logGroupName})

pulumi
  .output({
    cloudinaryUrl: config.requireSecret("cloudinaryUrl"),
    jwtSecret: config.requireSecret("jwtSecret"),
    db: {
      endpoint: dbInstance.endpoint,
      name: dbInstance.dbName,
      username: dbInstance.username,
      password: dbInstance.password,
    },
  })
  .apply((props) => {
    const { db } = props
    const containerDefinitions = [
      {
        command: [],
        cpu: 0,
        entryPoint: [],
        environment: [
          {
            name: "CLOUDINARY_URL",
            value: props.cloudinaryUrl,
          },
          {
            name: "HAMSTERI_GOOGLE_AUDIENCES",
            value: config.require("googleAudiences"),
          },
          {
            name: "HAMSTERI_JWT_SECRET",
            value: props.jwtSecret,
          },
          {
            name: "SERVER_PORT",
            value: "80",
          },
          {
            name: "SPRING_DATASOURCE_URL",
            value: `jdbc:postgresql://${db.endpoint}/${db.name}?user=${db.username}&password=${db.password}`,
          },
        ],
        essential: true,
        image: "806232589401.dkr.ecr.eu-north-1.amazonaws.com/" + defaultResourceName,
        logConfiguration: {
          logDriver: "awslogs",
          secretOptions: null,
          options: {
            "awslogs-group": logGroupName,
            "awslogs-region": awsConfig.require("region"),
            "awslogs-stream-prefix": "ecs",
          },
        },
        memoryReservation: 1024,
        mountPoints: [],
        name: defaultResourceName + "-container",
        portMappings: [
          {
            hostPort: 80,
            protocol: "tcp",
            containerPort: 80,
          },
        ],
        volumesFrom: [],
      },
    ]

    const taskDefinition = new aws.ecs.TaskDefinition(
      defaultResourceName + "-td",
      {
        containerDefinitions: JSON.stringify(containerDefinitions),
        cpu: "512",
        executionRoleArn: "arn:aws:iam::806232589401:role/ecsTaskExecutionRole",
        family: "kiekkohamsteri-task-" + env,
        memory: "1024",
        networkMode: "awsvpc",
        requiresCompatibilities: ["FARGATE"],
        runtimePlatform: {
          operatingSystemFamily: "LINUX",
        },
      },
      {
        dependsOn: [dbInstance],
      },
    )

    new aws.ecs.Service(
      defaultResourceName + "-service",
      {
        cluster: kiekkohamsteriEcsCluster.arn,
        taskDefinition: taskDefinition.arn,
        deploymentCircuitBreaker: {
          enable: false,
          rollback: false,
        },
        desiredCount: 1,
        enableEcsManagedTags: true,
        launchType: "FARGATE",
        name: defaultResourceName + "-service",
        networkConfiguration: {
          assignPublicIp: true,
          securityGroups: [kiekko_sg.id],
          subnets: subnets,
        },
        platformVersion: "LATEST",
        propagateTags: "NONE",
        loadBalancers: [
          {
            targetGroupArn: targetGroup.arn,
            containerName: defaultResourceName + "-container",
            containerPort: 80,
          },
        ],
      },
      {
        dependsOn: [kiekkohamsteriEcsCluster, taskDefinition, kiekko_sg, targetGroup],
      },
    )
  })

new aws.route53.Record(
  defaultResourceName + "-br",
  {
    type: "A",
    zoneId: zoneId,
    name: backendDomainName,
    aliases: [
      {
        name: lb.dnsName,
        zoneId: lb.zoneId,
        evaluateTargetHealth: true,
      },
    ],
  },
  {
    dependsOn: [lb],
  },
)

const s3OriginId = defaultResourceName + "-S3Origin"
const frontendDomainName = defaultResourceName + ".valuemotive.net"
const frontendCert = new aws.acm.Certificate(
  defaultResourceName + "-frontendCert",
  {
    domainName: frontendDomainName,
    subjectAlternativeNames: [frontendDomainName],
    validationMethod: "DNS",
  },
  {
    provider: usProvider,
    protect: true,
  },
)
const oac = new aws.cloudfront.OriginAccessControl(defaultResourceName + "-oac", {
  description: `${defaultResourceName} CloudFront-S3 access control`,
  originAccessControlOriginType: "s3",
  signingBehavior: "always",
  signingProtocol: "sigv4",
})
const distribution = new aws.cloudfront.Distribution(
  defaultResourceName + "-cf",
  {
    origins: [
      {
        domainName: bucket.bucketRegionalDomainName,
        originId: s3OriginId,
        originAccessControlId: oac.id,
      },
    ],
    enabled: true,
    isIpv6Enabled: false,
    defaultRootObject: "index.html",
    aliases: [frontendDomainName],
    defaultCacheBehavior: {
      allowedMethods: ["GET", "HEAD", "OPTIONS"],
      cachedMethods: ["GET", "HEAD"],
      targetOriginId: s3OriginId,
      forwardedValues: {
        queryString: false,
        cookies: {
          forward: "none",
        },
      },
      viewerProtocolPolicy: "allow-all",
      minTtl: 0,
      defaultTtl: 3600,
      maxTtl: 86400,
    },
    restrictions: {
      geoRestriction: {
        restrictionType: "whitelist",
        locations: ["FI"],
      },
    },
    viewerCertificate: {
      acmCertificateArn: frontendCert.arn,
      sslSupportMethod: "sni-only",
    },
  },
  {
    dependsOn: [bucket, frontendCert, oac],
  },
)

new aws.route53.Record(
  defaultResourceName + "-fr",
  {
    type: "A",
    zoneId: zoneId,
    name: frontendDomainName,
    aliases: [
      {
        name: distribution.domainName,
        zoneId: distribution.hostedZoneId,
        evaluateTargetHealth: true,
      },
    ],
  },
  {
    dependsOn: [distribution],
  },
)
