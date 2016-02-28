import java.util.List;

public interface ISocket {

	/**
	 * Put a message into socket
	 */
    public void acceptMessage(Message message);

    /**
     * Extract stored messages
     */
    public List<Message> getAllMessages();

}
