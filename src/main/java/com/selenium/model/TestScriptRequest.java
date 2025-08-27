package com.selenium.model;

import lombok.Data;

@Data
public class TestScriptRequest {
    private String scriptName;
    private String scriptContent;        // Direct script content in payload
    private String browser;
    private boolean headless;
    private int timeoutSeconds;
    private String screenshotPath;
}
