import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SampleTest {
    public static Object execute(WebDriver driver) {
        try {
            // Navigate to a test page
            driver.get("https://www.example.com");
            
            // Get the page title
            String title = driver.getTitle();
            System.out.println("Page title: " + title);
            
            // Get the page URL
            String url = driver.getCurrentUrl();
            System.out.println("Page URL: " + url);
            
            // Find the main heading
            WebElement heading = driver.findElement(By.tagName("h1"));
            String headingText = heading.getText();
            System.out.println("Main heading: " + headingText);
            
            // Return success result
            return "Test completed successfully! Title: " + title + ", Heading: " + headingText;
            
        } catch (Exception e) {
            throw new RuntimeException("Test failed: " + e.getMessage(), e);
        }
    }
}
