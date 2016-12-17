#!/bin/bash

# This script launches sbt if it is in path, or downloads and runs the current sbt-launch.jar

SBT_HOME="$(dirname "$0")/.sbt"
SBT_VERSION='0.13.13'
SBT_OPTS=''

fetch_sbt() {
    echo fetching
    mkdir -p "$SBT_HOME"
    wget \
        --output-document="$SBT_HOME/sbt-launch.jar" \
        "https://repo.typesafe.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/$SBT_VERSION/sbt-launch.jar"
}

main() {
    if [ -x "$(which sbt)" ]; then
        sbt $@
    else
        if [ ! -e "$SBT_HOME/sbt-launch.jar" ]; then
            fetch_sbt
        fi
        java $SBT_OPTS -jar "$SBT_HOME/sbt-launch.jar" "$@"
    fi
}

main $@

