import * as pulumi from "@pulumi/pulumi";
import * as aws from "@pulumi/aws";
import * as awsx from "@pulumi/awsx";

const config = new pulumi.Config();
const env = config.require("environment");
const defaultResourceName = `kiekkohamsteri-${env}`;
const subnets = ["subnet-e3947c8a", "subnet-bc3b30c4", "subnet-737d4839"]

const bucket = new aws.s3.Bucket(defaultResourceName);
const accessLogs = new aws.s3.Bucket(defaultResourceName + "-lb-access-logs");

const dbParameterGroup = new aws.rds.ParameterGroup(defaultResourceName + "-db-params", {
    family: "postgres13"
});

const vpc = new aws.ec2.Vpc("default-vpc", {
    cidrBlock: "172.31.0.0/16",
    enableDnsHostnames: true,
}, {
    protect: true,
});


const kiekko_sg = new aws.ec2.SecurityGroup("kiekko-sg", {
    description: "2022-09-16T10:34:28.283Z",
    egress: [{
        cidrBlocks: ["0.0.0.0/0"],
        fromPort: 0,
        protocol: "-1",
        toPort: 0,
    }],
    ingress: [{
        cidrBlocks: ["0.0.0.0/0"],
        fromPort: 5432,
        protocol: "tcp",
        toPort: 5432,
    },{
        cidrBlocks: ["0.0.0.0/0"],
        fromPort: 80,
        protocol: "tcp",
        toPort: 80,
    },{
        cidrBlocks: ["0.0.0.0/0"],
        fromPort: 443,
        protocol: "tcp",
        toPort: 443,
    }],
    name: "kiekko-4484",
    vpcId: vpc.id,
}, {
    protect: true,
});

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
    publiclyAccessible: false,
    vpcSecurityGroupIds: [kiekko_sg.id]
});

const kiekkohamsteri_ecs_cluster = new aws.ecs.Cluster("kiekkohamsteri-ecs-cluster", {
    name: "kiekkohamsteri",
    settings: [{
        name: "containerInsights",
        value: "disabled",
    }],
}, {
    protect: true,
});

const backend_cert = new aws.acm.Certificate("backend-cert", {
    domainName: "kiekkohamsteri-backend.valuemotive.net",
    subjectAlternativeNames: ["kiekkohamsteri-backend.valuemotive.net"],
    validationMethod: "DNS",
}, {
    protect: true,
});

const lb = new aws.lb.LoadBalancer(defaultResourceName + "-lb", {
    internal: false,
    loadBalancerType: "application",
    securityGroups: [kiekko_sg.id],
    subnets: subnets,
    enableDeletionProtection: true
});

const target_group = new aws.lb.TargetGroup(defaultResourceName + "-tg", {
    port: 80,
    protocol: "HTTP",
    targetType: "ip",
    vpcId: vpc.id,
    healthCheck: {
        enabled: true,
        path: "/actuator/health"
    }
});

const listener = new aws.lb.Listener(defaultResourceName + "listener", {
    loadBalancerArn: lb.arn,
    port: 443,
    protocol: "HTTPS",
    sslPolicy: "ELBSecurityPolicy-2016-08",
    certificateArn: backend_cert.arn,
    defaultActions: [
        {
            type: "forward",
            targetGroupArn: target_group.arn,
        },
    ],
});

const kiekkohamsteri_service = new aws.ecs.Service("kiekkohamsteri-service", {
    cluster: kiekkohamsteri_ecs_cluster.arn,
    deploymentCircuitBreaker: {
        enable: false,
        rollback: false,
    },
    desiredCount: 1,
    enableEcsManagedTags: true,
    iamRole: "aws-service-role",
    launchType: "FARGATE",
    name: "kiekkohamsteri-service",
    networkConfiguration: {
        assignPublicIp: true,
        securityGroups: [kiekko_sg.id],
        subnets: subnets
    },
    platformVersion: "LATEST",
    propagateTags: "NONE",
    loadBalancers: [{
        targetGroupArn: target_group.arn,
        containerName: "kiekkohamsteri-container",
        containerPort: 80,
    }]
}, {
    protect: true,
    ignoreChanges: ["taskDefinition"]
});

const valuemotive_zone = new aws.route53.Zone("valuemotive_zone", {
    comment: "",
    name: "valuemotive.net",
}, {
    protect: true,
});

const record = new aws.route53.Record(defaultResourceName + "-br", {
    type: "A",
    zoneId: valuemotive_zone.id,
    name: "kiekkohamsteri-backend.valuemotive.net",
    aliases: [{
        name: lb.dnsName,
        zoneId: lb.zoneId,
        evaluateTargetHealth: true,
    }]
});

const s3OriginId = "hamsteriS3Origin";
const distribution = new aws.cloudfront.Distribution(defaultResourceName + "-fe", {
    origins: [{
        domainName: bucket.bucketRegionalDomainName,
        originId: s3OriginId
    }],
    enabled: true,
    isIpv6Enabled: false,
    defaultRootObject: "index.html",
    aliases: [
        "kiekkohamsteri.valuemotive.net"
    ],
    defaultCacheBehavior: {
        allowedMethods: [
            "GET",
            "HEAD",
            "OPTIONS"
        ],
        cachedMethods: [
            "GET",
            "HEAD",
            "OPTIONS"
        ],
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
        maxTtl: 86400
    },
    restrictions: {
        geoRestriction: {
            restrictionType: "none"
        }
    },
    viewerCertificate: {
        acmCertificateArn: "arn:aws:acm:us-east-1:806232589401:certificate/39b623e4-efdb-4462-ac30-fd28713026f9",
        sslSupportMethod: "sni-only"
    }
});

const fe_record = new aws.route53.Record(defaultResourceName + "-fe", {
    type: "A",
    zoneId: valuemotive_zone.id,
    name: "kiekkohamsteri.valuemotive.net",
    aliases: [{
        name: distribution.domainName,
        zoneId: distribution.hostedZoneId,
        evaluateTargetHealth: true,
    }]
});
