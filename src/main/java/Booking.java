import static java.lang.String.format;
import static java.lang.Thread.sleep;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.html5.Location;


import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Booking {

    public static long newBookingStart(HashMap<String, String> map){
        try {
            sleep(1000);
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://213.232.228.186:3306/cityMobilLife",
                    "citymobiluser", "TaxistZnaetKudaEdit0");
            Statement statement = connection.createStatement();
            if (map.get("sms").equals("")) {
                String pquery = format("INSERT INTO cityMobilLife.carBooking (location, phone, bookingRule, mac) VALUE (?,?,?,?)");
//                String query = format("INSERT INTO cityMobilLife.carBooking (location, phone, bookingRule, mac) VALUE " +
//                        "('"
//                        + map.get("location") + "', '"
//                        + map.get("phone") + "', '"
//                        + map.get("bookingRule") + "', '"
//                        + map.get("mac") + "')");
//                statement.executeUpdate(query);
                PreparedStatement preparedStatement= connection.prepareStatement(pquery, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1,map.get("location"));
                preparedStatement.setString(2,map.get("phone"));
                preparedStatement.setString(3,map.get("bookingRule"));
                preparedStatement.setString(4,map.get("mac"));

//                connection.commit();
//                query = format("SELECT  MAX(id) FROM cityMobilLife.carBooking WHERE mac = '"+ map.get("mac") +"'");
//                statement.executeUpdate(query);
//                ResultSet rs = statement.executeQuery(query);
//                connection.close();
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }

                if (newBookingGetFirstStep(map)) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if(generatedKeys.next()){

                        return generatedKeys.getLong(1);
                    } else {
                        return -1;
                    }
                }
                else {
                    return -1;
                }
            }
            else {
                String query = format("UPDATE cityMobilLife.carBooking SET sms = '" + map.get("sms")+"' WHERE id = '"+ map.get("id") +"'");
                statement.executeUpdate(query);
                connection.close();
                if (newBookingGetSecondStep(map))
                    return 0;
                else
                    return -1;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    private static boolean newBookingGetFirstStep(HashMap<String, String> map){
        try {
            TestClass.Android_LaunchApp();
            String location = map.get("location");
            String[] locationArr = location.split(",");
            MobileDriver driver = TestClass.driver;
            Location currLocation = new Location(Double.parseDouble(locationArr[0]), Double.parseDouble(locationArr[1]), 0.0);
            driver.setLocation(currLocation);


            String phone = map.get("phone");
            MobileElement startButton = (MobileElement) driver.findElementById("ru.citymobil.driver:id/buttonStart");
            startButton.click();
            MobileElement phoneEditBox = (MobileElement) driver.findElementById("ru.citymobil.driver:id/editTextRegisterPhone");
            phoneEditBox.click();
//            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            phoneEditBox.setValue(phone);
            MobileElement sendPhoneButton = (MobileElement) driver.findElementById("ru.citymobil.driver:id/buttonRegisterPhone");
            sendPhoneButton.click();
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    private static boolean newBookingGetSecondStep(HashMap<String, String> map){
        try {
            MobileDriver driver = TestClass.driver;
            MobileElement sendPhoneButton = (MobileElement) driver.findElementById("ru.citymobil.driver:id/buttonRegisterPhone");
            sendPhoneButton.click();
            MobileElement permissionAllowButton = (MobileElement) driver.findElementById("com.android.packageinstaller:id/permission_allow_button");
            permissionAllowButton.wait(5);
            if (permissionAllowButton.isEnabled())
                permissionAllowButton.click();

            String sms = map.get("sms");
            MobileElement sms0 = (MobileElement) driver.findElementById("ru.citymobil.driver:id/editTextRegisterCode0");
            sms0.setValue(sms.substring(0, 1));
            MobileElement sms1 = (MobileElement) driver.findElementById("ru.citymobil.driver:id/editTextRegisterCode1");
            sms1.setValue(sms.substring(1, 2));
            MobileElement sms2 = (MobileElement) driver.findElementById("ru.citymobil.driver:id/editTextRegisterCode2");
            sms2.setValue(sms.substring(2, 3));
            MobileElement sms3 = (MobileElement) driver.findElementById("ru.citymobil.driver:id/editTextRegisterCode3");
            sms3.setValue(sms.substring(3, 4));

            List<MobileElement> partners = driver.findElementsByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.support.v7.widget.RecyclerView");
            for (MobileElement partner : partners) {
                if (partner.getText().contains("Дроботов"))
                    partner.click();
            }

            MobileElement continueButton = (MobileElement) driver.findElementById("ru.citymobil.driver:id/buttonRegisterPhone");
            continueButton.click();

            List<MobileElement>  сarTypes = driver.findElementsByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.support.v7.widget.RecyclerView/android.widget.LinearLayout[1]/android.widget.LinearLayout");
            for (MobileElement сarType : сarTypes) {
                if (сarType.getText().contains("Mercedes"))
                    сarType.click();
            }

            MobileElement continueButton2 = (MobileElement) driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.Button");
            continueButton2.click();

            MobileElement jobType = (MobileElement) driver.findElementById("ru.citymobil.driver:id/skipButton");
            if (jobType.isEnabled()) jobType.click();

            MobileElement permissionAllowButton2 = (MobileElement) driver.findElementById("com.android.packageinstaller:id/permission_allow_button");
            if (permissionAllowButton2.isEnabled()) permissionAllowButton2.click();

            MobileElement geoLocationPermission = (MobileElement) driver.findElementById("ru.citymobil.driver:id/buttonPositive");
            if (geoLocationPermission.isEnabled()) geoLocationPermission.click();

            MobileElement navigateToBooking = (MobileElement) driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.RelativeLayout/android.view.ViewGroup/android.widget.LinearLayout[2]/android.widget.HorizontalScrollView/android.widget.LinearLayout/android.support.v7.app.ActionBar.Tab[2]/android.view.ViewGroup/android.widget.RelativeLayout/android.widget.ImageView");
            navigateToBooking.click();

            int i = 0;
            while (true) {
                i++;
                MobileElement firstAdress = (MobileElement) driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.RelativeLayout/android.view.ViewGroup/android.support.v4.view.ViewPager/android.view.ViewGroup/android.widget.FrameLayout/android.widget.RelativeLayout/android.widget.FrameLayout/android.widget.RelativeLayout/android.widget.LinearLayout/android.view.ViewGroup/android.view.ViewGroup/android.support.v7.widget.RecyclerView/android.widget.FrameLayout[2]/android.widget.LinearLayout/android.widget.LinearLayout[" + i + "]/android.widget.LinearLayout[1]/android.widget.TextView");
                MobileElement secondAdres = (MobileElement) driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.RelativeLayout/android.view.ViewGroup/android.support.v4.view.ViewPager/android.view.ViewGroup/android.widget.FrameLayout/android.widget.RelativeLayout/android.widget.FrameLayout/android.widget.RelativeLayout/android.widget.LinearLayout/android.view.ViewGroup/android.view.ViewGroup/android.support.v7.widget.RecyclerView/android.widget.FrameLayout[2]/android.widget.LinearLayout/android.widget.LinearLayout[" + i + "]/android.widget.LinearLayout[2]/android.widget.TextView");
                MobileElement price = (MobileElement) driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.RelativeLayout/android.view.ViewGroup/android.support.v4.view.ViewPager/android.view.ViewGroup/android.widget.FrameLayout/android.widget.RelativeLayout/android.widget.FrameLayout/android.widget.RelativeLayout/android.widget.LinearLayout/android.view.ViewGroup/android.view.ViewGroup/android.support.v7.widget.RecyclerView/android.widget.FrameLayout[2]/android.widget.LinearLayout/android.widget.LinearLayout[" + i + "]/android.widget.LinearLayout[2]/android.widget.LinearLayout/android.widget.TextView");
                MobileElement distance = (MobileElement) driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.RelativeLayout/android.view.ViewGroup/android.support.v4.view.ViewPager/android.view.ViewGroup/android.widget.FrameLayout/android.widget.RelativeLayout/android.widget.FrameLayout/android.widget.RelativeLayout/android.widget.LinearLayout/android.view.ViewGroup/android.view.ViewGroup/android.support.v7.widget.RecyclerView/android.widget.FrameLayout[2]/android.widget.LinearLayout/android.widget.LinearLayout[" + i + "]/android.widget.LinearLayout[1]/android.widget.LinearLayout/android.widget.TextView");

                if (!firstAdress.isEnabled()) {
                    i = 0;
                    continue;
                }

                if (firstAdress.getText().toLowerCase().contains("аэропорт") ||
                        secondAdres.getText().toLowerCase().contains("аэропорт")) {

                    MobileElement bookingSet = (MobileElement) driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.RelativeLayout/android.view.ViewGroup/android.support.v4.view.ViewPager/android.view.ViewGroup/android.widget.FrameLayout/android.widget.RelativeLayout/android.widget.FrameLayout/android.widget.RelativeLayout/android.widget.LinearLayout/android.view.ViewGroup/android.view.ViewGroup/android.support.v7.widget.RecyclerView/android.widget.FrameLayout[1]/android.widget.LinearLayout/android.widget.LinearLayout[" + i + "]");
                    bookingSet.click();

                    MobileElement acceptBookingButton = (MobileElement) driver.findElementById("ru.citymobil.driver:id/buttonOrderAcceptOrder");
                    if (acceptBookingButton.isEnabled()) acceptBookingButton.click();

                    reportAboutNewBooking(map.get("id"));
                    return true;
                }
            }
            
        }
        catch (Exception e){
            return false;
        }
    }

    private static void reportAboutNewBooking(String id){
        try {
                Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://213.232.228.186:3306/cityMobilLife",
                        "citymobiluser", "TaxistZnaetKudaEdit0");
                Statement statement = connection.createStatement();
                String query = format("UPDATE cityMobilLife.carBooking SET compliteFlag = true WHERE id = '" + id + "'");
                statement.executeQuery(query);
                connection.commit();
                connection.close();
        }
        catch (Exception e){
            e.printStackTrace();

        };
    }
}
