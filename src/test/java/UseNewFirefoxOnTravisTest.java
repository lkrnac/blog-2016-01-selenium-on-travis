import net.lkrnac.blog.seleniumontravis.Application;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class UseNewFirefoxOnTravisTest {
    private static FirefoxDriver driver;

    @BeforeClass
    public static void setUp() throws IOException {
        String travisCiFlag = System.getenv().get("TRAVIS");
        FirefoxBinary firefoxBinary = "true".equals(travisCiFlag)
                ? getFirefoxBinaryForTravisCi()
                : new FirefoxBinary();

        driver = new FirefoxDriver(firefoxBinary, new FirefoxProfile());
    }

    private static FirefoxBinary getFirefoxBinaryForTravisCi() throws IOException {
        String firefoxPath = getFirefoxPath();
        Logger staticLog = LoggerFactory.getLogger(UseNewFirefoxOnTravisTest.class);
        staticLog.info("Firefox path: " + firefoxPath);

        return new FirefoxBinary(new File(firefoxPath));
    }

    private static String getFirefoxPath() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("which", "firefox");
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try (InputStreamReader isr = new InputStreamReader(process.getInputStream(), "UTF-8");
             BufferedReader br = new BufferedReader(isr)) {
            return br.readLine();
        }
    }

    @Test
    public void contextLoads() {
        driver.get("http://localhost:8080");
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("hello"),
                "Hello, world!")
        );
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
    }
}
