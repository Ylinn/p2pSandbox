
public class DynamoProtocolFactory implements IProtocolFactory {

	public IProtocol createProtocol(String pid) {
		
		return new DynamoProtocol(pid);
	}

}
