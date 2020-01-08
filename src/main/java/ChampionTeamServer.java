import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import utils.Broker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;

import static java.lang.String.format;
import static java.lang.Thread.sleep;
import static utils.CommonUtils.jsonParse;
import static utils.CommonUtils.serverResponse;

public class ChampionTeamServer {

    public static boolean serviceIsON = false;
    private static String version = "2.01";
    private static Broker broker = new Broker();
    private static ThreadGroup tg1 = new ThreadGroup("Parent ThreadGroup");
    private static Booking booking = new Booking();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(1234), 5);
        server.createContext("/start", new MyHandlerStart());
        server.createContext("/stop", new MyHandlerStop());
        server.createContext("/state", new MyHandlerState());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        while(true) {
            sleep(500);
            if (!serviceIsON) continue;
            waitNewRequest();
        }
    }

    public static void waitNewRequest(){
        String json = broker.getResultsFromChanel("", broker.SERVER);
        HashMap<String, String> map = jsonParse(json);
        String chanelName = map.get("phone");
        if (!json.equals("")) {
            Thread[] threads = new Thread[tg1.activeCount()];
            tg1.enumerate(threads, true);
            for (Thread t : threads){
                if (t.getName().equals("tread_" + chanelName))
                    return;
            }

            Thread tread = new Thread(tg1, "tread_" + chanelName) {
                public void run() {
                    if (startProcess(map) == -1)
                        return;
                    bookingStartAfterReceiveSMS(chanelName);
                    Thread.currentThread().interrupt();
                }
            };
            tread.start();
        }
    }


    public static long startProcess(HashMap<String, String> map) {
        long id = booking.newBookingStart(map);
        broker.publicToChanel("{'id':'" + id + "'}", map.get("phone"), broker.SERVER);
        return id;
    }

    public static void bookingStartAfterReceiveSMS(String chanelName){
        String json = "";
        while (json.equals("")) {
            json = broker.getResultsFromChanel(chanelName, broker.SERVER);
        }
        HashMap<String, String> map = jsonParse(json);
        long id = booking.newBookingContinue(map);
        broker.publicToChanel("{'id':'" + id + "'}", chanelName, broker.SERVER);
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


