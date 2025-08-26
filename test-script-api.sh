#!/bin/bash

# Test script for Selenium Script Execution API

BASE_URL="http://localhost:8081"

echo "Testing Selenium Script Execution API"
echo "====================================="

# Health check
echo "1. Testing health endpoint..."
curl -s "$BASE_URL/api/selenium/health"
echo -e "\n"

# Test Java script execution
echo "2. Testing Java script execution..."
curl -X POST "$BASE_URL/api/selenium/execute-script" \
  -H "Content-Type: application/json" \
  -d '{
    "scriptContent": "driver.get(\"https://www.google.com\"); WebElement searchBox = driver.findElement(By.name(\"q\")); searchBox.sendKeys(\"Selenium automation\"); searchBox.submit(); Thread.sleep(3000);",
    "scriptType": "java",
    "browser": "chrome",
    "headless": true,
    "timeoutSeconds": 30,
    "screenshotPath": "/tmp/screenshots/script-test.png"
  }' | jq '.'

echo -e "\n"

# Test JavaScript execution
echo "3. Testing JavaScript execution..."
curl -X POST "$BASE_URL/api/selenium/execute-script" \
  -H "Content-Type: application/json" \
  -d '{
    "scriptContent": "document.title = \"Modified by Selenium\"; return document.title;",
    "scriptType": "javascript",
    "browser": "chrome",
    "headless": true,
    "timeoutSeconds": 30
  }' | jq '.'

echo -e "\n"
echo "Script execution tests completed!"
