import java.util.List;

public interface IVirtualNode {

    public String getNodeId();

    public void registerOtherNodes(List<String> otherIds);

    public void setSocket(ISocket socket);

    public void setPhysicalNodeId(String id);

    public void process(Message message);

}
