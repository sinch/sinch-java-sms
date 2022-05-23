#!/bin/sh

set -euo

[ -z "$REPO_URL" ] && echo "Need to set REPO_URL" && exit 1;
[ -z "$GPG_PASSPHRASE" ] && echo "Need to set GPG_PASSPHRASE" && exit 1;

mvn release:clean
mvn release:prepare --batch-mode "-Darguments=-DskipTests -DuseInternalRepo=true -Ddependency-check.skip=true"
mvn release:perform "-Darguments=-DskipTests -DuseInternalRepo=true"
