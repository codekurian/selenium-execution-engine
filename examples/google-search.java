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
