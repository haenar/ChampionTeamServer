import org.joda.time.DateTime;
import org.testng.annotations.Test;
import utils.Broker;

import java.util.Random;

import static java.lang.String.format;
import static org.testng.Assert.assertEquals;

public class CommonFlowTest {
    private Broker broker = new Broker();
    private String json = "{'phone' : '%s'," +
                            "'location' : '10,10'," +
                            "'currentTime' : '" + DateTime.now() + "'," +
                            "'comment' : 'Test Comment'," +
                            "'actualFlag' : true," +
                            "'completeFlag' : false," +
                            "'sms' : ''}";

    @Test
    public void startFlow(){
        String phone = "915-555-55-56";
        json = format(json, phone);
        broker.publicToChanel(json, "TestChanel", broker.CLIENT);

        String jsonReq = broker.getResultsFromChanel("TestChanel", broker.SERVER);
        assertEquals(json.equals(jsonReq),
                true,
                "Не смогли прочесть приватное сообщение в общем чате");

        String chanelName = ChampionTeamServer.startProcess(jsonReq);
        String result = broker.getResultsFromChanel(chanelName, broker.CLIENT);
        assertEquals(result.equals("{'id':'-1'}"),
                true,
                "Не смогли прочесть приватное сообщение после обработки первого сообщения");
    }
}
