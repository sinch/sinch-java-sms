#!/bin/sh

set -euo

INTERNAL_RELEASE=true

while getopts 'ph' OPTION; do
  case "$OPTION" in
    p)
      echo "Making public release"
      INTERNAL_RELEASE=false
      ;;
    ?)
      echo "Makes an internal release, unless -p (public) flag is set. script usage: $(basename "$0") [-h] [-p]" >&2
      exit 1
      ;;
  esac
done
shift "$(($OPTIND -1))"

[ -z "$REPO_URL" ] && echo "Need to set REPO_URL" && exit 1;
[ -z "$GPG_PASSPHRASE" ] && echo "Need to set GPG_PASSPHRASE" && exit 1;

if [ "$INTERNAL_RELEASE" = true ] ; then
  echo "Making internal release"
fi

mvn release:clean
mvn release:prepare --batch-mode "-Darguments=-DskipTests -Ddependency-check.skip=true"
mvn release:perform "-Darguments=-DskipTests -DuseInternalRepo=${INTERNAL_RELEASE}"
