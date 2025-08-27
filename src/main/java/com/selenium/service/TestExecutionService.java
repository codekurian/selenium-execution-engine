package com.selenium.service;

import com.selenium.model.SeleniumResponse;
import com.selenium.model.TestScriptRequest;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;

@Service
@Slf4j
public class TestExecutionService {

    @Value("${selenium.scripts.directory:/tmp/test-scripts}")
    private String scriptsDirectory;

    @Value("${selenium.timeout:30}")
    private int defaultTimeout;

    /**
     * Execute a test script by taking it directly from the payload
     * This replaces what Selenium Grid would do
     */
    public SeleniumResponse executeTestScript(TestScriptRequest request) {
        long startTime = System.currentTimeMillis();
        WebDriver driver = null;
        
        try {
            // Validate script content
            if (request.getScriptContent() == null || request.getScriptContent().trim().isEmpty()) {
                throw new IllegalArgumentException("Script content is required");
            }
            
            // Create scripts directory if it doesn't exist
            createScriptsDirectory();
            
            // Save the script as a Java file
            String scriptFileName = saveScriptToFile(request.getScriptName(), request.getScriptContent());
            
            // Compile the Java file
            String className = compileScript(scriptFileName);
            
            // Setup WebDriver (like Selenium Grid would)
            driver = setupWebDriver(request);
            
            // Execute the compiled test class
            Object result = executeTestClass(className, driver);
            
            // Take screenshot if requested
            String screenshotPath = null;
            if (request.getScreenshotPath() != null) {
                screenshotPath = takeScreenshot(driver, request.getScreenshotPath());
            }
            
            // Build response
            SeleniumResponse response = new SeleniumResponse();
            response.setSuccess(true);
            response.setMessage("Test executed successfully");
            response.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            response.setScreenshotPath(screenshotPath);
            response.setPageTitle(driver.getTitle());
            response.setPageUrl(driver.getCurrentUrl());
            
            return response;
            
        } catch (Exception e) {
            log.error("Error executing test script", e);
            
            SeleniumResponse response = new SeleniumResponse();
            response.setSuccess(false);
            response.setMessage("Error executing test: " + e.getMessage());
            response.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            
            return response;
        } finally {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception e) {
                    log.warn("Error closing WebDriver", e);
                }
            }
        }
    }

    /**
     * Create the scripts directory
     */
    private void createScriptsDirectory() throws IOException {
        Path scriptsPath = Paths.get(scriptsDirectory);
        if (!Files.exists(scriptsPath)) {
            Files.createDirectories(scriptsPath);
        }
    }

    /**
     * Save the script content to a Java file
     */
    private String saveScriptToFile(String scriptName, String scriptContent) throws IOException {
        String fileName = scriptName + ".java";
        Path filePath = Paths.get(scriptsDirectory, fileName);
        
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write(scriptContent);
        }
        
        log.info("Saved script to file: {}", filePath);
        return fileName;
    }

    /**
     * Compile the Java script file
     */
    private String compileScript(String fileName) throws Exception {
        Path filePath = Paths.get(scriptsDirectory, fileName);
        String className = fileName.substring(0, fileName.lastIndexOf('.'));
        
        // Get the Java compiler
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new RuntimeException("Java compiler not available. Make sure you're running with JDK, not JRE.");
        }
        
        // Compile the file
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(
            Arrays.asList(filePath.toString())
        );
        
        JavaCompiler.CompilationTask task = compiler.getTask(
            null, fileManager, diagnostics, null, null, compilationUnits
        );
        
        boolean success = task.call();
        fileManager.close();
        
        if (!success) {
            StringBuilder errorMessage = new StringBuilder("Compilation failed:\n");
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                errorMessage.append(diagnostic.getMessage(null)).append("\n");
            }
            throw new RuntimeException(errorMessage.toString());
        }
        
        log.info("Successfully compiled script: {}", className);
        return className;
    }

    /**
     * Setup WebDriver based on browser preference (like Selenium Grid would)
     */
    private WebDriver setupWebDriver(TestScriptRequest request) {
        String browser = request.getBrowser() != null ? request.getBrowser().toLowerCase() : "chrome";
        boolean headless = request.isHeadless();
        int timeout = request.getTimeoutSeconds() > 0 ? request.getTimeoutSeconds() : defaultTimeout;
        
        WebDriver driver;
        
        switch (browser) {
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("--headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case "chrome":
            default:
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless");
                }
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-gpu");
                driver = new ChromeDriver(chromeOptions);
                break;
        }
        
        // Set timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(timeout));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(timeout));
        
        return driver;
    }

    /**
     * Execute the compiled test class
     */
    private Object executeTestClass(String className, WebDriver driver) throws Exception {
        // Load the compiled class
        URLClassLoader classLoader = new URLClassLoader(new URL[]{new File(scriptsDirectory).toURI().toURL()});
        Class<?> testClass = classLoader.loadClass(className);
        
        // Find the execute method
        Method executeMethod = testClass.getMethod("execute", WebDriver.class);
        
        // Execute the test
        Object result = executeMethod.invoke(null, driver);
        
        classLoader.close();
        return result;
    }

    /**
     * Take screenshot
     */
    private String takeScreenshot(WebDriver driver, String screenshotPath) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
            
            // Save screenshot logic here
            // For now, just return the path
            return screenshotPath;
        } catch (Exception e) {
            log.warn("Failed to take screenshot", e);
            return null;
        }
    }
}
