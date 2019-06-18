#!/usr/bin/env bash

git clone --depth 1 https://github.com/5zig-reborn/deployments
cd deployments

if [ ! $TRAVIS_BRANCH = "master" ]; then
    exit
fi

git config --local user.name "Travis CI"
git config --local user.email "travis@5zigreborn.eu"

rsync -av ../version-specific/artifacts/ .
git add --all
git commit -m "${TRAVIS_COMMIT_MESSAGE} (${TRAVIS_COMMIT})"
git push "https://${GITHUB_TOKEN}@github.com/5zig-reborn/deployments.git" master