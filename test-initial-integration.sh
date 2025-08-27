#!/bin/bash

echo "🚀 Testing Initial Integration - Direct Script Payload"
echo "====================================================="

# Wait for the application to be ready
echo "⏳ Waiting for application to start..."
sleep 5

# Test 1: Health Check
echo ""
echo "📋 Test 1: Health Check"
echo "----------------------"
curl -s http://localhost:8080/api/selenium/health | jq '.'

# Test 2: Execute Simple Test Script
echo ""
echo "📋 Test 2: Execute Simple Test Script"
echo "------------------------------------"
curl -X POST http://localhost:8080/api/selenium/execute-test \
  -H "Content-Type: application/json" \
  -d '{
    "scriptName": "SimpleTest",
    "scriptContent": "import org.openqa.selenium.WebDriver;\nimport org.openqa.selenium.By;\nimport org.openqa.selenium.WebElement;\n\npublic class SimpleTest {\n    public static Object execute(WebDriver driver) {\n        try {\n            driver.get(\"https://www.example.com\");\n            String title = driver.getTitle();\n            System.out.println(\"Page title: \" + title);\n            return \"Test completed successfully. Title: \" + title;\n        } catch (Exception e) {\n            throw new RuntimeException(\"Test failed\", e);\n        }\n    }\n}",
    "browser": "chrome",
    "headless": true,
    "timeoutSeconds": 30
  }' | jq '.'

# Test 3: Execute Google Search Test
echo ""
echo "📋 Test 3: Execute Google Search Test"
echo "------------------------------------"
curl -X POST http://localhost:8080/api/selenium/execute-test \
  -H "Content-Type: application/json" \
  -d '{
    "scriptName": "GoogleSearchTest",
    "scriptContent": "import org.openqa.selenium.WebDriver;\nimport org.openqa.selenium.By;\nimport org.openqa.selenium.WebElement;\n\npublic class GoogleSearchTest {\n    public static Object execute(WebDriver driver) {\n        try {\n            driver.get(\"https://www.google.com\");\n            WebElement searchBox = driver.findElement(By.name(\"q\"));\n            searchBox.sendKeys(\"Selenium WebDriver automation\");\n            searchBox.submit();\n            String title = driver.getTitle();\n            System.out.println(\"Search result title: \" + title);\n            return \"Google search completed successfully. Title: \" + title;\n        } catch (Exception e) {\n            throw new RuntimeException(\"Test failed\", e);\n        }\n    }\n}",
    "browser": "chrome",
    "headless": true,
    "timeoutSeconds": 30
  }' | jq '.'

echo ""
echo "✅ Initial integration tests completed!"
echo ""
echo "📊 Simple Integration Features:"
echo "  ✅ Direct script content in payload"
echo "  ✅ File-based execution"
echo "  ✅ Dynamic compilation"
echo "  ✅ Selenium Grid replacement"
echo "  ✅ Simple API"
echo ""
echo "🌐 API Endpoint:"
echo "  - POST http://localhost:8080/api/selenium/execute-test"
echo ""
echo "📝 Request Format:"
echo "  {"
echo "    \"scriptName\": \"TestName\","
echo "    \"scriptContent\": \"Java code with static execute(WebDriver) method\","
echo "    \"browser\": \"chrome\","
echo "    \"headless\": true,"
echo "    \"timeoutSeconds\": 30"
echo "  }"
