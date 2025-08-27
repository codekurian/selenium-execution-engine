package com.selenium.controller;

import com.selenium.model.SeleniumResponse;
import com.selenium.model.TestScriptRequest;
import com.selenium.service.TestExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/selenium")
@RequiredArgsConstructor
@Slf4j
public class SeleniumController {

    private final TestExecutionService testExecutionService;

    /**
     * Execute a Java test script
     * Takes the script content in the payload, saves it as a file, and executes it
     */
    @PostMapping("/execute-test")
    public ResponseEntity<SeleniumResponse> executeTest(@RequestBody TestScriptRequest request) {
        log.info("Received test execution request for script: {}", request.getScriptName());
        
        try {
            SeleniumResponse response = testExecutionService.executeTestScript(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error executing test script", e);
            
            SeleniumResponse errorResponse = new SeleniumResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error executing test: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Execute a Java test script from uploaded file
     * Takes the actual Java file in the multipart payload
     */
    @PostMapping("/execute-file")
    public ResponseEntity<SeleniumResponse> executeTestFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "scriptName", required = false) String scriptName,
            @RequestParam(value = "browser", defaultValue = "chrome") String browser,
            @RequestParam(value = "headless", defaultValue = "true") boolean headless,
            @RequestParam(value = "timeoutSeconds", defaultValue = "30") int timeoutSeconds,
            @RequestParam(value = "screenshotPath", required = false) String screenshotPath) {
        
        log.info("Received file upload request: {}", file.getOriginalFilename());
        
        try {
            // Read the file content
            String scriptContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            
            // Use filename as script name if not provided
            String finalScriptName = scriptName != null ? scriptName : 
                file.getOriginalFilename().replaceFirst("[.][^.]+$", "");
            
            // If custom script name is provided, update the class name in the content
            if (scriptName != null && !scriptName.isEmpty()) {
                String originalClassName = file.getOriginalFilename().replaceFirst("[.][^.]+$", "");
                scriptContent = scriptContent.replaceAll(
                    "public class " + originalClassName,
                    "public class " + scriptName
                );
            }
            
            // Create request object
            TestScriptRequest request = new TestScriptRequest();
            request.setScriptName(finalScriptName);
            request.setScriptContent(scriptContent);
            request.setBrowser(browser);
            request.setHeadless(headless);
            request.setTimeoutSeconds(timeoutSeconds);
            request.setScreenshotPath(screenshotPath);
            
            SeleniumResponse response = testExecutionService.executeTestScript(request);
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("Error reading uploaded file", e);
            
            SeleniumResponse errorResponse = new SeleniumResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error reading uploaded file: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            log.error("Error executing test script from file", e);
            
            SeleniumResponse errorResponse = new SeleniumResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error executing test: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<SeleniumResponse> health() {
        SeleniumResponse response = new SeleniumResponse();
        response.setSuccess(true);
        response.setMessage("Selenium Execution Engine is running");
        return ResponseEntity.ok(response);
    }
}
