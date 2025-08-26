driver.get("https://www.google.com");
WebElement searchBox = driver.findElement(By.name("q"));
searchBox.sendKeys("Selenium automation");
searchBox.submit();
Thread.sleep(3000);
