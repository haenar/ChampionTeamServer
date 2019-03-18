import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;

import static java.lang.String.format;
import static utils.CommonUtils.jsonParse;
import static utils.CommonUtils.serverResponse;
import static utils.CommonUtils.serverResponseErr;

public class ChampionTeamServer {

    public static boolean serviceIsON = false;
    private static String version = "1.00";
    private static Queue<List<String[]>> queue = new LinkedList<List<String[]>>();



    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(1234), 11);
        server.createContext("/front", new MyHandlerFront());
        server.createContext("/start", new MyHandlerStart());
        server.createContext("/stop", new MyHandlerStop());
        server.createContext("/state", new MyHandlerState());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

    }

    static class MyHandlerFront implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            if (serviceIsON) {
                BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody()));
                HashMap<String, String> map = jsonParse(br.readLine());
                br.close();
                int id = Booking.newBookingStart(map);
                serverResponse("{" + Integer.toString(id)  + "}", t);
            } else
                serverResponseErr("ChampionTeamServer is OFF", t);
        }
    }


    static class MyHandlerStart implements HttpHandler {
        public void handle(HttpExchange t) throws IOException{
            serverResponse("Service is ON", t);
            serviceIsON = true;
        }
    }

    static class MyHandlerStop implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            serverResponse("Service is OFF", t);
            serviceIsON = false;
        }
    }

    static class MyHandlerState implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            serverResponse(format("<html><body>Service working: %s;<br>Version: %s;<body></html>",
                    serviceIsON,
                    version), t);
        }
    }




}


