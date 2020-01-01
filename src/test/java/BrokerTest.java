
import org.testng.annotations.Test;
import utils.Broker;

import java.util.Random;

import static org.testng.Assert.assertEquals;

public class BrokerTest {
    Broker broker = new Broker();

    @Test
    public void  publicMessageToPrivateChanelAndReadOne (){
        String payload = "{'message':'test" + new Random().nextInt(1000) + "'}";
        broker.publicToChanel(payload, "TestChanel", broker.CLIENT);
        String result = broker.getResultsFromChanel("TestChanel", broker.SERVER);

        assertEquals(result.equals(payload),
                true,
                "Не смогли прочесть приватное сообщение");
    }

    @Test
    public void  publicMessageToPrivateChanelAndReadOneAndOne (){
        String result;

        String payload = "{'message':'test" + new Random().nextInt(1000) + "'}";
        broker.publicToChanel(payload, "TestChanel", broker.SERVER);
        result = broker.getResultsFromChanel("TestChanel", broker.CLIENT);

        payload = "{'message':'test" + new Random().nextInt(1000) + "'}";
        broker.publicToChanel(payload, "TestChanel", broker.CLIENT);
        result = broker.getResultsFromChanel("TestChanel", broker.SERVER);

        assertEquals(result.equals(payload),
                true,
                "Не смогли прочесть приватное сообщение");
    }
}
