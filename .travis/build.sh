#!/usr/bin/env bash

set -e

# Build
./gradlew build

# Move artifacts
cd version-specific
# Build 1.15.x fabric jar
cd 1.15.1/build/libs
wget https://launcher.mojang.com/v1/objects/e3f78cd16f9eb9a52307ed96ebec64241cc5b32d/client.jar
wget -O fabric.jar https://github.com/5zig-reborn/5zig-fabric/releases/download/2.0/5zig-Fabric-Installer-2.0.jar
java -jar fabric.jar client.jar
rm client.jar fabric.jar # Delete MC client jar and remapper
cd -
mkdir artifacts
mv **/build/libs/*.jar artifacts
cd -

