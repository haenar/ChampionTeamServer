package utils;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import javax.net.ssl.*;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Broker{
    private int qos = 1;
    public final Boolean SERVER = true;
    public final Boolean CLIENT = false;
    public String topicFromClient = "topicFromClient";
    private String broker = "ssl://194.67.92.65:51883";
    private final String clientId = "Server_" + UUID.randomUUID().toString();
    private MemoryPersistence persistence = new MemoryPersistence();

    private String caCrtFile = "src/main/resources/ca.crt";
    private String clientCrtFilePath = "src/main/resources/client.crt";
    private String clientKeyFilePath = "src/main/resources/client.key";
    private String password = "IllusionistAEzenha1m";

    private CommonUtils utils = new CommonUtils();

    public String getResultsFromChanel(String chanelID, Boolean serverType) {
        String clientType = clientId;
        String topic = topicFromClient + chanelID;
        if (!serverType){
            clientType = UUID.randomUUID().toString();
        }

        String[] result = new String[1];
        try {
            SSLSocketFactory ssl = utils.getSocketFactory(caCrtFile, clientCrtFilePath, clientKeyFilePath, password);
            MqttClient client = new MqttClient(broker, clientType, persistence);

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setSocketFactory(ssl);
            client.connect(connOpts);
            CountDownLatch receivedSignal = new CountDownLatch(100);
            client.subscribe(topic, (top, msg) -> {
                byte[] payload = msg.getPayload();
                result[0] = new String(payload);
                receivedSignal.countDown();
            });
            try {
                receivedSignal.await(100, TimeUnit.MILLISECONDS);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
            client.disconnect();
        } catch (MqttException me) {
            System.out.println("reasonGet " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }

        return result[0];
    }


    public void publicToChanel(String payLoad, String chanelID, Boolean serverType) {
        String clientType = clientId;
        String topic = topicFromClient + chanelID;
        if (!serverType){
            clientType = UUID.randomUUID().toString();
        }

        try {
            SSLSocketFactory ssl = utils.getSocketFactory(caCrtFile, clientCrtFilePath, clientKeyFilePath, password);
            MqttClient client = new MqttClient(broker, clientType, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setSocketFactory(ssl);
            client.connect(connOpts);
            MqttMessage message = new MqttMessage(payLoad.getBytes());
            message.setQos(qos);
            message.setRetained(true);
            client.publish(topic, message);
            client.disconnect();
        } catch (MqttException me) {
            System.out.println("reasonPub " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }
}
