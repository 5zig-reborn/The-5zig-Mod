#!/usr/bin/env bash

# Build for versions that need the 4.9 wrapper
./gradlew :1.13.2:build

# Install the new wrapper
./gradlew wrapper --gradle-version 4.4.1

# Build
./gradlew -c .travis/1_8_9.gradle :1.8.9:build
./gradlew -c .travis/1_12_2.gradle :1.12.2:build

# Move artifacts
cd version-specific
mkdir artifacts
mv **/build/libs/*.jar artifacts
cd -

