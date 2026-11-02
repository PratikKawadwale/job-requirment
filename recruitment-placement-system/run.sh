#!/usr/bin/env bash
# ==============================================================================
# Execution Script for RPMS (Recruitment & Placement Management System)
# ==============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "=========================================================="
echo " Starting Recruitment & Placement Management System (RPMS)"
echo "=========================================================="

# 1. Ensure bin and lib directory exist
mkdir -p bin
mkdir -p lib

# 2. Verify MySQL connector jar is present
CONNECTOR_JAR="lib/mysql-connector-j-8.3.0.jar"
if [ ! -f "$CONNECTOR_JAR" ]; then
    echo "[INFO] Locating MySQL connector jar in local repository..."
    M2_JAR=$(find "$HOME/.m2" -name "mysql-connector-j-*.jar" -not -name "*sources*" -not -name "*javadoc*" 2>/dev/null | head -n 1)
    if [ -n "$M2_JAR" ]; then
        cp "$M2_JAR" "$CONNECTOR_JAR"
        echo "[INFO] Copied $M2_JAR -> $CONNECTOR_JAR"
    else
        echo "[ERROR] mysql-connector jar not found. Please place mysql-connector-j-8.3.0.jar in lib/"
        exit 1
    fi
fi

# 3. Compile Java sources into bin/
echo "[1/3] Compiling Java classes..."
find src/main/java -name "*.java" > sources.txt
javac -d bin -cp "$CONNECTOR_JAR:src/main/resources" @sources.txt
rm -f sources.txt

# 4. Copy resource files (db.properties, etc.) into bin/
echo "[2/3] Copying runtime resources..."
cp -r src/main/resources/* bin/

# 5. Launch the console application
echo "[3/3] Launching RPMS Console Application..."
echo "=========================================================="
java -cp "bin:$CONNECTOR_JAR" com.recruitment.app.RecruitmentApp
