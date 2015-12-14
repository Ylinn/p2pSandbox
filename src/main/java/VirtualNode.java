import java.util.*;

public class VirtualNode implements IVirtualNode {

	private IProtocol protocol;	//TODO: Change to IProtocol! Initialize! (need to fix registerOtherNodes)
	
    private String nodeId;
    private String physicalNodeId;
    private ISocket socket;
    //this map holds other nodes' ids and the occupied space in it
    private List<String> otherNodes;
    private Map<String, String> data;
    private Map<String, Integer> checksums;

    public String getNodeId()
    {
        return nodeId;
    }

    public VirtualNode(String nodeId, IProtocol protocol)
    {
        this.nodeId = nodeId;
        socket = new Socket();
        otherNodes = new ArrayList<>();
        data = new HashMap<>();
        checksums = new HashMap<>();
        this.protocol = protocol;
    }

    @Override
    public void registerOtherNodes(List<String> otherIds)
    {
    	//Obsolete?
    	protocol.registerOtherNodes(otherIds);
    }

    @Override
    public void setSocket(ISocket socket)
    {
        this.socket = socket;
    }

    @Override
    public void setPhysicalNodeId(String id)
    {
        this.physicalNodeId = id;
    }

    @Override
    public void process(Message message)
    {
        if(message != null)
        	protocol.processMessage(message, data, socket);
        else
        	protocol.wait(data, socket);
        
        Message userMsg = parseUserCommand();
        if(userMsg != null)
        	protocol.processMessage(userMsg, data, socket);
    }

    private Message parseUserCommand()
    {
        String[] commands = protocol.getUserCommandList();
        System.out.println("Command list:");
        for(int i = 0; i < commands.length; i++) {
        	System.out.println(i + ". " + commands[i]);
        }
        System.out.println("Please select a command.");
        try {
            Scanner userInputScanner = new Scanner(System.in);
            String input = userInputScanner.nextLine();            
            if(input == "") {
            	//userInputScanner.close();
            	return null;            
            }
            int command = Integer.parseInt(input);
            
            System.out.println("Please enter data in format key:value");
            
            input = userInputScanner.nextLine();
            String[] splittedInput = input.split(":");
            String key = splittedInput[0];
            String value = splittedInput[1];
            
            Message userMsg = new Message("", protocol.getPID(), new MessageData(key, value), command);
            
            //userInputScanner.close();
            return userMsg;
        }
        catch (Exception ex) {
        	return null;
        }
    }
    /* void insertData(String key, String value) {
        int checksum = hash(key) + hash(value);
        this.data.put(key, value);
        this.checksums.put(key, new Integer(checksum));

    }
    
    private int hash(String id)
    {
        int hash=7;
        for (int i=0; i < id.length(); i++) {
            hash = hash * 31 + id.charAt(i);
        }
        return hash;
    }*/


    public String getStatus()
    {
        StringBuilder nodeStatus = new StringBuilder();
        nodeStatus.append("Node: " + nodeId);
        nodeStatus.append(System.getProperty("line.separator"));
        nodeStatus.append(data.toString());
        nodeStatus.append(System.getProperty("line.separator"));
        nodeStatus.append(checksums.toString());
        nodeStatus.append(System.getProperty("line.separator"));
        nodeStatus.append("Physical node: " + this.physicalNodeId);
        nodeStatus.append(System.getProperty("line.separator"));
        nodeStatus.append("Other nodes " + otherNodes.toString());
        nodeStatus.append(System.getProperty("line.separator"));
        return nodeStatus.toString();
    }

}
