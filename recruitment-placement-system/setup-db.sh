#!/usr/bin/env bash
# ==============================================================================
# Database Setup Script for RPMS (Recruitment & Placement Management System)
# ==============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SQL_FILE="$SCRIPT_DIR/database/rpms_full_setup.sql"

echo "=========================================================="
echo " RPMS Database Setup (rpms_db)"
echo "=========================================================="
echo "Target SQL script: $SQL_FILE"
echo ""

# Check if docker container 'mysql8' is running
if docker ps --format '{{.Names}}' | grep -q "^mysql8$"; then
    echo "[INFO] Detected running MySQL container: mysql8"
    echo "[INFO] Importing $SQL_FILE into container mysql8..."
    docker exec -i mysql8 mysql -u root -proot123 < "$SQL_FILE"
    echo ""
    echo "[SUCCESS] rpms_db created and populated with all 17 tables & sample data!"
    exit 0
fi

# Fallback: local mysql command line
if command -v mysql &> /dev/null; then
    echo "[INFO] Running local MySQL client..."
    mysql -u root -proot123 < "$SQL_FILE"
    echo "[SUCCESS] rpms_db created and populated with all 17 tables & sample data!"
    exit 0
fi

echo "[ERROR] Could not find running MySQL instance or mysql CLI."
echo "Please start MySQL or Docker container 'mysql8' and re-run."
exit 1
