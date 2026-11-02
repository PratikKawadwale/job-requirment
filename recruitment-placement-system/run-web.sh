#!/usr/bin/env bash
# ==============================================================================
# Web UI Runner for RPMS (Recruitment & Placement Management System)
# ==============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "=========================================================="
echo " Starting RPMS Web UI Server (Pure Java 17 + JDBC + MySQL)"
echo "=========================================================="

mkdir -p bin
mkdir -p lib

CONNECTOR_JAR="lib/mysql-connector-j-8.3.0.jar"
if [ ! -f "$CONNECTOR_JAR" ]; then
    M2_JAR=$(find "$HOME/.m2" -name "mysql-connector-j-*.jar" -not -name "*sources*" -not -name "*javadoc*" 2>/dev/null | head -n 1)
    if [ -n "$M2_JAR" ]; then
        cp "$M2_JAR" "$CONNECTOR_JAR"
    else
        echo "[ERROR] mysql-connector jar not found."
        exit 1
    fi
fi

echo "[1/3] Compiling Java classes..."
find src/main/java -name "*.java" > sources.txt
javac -d bin -cp "$CONNECTOR_JAR:src/main/resources" @sources.txt
rm -f sources.txt

echo "[2/3] Copying runtime resources..."
cp -r src/main/resources/* bin/

echo "[3/3] Launching RPMS Web Server..."
java -cp "bin:$CONNECTOR_JAR" com.recruitment.web.WebServerApp 8080
