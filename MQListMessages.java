import com.ibm.mq.*;
import com.ibm.mq.constants.MQConstants;

public class MQListMessages {

    private static final String QUEUE_MANAGER_NAME = "QUEUE_MANAGER_NAME";
    private static final String QUEUE_NAME = "QUEUE_NAME";
    private static final String CHANNEL_NAME = "CHANNEL_NAME";
    private static final String HOST_NAME = "HOST_NAME";
    private static final int PORT = 1414;

    public static void main(String[] args) {
        MQQueueManager queueManager = null;
        MQQueue queue = null;

        try {
            MQEnvironment.hostname = HOST_NAME;
            MQEnvironment.port = PORT;
            MQEnvironment.channel = CHANNEL_NAME;

            MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES_CLIENT);

            queueManager = new MQQueueManager(QUEUE_MANAGER_NAME);
            int openOptions = MQConstants.MQOO_INQUIRE | MQConstants.MQOO_FAIL_IF_QUIESCING;
            queue = queueManager.accessQueue(QUEUE_NAME, openOptions);

            MQMessage[] messages = new MQMessage[10]; // Limite de 10 messages pour cet exemple
            MQGetMessageOptions getMessageOptions = new MQGetMessageOptions();
            getMessageOptions.options = MQConstants.MQGMO_BROWSE_FIRST;

            while (true) {
                queue.get(messages, getMessageOptions);
                if (messages[0] == null) {
                    break; // Aucun autre message trouvé
                }
                System.out.println("Message ID: " + messages[0].messageId);
                getMessageOptions.options = MQConstants.MQGMO_BROWSE_NEXT;
            }
        } catch (MQException mqe) {
            System.err.println("Une erreur MQ est survenue : " + mqe.getMessage());
        } finally {
            try {
                if (queue != null) {
                    queue.close();
                }
                if (queueManager != null) {
                    queueManager.disconnect();
                }
            } catch (MQException mqe) {
                System.err.println("Erreur lors de la fermeture de la file d'attente : " + mqe.getMessage());
            }
        }
    }
}
