FROM alpine/mysql:15.1

ENV APP_DIR /app

COPY ./starships.json ${APP_DIR}/starships.json
COPY ./run.sh ${APP_DIR}/run.sh

RUN apk update && apk add jq && apk add mariadb-connector-c

WORKDIR ${APP_DIR}

ENTRYPOINT exec ${APP_DIR}/run.sh