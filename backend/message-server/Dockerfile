# 使用OpenJDK 17作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 安装必要的工具
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 设置时区上海
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建应用用户
RUN groupadd -r unimsg  && useradd -r -g unimsg unimsg

# 创建日志目录并赋权
RUN mkdir -p /app/logs && chown -R unimsg:unimsg /app

# 复制Maven构建的jar文件
COPY target/*.jar app.jar

# 设置文件权限
RUN chown unimsg:unimsg app.jar

# 切换到应用用户
USER unimsg

# 暴露端口
EXPOSE 8079

# 设置JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:+UseStringDeduplication"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
