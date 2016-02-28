import java.util.Map;

public interface IPhysicalNode {

	/**
	 * Get node identifier
	 */
    public String getNodeId();

    /**
     * Attach existing "virtual node" to physical node.
     * 
     * @param virtualNode exisiting virtual node
     */
    public void registerVirtualNode(IVirtualNode virtualNode);


    /**
     * Get an attached virtual node by id
     */
    public IVirtualNode getVirtualNode(String virtualNodeId);

    /**
     * Get the id of the virtual node that gets the time slot of this physical node.
     * 
     * When the platform decides to grant control to a physical node, the physical node gets to decide
     * 	which virtual node attached to it get the control. 
     */
    public IVirtualNode getNextVirtualNode();

    /**
     * Get all attached virtual nodes indexed by theri ids
     * @return mapping id -> IVIrtualNodes of all attached virtual nodes
     */
    public Map<String, IVirtualNode> getVirtualNodes();

	/**
	 * Transfer a virtual node to another physical node
	 */
    public void transferVirtualNode(String virtualNodeId, IPhysicalNode physicalNodeId);

}
