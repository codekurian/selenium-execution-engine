package com.selenium.service;

import com.selenium.model.SeleniumScriptRequest;
import com.selenium.model.SeleniumResponse;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@Service
public class ScriptExecutionService {

    public SeleniumResponse executeScript(SeleniumScriptRequest request) {
        Instant startTime = Instant.now();
        SeleniumResponse response = new SeleniumResponse();

        try {
            // Setup WebDriver
            WebDriver driver = setupWebDriver(request);
            
            // Execute the script based on type
            Object result = executeScriptByType(request, driver);
            
            // Take screenshot if requested
            if (request.getScreenshotPath() != null && !request.getScreenshotPath().isEmpty()) {
                takeScreenshot(driver, request.getScreenshotPath());
                response.setScreenshotPath(request.getScreenshotPath());
            }
            
            // Set response data
            response.setSuccess(true);
            response.setMessage("Script executed successfully");
            response.setPageTitle(driver.getTitle());
            response.setPageUrl(driver.getCurrentUrl());
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error executing script: " + e.getMessage());
        }
        
        // Calculate execution time
        long executionTime = Duration.between(startTime, Instant.now()).toMillis();
        response.setExecutionTimeMs(executionTime);
        
        return response;
    }

    private WebDriver setupWebDriver(SeleniumScriptRequest request) {
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

    private Object executeScriptByType(SeleniumScriptRequest request, WebDriver driver) throws Exception {
        switch (request.getScriptType().toLowerCase()) {
            case "java":
                return executeJavaScript(request, driver);
            case "groovy":
                return executeGroovyScript(request, driver);
            case "javascript":
                return executeBrowserJavaScript(request, driver);
            default:
                throw new IllegalArgumentException("Unsupported script type: " + request.getScriptType());
        }
    }

    private Object executeJavaScript(SeleniumScriptRequest request, WebDriver driver) throws Exception {
        // Create a temporary Java file
        String className = "SeleniumScript_" + System.currentTimeMillis();
        String javaCode = generateJavaWrapper(request.getScriptContent(), className);
        
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "selenium-scripts");
        tempDir.mkdirs();
        
        File javaFile = new File(tempDir, className + ".java");
        try (FileWriter writer = new FileWriter(javaFile)) {
            writer.write(javaCode);
        }
        
        // Compile the Java code
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(
            Arrays.asList(javaFile.getAbsolutePath())
        );
        
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
        
        if (!task.call()) {
            StringBuilder errors = new StringBuilder();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                errors.append(diagnostic.getMessage(null)).append("\n");
            }
            throw new RuntimeException("Compilation failed:\n" + errors.toString());
        }
        
        // Load and execute the compiled class
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{tempDir.toURI().toURL()});
        Class<?> scriptClass = classLoader.loadClass(className);
        
        // Create instance and execute
        Object instance = scriptClass.getDeclaredConstructor().newInstance();
        Method executeMethod = scriptClass.getMethod("execute", WebDriver.class);
        
        return executeMethod.invoke(instance, driver);
    }

    private String generateJavaWrapper(String scriptContent, String className) {
        return "import org.openqa.selenium.*;\n" +
               "import org.openqa.selenium.support.ui.WebDriverWait;\n" +
               "import org.openqa.selenium.support.ui.ExpectedConditions;\n" +
               "import java.time.Duration;\n" +
               "import java.util.concurrent.TimeUnit;\n\n" +
               "public class " + className + " {\n" +
               "    public Object execute(WebDriver driver) {\n" +
               "        try {\n" +
               "            " + scriptContent + "\n" +
               "            return \"Script executed successfully\";\n" +
               "        } catch (Exception e) {\n" +
               "            throw new RuntimeException(\"Script execution failed: \" + e.getMessage(), e);\n" +
               "        }\n" +
               "    }\n" +
               "}";
    }

    private Object executeGroovyScript(SeleniumScriptRequest request, WebDriver driver) throws Exception {
        // For Groovy execution, we would need to add Groovy dependency
        // This is a placeholder implementation
        throw new UnsupportedOperationException("Groovy script execution not yet implemented");
    }

    private Object executeBrowserJavaScript(SeleniumScriptRequest request, WebDriver driver) {
        // Execute JavaScript directly in the browser
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // First navigate to a page if the script doesn't contain navigation
        if (!request.getScriptContent().contains("driver.get")) {
            driver.get("https://www.example.com");
        }
        return js.executeScript(request.getScriptContent());
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
