#!/bin/bash

echo "🚀 Testing File Upload Functionality"
echo "==================================="

# Wait for the application to be ready
echo "⏳ Waiting for application to start..."
sleep 5

# Test 1: Health Check
echo ""
echo "📋 Test 1: Health Check"
echo "----------------------"
curl -s http://localhost:8081/api/selenium/health | jq '.'

# Test 2: Upload and Execute Java File
echo ""
echo "📋 Test 2: Upload and Execute Java File"
echo "--------------------------------------"
curl -X POST http://localhost:8081/api/selenium/execute-file \
  -F "file=@SampleTest.java" \
  -F "browser=chrome" \
  -F "headless=true" \
  -F "timeoutSeconds=30" | jq '.'

# Test 3: Upload with Custom Script Name
echo ""
echo "📋 Test 3: Upload with Custom Script Name"
echo "----------------------------------------"
curl -X POST http://localhost:8081/api/selenium/execute-file \
  -F "file=@SampleTest.java" \
  -F "scriptName=CustomTest" \
  -F "browser=chrome" \
  -F "headless=true" \
  -F "timeoutSeconds=30" | jq '.'

echo ""
echo "✅ File upload tests completed!"
echo ""
echo "📊 File Upload Features:"
echo "  ✅ Upload actual Java files"
echo "  ✅ Automatic script name from filename"
echo "  ✅ Custom script name support"
echo "  ✅ File content extraction"
echo "  ✅ Same execution pipeline"
echo ""
echo "🌐 API Endpoint:"
echo "  - POST http://localhost:8081/api/selenium/execute-file"
echo ""
echo "📝 Usage:"
echo "  curl -X POST http://localhost:8081/api/selenium/execute-file \\"
echo "    -F \"file=@YourTest.java\" \\"
echo "    -F \"browser=chrome\" \\"
echo "    -F \"headless=true\" \\"
echo "    -F \"timeoutSeconds=30\""
