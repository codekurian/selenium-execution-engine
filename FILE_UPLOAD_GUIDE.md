# File Upload Script Execution Guide

## Overview
The Selenium Execution Engine now supports uploading and executing Selenium scripts directly from text files. This makes it easy to manage and execute complex automation scripts.

## API Endpoint

### Execute Script File
**POST** `/api/selenium/execute-file`

Upload a script file and execute it immediately.

## Usage Examples

### 1. Basic File Upload

**cURL Example:**
```bash
curl -X POST http://localhost:8081/api/selenium/execute-file \
  -F "file=@scripts/simple-navigation.java" \
  -F "scriptType=java" \
  -F "browser=chrome" \
  -F "headless=true" \
  -F "timeoutSeconds=30"
```

**Python Example:**
```python
import requests

url = "http://localhost:8081/api/selenium/execute-file"

with open("scripts/simple-navigation.java", "rb") as file:
    files = {"file": file}
    data = {
        "scriptType": "java",
        "browser": "chrome",
        "headless": "true",
        "timeoutSeconds": "30"
    }
    
    response = requests.post(url, files=files, data=data)
    result = response.json()
    print(f"Success: {result['success']}")
    print(f"Execution time: {result['executionTimeMs']}ms")
```

### 2. File Upload with Screenshot

```bash
curl -X POST http://localhost:8081/api/selenium/execute-file \
  -F "file=@scripts/google-search.java" \
  -F "scriptType=java" \
  -F "browser=chrome" \
  -F "headless=true" \
  -F "timeoutSeconds=30" \
  -F "screenshotPath=/tmp/screenshots/result.png"
```

## Script File Formats

### Java Script Files
Create `.java` files with Selenium WebDriver code:

**Example: `scripts/simple-navigation.java`**
```java
driver.get("https://www.example.com");
Thread.sleep(2000);
String title = driver.getTitle();
System.out.println("Page title: " + title);
```

**Example: `scripts/google-search.java`**
```java
// Navigate to Google
driver.get("https://www.google.com");

// Find and fill the search box
WebElement searchBox = driver.findElement(By.name("q"));
searchBox.sendKeys("Selenium WebDriver automation");

// Submit the search
searchBox.submit();

// Wait for results
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.presenceOfElementLocated(By.id("search")));

// Get the page title
String title = driver.getTitle();
System.out.println("Search result title: " + title);

// Wait a bit to see the results
Thread.sleep(3000);
```

**Example: `scripts/form-interaction.java`**
```java
// Navigate to a test form page
driver.get("https://httpbin.org/forms/post");

// Find form elements
WebElement nameField = driver.findElement(By.name("custname"));
nameField.sendKeys("John Doe");

WebElement emailField = driver.findElement(By.name("custemail"));
emailField.sendKeys("john@example.com");

// Submit the form
WebElement submitButton = driver.findElement(By.cssSelector("input[type='submit']"));
submitButton.click();

// Wait for response
Thread.sleep(3000);

// Get the result
String pageTitle = driver.getTitle();
System.out.println("Form submission result: " + pageTitle);
```

### JavaScript Files
Create `.js` files with browser JavaScript code:

**Example: `scripts/browser-script.js`**
```javascript
// Navigate to page first (handled by the service)
return document.title;
```

## Request Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `file` | File | Yes | - | The script file to upload |
| `scriptType` | String | No | "java" | Script type: "java" or "javascript" |
| `browser` | String | No | "chrome" | Browser: "chrome" or "firefox" |
| `headless` | Boolean | No | true | Run in headless mode |
| `timeoutSeconds` | Integer | No | 30 | Execution timeout in seconds |
| `screenshotPath` | String | No | - | Path to save screenshot |

## Response Format

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

## Error Handling

The API returns appropriate error messages for:
- File reading errors
- Script compilation errors
- Execution errors
- Timeout errors

## Best Practices

### 1. Script Structure
- Keep scripts focused on a single task
- Include proper error handling
- Use explicit waits instead of Thread.sleep() when possible
- Add meaningful console output for debugging

### 2. File Management
- Use descriptive file names
- Organize scripts in directories by functionality
- Include comments in your scripts
- Version control your script files

### 3. Error Handling
- Always check the response success field
- Handle timeout scenarios
- Use try-catch blocks in complex scripts
- Log important steps for debugging

## Example Workflow

1. **Create Script File:**
   ```bash
   # Create a new script file
   cat > scripts/my-test.java << 'EOF'
   driver.get("https://www.example.com");
   String title = driver.getTitle();
   System.out.println("Page title: " + title);
   EOF
   ```

2. **Execute Script:**
   ```bash
   curl -X POST http://localhost:8081/api/selenium/execute-file \
     -F "file=@scripts/my-test.java" \
     -F "scriptType=java" \
     -F "browser=chrome" \
     -F "headless=true"
   ```

3. **Check Results:**
   ```bash
   # The response will show execution status and timing
   echo "Script execution completed"
   ```

## Integration Examples

### Jenkins Pipeline
```groovy
pipeline {
    agent any
    stages {
        stage('Execute Selenium Script') {
            steps {
                script {
                    def response = httpRequest(
                        url: 'http://localhost:8081/api/selenium/execute-file',
                        httpMode: 'POST',
                        multipart: true,
                        multipartContent: [
                            [file: 'scripts/test-script.java', contentType: 'text/plain'],
                            [name: 'scriptType', value: 'java'],
                            [name: 'browser', value: 'chrome'],
                            [name: 'headless', value: 'true']
                        ]
                    )
                    
                    def result = readJSON text: response.content
                    if (!result.success) {
                        error "Script execution failed: ${result.message}"
                    }
                }
            }
        }
    }
}
```

### GitHub Actions
```yaml
name: Execute Selenium Script
on: [push]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Execute Script
        run: |
          curl -X POST http://localhost:8081/api/selenium/execute-file \
            -F "file=@scripts/test-script.java" \
            -F "scriptType=java" \
            -F "browser=chrome" \
            -F "headless=true"
```

## Troubleshooting

### Common Issues

1. **File Not Found:**
   - Ensure the file path is correct
   - Check file permissions
   - Verify the file exists

2. **Compilation Errors:**
   - Check Java syntax in your script
   - Ensure all required imports are available
   - Verify Selenium WebDriver syntax

3. **Execution Timeouts:**
   - Increase timeoutSeconds parameter
   - Check network connectivity
   - Verify target website availability

4. **Browser Issues:**
   - Ensure Chrome/Firefox is available
   - Check WebDriver compatibility
   - Verify headless mode settings

### Debug Tips

1. **Enable Logging:**
   - Check application logs for detailed error messages
   - Use System.out.println() in scripts for debugging

2. **Test Incrementally:**
   - Start with simple scripts
   - Add complexity gradually
   - Test each component separately

3. **Use Screenshots:**
   - Enable screenshot capture for debugging
   - Check screenshot paths and permissions
