#!/bin/sh
#
# Gradle startup script for Unix
#

DIRNAME=$(dirname "$0")
cd "$DIRNAME" || exit 1
GRADLE_WRAPPER_JAR="gradle/wrapper/gradle-wrapper.jar"

if [ ! -f "$GRADLE_WRAPPER_JAR" ]; then
    echo ""
    echo "ERROR: gradle-wrapper.jar not found. Create Gradle Wrapper first:"
    echo "  1. Install Gradle from https://gradle.org/install/"
    echo "  2. In this folder run: gradle wrapper"
    echo "  Or download gradle-wrapper.jar and put in gradle/wrapper/"
    echo ""
    exit 1
fi

exec java -jar "$GRADLE_WRAPPER_JAR" "$@"
