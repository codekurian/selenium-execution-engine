# Selenium Execution Engine

A Spring Boot application that provides a REST API for executing Selenium-based automation scripts. Built with Java 17 and supports Java 21, containerized with RHEL 8 and Oracle JDK.

## Features

- **REST API for Selenium script execution**
- **Raw script execution** - Execute complete Selenium scripts as raw content
- **File upload support** - Upload and execute script files directly
- **Dynamic script compilation** - Java scripts compiled and executed at runtime
- **Multiple script types** - Support for Java and JavaScript
- **Support for Chrome and Firefox browsers**
- **Headless mode support**
- **Screenshot capture capability**
- **Configurable timeouts and browser options**
- **Docker containerization with RHEL 8 base image**

## Prerequisites

- Java 17 or higher
- Gradle 8.5+
- Docker (for containerized deployment)

## Quick Start

### Local Development

1. Clone the repository:
```bash
git clone <repository-url>
cd selenium-execution-engine
```

2. Build the project:
```bash
./gradlew build
```

3. Run the application:
```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### Docker Deployment

1. Build the Docker image:
```bash
docker build -t selenium-execution-engine .
```

2. Run the container:
```bash
docker run -p 8080:8080 selenium-execution-engine
```

## API Endpoints

### 1. Health Check
```bash
GET /api/selenium/health
```

### 2. Execute Simple Actions
```bash
POST /api/selenium/execute
Content-Type: application/json

{
  "url": "https://example.com",
  "browser": "chrome",
  "headless": true,
  "timeoutSeconds": 30,
  "actions": [
    "click:#submit-button",
    "type:#search-input:search term",
    "wait:5"
  ],
  "screenshotPath": "/tmp/screenshots/result.png"
}
```

### 3. Execute Raw Script (JSON)
```bash
POST /api/selenium/execute-script
Content-Type: application/json

{
  "scriptContent": "driver.get(\"https://www.google.com\"); WebElement searchBox = driver.findElement(By.name(\"q\")); searchBox.sendKeys(\"Selenium automation\"); searchBox.submit();",
  "scriptType": "java",
  "browser": "chrome",
  "headless": true,
  "timeoutSeconds": 30,
  "screenshotPath": "/tmp/screenshots/result.png"
}
```

### 4. Execute Script File (File Upload)
```bash
POST /api/selenium/execute-file
Content-Type: multipart/form-data

# Upload a script file with parameters
curl -X POST http://localhost:8081/api/selenium/execute-file \
  -F "file=@scripts/simple-navigation.java" \
  -F "scriptType=java" \
  -F "browser=chrome" \
  -F "headless=true" \
  -F "timeoutSeconds=30"
```

## Script Examples

### Java Script Example
```java
// Navigate to Google
driver.get("https://www.google.com");

// Find and fill the search box
WebElement searchBox = driver.findElement(By.name("q"));
searchBox.sendKeys("Selenium WebDriver");

// Submit the search
searchBox.submit();

// Wait for results
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.presenceOfElementLocated(By.id("search")));

// Get the first result
WebElement firstResult = driver.findElement(By.cssSelector("#search .g:first-child h3"));
String resultText = firstResult.getText();

System.out.println("First search result: " + resultText);
```

### JavaScript Example
```javascript
// Execute JavaScript in the browser
return document.title;
```

## Request Parameters

### Simple Actions API
- `url` (required): The URL to navigate to
- `browser` (optional): Browser type ("chrome" or "firefox"), defaults to "chrome"
- `headless` (optional): Run in headless mode, defaults to true
- `timeoutSeconds` (optional): Page load timeout, defaults to 30
- `actions` (optional): Array of actions to perform
- `screenshotPath` (optional): Path to save screenshot

### Raw Script API
- `scriptContent` (required): Raw script content to execute
- `scriptType` (optional): "java", "javascript" (default: "java")
- `browser` (optional): "chrome", "firefox" (default: "chrome")
- `headless` (optional): true/false (default: true)
- `timeoutSeconds` (optional): timeout in seconds (default: 30)
- `screenshotPath` (optional): path to save screenshot
- `scriptArguments` (optional): array of arguments to pass to script

### File Upload API
- `file` (required): The script file to upload
- `scriptType` (optional): "java", "javascript" (default: "java")
- `browser` (optional): "chrome", "firefox" (default: "chrome")
- `headless` (optional): true/false (default: true)
- `timeoutSeconds` (optional): timeout in seconds (default: 30)
- `screenshotPath` (optional): path to save screenshot

### Supported Actions

- `click:selector`: Click on element with CSS selector
- `type:selector:text`: Type text into element with CSS selector
- `wait:seconds`: Wait for specified number of seconds

## Response Format

All endpoints return a JSON response with the following structure:

```json
{
  "success": true,
  "message": "Script executed successfully",
  "screenshotPath": "/tmp/screenshots/result.png",
  "executionTimeMs": 2500,
  "pageTitle": "Example Page",
  "pageUrl": "https://example.com"
}
```

## Usage Examples

### cURL Examples

#### Execute Java Script
```bash
curl -X POST http://localhost:8081/api/selenium/execute-script \
  -H "Content-Type: application/json" \
  -d '{
    "scriptContent": "driver.get(\"https://www.example.com\"); Thread.sleep(2000); String title = driver.getTitle(); System.out.println(\"Page title: \" + title);",
    "scriptType": "java",
    "browser": "chrome",
    "headless": true,
    "timeoutSeconds": 30
  }'
