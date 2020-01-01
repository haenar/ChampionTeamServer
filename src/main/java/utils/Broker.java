package utils;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Broker{
    int qos = 1;
    public final Boolean SERVER = true;
    public final Boolean CLIENT = false;
    private String topicFromClient = "topicFromClient";
    private String broker = "tcp://194.67.92.65:1883";
    final String clientId = "Server_" + UUID.randomUUID().toString();
    MemoryPersistence persistence = new MemoryPersistence();


    public String getResultsFromChanel(String chanelID, Boolean serverType) {
        String clientType = clientId;
        String topic = topicFromClient + chanelID;
        if (!serverType){
            clientType = UUID.randomUUID().toString();
        }

        String[] result = new String[1];
        try {
            MqttClient client = new MqttClient(broker, clientType, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
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
            };
            client.disconnect();
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
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
            MqttClient client = new MqttClient(broker, clientType, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            client.connect(connOpts);
            MqttMessage message = new MqttMessage(payLoad.getBytes());
            message.setQos(qos);
            message.setRetained(true);
            client.publish(topic, message);
            client.disconnect();
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }
}
