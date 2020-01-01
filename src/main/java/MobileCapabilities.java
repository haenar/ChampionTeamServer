import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;

public class MobileCapabilities {

    public static AppiumDriver<WebElement> driver;
    public static DesiredCapabilities cap;

    public static void Android_LaunchApp() throws Exception{
        cap = new DesiredCapabilities();
//        cap.setCapability("udid", "emulator-5556");
        cap.setCapability("platformName", "Android");
        cap.setCapability("deviceName", "EM1");
        cap.setCapability("appPackage", "ru.citymobil.driver");
        cap.setCapability("appActivity", "ru.citymobil.driver.links.AppLinksActivity");
//        cap.setCapability("autoWebviewTimeout", 10000);
        cap.setCapability("automationName","UiAutomator2");
        driver = new AndroidDriver<WebElement>(new URL("http://127.0.0.1:4723/wd/hub"), cap);
    }
}
