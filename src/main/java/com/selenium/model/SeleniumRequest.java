package com.selenium.model;

import lombok.Data;

@Data
public class SeleniumRequest {
    private String url;
    private String browser = "chrome"; // Default to chrome
    private boolean headless = true; // Default to headless mode
    private int timeoutSeconds = 30;
    private String[] actions; // Array of actions to perform
    private String screenshotPath; // Optional screenshot path
}
