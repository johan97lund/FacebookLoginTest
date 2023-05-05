/**
 * @author Johan Lund
 * @project FacebookTests
 * @date 2023-04-26
 */
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;

public class FacebookLoginTest {

    static Logger logger = LoggerFactory.getLogger(FacebookLoginTest.class);

    public static void main(String[] args) {
        logger.info("Logback initialized");
        logger.info("Starting the test");

        String email = null;
        String password = null;
        try {
            logger.info("Reading email and password from config.json");
            // Read the email and password from config.json
            Gson gson = new Gson();
            JsonElement config = gson.fromJson(new FileReader("config.json"), JsonElement.class);
            email = config.getAsJsonObject().get("email").getAsString();
            password = config.getAsJsonObject().get("password").getAsString();
            logger.info("Read successfully!");
        } catch (IOException e) {
            // Handle any exceptions that might occur while reading the file
            logger.error("Could not read config.json", e);
            System.exit(1);
        }

        WebDriver driver = null;
        try {
            logger.info("Instantiating and launching ChromeDriver from my local machine.");
            // Set the path to the ChromeDriver executable
            System.setProperty("/users/johanlund/Downloads/Chromedriver_mac64\\chromedriver", "path/to/chromedriver");

            // Creates an instance of ChromeOptions and add the desired option
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-notifications");
            options.addArguments("--remote-debugging-port=9222");

            // Launch ChromeDriver
            driver = new ChromeDriver(options);
            driver.manage().window().maximize();
            logger.info("Launch successful!");
        } catch (WebDriverException e) {
            // Handle any exceptions that might occur while launching the ChromeDriver
            logger.error("Could not launch ChromeDriver", e);
            System.exit(1);
        }


        // Go to the Facebook login page
        logger.info("Attempting to log in.");
        driver.get("https://www.facebook.com/login.php");

        WebElement button = driver.findElement(By.xpath("//button[@data-testid='cookie-policy-manage-dialog-accept-button']"));
        button.click();

        // Enter email address
        WebElement emailField = driver.findElement(By.id("email"));
        emailField.sendKeys(email);

        // Enter password
        WebElement passwordField = driver.findElement(By.id("pass"));
        passwordField.sendKeys(password);

        // Click the "Log In" button
        WebElement loginButton = driver.findElement(By.name("login"));
        loginButton.click();

        // Wait for the login process to complete
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.error("Thread was interrupted", e);
            System.exit(1);
        }

        // Profile menu click
        WebElement profilePic = driver.findElement(By.xpath("//*[@aria-label='Your profile']"));
        profilePic.click();


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.error("Thread was interrupted", e);
            System.exit(1);
        }

        try {
            int i = 0;
            boolean siteFound = false;
            while (i < 4 && !siteFound) {
                driver.navigate().refresh();
                Thread.sleep(1000);
                if (driver.getPageSource().contains("Joe Dogtown")) {
                    siteFound = true;
                    logger.info("The profile was found!");
                }
                i++;

            }
            if (!siteFound) {
                logger.info("The profile was not found!");
            }
        } catch (Exception e) {
            logger.error("Site or profile was not found", e);
            System.exit(1);
        }
        // Close the browser
        logger.info("Test successful!");
        driver.quit();
    }
}

