FROM openjdk:21-jdk

ENV APP_DIR /app
ENV LOG_DIR /app/log

RUN mkdir -p ${APP_DIR} ${LOG_DIR}

COPY ./docker/data_processor/run.sh ${APP_DIR}/run.sh
COPY ./target/dataProcessor*.jar ${APP_DIR}/data-processor.jar

EXPOSE 8080

RUN \
  groupadd -r app-user && \
  useradd -r -g app-user -d $APP_DIR -s /sbin/nologin -c "docker app user" app-user && \
  chown -R app-user:app-user $APP_DIR $LOG_DIR && \
  chmod +x ${APP_DIR}/run.sh

USER app-user

CMD exec ${APP_DIR}/run.sh