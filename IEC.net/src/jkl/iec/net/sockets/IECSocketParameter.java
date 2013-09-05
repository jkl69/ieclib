package jkl.iec.net.sockets;

/**
 * 	Defines the default parameters
 */	
public class IECSocketParameter implements Cloneable {
	
	/**
	 * IECSocket types
	 */
	public 	enum IECSocketType {
		/**
		 * defines the IECSocket as Server socket
		 */
		IECServer,
		/**
		 * defines the IECSocket as Client socket
		 */
		IECClient
	}

	/**
	 * defines the Txpe of the IECSocket
	 */
	public IECSocketType iecSocketType;
	/**
	 * 	reconnect timeout for Client Sockets<br>
	 *  or  for Server sockets timeout who he expect an polling
	 */
	public long T0;
	/**
	 * the socket expect an acknowledge 
	 * on his transmitted data after this time
	 */
	public long  T1;
	/**
	 * 	the socket has to acknowledge 
	 *  for his received data after this time
	 */
	public long T2;
	/**
	 * 	 timeout for polling (testFrames)
	 *   if there are currently no Data transmission
	 */	
	public long T3;
	public long t0,t1,t2,t3;
	public int w,k;
	/**
	 * 	the socket has to acknowledge 
	 *  for his received data after this amount of messages
	 */
	public int W;
	/**
	 * the socket expect an acknowledge 
	 * on his transmitted data after amount of messages
	 */
	public int K;
	
	public IECSocketParameter() {
 			T0=30000;
 			T1=15000;		
			T2=10000;		
			T3=20000;		//client polling time 

			t0=Long.MAX_VALUE;
			t1=Long.MAX_VALUE;
			t2=Long.MAX_VALUE;
			t3=Long.MAX_VALUE;
			
			w=0;
			k=0;
			W=8;
			K=12;
		} 
	
		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
			   
		   }
	 }   