```

#### Execute JavaScript
```bash
curl -X POST http://localhost:8081/api/selenium/execute-script \
  -H "Content-Type: application/json" \
  -d '{
    "scriptContent": "driver.get(\"https://www.example.com\"); return document.title;",
    "scriptType": "javascript",
    "browser": "chrome",
    "headless": true
  }'
```

#### Upload Script File
```bash
curl -X POST http://localhost:8081/api/selenium/execute-file \
  -F "file=@scripts/simple-navigation.java" \
  -F "scriptType=java" \
  -F "browser=chrome" \
  -F "headless=true" \
  -F "screenshotPath=/tmp/screenshots/result.png"
```

### Python Example
```python
import requests
import json

url = "http://localhost:8081/api/selenium/execute-script"
script_content = """
driver.get("https://www.google.com");
WebElement searchBox = driver.findElement(By.name("q"));
searchBox.sendKeys("Selenium automation");
searchBox.submit();
Thread.sleep(3000);
"""

payload = {
    "scriptContent": script_content,
    "scriptType": "java",
    "browser": "chrome",
    "headless": True,
    "timeoutSeconds": 30
}

response = requests.post(url, json=payload)
result = response.json()
print(f"Success: {result['success']}")
print(f"Execution time: {result['executionTimeMs']}ms")
```

## Configuration

The application can be configured through `application.properties`:

```properties
# Server Configuration
server.port=8080

# Selenium Configuration
selenium.default.browser=chrome
selenium.default.headless=true
selenium.default.timeout=30
selenium.screenshot.directory=/tmp/screenshots
```

## Project Structure

```
selenium-execution-engine/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/selenium/
│   │   │       ├── SeleniumExecutionEngineApplication.java
│   │   │       ├── controller/
│   │   │       │   └── SeleniumController.java
│   │   │       ├── model/
│   │   │       │   ├── SeleniumRequest.java
│   │   │       │   ├── SeleniumScriptRequest.java
│   │   │       │   └── SeleniumResponse.java
│   │   │       └── service/
│   │   │           ├── SeleniumService.java
│   │   │           └── ScriptExecutionService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/selenium/
│               └── SeleniumExecutionEngineApplicationTests.java
├── scripts/
│   ├── simple-navigation.java
│   ├── google-search.java
│   ├── form-interaction.java
│   └── example-usage.java
├── examples/
│   ├── google-search.java
│   └── simple-navigation.java
├── build.gradle
├── Dockerfile
├── docker-compose.yml
├── README.md
├── API_DOCUMENTATION.md
├── FILE_UPLOAD_GUIDE.md
├── test-api.sh
├── test-raw-script.sh
└── test-file-upload.sh
```

## Available Scripts

The project includes several example scripts in the `scripts/` directory:

- `simple-navigation.java` - Basic navigation example
- `google-search.java` - Google search automation
- `form-interaction.java` - Form filling example
- `example-usage.java` - Comprehensive example

## Testing

Run the test scripts to verify functionality:

```bash
# Test basic API
./test-api.sh

# Test raw script execution
./test-raw-script.sh

# Test file upload functionality
./test-file-upload.sh
```

## Dependencies

- Spring Boot 3.2.0
- Selenium WebDriver 4.15.0
- WebDriverManager 5.6.2
- Lombok
- Jackson for JSON processing

## Documentation

- **API Documentation:** `API_DOCUMENTATION.md` - Complete API reference
- **File Upload Guide:** `FILE_UPLOAD_GUIDE.md` - File upload functionality guide

## Error Handling

The API returns appropriate HTTP status codes and error messages:

- `200 OK`: Script executed successfully
- `400 Bad Request`: Invalid request parameters or script compilation errors
- `500 Internal Server Error`: Server-side errors

## Security Considerations

- Scripts are executed in a controlled environment
- Timeout limits prevent infinite execution
- Headless mode is recommended for production
- Screenshots are saved to specified directories only
- Script compilation errors are caught and reported

## License

This project is licensed under the MIT License.
