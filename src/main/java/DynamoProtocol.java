import java.util.*;

/**
 * Dynamo protocol implementation
 * 
 * Message commandID interpretation:
 * 0 - LOCAL_GET_REQUEST
 * 1 - GET_REPLY
 * 2 - LOCAL_PUT_REQUEST
 * 3 - PUT_REQUEST
 * 4 - LOCAL_REMOVE_REQUEST
 * 5 - CONSISTENCY_CHECK_REQUEST
 * 6 - GET_REQUEST
 */

public class DynamoProtocol implements IProtocol {

	private Map<String, Integer> checksums;
	private String pid;
    private List<Message> consistencyReplies;
    private boolean checkingConsistency;
    private int consistencyWait;
    private static final int consistencyTimeout = 3;
	
    private List<String> otherNodes;
    
    @Override
    public void registerOtherNodes(List<String> otherIds) {
        for (String id: otherIds) {
            if (!this.otherNodes.contains(id))
                //in the beginning all nodes are empty (for now)
                this.otherNodes.add(id);
        }
    }
    
    @Override
    public String getPID()
    {
    	return pid;
    }
    
	public DynamoProtocol(String pid)
	{
        checksums = new HashMap<>();
        consistencyReplies = new ArrayList<>();
        checkingConsistency = false;
        consistencyWait = 0;
        otherNodes = new ArrayList<String>();
        this.pid = pid;
	}
	
    private int hash(String id) {
        int hash = 7;
        for (int i = 0; i < id.length(); i++) {
            hash = hash * 31 + id.charAt(i);
        }
        return hash;
    }
	
	@Override
	public String[] getUserCommandList()
	{
		String[] result = {
				 "LOCAL_GET_REQUEST",
				 "GET_REPLY",
				 "LOCAL_PUT_REQUEST",
				 "PUT_REQUEST",
				 "LOCAL_REMOVE_REQUEST",
				 "CONSISTENCY_CHECK_REQUEST",
				 "GET_REQUEST"
		};
		
		return result;
	}
	
	@Override
	public void wait(Map<String, String> data, ISocket socket)
	{

		if (checkingConsistency) {
            if (consistencyWait < consistencyTimeout) {
                System.out.println("\n\n\nConsistency waiting\n\n\n");
                consistencyWait++;
                return;
            }
            else {
                consistencyWait = 0;
                checkingConsistency = false;
                analyzeConsistencyReplies(socket);
            }

        }
	}
	
	/**
	 * Analyze if responses from other replicas are consistent
	 * 
	 * Execute this function after consistency check request. Checks if all replicas responded consistently
	 * TODO: Wrong, needs rework
	 * 
	 * @param socket this processes socket
	 */
	private void analyzeConsistencyReplies(ISocket socket) {
        System.out.println("\n\nCONSISTENCY REPORT:");
        //magic number 3 - replicas number
        if (this.consistencyReplies.size() != 3) {
            System.out.println("\n\nNOT ALL NODES HAVE REPLICAS. RESOLVING\n\n");
            String key = this.consistencyReplies.get(0).getMessageData().getKey();
            String value = this.consistencyReplies.get(0).getMessageData().getValue();
            executePut(key, value, socket);
            System.out.println("PUT INITIATED WITH KEY " + key + " AND VALUE " + value + "\n\n");
        }
        else {
            //check checksums
            int cs1 = this.consistencyReplies.get(0).getChecksum();
            int cs2 = this.consistencyReplies.get(1).getChecksum();
            int cs3 = this.consistencyReplies.get(2).getChecksum();
            if (cs1 == cs2 && cs2 == cs3) {
                System.out.println("\n\nCHECKSUMS OK, EVERYTHING CONSISTENG\n\n");
            }
            else {
                System.out.println("\n\nCHECKSUMS WRONG, RESOLUTION REQUIRED\n\n");
            }
        }
    }
	
