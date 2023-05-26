import com.ibm.mq.*;
import com.ibm.mq.constants.MQConstants;

public class MQListQueues {

    private static final String QUEUE_MANAGER_NAME = "QUEUE_MANAGER_NAME";
    private static final String CHANNEL_NAME = "CHANNEL_NAME";
    private static final String HOST_NAME = "HOST_NAME";
    private static final int PORT = 1414;

    public static void main(String[] args) {
        MQQueueManager queueManager = null;

        try {
            MQEnvironment.hostname = HOST_NAME;
            MQEnvironment.port = PORT;
            MQEnvironment.channel = CHANNEL_NAME;

            MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES_CLIENT);

            queueManager = new MQQueueManager(QUEUE_MANAGER_NAME);

            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);

            PCFMessage request = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q_NAMES);
            request.addParameter(MQConstants.MQCA_Q_NAME, "*"); // Utilisez un masque ou laissez "*" pour toutes les files d'attente

            PCFMessage[] responses = pcfMessageAgent.send(request);

            for (PCFMessage response : responses) {
                if (response.getParameterCount() > 0) {
                    String queueName = response.getStringParameterValue(MQConstants.MQCA_Q_NAME);
                    System.out.println("Queue Name: " + queueName);
                }
            }
        } catch (MQException mqe) {
            System.err.println("Une erreur MQ est survenue : " + mqe.getMessage());
        } finally {
            try {
                if (queueManager != null) {
                    queueManager.disconnect();
                }
            } catch (MQException mqe) {
                System.err.println("Erreur lors de la fermeture du gestionnaire de files d'attente : " + mqe.getMessage());
            }
        }
    }
}
