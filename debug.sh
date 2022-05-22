#!/usr/bin/env bash
./gradlew clean
./gradlew assembleDebug -Dorg.gradle.daemon=false -Dorg.gradle.debug=true  --stacktrace