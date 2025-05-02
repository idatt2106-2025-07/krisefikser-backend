#!/bin/sh
# startup.sh

# Wait for database connection
echo "Checking database connection..."
MAX_RETRIES=30
RETRY_INTERVAL=2
RETRY_COUNT=0

# Extract host and port from MYSQL_URL environment variable
# This assumes MYSQL_URL is in jdbc:mysql://host:port/database format
DB_HOST=$(echo $MYSQL_URL | sed -n 's/.*\/\/\([^:]*\).*/\1/p')
DB_PORT=$(echo $MYSQL_URL | sed -n 's/.*:\([0-9]*\)\/.*/\1/p')

if [ -z "$DB_PORT" ]; then
  DB_PORT=3306  # Default MySQL port
fi

echo "Will attempt to connect to database at $DB_HOST:$DB_PORT"

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
  if nc -z -w5 $DB_HOST $DB_PORT; then
    echo "Database connection successful! Starting application..."
    # Start the Spring Boot application
    exec java -Dspring.profiles.active=prod -jar /app/app.jar
    exit 0
  else
    echo "Cannot connect to database yet. Retry $RETRY_COUNT/$MAX_RETRIES. Retrying in $RETRY_INTERVAL seconds..."
    sleep $RETRY_INTERVAL
    RETRY_COUNT=$((RETRY_COUNT+1))
  fi
done

echo "FATAL: Failed to connect to database after $MAX_RETRIES attempts. Application will not start."
exit 1