package utils;

import com.sun.net.httpserver.HttpExchange;
import json.JSONObject;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;

import javax.net.ssl.*;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.X509Certificate;
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
            line = URLDecoder.decode(line, StandardCharsets.UTF_8);
        }
        catch (Exception e) {}

        HashMap<String, String> map = new HashMap<>();
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

    public SSLSocketFactory getSocketFactory (final String caCrtFile, final String crtFile, final String keyFile,
                                              final String password)
    {
        try {
            Security.addProvider(new BouncyCastleProvider());

            // load CA certificate
            Path caCrtFilePath = new File(caCrtFile).getAbsoluteFile().toPath();
            PEMReader reader = new PEMReader(new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(caCrtFilePath))));
            X509Certificate caCert = (X509Certificate) reader.readObject();
            reader.close();

            // load client certificate
            Path crtFilePath = new File(crtFile).getAbsoluteFile().toPath();
            reader = new PEMReader(new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(crtFilePath))));
            X509Certificate cert = (X509Certificate) reader.readObject();
            reader.close();

            // load client private key
            Path keyFilePath = new File(keyFile).getAbsoluteFile().toPath();
            reader = new PEMReader(
                    new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(keyFilePath))),
                    new PasswordFinder() {
                        @Override
                        public char[] getPassword() {
                            return password.toCharArray();
                        }
                    }
            );
            KeyPair key = (KeyPair) reader.readObject();
            reader.close();

            // CA certificate is used to authenticate server
            KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
            caKs.load(null, null);
            caKs.setCertificateEntry("ca-certificate", caCert);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(caKs);

            // client key and certificates are sent to server so it can authenticate us
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null);
            ks.setCertificateEntry("certificate", cert);
            ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(), new java.security.cert.Certificate[]{cert});
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password.toCharArray());

            // finally, create SSL socket factory
            SSLContext context = SSLContext.getInstance("TLSv1.1");
            context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            return context.getSocketFactory();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        return null;
    }


}
