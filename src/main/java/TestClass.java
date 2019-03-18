import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;

public class TestClass {

    public static AppiumDriver<WebElement> driver;
    public static DesiredCapabilities cap;

    public static void Android_LaunchApp() throws Exception{
        cap = new DesiredCapabilities();
        cap.setCapability("platformName", "Android");
        cap.setCapability("deviceName", "5.4_FWVGA_API_28");
        cap.setCapability("appPackage", "ru.citymobil.driver");
        cap.setCapability("appActivity", "ru.citymobil.driver.links.AppLinksActivity");
        driver = new AndroidDriver<WebElement>(new URL("http://127.0.0.1:4723/wd/hub"), cap);
    }
}
