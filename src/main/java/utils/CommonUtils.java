package utils;

import com.sun.net.httpserver.HttpExchange;
import json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.HashMap;

public class CommonUtils {

    public static void serverResponse(String s, HttpExchange t) throws IOException {
        t.sendResponseHeaders(200, s.length());
        OutputStream os = t.getResponseBody();
        os.write(s.getBytes());
        os.close();
    }

    public static void serverResponseErr(String s, HttpExchange t) throws IOException {
        t.sendResponseHeaders(502, s.length());
        OutputStream os = t.getResponseBody();
        os.write(s.getBytes());
        os.close();
    }


    public static HashMap<String, String> jsonParse(String line) {
        try {
            line = URLDecoder.decode(line, "UTF-8");
        }
        catch (Exception e) {};

        HashMap<String, String> map = new HashMap<String, String>();
        JSONObject obj = new JSONObject(line);
//        String id = obj.getString("id");
        if (obj.getString("sms").equals("")){
            String phone = obj.getString("phone");
            String location = obj.getString("location");
            String currentTime = obj.getString("currentTime");
            //String mac = obj.getString("mac");
            String comment = obj.getString("comment");
            Boolean actualFlag = obj.getBoolean("actualFlag");
            Boolean completeFlag = obj.getBoolean("completeFlag");
            String sms = obj.getString("sms");
            //        map.put("id", id);
            map.put("phone", phone);
            map.put("location", location);
            map.put("currentTime", currentTime);
            //map.put("mac", mac);
            map.put("comment", comment);
            map.put("actualFlag", actualFlag.toString());
            map.put("completeFlag", completeFlag.toString());
            map.put("sms", sms);
        } else {
            String id = obj.getString("id");
            String sms = obj.getString("sms");

            map.put("id", id );
            map.put("sms", sms);
        }
        return map;
    }




}
