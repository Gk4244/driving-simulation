#!/usr/bin/env bash
# Builds and launches the Driving Simulation application.
# Every invocation performs a fresh Maven build and starts a brand-new JVM,
# so no state from a previous run is ever carried over.
set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

MVN_CMD="mvn"

if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven not found on PATH — bootstrapping a local copy (requires internet access)..."
  MAVEN_VERSION="3.9.9"
  MAVEN_DIR="$DIR/.maven-local"
  MAVEN_HOME_DIR="$MAVEN_DIR/apache-maven-${MAVEN_VERSION}"

  if [ ! -x "$MAVEN_HOME_DIR/bin/mvn" ]; then
    mkdir -p "$MAVEN_DIR"
    DOWNLOAD_URL="https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz"
    echo "Downloading Maven ${MAVEN_VERSION} from ${DOWNLOAD_URL} ..."
    curl -fsSL "$DOWNLOAD_URL" -o "$MAVEN_DIR/maven.tar.gz"
    tar -xzf "$MAVEN_DIR/maven.tar.gz" -C "$MAVEN_DIR"
    rm -f "$MAVEN_DIR/maven.tar.gz"
  fi

  MVN_CMD="$MAVEN_HOME_DIR/bin/mvn"
fi

echo "Building the application (this may take a moment on first run while dependencies download)..."
"$MVN_CMD" -q -DskipTests clean package

JAR_FILE="$(find "$DIR/target" -maxdepth 1 -name "*.jar" ! -name "*sources*" | head -n 1)"

if [ -z "$JAR_FILE" ]; then
  echo "Build failed: no jar found in target/" >&2
  exit 1
fi

echo ""
echo "Starting the Driving Simulation server..."
echo "Once it's up, open http://localhost:8080 in a browser."
echo "Press Ctrl+C to stop."
echo ""

exec java -jar "$JAR_FILE"
