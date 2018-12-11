#!/usr/bin/env bash
docker-compose down
docker rmi registry.cn-shenzhen.aliyuncs.com/thunk/easylinker-open:3.0.0
docker-compose up -d
