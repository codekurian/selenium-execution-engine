package com.selenium.service;

import com.selenium.model.SeleniumRequest;
import com.selenium.model.SeleniumResponse;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

@Service
public class SeleniumService {

    public SeleniumResponse executeScript(SeleniumRequest request) {
        WebDriver driver = null;
        Instant startTime = Instant.now();
        SeleniumResponse response = new SeleniumResponse();

        try {
            // Setup WebDriver based on browser type
            driver = setupWebDriver(request);
            
            // Set timeout
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(request.getTimeoutSeconds()));
            
            // Navigate to URL
            driver.get(request.getUrl());
            
            // Execute actions if provided
            if (request.getActions() != null && request.getActions().length > 0) {
                executeActions(driver, request.getActions());
            }
            
            // Take screenshot if path is provided
            if (request.getScreenshotPath() != null && !request.getScreenshotPath().isEmpty()) {
                takeScreenshot(driver, request.getScreenshotPath());
                response.setScreenshotPath(request.getScreenshotPath());
            }
            
            // Set response data
            response.setSuccess(true);
            response.setMessage("Selenium script executed successfully");
            response.setPageTitle(driver.getTitle());
            response.setPageUrl(driver.getCurrentUrl());
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error during Selenium execution: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        
        // Calculate execution time
        long executionTime = Duration.between(startTime, Instant.now()).toMillis();
        response.setExecutionTimeMs(executionTime);
        
        return response;
    }

    private WebDriver setupWebDriver(SeleniumRequest request) {
        switch (request.getBrowser().toLowerCase()) {
            case "firefox":
                return setupFirefoxDriver(request.isHeadless());
            case "chrome":
            default:
                return setupChromeDriver(request.isHeadless());
        }
    }

    private WebDriver setupChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        
        if (headless) {
            options.addArguments("--headless");
        }
        
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        
        return new ChromeDriver(options);
    }

    private WebDriver setupFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        
        if (headless) {
            options.addArguments("--headless");
        }
        
        return new FirefoxDriver(options);
    }

    private void executeActions(WebDriver driver, String[] actions) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        for (String action : actions) {
            try {
                // Simple action parsing - can be extended for more complex scenarios
                if (action.startsWith("click:")) {
                    String selector = action.substring(6);
                    WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)));
                    element.click();
                } else if (action.startsWith("type:")) {
                    String[] parts = action.substring(5).split(":", 2);
                    if (parts.length == 2) {
                        String selector = parts[0];
                        String text = parts[1];
                        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)));
                        element.clear();
                        element.sendKeys(text);
                    }
                } else if (action.startsWith("wait:")) {
                    int seconds = Integer.parseInt(action.substring(5));
                    Thread.sleep(seconds * 1000L);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to execute action: " + action, e);
            }
        }
    }

    private void takeScreenshot(WebDriver driver, String screenshotPath) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(screenshotPath);
            screenshot.renameTo(destFile);
        } catch (Exception e) {
            throw new RuntimeException("Failed to take screenshot", e);
        }
    }
}
