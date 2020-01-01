import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import utils.Broker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.Executors;

import static java.lang.String.format;
import static java.lang.Thread.sleep;
import static utils.CommonUtils.jsonParse;
import static utils.CommonUtils.serverResponse;

public class ChampionTeamServer {

    public static boolean serviceIsON = false;
    private static String version = "2.01";
    private static Broker broker = new Broker();


    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(1234), 5);
        server.createContext("/start", new MyHandlerStart());
        server.createContext("/stop", new MyHandlerStop());
        server.createContext("/state", new MyHandlerState());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        ThreadGroup tg1 = new ThreadGroup("Parent ThreadGroup");
        while(true) {
            if (!serviceIsON) continue;
            String json = broker.getResultsFromChanel("", broker.SERVER);
            if (!json.equals("")) {
                Thread tread = new Thread(tg1, "") {
                    public void run() {
                        startProcess(json);
                    }
                };
                tread.start();
            }
            sleep(100);
        }
    }


    public static void startProcess(String json){
        HashMap<String, String> map = jsonParse(json);
        String chanelName = map.get("phone");
        Booking booking = new Booking();
        json = "";

        long id = booking.newBookingStart(map);
        broker.publicToChanel("{'id':'" + id + "'}", map.get("phone"), broker.SERVER);

        while (json.equals("")) {
            json = broker.getResultsFromChanel(chanelName, broker.SERVER);
        }
        map = jsonParse(json);
        id = booking.newBookingStart(map);
        broker.publicToChanel("{'id':'" + id + "'}", map.get("phone"), broker.SERVER);
        Thread.currentThread().interrupt();
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


