driver.get("https://www.example.com");
Thread.sleep(2000);
String title = driver.getTitle();
System.out.println("Page title: " + title);
