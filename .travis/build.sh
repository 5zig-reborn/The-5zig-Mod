#!/usr/bin/env bash

set -e

# Build
./gradlew build

# Move artifacts
cd version-specific
mkdir artifacts
mv **/build/libs/*.jar artifacts
cd -