	@Override
	public void processMessage(Message message, Map<String, String> data, ISocket socket)
	{
        MessageData mdata = message.getMessageData();
        String messageKey = mdata.getKey();
        String messageValue = mdata.getValue();        
        int commandID = message.getCommandID();

        switch(commandID) {
        	case 0: 
        		//LOCAL_GET_REQUEST	
        		executeLocalGet(messageKey, message.getSender(), data, socket);
        		break;
        	case 1:
        		//GET_REPLY
        		consistencyReplies.add(message);
                System.out.format("\nThe value for the key \"%s\" is \"%s\"\n\n", messageKey, messageValue);
        		break;
        	case 2:
        		//LOCAL_PUT_REQUEST
                executeLocalPut(messageKey, messageValue, data);
        		break;        		
        	case 3:
        		//PUT_REQUEST
        		executePut(messageKey, messageValue, socket);
        		break;
        	case 4:
        		//LOCAL_REMOVE_REQUEST
        		executeLocaRemove(messageKey, data);
        		break;
        	case 5:
        		//CONSISTENCY_CHECK_REQUEST
        		executeConsistencyCheck(data, socket);
        		break;
        	case 6:
        		//GET_REQUEST
        		executeGet(messageKey, socket);
        		break;


        }
	}
	
	/**
	 * Get ids of nodes storing the key
	 */
    private List<String> getNodeIdsByKey(String key) {
    	int size = this.otherNodes.size();
        int centralNodeIndex = hash(key) % size;
        int leftNodeIndex = (centralNodeIndex + size - 1) % size;
        int rightNodeIndex = (centralNodeIndex + 1) % size;

        List<String> result = new ArrayList<String>();
        result.add(this.otherNodes.get(centralNodeIndex));
        result.add(this.otherNodes.get(leftNodeIndex));
        result.add(this.otherNodes.get(rightNodeIndex));
        return result;
    }

    /**
     * distributed get. Sends a local get request to an appropriate node
     */
    private void executeGet(String key, ISocket socket)
    {
        String receiverNodeId = getNodeIdsByKey(key).get(0);
        MessageData data = new MessageData(key);
        Message getMessage = new Message(pid, receiverNodeId, data, 0);
        socket.acceptMessage(getMessage);
    }
    
    /**
     * distributed put. Sends local put requests to all appropriate nodes.
     */
    private void executePut(String key, String value, ISocket socket)
    {
        List<String> receiverIds = getNodeIdsByKey(key);
        MessageData data = new MessageData(key, value);
        Message putMessage = new Message(pid, receiverIds, data, 2);
        socket.acceptMessage(putMessage);
    }

    /**
     * Sends a response with the data associated with the key
     * 
     * @param key requested key
     * @param sender requesting node
     * @param data local database
     * @param socket local socket
     */
    private void executeLocalGet(String key, String sender, Map<String, String> data, ISocket socket)
    {
		String value = data.get(key);
        if (value != null) {
            int checksum = this.checksums.get(key);
            MessageData valueMessageData = new MessageData(key, value);
            Message valueMessage = new Message(pid, sender, valueMessageData, checksum, 1);
            socket.acceptMessage(valueMessage);
            
        }
    }
    
    /**
     * remove a key from local node
     * 
     * @param key key to remove
     * @param data local database
     */
    private void executeLocaRemove(String key, Map<String, String> data)
    {
		if (data.containsKey(key)) {
            data.remove(key);
            checksums.remove(key);
            System.out.println("Data removed");
        }
        else {
            System.out.println("No data with such key on node");
        }
    }
    
    /**
     * add a key to local node
     * 
     * @param key key to add
     * @param value value to add
     * @param data local database
     */
	private void executeLocalPut(String key, String value, Map<String, String> data)
	{
        int checksum = hash(key) + hash(value);
        data.put(key, value);
        checksums.put(key, new Integer(checksum));
		System.out.println("Data inserted to node " + pid);
    }
	
	/**
	 * Initiate consistency check
	 * 
	 * @param data local database
	 * @param socket local socket
	 */
	private void executeConsistencyCheck(Map<String, String> data, ISocket socket)
	{
        checkingConsistency = true;
        consistencyReplies.clear();
        consistencyWait = 0;
        for (String key : data.keySet()) {
            List<String> ids = getNodeIdsByKey(key);
            //boolean dataMissing = true;
            for (String id : ids) {
                MessageData mdata = new MessageData(key);
                Message getMessage = new Message(pid, id, mdata, this.checksums.get(key), 0);
                socket.acceptMessage(getMessage);
            }
            System.out.println("\n\nsending out messages\n\n");
        }
    }
}
