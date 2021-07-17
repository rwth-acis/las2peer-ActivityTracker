#!/usr/bin/env bash

set -e

# print all comands to console if DEBUG is set
if [[ ! -z "${DEBUG}" ]]; then
  set -x
fi

function getProperty() {
  PROP_KEY=$1
  PROP_VALUE=$(cat $PROPERTY_FILE | grep "$PROP_KEY" | cut -d'=' -f2)
  echo $PROP_VALUE
}

# set some helpful variables
export SERVICE_PROPERTY_FILE='etc/de.rwth.dbis.acis.activitytracker.service.ActivityTrackerService.properties'
export WEB_CONNECTOR_PROPERTY_FILE='etc/i5.las2peer.connectors.webConnector.WebConnector.properties'

export SERVICE_VERSION=$(getProperty "service.version")
export SERVICE_NAME=$(getProperty "service.name")
export SERVICE_CLASS=$(getProperty "service.class")
export SERVICE=${SERVICE_NAME}.${SERVICE_CLASS}@${SERVICE_VERSION}

export POSTGRES_DATABASE='reqbaztrack'

# check mandatory variables
[[ -z "${POSTGRES_USER}" ]] &&
  echo "Mandatory variable POSTGRES_USER is not set. Add -e POSTGRES_USER=myuser to your arguments." && exit 1
[[ -z "${POSTGRES_PASSWORD}" ]] &&
  echo "Mandatory variable POSTGRES_PASSWORD is not set. Add -e POSTGRES_PASSWORD=mypasswd to your arguments." && exit 1

# set defaults for optional service parameters
[[ -z "${POSTGRES_HOST}" ]] && export POSTGRES_HOST='postgres'
[[ -z "${POSTGRES_PORT}" ]] && export POSTGRES_PORT='5432'

[[ -z "${BASE_URL}" ]] && export BASE_URL="http://localhost:${HTTP_PORT}/activities/"
[[ -z "${SERVICE_PASSPHRASE}" ]] && export SERVICE_PASSPHRASE='Passphrase'
[[ -z "${MQTT_BROKER}" ]] && export MQTT_BROKER=''
[[ -z "${MQTT_USER}" ]] && export MQTT_USER=''
[[ -z "${MQTT_PASSWORD}" ]] && export MQTT_PASSWORD=''
[[ -z "${MQTT_ORGANIZATION}" ]] && export MQTT_ORGANIZATION=''

# set defaults for optional web connector parameters
[[ -z "${START_HTTP}" ]] && export START_HTTP='TRUE'
[[ -z "${START_HTTPS}" ]] && export START_HTTPS='FALSE'
[[ -z "${SSL_KEYSTORE}" ]] && export SSL_KEYSTORE=''
[[ -z "${SSL_KEY_PASSWORD}" ]] && export SSL_KEY_PASSWORD=''
[[ -z "${CROSS_ORIGIN_RESOURCE_DOMAIN}" ]] && export CROSS_ORIGIN_RESOURCE_DOMAIN='*'
[[ -z "${CROSS_ORIGIN_RESOURCE_MAX_AGE}" ]] && export CROSS_ORIGIN_RESOURCE_MAX_AGE='60'
[[ -z "${ENABLE_CROSS_ORIGIN_RESOURCE_SHARING}" ]] && export ENABLE_CROSS_ORIGIN_RESOURCE_SHARING='TRUE'
[[ -z "${OIDC_PROVIDERS}" ]] && export OIDC_PROVIDERS='https://api.learning-layers.eu/o/oauth2,https://accounts.google.com'

# configure service properties

function set_in_service_config() {
  sed -i "s?${1}[[:blank:]]*=.*?${1}=${2}?g" ${SERVICE_PROPERTY_FILE}
}
set_in_service_config dbUserName ${POSTGRES_USER}
set_in_service_config dbPassword ${POSTGRES_PASSWORD}
set_in_service_config dbUrl "jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DATABASE}"
set_in_service_config baseURL ${BASE_URL}
set_in_service_config mqttBroker ${MQTT_BROKER}
set_in_service_config mqttUserName ${MQTT_USER}
set_in_service_config mqttPassword ${MQTT_PASSWORD}
set_in_service_config mqttOrganization ${MQTT_ORGANIZATION}

# configure web connector properties

function set_in_web_config() {
  sed -i "s?${1}[[:blank:]]*=.*?${1}=${2}?g" ${WEB_CONNECTOR_PROPERTY_FILE}
}
set_in_web_config httpPort ${HTTP_PORT}
set_in_web_config httpsPort ${HTTPS_PORT}
set_in_web_config startHttp ${START_HTTP}
set_in_web_config startHttps ${START_HTTPS}
set_in_web_config sslKeystore ${SSL_KEYSTORE}
set_in_web_config sslKeyPassword ${SSL_KEY_PASSWORD}
set_in_web_config crossOriginResourceDomain ${CROSS_ORIGIN_RESOURCE_DOMAIN}
set_in_web_config crossOriginResourceMaxAge ${CROSS_ORIGIN_RESOURCE_MAX_AGE}
set_in_web_config enableCrossOriginResourceSharing ${ENABLE_CROSS_ORIGIN_RESOURCE_SHARING}
set_in_web_config oidcProviders ${OIDC_PROVIDERS}

# set pod ip in pastry conf
if [[ ! -z "${POD_IP}" ]]; then
  echo external_address = ${POD_IP}:${LAS2PEER_PORT} > etc/pastry.properties
  echo socket_bindAddress = ${POD_IP} >> etc/pastry.properties
fi

# wait for any bootstrap host to be available
if [[ ! -z "${BOOTSTRAP}" ]]; then
  echo "Waiting for any bootstrap host to become available..."
  for host_port in ${BOOTSTRAP//,/ }; do
    arr_host_port=(${host_port//:/ })
    host=${arr_host_port[0]}
    port=${arr_host_port[1]}
    if { </dev/tcp/${host}/${port}; } 2>/dev/null; then
      echo "${host_port} is available. Continuing..."
      break
    fi
  done
fi

# prevent glob expansion in lib/*
set -f
LAUNCH_COMMAND='java -cp lib/* i5.las2peer.tools.L2pNodeLauncher -s service -p '"${LAS2PEER_PORT} ${SERVICE_EXTRA_ARGS}"
if [[ ! -z "${BOOTSTRAP}" ]]; then
  LAUNCH_COMMAND="${LAUNCH_COMMAND} -b ${BOOTSTRAP}"
fi

# start the service within a las2peer node
if [[ -z "${@}" ]]; then
  exec ${LAUNCH_COMMAND} startService\("'""${SERVICE}""'", "'""${SERVICE_PASSPHRASE}""'"\) startWebConnector
else
  exec ${LAUNCH_COMMAND} ${@}
fi
