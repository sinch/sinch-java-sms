#!/bin/sh

set -eu

INTERNAL_RELEASE=true

# Parses command line arguments
# If called with -p, a public release is made
# If called with -h, help text is displayed
# If called without any arg, an internal release is made
while getopts 'ph' OPTION; do
  case "$OPTION" in
    p)
      INTERNAL_RELEASE=false
      ;;
    ?)
      echo "Makes an internal release, unless -p (public) flag is set. script usage: $(basename "$0") [-h] [-p]" >&2
      exit 1
      ;;
  esac
done
shift "$((OPTIND -1))"

# Checks that required environment variables are set
[ -z "$REPO_URL" ] && echo "Need to set REPO_URL" && exit 1;
[ -z "$GPG_PASSPHRASE" ] && echo "Need to set GPG_PASSPHRASE" && exit 1;

if [ "$INTERNAL_RELEASE" = true ] ; then
  echo "Making internal release"
  TAG="@{project.artifactId}-internal"
else
  echo "Making public release"
  TAG="@{project.artifactId}"
fi

# Make release
mvn release:clean
mvn -DtagNameFormat="$TAG" release:prepare --batch-mode "-Darguments=-DskipTests -Ddependency-check.skip=true"
mvn release:perform "-Darguments=-DskipTests -DuseInternalRepo=${INTERNAL_RELEASE}"
