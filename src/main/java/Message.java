import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Message {

    private final MessageData messageData;
    private final String sender;
    private final List<String> receivers;
    private int checksum;
    
    private int commandID;

    public int getCommandID()
    {
		return commandID;
	}
    
    /**
     * constructor
     * @param sender process id of the sending instance of protocol
     * @param receivers list of process IDs of the recieving instances of protocol
     * @param messageData a key:value pair
     */
    public Message(String sender, List<String> receivers, MessageData messageData, int checksum, int commandID)
    {
        this.sender = sender;
        this.receivers = receivers;
        this.messageData = messageData;
        this.commandID = commandID;
        this.checksum = checksum;
    }

    /**
     * constructor
     * @param sender process id of the sending instance of protocol
     * @param receivers list of process IDs of the recieving instances of protocol
     * @param messageData a key:value pair
     */
	public Message(String sender, List<String> receivers, MessageData messageData, int commandID)
	{
        this.sender = sender;
        this.receivers = receivers;
        this.messageData = messageData;
        this.commandID = commandID;
        this.checksum = 0;
    }

    /**
     * constructor
     * @param sender process id of the sending instance of protocol
     * @param receiver process id of the recieving instance of protocol
     * @param messageData a key:value pair
     */
    public Message(String sender, String receiver, MessageData messageData, int checksum, int commandID)
    {
        this.sender = sender;
        this.receivers = new ArrayList<String>(Arrays.asList(receiver));
        this.messageData = messageData;
        this.checksum = checksum;
        this.commandID = commandID;
    }
    
    /**
     * constructor
     * @param sender process id of the sending instance of protocol
     * @param receiver process id of the recieving instance of protocol
     * @param messageData a key:value pair
     */
    public Message(String sender, String receiver, MessageData messageData, int commandID)
    {
        this.sender = sender;
        this.receivers = new ArrayList<String>(Arrays.asList(receiver));
        this.messageData = messageData;
        this.checksum = 0;
        this.commandID = commandID;
    }

    public String getSender()
    {
        return this.sender;
    }

    public List<String> getReceiverIds()
    {
        return this.receivers;
    }

    public MessageData getMessageData()
    {
        return this.messageData;
    }

    public int getChecksum()
    {
    	return this.checksum;
    }

}