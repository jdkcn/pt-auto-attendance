FROM eclipse-temurin:17-jdk-jammy

MAINTAINER Ye Rory <rory.cn@gmail.com>

ARG user=spring
ARG group=spring
ARG LAYERS_DIR=target

ENV JAVA_OPTS=""
ENV DEFAULT_JAVA_OPTS -XX:MaxRAMPercentage=80 -Djava.awt.headless=true -XX:+HeapDumpOnOutOfMemoryError \
 -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -Xlog:gc:file=/home/spring/logs/gc.log \
 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9876 -Dcom.sun.management.jmxremote.ssl=false \
 -Dcom.sun.management.jmxremote.authenticate=false -Dlogging.file.path=/home/spring/logs \
 -Dlogging.level.com.homolo=info -Dserver.port=8080 -Dspring.jpa.primary.show-sql=false
ENV SPRING_HOME=/home/spring
ENV APP_HOME=$SPRING_HOME/app

RUN groupadd -g 1000 ${group} \
	&& useradd -d "$SPRING_HOME" -u 1000 -g 1000 -m -s /bin/bash ${user} \
	&& mkdir -p $SPRING_HOME/config \
	&& mkdir -p $SPRING_HOME/logs \
	&& mkdir -p $APP_HOME \
	&& chown -R ${user}:${group} $SPRING_HOME/config $APP_HOME $SPRING_HOME/logs

VOLUME ["$SPRING_HOME/config", "$SPRING_HOME/logs"]

USER ${user}

WORKDIR $SPRING_HOME

EXPOSE 8900 9876

ENTRYPOINT ["bash","-c","java $DEFAULT_JAVA_OPTS $JAVA_OPTS -cp ./app org.springframework.boot.loader.JarLauncher"]

COPY --chown=${user}:${group} ${LAYERS_DIR}/dependencies $APP_HOME/
COPY --chown=${user}:${group} ${LAYERS_DIR}/spring-boot-loader $APP_HOME/
COPY --chown=${user}:${group} ${LAYERS_DIR}/snapshot-dependencies $APP_HOME/
COPY --chown=${user}:${group} ${LAYERS_DIR}/application $APP_HOME/
