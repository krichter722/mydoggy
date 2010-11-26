#!/bin/sh
#
# ------------------------------------------------------
#  TutorialSet Startup Script for Unix
# ------------------------------------------------------
#

if [ -z "$MYDOGGY_JDK" ]; then
  MYDOGGY_JDK=$JAVA_HOME
  if [ -z "MYDOGGY_JDK" ]; then
    echo ERROR: cannot start MyDoggySet.
    echo No JDK found to run. Please validate either MYDOGGY_JDK or JAVA_HOME
    echo points to valid installation path.
  fi
fi

MYDOGGY_HOME=`dirname "$0"`/..
MYDOGGY_BIN_HOME=`dirname "$0"`

export JAVA_HOME
export MYDOGGY_HOME

MYDOGGY_MCN="org.noos.xing.mydoggy.tutorialset.TutorialSet"

CLASSPATH=../lib/mydoggy-api-1.5.0.jar
CLASSPATH=$CLASSPATH:../lib/mydoggy-plaf-1.5.0.jar
CLASSPATH=$CLASSPATH:../lib/mydoggy-res-1.5.0.jar
CLASSPATH=$CLASSPATH:../lib/mydoggy-examples-1.5.0.jar
CLASSPATH=$CLASSPATH:../lib/TableLayout-20050920.jar

export CLASSPATH

LD_LIBRARY_PATH=.:$LD_LIBRARY_PATH
export LD_LIBRARY_PATH

cd "$MYDOGGY_BIN_HOME"
exec $MYDOGGY_JDK/bin/java $JVM_ARGS $MYDOGGY_MCN $*
