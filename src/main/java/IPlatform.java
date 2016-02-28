public interface IPlatform {

	/**
	 * Connect a physical node to platform.
	 * Also connects all "virtual nodes" attached to the physical node.
	 * 
	 * @param id physical node identifier inside platform
	 * @param node existing physical node
	 */
    public void registerPhysicalNode(String id, IPhysicalNode node);
    
    /**
     * Connect a "cirtual node" to platform.
     * TODO: eliminate virtual nodes
     * 
     * @param id virtual node inside platform
     * @param node existing virtual node
     */
    public void registerVirtualNode(String id, IVirtualNode node);
    
    /**
     * Check if platform contains a virtual node
     * 
     * @param id the virtual node identifier
     */
    public boolean isRegisteredNode(String id);
    
    /**
     * Run the simulation
     */
    public void run();

}
