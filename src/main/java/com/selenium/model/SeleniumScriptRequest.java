package com.selenium.model;

import lombok.Data;

@Data
public class SeleniumScriptRequest {
    private String scriptContent; // Raw script content
    private String scriptType = "java"; // java, groovy, javascript, etc.
    private String browser = "chrome"; // Default to chrome
    private boolean headless = true; // Default to headless mode
    private int timeoutSeconds = 30;
    private String screenshotPath; // Optional screenshot path
    private String[] scriptArguments; // Arguments to pass to the script
}
