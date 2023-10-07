#!/bin/bash

set -e
set -u

function create_user_and_database() {
	local database=$1
	psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
	    CREATE USER $database;
	    CREATE DATABASE $database;
	    GRANT ALL PRIVILEGES ON DATABASE $database TO $database;
EOSQL
}

if [ -n "$POSTGRES_DBS" ]; then
	for db in $(echo "$POSTGRES_DBS" | tr ',' ' '); do
		create_user_and_database "$db"
	done
	echo "Databases created"
fi
