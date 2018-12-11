#!/usr/bin/env bash
cp ./easylinker-service-proxy/target/easylinker-service-proxy-3.0.0.RELEASE.jar ./docker
cd ./docker
docker build -t registry-vpc.cn-shenzhen.aliyuncs.com/thunk/easylinker-open:3.0.0 .
docker push registry-vpc.cn-shenzhen.aliyuncs.com/thunk/easylinker-open:3.0.0
rm -rf ./Dockerfile ./easylinker-service-proxy-3.0.0.RELEASE.jar