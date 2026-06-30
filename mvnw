#!/usr/bin/env sh
set -eu

MAVEN_VERSION="${MAVEN_VERSION:-3.9.9}"
MAVEN_USER_HOME="${MAVEN_USER_HOME:-"$HOME/.m2"}"
MVNW_DIST_DIR="$MAVEN_USER_HOME/wrapper/dists/apache-maven-$MAVEN_VERSION"
MVNW_MAVEN_HOME="$MVNW_DIST_DIR/apache-maven-$MAVEN_VERSION"

if [ ! -x "$MVNW_MAVEN_HOME/bin/mvn" ]; then
  echo "Downloading Apache Maven $MAVEN_VERSION..."
  mkdir -p "$MVNW_DIST_DIR"
  archive="$MVNW_DIST_DIR/apache-maven-$MAVEN_VERSION-bin.tar.gz"
  url="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MAVEN_VERSION/apache-maven-$MAVEN_VERSION-bin.tar.gz"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$url" -o "$archive"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$url" -O "$archive"
  else
    echo "curl or wget is required to download Maven." >&2
    exit 1
  fi
  tar -xzf "$archive" -C "$MVNW_DIST_DIR"
fi

exec "$MVNW_MAVEN_HOME/bin/mvn" "$@"
