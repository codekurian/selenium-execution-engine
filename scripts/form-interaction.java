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
