package com.selenium.controller;

import com.selenium.service.SeleniumService;
import com.selenium.service.ScriptExecutionService;
import com.selenium.model.SeleniumRequest;
import com.selenium.model.SeleniumScriptRequest;
import com.selenium.model.SeleniumResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/selenium")
@CrossOrigin(origins = "*")
public class SeleniumController {

    @Autowired
    private SeleniumService seleniumService;
    
    @Autowired
    private ScriptExecutionService scriptExecutionService;

    @PostMapping("/execute")
    public ResponseEntity<SeleniumResponse> executeSeleniumScript(@RequestBody SeleniumRequest request) {
        try {
            SeleniumResponse response = seleniumService.executeScript(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SeleniumResponse errorResponse = new SeleniumResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error executing Selenium script: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Selenium Execution Engine is running!");
    }
    
    @PostMapping("/execute-script")
    public ResponseEntity<SeleniumResponse> executeRawScript(@RequestBody SeleniumScriptRequest request) {
        try {
            SeleniumResponse response = scriptExecutionService.executeScript(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SeleniumResponse errorResponse = new SeleniumResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error executing raw script: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/upload-script")
    public ResponseEntity<SeleniumResponse> uploadAndExecuteScript(
            @RequestParam("script") String scriptContent,
            @RequestParam(value = "scriptType", defaultValue = "java") String scriptType,
            @RequestParam(value = "browser", defaultValue = "chrome") String browser,
            @RequestParam(value = "headless", defaultValue = "true") boolean headless,
            @RequestParam(value = "timeoutSeconds", defaultValue = "30") int timeoutSeconds,
            @RequestParam(value = "screenshotPath", required = false) String screenshotPath) {
        
        try {
            SeleniumScriptRequest request = new SeleniumScriptRequest();
            request.setScriptContent(scriptContent);
            request.setScriptType(scriptType);
            request.setBrowser(browser);
            request.setHeadless(headless);
            request.setTimeoutSeconds(timeoutSeconds);
            request.setScreenshotPath(screenshotPath);
            
            SeleniumResponse response = scriptExecutionService.executeScript(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SeleniumResponse errorResponse = new SeleniumResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error uploading and executing script: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/execute-file")
    public ResponseEntity<SeleniumResponse> executeScriptFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "scriptType", defaultValue = "java") String scriptType,
            @RequestParam(value = "browser", defaultValue = "chrome") String browser,
            @RequestParam(value = "headless", defaultValue = "true") boolean headless,
            @RequestParam(value = "timeoutSeconds", defaultValue = "30") int timeoutSeconds,
            @RequestParam(value = "screenshotPath", required = false) String screenshotPath) {
        
        try {
            // Read the file content
            String scriptContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            
            SeleniumScriptRequest request = new SeleniumScriptRequest();
            request.setScriptContent(scriptContent);
            request.setScriptType(scriptType);
            request.setBrowser(browser);
            request.setHeadless(headless);
            request.setTimeoutSeconds(timeoutSeconds);
            request.setScreenshotPath(screenshotPath);
            
            SeleniumResponse response = scriptExecutionService.executeScript(request);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            SeleniumResponse errorResponse = new SeleniumResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error reading script file: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            SeleniumResponse errorResponse = new SeleniumResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error executing script file: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
