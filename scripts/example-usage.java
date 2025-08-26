// Comprehensive Selenium Example Script
// This script demonstrates various Selenium WebDriver operations

// Navigate to a test page
driver.get("https://httpbin.org/html");

// Wait for page to load
Thread.sleep(2000);

// Get page information
String title = driver.getTitle();
String url = driver.getCurrentUrl();
System.out.println("Page Title: " + title);
System.out.println("Page URL: " + url);

// Find and interact with elements
WebElement heading = driver.findElement(By.tagName("h1"));
String headingText = heading.getText();
System.out.println("Heading: " + headingText);

// Take a screenshot (optional - can be enabled via API parameter)
// File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

// Navigate to another page
driver.get("https://httpbin.org/forms/post");

// Fill out a form
WebElement nameField = driver.findElement(By.name("custname"));
nameField.sendKeys("Test User");

WebElement emailField = driver.findElement(By.name("custemail"));
emailField.sendKeys("test@example.com");

// Submit the form
WebElement submitButton = driver.findElement(By.cssSelector("input[type='submit']"));
submitButton.click();

// Wait for response
Thread.sleep(3000);

// Get final page information
String finalTitle = driver.getTitle();
String finalUrl = driver.getCurrentUrl();
System.out.println("Final Page Title: " + finalTitle);
System.out.println("Final Page URL: " + finalUrl);

System.out.println("Script execution completed successfully!");
