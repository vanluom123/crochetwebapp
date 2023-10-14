#!/bin/bash

# Default setting
ENV="release"
LOG_FILE="liquibase-update.log"
MAIL_RECIPIENT="phanvanluom97bd@gmail.com"

# Help function
usage() {
    echo "Usage: $0 [-e environment] [-l log_file] [-m email]"
    echo "Options:"
    echo "  -e    Set environment (default: release)"
    echo "  -l    Set log file (default: liquibase-update.log)"
    echo "  -m    Set email for notifications"
    exit 1
}

# Handle options
while getopts ":e:l:m:" opt; do
    case $opt in
        e) ENV="$OPTARG" ;;
        l) LOG_FILE="$OPTARG" ;;
        m) MAIL_RECIPIENT="$OPTARG" ;;
        *) usage ;;
    esac
done

# Define function to send email
send_email() {
    SUBJECT=$1
    BODY=$2
    echo "$BODY" | mail -s "$SUBJECT" "$MAIL_RECIPIENT"
}

# Start updating
echo "Starting Liquibase update for $ENV environment..." | tee -a $LOG_FILE

# Change the path to the configuration file based on the environment
PROPERTIES_PATH="./src/main/resources/$ENV-liquibase.properties"

MYSQL_CONNECTOR_PATH="./mysql-connector-java-8.0.26/mysql-connector-java-8.0.26.jar"

# Run updating
if /usr/local/bin/liquibase --defaultsFile=$PROPERTIES_PATH --classpath=$MYSQL_CONNECTOR_PATH update >> $LOG_FILE 2>&1; then
    echo "Liquibase update completed successfully." | tee -a $LOG_FILE
    send_email "Liquibase Update Successful" "Liquibase update for $ENV was successful."
else
    echo "Liquibase update failed." | tee -a $LOG_FILE
    send_email "Liquibase Update Failed" "Liquibase update for $ENV failed. Check the logs for details."
    exit 1
fi
