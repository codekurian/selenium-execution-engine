# Selenium Execution Engine API Documentation

## Overview
The Selenium Execution Engine provides REST APIs for executing Selenium automation scripts. It supports both simple action-based execution and raw script execution.

## Base URL
```
http://localhost:8081/api/selenium
```

## Endpoints

### 1. Health Check
**GET** `/health`

Returns the health status of the application.

**Response:**
```
Selenium Execution Engine is running!
```

### 2. Execute Simple Actions
**POST** `/execute`

Execute predefined Selenium actions.

**Request Body:**
```json
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
**POST** `/execute-script`

Execute complete Selenium scripts as raw content.

**Request Body:**
```json
{
  "scriptContent": "driver.get(\"https://www.google.com\"); WebElement searchBox = driver.findElement(By.name(\"q\")); searchBox.sendKeys(\"Selenium automation\"); searchBox.submit();",
  "scriptType": "java",
  "browser": "chrome",
  "headless": true,
  "timeoutSeconds": 30,
  "screenshotPath": "/tmp/screenshots/result.png",
  "scriptArguments": ["arg1", "arg2"]
}
```

**Parameters:**
- `scriptContent` (required): Raw script content to execute
- `scriptType` (optional): "java", "javascript" (default: "java")
- `browser` (optional): "chrome", "firefox" (default: "chrome")
- `headless` (optional): true/false (default: true)
- `timeoutSeconds` (optional): timeout in seconds (default: 30)
- `screenshotPath` (optional): path to save screenshot
- `scriptArguments` (optional): array of arguments to pass to script

### 4. Upload and Execute Script (Form Data)
**POST** `/upload-script`

Upload and execute scripts using form data.

**Form Parameters:**
- `script` (required): Raw script content
- `scriptType` (optional): "java", "javascript" (default: "java")
- `browser` (optional): "chrome", "firefox" (default: "chrome")
- `headless` (optional): true/false (default: true)
- `timeoutSeconds` (optional): timeout in seconds (default: 30)
- `screenshotPath` (optional): path to save screenshot

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

**Fields:**
- `success`: boolean indicating if execution was successful
- `message`: descriptive message about the execution
- `screenshotPath`: path to saved screenshot (if requested)
- `executionTimeMs`: execution time in milliseconds
- `pageTitle`: title of the final page
- `pageUrl`: URL of the final page

## Script Examples

### Java Script Examples

#### Simple Navigation
```java
driver.get("https://www.example.com");
Thread.sleep(2000);
String title = driver.getTitle();
System.out.println("Page title: " + title);
```

#### Google Search
```java
driver.get("https://www.google.com");
WebElement searchBox = driver.findElement(By.name("q"));
searchBox.sendKeys("Selenium WebDriver");
searchBox.submit();
Thread.sleep(3000);
```

#### Form Interaction
```java
driver.get("https://example.com/form");
WebElement nameField = driver.findElement(By.id("name"));
nameField.sendKeys("John Doe");
WebElement emailField = driver.findElement(By.id("email"));
emailField.sendKeys("john@example.com");
WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
submitButton.click();
```

#### Advanced Example with Waits
```java
driver.get("https://www.google.com");
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("q")));
searchBox.sendKeys("Selenium automation");
searchBox.submit();
wait.until(ExpectedConditions.presenceOfElementLocated(By.id("search")));
List<WebElement> results = driver.findElements(By.cssSelector("#search .g h3"));
System.out.println("Found " + results.size() + " search results");
```

### JavaScript Examples

#### Get Page Title
```javascript
driver.get("https://www.example.com");
return document.title;
```

#### Modify Page Content
```javascript
driver.get("https://www.example.com");
document.body.style.backgroundColor = "red";
return "Page background changed to red";
```

#### Extract Data
```javascript
driver.get("https://www.example.com");
return {
  title: document.title,
  url: window.location.href,
  links: Array.from(document.querySelectorAll('a')).map(a => a.href)
};
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

#### Upload Script via Form
```bash
curl -X POST http://localhost:8081/api/selenium/upload-script \
  -F "script=driver.get(\"https://www.example.com\"); Thread.sleep(2000); String title = driver.getTitle(); System.out.println(\"Result: \" + title);" \
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

## Error Handling

The API returns appropriate HTTP status codes and error messages:

- `200 OK`: Script executed successfully
- `400 Bad Request`: Invalid request parameters or script compilation errors
- `500 Internal Server Error`: Server-side errors

Error response format:
```json
{
  "success": false,
  "message": "Error description",
  "executionTimeMs": 0
}
```

## Supported Browsers

- **Chrome**: Full support with headless mode
- **Firefox**: Full support with headless mode

## Configuration

The application can be configured through `application.properties`:

```properties
# Server Configuration
server.port=8081

# Selenium Configuration
selenium.default.browser=chrome
selenium.default.headless=true
selenium.default.timeout=30
selenium.screenshot.directory=/tmp/screenshots
```

## Security Considerations

- Scripts are executed in a controlled environment
- Timeout limits prevent infinite execution
- Headless mode is recommended for production
- Screenshots are saved to specified directories only
- Script compilation errors are caught and reported
