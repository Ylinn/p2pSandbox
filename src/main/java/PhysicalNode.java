import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class PhysicalNode implements IPhysicalNode
{

    private String id;
    private Map<String, IVirtualNode> virtualNodes;
    private int nextVN;
    private ArrayList<String> virtualIDs;
    
    public PhysicalNode(String nodeId)
    {
        this.id = nodeId;
        this.virtualNodes = new HashMap<String, IVirtualNode>();
        this.virtualIDs = new ArrayList<String>();
        this.nextVN = 0;
    }

    @Override
    public String getNodeId()
    {
        return this.id;
    }

    @Override
    public void registerVirtualNode(IVirtualNode virtualNode) 
   {
        if (!this.virtualNodes.containsKey(virtualNode.getNodeId())) {
            this.virtualNodes.put(virtualNode.getNodeId(), virtualNode);
            this.virtualIDs.add(virtualNode.getNodeId());
            virtualNode.setPhysicalNodeId(this.id);
        }
    }

    @Override
    public IVirtualNode getVirtualNode(String virtualNodeId)
    {
        return this.virtualNodes.get(virtualNodeId);
    }

    @Override
    public Map<String, IVirtualNode> getVirtualNodes()
    {
        return this.virtualNodes;
    }

    @Override
    public void transferVirtualNode(String virtualNodeId, IPhysicalNode physicalNodeId)
    {
        if (this.virtualNodes.containsKey(virtualNodeId)) {
            IVirtualNode nodeToTransfer = this.getVirtualNode(virtualNodeId);
            if (nodeToTransfer != null) {
                this.virtualNodes.remove(virtualNodeId);
                this.virtualIDs.remove(virtualNodeId);
                physicalNodeId.registerVirtualNode(nodeToTransfer);
                
                this.nextVN = this.nextVN % this.virtualNodes.size();
            }
        }
    }
    
    @Override
    public IVirtualNode getNextVirtualNode()
    {
    	IVirtualNode result = virtualNodes.get(virtualIDs.get(nextVN));
    	nextVN = (nextVN + 1) % virtualIDs.size();
    	return result;
    }
}
