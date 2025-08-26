#!/bin/bash

# Test script for File Upload Script Execution API

BASE_URL="http://localhost:8081"

echo "Testing File Upload Script Execution API"
echo "======================================="

# Health check
echo "1. Health check..."
curl -s "$BASE_URL/api/selenium/health"
echo -e "\n"

# Test 1: Upload simple navigation script
echo "2. Testing file upload with simple navigation script..."
curl -X POST "$BASE_URL/api/selenium/execute-file" \
  -F "file=@scripts/simple-navigation.java" \
  -F "scriptType=java" \
  -F "browser=chrome" \
  -F "headless=true" \
  -F "timeoutSeconds=30" | jq '.'

echo -e "\n"

# Test 2: Upload Google search script
echo "3. Testing file upload with Google search script..."
curl -X POST "$BASE_URL/api/selenium/execute-file" \
  -F "file=@scripts/google-search.java" \
  -F "scriptType=java" \
  -F "browser=chrome" \
  -F "headless=true" \
  -F "timeoutSeconds=30" \
  -F "screenshotPath=/tmp/screenshots/google-search-file.png" | jq '.'

echo -e "\n"

# Test 3: Upload form interaction script
echo "4. Testing file upload with form interaction script..."
curl -X POST "$BASE_URL/api/selenium/execute-file" \
  -F "file=@scripts/form-interaction.java" \
  -F "scriptType=java" \
  -F "browser=chrome" \
  -F "headless=true" \
  -F "timeoutSeconds=30" | jq '.'

echo -e "\n"
echo "File upload script execution tests completed!"
