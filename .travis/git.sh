#!/usr/bin/env bash
set -e
GIT_REV=$(git rev-list --count origin/master)
git clone --depth 1 --no-single-branch https://github.com/5zig-reborn/deployments
cd deployments
git checkout $TRAVIS_BRANCH

git config --local user.name "Travis CI"
git config --local user.email "travis@5zigreborn.eu"

rm *
rsync -av ../version-specific/artifacts/ .
git add --all
git commit -m "${TRAVIS_COMMIT_MESSAGE} (${TRAVIS_COMMIT})" -m "Git-Rev: [${GIT_REV}]"
git push "https://${GITHUB_TOKEN}@github.com/5zig-reborn/deployments.git"