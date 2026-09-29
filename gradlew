#!/bin/sh
set -eu
BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
GRADLE_VERSION=9.6.0
CACHE_DIR="${GRADLE_USER_HOME:-$HOME/.gradle}/wrapper/dists/opponify-gradle-$GRADLE_VERSION"
DIST="$CACHE_DIR/gradle-$GRADLE_VERSION"
ZIP="$CACHE_DIR/gradle-$GRADLE_VERSION-bin.zip"
if [ ! -x "$DIST/bin/gradle" ]; then
  mkdir -p "$CACHE_DIR"
  if [ ! -f "$ZIP" ]; then
    curl --fail --location --retry 3 "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -o "$ZIP"
  fi
  curl --fail --location --retry 3 "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip.sha256" -o "$ZIP.sha256"
  EXPECTED_SHA256=$(tr -d '\r\n[:space:]' < "$ZIP.sha256")
  ACTUAL_SHA256=$(sha256sum "$ZIP" | awk '{print $1}')
  if [ "$EXPECTED_SHA256" != "$ACTUAL_SHA256" ]; then
    echo "Gradle distribution checksum mismatch" >&2
    exit 1
  fi
  rm -rf "$DIST.tmp"
  mkdir -p "$DIST.tmp"
  unzip -q "$ZIP" -d "$DIST.tmp"
  mv "$DIST.tmp/gradle-$GRADLE_VERSION" "$DIST"
  rmdir "$DIST.tmp"
fi
exec "$DIST/bin/gradle" -p "$BASE_DIR" "$@"
