import java.util.List;
import java.util.Map;

public interface IProtocol {
	/**
	 * process incoming message
	 * 
	 * @param message the message to process
	 * @param data this process database
	 * @param socket this process socket
	 */
	public void processMessage(Message message, Map<String, String> data, ISocket socket);
	
	/**
	 * execute if process gets control with no messages to process
	 * 
	 * @param data this process database
	 * @param socket this process socket
	 */
	public void wait(Map<String, String> data, ISocket socket);
	
	/**
	 * get process id
	 */
	public String getPID();
	
	/**
	 * get descriptions of the commands supported by the protocol
	 * position in the list corresponds to the command id
	 */
	public String[] getUserCommandList();
	
	/**
	 * Inform this process about other existing nodes
	 * @param otherIds
	 */
	public void registerOtherNodes(List<String> otherIds);
}