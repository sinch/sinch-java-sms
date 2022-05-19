#!/bin/sh

set -euo pipefail

mvn release:clean
# TODO: tags are entered manually, need to fix for cicd, TODO: Gpg password needs to be provded manually
mvn release:prepare --batch-mode "-Darguments=-DskipTests -DuseTestRepo=true" # -Darguments=-Dgpg.passphrase="$GPG_PASSWORD"
mvn release:perform "-Darguments=-DskipTests -DuseTestRepo=true"
