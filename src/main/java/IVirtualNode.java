import java.util.List;
/**
 * TODO: eliminate
 */
public interface IVirtualNode {
	/**
	 * Get this virtual node identifier
	 */
    public String getNodeId();

    /**
     * Inform this virtual node about other existing nodes
     * @param otherIds ids of other virtual nodes to communicate with
     */
    public void registerOtherNodes(List<String> otherIds);

    /**
     * Attach an existing socket to this node  
     */
    public void setSocket(ISocket socket);

    /**
     * Set host physical node
     * @param id physical node id
     */
    public void setPhysicalNodeId(String id);

    /**
     * process incoming message
     * @param message incoming message. null if none
     */
    public void process(Message message);

}
