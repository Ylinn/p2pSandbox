import java.util.List;
import java.util.Map;

public interface IProtocol {
	public void processMessage(Message message, Map<String, String> data, ISocket socket);
	public void wait(Map<String, String> data, ISocket socket);
	public String getPID();
	public String[] getUserCommandList();
	public void registerOtherNodes(List<String> otherIds);
}