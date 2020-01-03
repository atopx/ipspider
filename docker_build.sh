#!/bin/bash
set -e

HARBOR=harbor.socmap.net
# 编译jar包
echo "开始编译jar包"
mvn clean
mvn package

rm -f ./slide/slide.jar
cp ./target/slide-*.jar ./slide/slide.jar
echo "jar包构建完成"
sleep 1

# 登录harbor
echo "正在登录harbor镜像仓库，请输入用户名和密码"
docker login $HARBOR

echo -n "请输入上次构建镜像的版本（可在`docker-compose.yml`文件中查看） ->"
read OLDTAG
echo -n "请输入这次构建镜像的版本，例如(v1.0.1) ->"
read TAG

# Linux替换命令
#sed -i "s/$OLDTAG/$TAG/g" docker-compose.yml

# MacOS替换命令
sed -i "" "s/$OLDTAG/$TAG/g" "docker-compose.yml"

echo "您输入的镜像版本为： $TAG!"
echo "此时docker-compose.yml中版本号如下所示，请检查值是否为$TAG"
cat docker-compose.yml | grep $HARBOR/bs-ip/ip-slide

echo "5秒后开始构建镜像,如果上述检查版本有误，请终止..."
sleep 5

# 构建镜像
docker-compose build

echo "镜像构建完成！"

echo "3秒后开始推送镜像到Harbor..."
sleep 3

# 推送镜像到仓库
docker push $HARBOR/bs-ip/ip-slide:$TAG
echo "镜像已成功推送到镜像仓库!"
