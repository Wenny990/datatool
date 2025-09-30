#基础镜像使用java
FROM openjdk:8-jdk-alpine3.9

VOLUME /tmp

ENV LANG en_US.UTF-8

# 修改时区和安装字体
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories \
    && apk add tzdata && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime  \
    && echo "Asia/Shanghai" > /etc/timezone && apk del tzdata \
    && apk add --update ttf-dejavu fontconfig && rm -rf /var/cache/apk/*

# 复制本地的Java应用程序到容器内的指定目录
add ./*.jar /usr/app/app.jar
 
# 设置工作目录为容器内的/usr/app
WORKDIR /usr/app

EXPOSE 18110

# 指定容器启动时运行的命令
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/urandom","-jar", "app.jar"]
