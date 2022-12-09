#!/bin/bash
set -e

REGION=$1
ACCOUNT_ID=$2
IMAGE_ID=$3

if [[ -z $REGION || -z $ACCOUNT_ID || -z $IMAGE_ID ]]; then
    echo "You must give region, account id and image id as variables"
    exit -1
fi

echo "Region: ${REGION}"
echo "Account ID: ${ACCOUNT_ID}"
echo "Image ID: ${IMAGE_ID}"

aws ecr get-login-password --region ${REGION} | docker login --username AWS --password-stdin ${ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com
docker tag ${IMAGE_ID} ${ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com/kiekkohamsteri-dev
docker push ${ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com/kiekkohamsteri-dev
