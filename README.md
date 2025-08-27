# Selenium Execution Engine

A simple Spring Boot application that executes Java Selenium test scripts by taking them directly in the API payload or uploading Java files.

## 🚀 **How It Works**

1. **Send Test Script**: Send a Java test script in the API payload OR upload a Java file
2. **Save as File**: The script gets saved as a `.java` file
3. **Compile**: The Java file gets compiled dynamically
4. **Execute**: The compiled class gets executed with Selenium WebDriver
5. **Return Result**: Execution results are returned as JSON response

## 📋 **API Endpoints**

### **POST** `/api/selenium/execute-test` (JSON Payload)

**Request Body:**
```json
{
  "scriptName": "MyTest",
  "scriptContent": "import org.openqa.selenium.WebDriver;\nimport org.openqa.selenium.By;\nimport org.openqa.selenium.WebElement;\n\npublic class MyTest {\n    public static Object execute(WebDriver driver) {\n        try {\n            driver.get(\"https://www.example.com\");\n            String title = driver.getTitle();\n            System.out.println(\"Page title: \" + title);\n            return \"Test completed successfully. Title: \" + title;\n        } catch (Exception e) {\n            throw new RuntimeException(\"Test failed\", e);\n        }\n    }\n}",
  "browser": "chrome",
  "headless": true,
  "timeoutSeconds": 30
}
```

### **POST** `/api/selenium/execute-file` (File Upload)

**Request (multipart/form-data):**
```bash
curl -X POST http://localhost:8081/api/selenium/execute-file \
  -F "file=@YourTest.java" \
  -F "browser=chrome" \
  -F "headless=true" \
  -F "timeoutSeconds=30"
```

**Parameters:**
- `file` (required): The Java file to upload
- `scriptName` (optional): Custom name for the script (uses filename if not provided)
- `browser` (optional): "chrome" or "firefox" (default: "chrome")
- `headless` (optional): true/false (default: true)
- `timeoutSeconds` (optional): timeout in seconds (default: 30)
- `screenshotPath` (optional): path to save screenshot

**Response:**
```json
{
  "success": true,
  "message": "Test executed successfully",
  "executionTimeMs": 2500,
  "pageTitle": "Example Domain",
  "pageUrl": "https://www.example.com/",
  "screenshotPath": null
}
```

## 🔧 **Test Script Requirements**

Your Java test script must:

1. **Have a static `execute` method** that takes a `WebDriver` parameter
2. **Return an Object** (can be String, Map, etc.)
3. **Handle exceptions** properly
4. **Use proper imports** for Selenium classes

**Example Test Script:**
```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class MyTest {
    public static Object execute(WebDriver driver) {
        try {
            // Navigate to a page
            driver.get("https://www.google.com");
            
            // Find and interact with elements
            WebElement searchBox = driver.findElement(By.name("q"));
            searchBox.sendKeys("Selenium automation");
            searchBox.submit();
            
            // Get results
            String title = driver.getTitle();
            System.out.println("Page title: " + title);
            
            // Return result
            return "Test completed successfully. Title: " + title;
            
        } catch (Exception e) {
            throw new RuntimeException("Test failed", e);
        }
    }
}
```

## 🚀 **Quick Start**

### **1. Build and Run**
```bash
# Build the application
./gradlew build

# Run locally
./gradlew bootRun

# Or use Docker
docker-compose up -d
```

### **2. Test the API**

#### **Test JSON Payload:**
```bash
# Make the test script executable
chmod +x test-initial-integration.sh

# Run the tests
./test-initial-integration.sh
```

#### **Test File Upload:**
```bash
# Make the test script executable
chmod +x test-file-upload.sh

# Run the tests
./test-file-upload.sh
```

### **3. Manual Test**

#### **JSON Payload:**
```bash
curl -X POST http://localhost:8080/api/selenium/execute-test \
  -H "Content-Type: application/json" \
  -d '{
    "scriptName": "SimpleTest",
    "scriptContent": "import org.openqa.selenium.WebDriver;\n\npublic class SimpleTest {\n    public static Object execute(WebDriver driver) {\n        driver.get(\"https://www.example.com\");\n        return \"Success: \" + driver.getTitle();\n    }\n}",
    "browser": "chrome",
    "headless": true
  }'
```

#### **File Upload:**
```bash
curl -X POST http://localhost:8080/api/selenium/execute-file \
  -F "file=@SampleTest.java" \
  -F "browser=chrome" \
  -F "headless=true"
```

## ⚙️ **Configuration**

### **Application Properties**
```properties
# Server
server.port=8080

# Selenium
selenium.scripts.directory=/tmp/test-scripts
selenium.timeout=30
selenium.default.browser=chrome
selenium.default.headless=true
selenium.screenshot.directory=/tmp/screenshots
```

### **Environment Variables**
```bash
SERVER_PORT=8080
SELENIUM_SCRIPTS_DIRECTORY=/tmp/test-scripts
SELENIUM_TIMEOUT=30
```

## 📁 **File Structure**

```
selenium-execution-engine/
├── src/main/java/com/selenium/
│   ├── controller/
│   │   └── SeleniumController.java          # API endpoints
│   ├── model/
│   │   ├── TestScriptRequest.java           # Request model
│   │   └── SeleniumResponse.java            # Response model
│   └── service/
│       └── TestExecutionService.java        # Test execution logic
├── src/main/resources/
│   └── application.properties               # Configuration
├── Dockerfile                               # Docker image
├── docker-compose.yml                       # Docker deployment
├── SampleTest.java                          # Sample test file
├── test-initial-integration.sh              # JSON payload test script
├── test-file-upload.sh                      # File upload test script
└── README.md                                # This file
```

## 🔍 **Features**

- ✅ **Simple API**: Single endpoint for test execution
- ✅ **File Upload**: Upload actual Java files
- ✅ **JSON Payload**: Send script content directly
- ✅ **File-based**: Scripts saved as files for debugging
- ✅ **Dynamic Compilation**: Java files compiled on-the-fly
- ✅ **Multiple Browsers**: Support for Chrome and Firefox
- ✅ **Headless Mode**: Run tests without GUI
- ✅ **Timeout Control**: Configurable execution timeouts
- ✅ **Error Handling**: Proper error reporting
- ✅ **Docker Support**: Easy containerized deployment

## 🛡️ **Security Considerations**

⚠️ **Warning**: This approach uses dynamic compilation, which can be a security risk if not properly controlled.

**Recommendations:**
- Use in controlled environments only
- Validate script content before execution
- Implement proper access controls
- Consider using pre-compiled test classes for production

## 🐛 **Troubleshooting**

### **Common Issues**

1. **"Java compiler not available"**
   - Ensure you're running with JDK, not JRE
   - Check `JAVA_HOME` environment variable

2. **"Compilation failed"**
   - Check your Java syntax
   - Ensure all required imports are present
   - Verify the `execute` method signature
   - Make sure class name matches filename for file uploads

3. **"WebDriver not found"**
   - Ensure Chrome/Firefox is installed
   - Check WebDriverManager configuration

4. **"Port already in use"**
   - Change the server port in `application.properties`
   - Kill existing processes using the port

## 📚 **Next Steps**

1. **Add Authentication**: Implement API key or token-based auth
2. **Script Validation**: Add syntax and security validation
3. **Test Libraries**: Support for TestNG or JUnit
4. **Parallel Execution**: Run multiple tests simultaneously
5. **Reporting**: Generate detailed test reports
6. **Screenshots**: Implement proper screenshot capture

---

**Simple, Direct, and Effective** - Execute your Selenium test scripts with ease!
