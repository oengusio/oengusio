#!/bin/sh
SPRING_PROFILE="${SPRING_PROFILE:-prod}"
JAVA_XMX="${JAVA_XMX:-256m}"
java -Dspring.profiles.active=${SPRING_PROFILE} -Xmx${JAVA_XMX} -jar oengusio.jar