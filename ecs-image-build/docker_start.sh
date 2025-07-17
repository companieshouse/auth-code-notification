#!/bin/bash

# Start script for auth-code-notification

PORT=8080
exec java -jar -Dserver.port="${PORT}" -XX:MaxRAMPercentage=80 "auth-code-notification.jar"
