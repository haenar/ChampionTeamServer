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


    public static HashMap<String, String> jsonParse(String line) throws IOException {
        line = URLDecoder.decode(line, "UTF-8");
        JSONObject obj = new JSONObject(line);
        String id = obj.getString("id");
        String phone = obj.getString("phone");
        String location = obj.getString("location");
        String currentTime = obj.getString("currentTime");
        String mac = obj.getString("mac");
        String comment = obj.getString("comment");
        String actualFlag = obj.getString("actualFlag");
        String completeFlag = obj.getString("completeFlag");
        String sms = obj.getString("sms");

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        map.put("phone", phone);
        map.put("location", location);
        map.put("currentTime", currentTime);
        map.put("mac", mac);
        map.put("comment", comment);
        map.put("actualFlag", actualFlag);
        map.put("completeFlag", completeFlag);
        map.put("sms", sms);
        return map;
    }




}
