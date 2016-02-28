public interface IProtocolFactory
{
	/**
	 * Create an instance of the protocol
	 * 
	 * @param pid process ID
	 */
	IProtocol createProtocol(String pid);
}
