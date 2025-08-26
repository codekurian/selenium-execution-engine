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
