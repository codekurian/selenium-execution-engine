#!/bin/bash

# Test script for Raw Selenium Script Execution API

BASE_URL="http://localhost:8081"

echo "Testing Raw Selenium Script Execution API"
echo "========================================="

# Health check
echo "1. Health check..."
curl -s "$BASE_URL/api/selenium/health"
echo -e "\n"

# Test 1: Simple Java script
echo "2. Testing simple Java script..."
curl -X POST "$BASE_URL/api/selenium/execute-script" \
  -H "Content-Type: application/json" \
  -d '{
    "scriptContent": "driver.get(\"https://www.example.com\"); Thread.sleep(2000); String title = driver.getTitle(); System.out.println(\"Page title: \" + title);",
    "scriptType": "java",
    "browser": "chrome",
    "headless": true,
    "timeoutSeconds": 30
  }' | jq '.'

echo -e "\n"

# Test 2: Google search script
echo "3. Testing Google search script..."
curl -X POST "$BASE_URL/api/selenium/execute-script" \
  -H "Content-Type: application/json" \
  -d '{
    "scriptContent": "driver.get(\"https://www.google.com\"); WebElement searchBox = driver.findElement(By.name(\"q\")); searchBox.sendKeys(\"Selenium automation\"); searchBox.submit(); Thread.sleep(3000); String title = driver.getTitle(); System.out.println(\"Search result title: \" + title);",
    "scriptType": "java",
    "browser": "chrome",
    "headless": true,
    "timeoutSeconds": 30,
    "screenshotPath": "/tmp/screenshots/google-search.png"
  }' | jq '.'

echo -e "\n"

# Test 3: JavaScript execution
echo "4. Testing JavaScript execution..."
curl -X POST "$BASE_URL/api/selenium/execute-script" \
  -H "Content-Type: application/json" \
  -d '{
    "scriptContent": "driver.get(\"https://www.example.com\"); return document.title;",
    "scriptType": "javascript",
    "browser": "chrome",
    "headless": true,
    "timeoutSeconds": 30
  }' | jq '.'

echo -e "\n"

# Test 4: Form upload script
echo "5. Testing form upload script..."
curl -X POST "$BASE_URL/api/selenium/upload-script" \
  -F "script=driver.get(\"https://www.example.com\"); Thread.sleep(2000); String title = driver.getTitle(); System.out.println(\"Uploaded script result: \" + title);" \
  -F "scriptType=java" \
  -F "browser=chrome" \
  -F "headless=true" \
  -F "timeoutSeconds=30" \
  -F "screenshotPath=/tmp/screenshots/upload-test.png" | jq '.'

echo -e "\n"
echo "Raw script execution tests completed!"
