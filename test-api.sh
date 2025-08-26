#!/bin/bash

# Test script for Selenium Execution Engine API

BASE_URL="http://localhost:8080"

echo "Testing Selenium Execution Engine API"
echo "====================================="

# Health check
echo "1. Testing health endpoint..."
curl -s "$BASE_URL/api/selenium/health"
echo -e "\n"

# Test Selenium execution
echo "2. Testing Selenium execution..."
curl -X POST "$BASE_URL/api/selenium/execute" \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://www.google.com",
    "browser": "chrome",
    "headless": true,
    "timeoutSeconds": 30,
    "actions": [
      "wait:3"
    ],
    "screenshotPath": "/tmp/screenshots/google-test.png"
  }' | jq '.'

echo -e "\n"
echo "Test completed!"
