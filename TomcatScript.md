
```
#!/bin/sh
#######################################################################
#
#   Copyright 2004 The Apache Software Foundation.
#
#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
#   implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.
#######################################################################

# Adapt the following lines to your configuration
JAVA_HOME=/usr/lib/jvm/java-6-sun
CATALINA_HOME=/opt/tomcat
DAEMON_HOME=/opt/tomcat/bin
TOMCAT_USER=tomcat

# for multi instances adapt those lines.
TMP_DIR=/tmp
PID_FILE=/var/run/jsvc.pid
CATALINA_BASE=/opt/tomcat

CATALINA_OPTS="-Djava.rmi.server.hostname=localhost \
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=1090 \
-Dcom.sun.management.jmxremote.ssl=false \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.password.file=/opt/tomcat/conf/jmxremote.password \
-Dcom.sun.management.jmxremote.access.file=/opt/tomcat/conf/jmxremote.access \
-Djava.library.path=/usr/local/apr/lib \
-Xmx256m"

CLASSPATH=\
$JAVA_HOME/lib/tools.jar:\
$CATALINA_HOME/bin/commons-daemon.jar:\
$CATALINA_HOME/bin/bootstrap.jar

. /lib/lsb/init-functions

case "$1" in
  start)
    #
    # Start Tomcat
    #
    log_daemon_msg "Starting java container" "tomcat"
    $DAEMON_HOME/jsvc \
    -user $TOMCAT_USER \
    -home $JAVA_HOME \
    -Dcatalina.home=$CATALINA_HOME \
    -Dcatalina.base=$CATALINA_BASE \
    -Djava.io.tmpdir=$TMP_DIR \
    -wait 10 \
    -pidfile $PID_FILE \
    -outfile $CATALINA_HOME/logs/catalina.out \
    -errfile '&1' \
    $CATALINA_OPTS \
    -cp $CLASSPATH \
    org.apache.catalina.startup.Bootstrap
    #
    # To get a verbose JVM
    #-verbose \
    # To get a debug of jsvc.
    #-debug \
    log_end_msg 0
    exit $?
    ;;

  stop)
    #
    # Stop Tomcat
    #
    log_daemon_msg "Stopping java container" "tomcat"
    $DAEMON_HOME/jsvc \
    -stop \
    -pidfile $PID_FILE \
    org.apache.catalina.startup.Bootstrap
    log_end_msg 0
    exit $?
    ;;

  *)
    echo "Usage tomcat.sh start/stop"
    exit 1;;
esac
```