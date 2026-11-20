#!/bin/sh
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Default (empty profile) and "mysql" use MySQL; "postgres" uses PostgreSQL.
usage="Usage: zhouda-data-check-daemon.sh (start|start_container|start_with_jmx|stop|restart_with_jmx|status) <''|mysql|postgres>"

# if no args specified, show usage
if [ $# -le 0 ]; then
  echo $usage
  exit 1
fi

startStop=$1
shift
profile=$1
shift

springProfileActive=

if [ -n "$profile" ]; then
    if [ "$profile" = "postgres" ] || [ "$profile" = "mysql" ]; then
      springProfileActive="-Dspring.profiles.active=$profile"
    else
      echo "Error: No profile named \`$profile' was found."
      exit 1
    fi
fi

echo "Begin $startStop zhoudaDataCheck-server $profile......"

BIN_DIR=`dirname $0`
BIN_DIR=`cd "$BIN_DIR"; pwd`
ZHOUDA_DATA_CHECK_HOME=$BIN_DIR/..

source /etc/profile

export JAVA_HOME=$JAVA_HOME
export HOSTNAME=`hostname`

export ZHOUDA_DATA_CHECK_PID_DIR=$ZHOUDA_DATA_CHECK_HOME/pid
export ZHOUDA_DATA_CHECK_LOG_DIR=$ZHOUDA_DATA_CHECK_HOME/logs
export ZHOUDA_DATA_CHECK_CONF_DIR=$ZHOUDA_DATA_CHECK_HOME/conf
export ZHOUDA_DATA_CHECK_LIB_JARS=$ZHOUDA_DATA_CHECK_HOME/libs/*

export ZHOUDA_DATA_CHECK_OPTS="-server -Xmx16g -Xms1g -XX:+UseG1GC -XX:G1HeapRegionSize=8M"
export STOP_TIMEOUT=5

if [ ! -d "$ZHOUDA_DATA_CHECK_LOG_DIR" ]; then
  mkdir $ZHOUDA_DATA_CHECK_LOG_DIR
fi

log=$ZHOUDA_DATA_CHECK_LOG_DIR/zhoudaDataCheck-server-$HOSTNAME.out
pid=$ZHOUDA_DATA_CHECK_PID_DIR/zhoudaDataCheck-server.pid

cd $ZHOUDA_DATA_CHECK_HOME

LOG_FILE="-Dlogging.config=classpath:server-logback.xml $springProfileActive"
CLASS=io.zhoudadatacheck.server.ZhoudaDataCheckServer

# JMX path
JMX="-javaagent:$ZHOUDA_DATA_CHECK_HOME/libs/jmx_prometheus_javaagent-0.20.0.jar=10010:$ZHOUDA_DATA_CHECK_CONF_DIR/jmx/jmx_exporter_config.yaml"

case $startStop in
  (start)
    [ -w "$ZHOUDA_DATA_CHECK_PID_DIR" ] ||  mkdir -p "$ZHOUDA_DATA_CHECK_PID_DIR"

    if [ -f $pid ]; then
      if kill -0 `cat $pid` > /dev/null 2>&1; then
        echo "zhoudaDataCheck-server running as process `cat $pid`. Stop it first."
        exit 1
      fi
    fi

    echo "Starting zhoudaDataCheck-server, logging to $log ..."

    exec_command="$LOG_FILE $ZHOUDA_DATA_CHECK_OPTS -classpath $ZHOUDA_DATA_CHECK_CONF_DIR:$ZHOUDA_DATA_CHECK_LIB_JARS $CLASS"

    echo "nohup $JAVA_HOME/bin/java $exec_command > $log 2>&1 &"
    nohup $JAVA_HOME/bin/java $exec_command > $log 2>&1 &
    echo $! > $pid
    ;;

  (start_container)
    [ -w "$ZHOUDA_DATA_CHECK_PID_DIR" ] ||  mkdir -p "$ZHOUDA_DATA_CHECK_PID_DIR"

    if [ -f $pid ]; then
      if kill -0 `cat $pid` > /dev/null 2>&1; then
        echo "zhoudaDataCheck-server running as process `cat $pid`. Stop it first."
        exit 1
      fi
    fi

    echo "Starting zhoudaDataCheck-server, logging to $log ..."

    exec_command="$LOG_FILE $ZHOUDA_DATA_CHECK_OPTS -classpath $ZHOUDA_DATA_CHECK_CONF_DIR:$ZHOUDA_DATA_CHECK_LIB_JARS $CLASS"

    echo "$JAVA_HOME/bin/java $exec_command"
    $JAVA_HOME/bin/java $exec_command
    echo $! > $pid
    ;;

  (start_with_jmx)
    [ -w "$ZHOUDA_DATA_CHECK_PID_DIR" ] ||  mkdir -p "$ZHOUDA_DATA_CHECK_PID_DIR"

    if [ -f $pid ]; then
      if kill -0 `cat $pid` > /dev/null 2>&1; then
        echo "zhoudaDataCheck-server running as process `cat $pid`. Stop it first."
        exit 1
      fi
    fi

    echo "Starting zhoudaDataCheck-server, logging to $log ..."

    exec_command="$LOG_FILE $ZHOUDA_DATA_CHECK_OPTS ${JMX} -classpath $ZHOUDA_DATA_CHECK_CONF_DIR:$ZHOUDA_DATA_CHECK_LIB_JARS $CLASS"

    echo "nohup $JAVA_HOME/bin/java $exec_command > $log 2>&1 &"
    nohup $JAVA_HOME/bin/java $exec_command > $log 2>&1 &
    echo $! > $pid
    ;;

  (stop)
      if [ -f $pid ]; then
        TARGET_PID=`cat $pid`
        if kill -0 $TARGET_PID > /dev/null 2>&1; then
          echo "Stopping zhoudaDataCheck-server..."
          kill $TARGET_PID
          sleep $STOP_TIMEOUT
          if kill -0 $TARGET_PID > /dev/null 2>&1; then
            echo "zhoudaDataCheck-server did not stop gracefully after $STOP_TIMEOUT seconds: killing with kill -9"
            kill -9 $TARGET_PID
          fi
        else
          echo "No zhoudaDataCheck-server to stop."
        fi
        rm -f $pid
      else
        echo "No zhoudaDataCheck-server to stop."
      fi
      ;;

  (status)
    if [ -f $pid ]; then
      echo ""
      echo "Service zhoudaDataCheck-server is running. It's pid=${pid}"
      echo ""
    else
      echo ""
      echo "Service zhoudaDataCheck-server is not running!"
      echo ""
      exit 1
    fi
    ;;

  (restart_with_jmx)
    echo ""
    stop
    start_with_jmx
    echo "........................................Restart with Jmx Successfully........................................"
    ;;

  (*)
    echo $usage
    exit 1
    ;;

esac

echo "End $startStop zhoudaDataCheck-server $profile."
