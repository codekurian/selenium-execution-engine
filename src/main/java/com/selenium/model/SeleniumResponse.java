package com.selenium.model;

import lombok.Data;

@Data
public class SeleniumResponse {
    private boolean success;
    private String message;
    private String screenshotPath;
    private long executionTimeMs;
    private String pageTitle;
    private String pageUrl;
}
