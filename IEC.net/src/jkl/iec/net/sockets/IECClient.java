package jkl.iec.net.sockets;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Logger;

import jkl.iec.net.sockets.IECSocket.IECSocketStatus;
import jkl.iec.net.sockets.IECSocketParameter.IECSocketType;

public class IECClient extends Thread {
	
	/** These are the possible ActionEvent Strings<br>
	 *  IECClient sends to an ActionListener **/
	public enum IECClientAction {
		/** Identifies an IEC Server start Event **/
		IECClientStart,
		/** Identifies that the client has successfully connected to an IEC Server **/
		IECClientConnect,
		/** Identifies that an client was Disconnected from the IEC Server **/
		IECClientDisconnect, 
		/** Identifies an IEC Server stop Event **/
		IECClientStop
	}
	
    private class ThreadAction extends Thread {
		ActionEvent ev;
		IECSocket sock;
		byte[] b;
		int l;
		
		public ThreadAction(ActionEvent ev){
        	this.ev=ev;
        	this.l = -1;
        	start();
        }
		public ThreadAction(IECSocket sender, byte[] b, int len){
        	this.sock = sender;
        	this.l = len;
        	this.b = b;
        	start();
        }

		@Override
		public void run() {
	    	if (iecclientListener!=null) {
	    		if (l> -1) {
	    			iecclientListener.onReceive(sock, b, l);
	    		} else {
		    		iecclientListener.actionPerformed(ev);
	    		}
	    	}
		}
	}
    
    private class IECSocketListener implements IIECnetActionListener {
    	@SuppressWarnings("unused")
		private ThreadAction a;
		@Override
		public void actionPerformed(ActionEvent e) {
			a= new ThreadAction(e);
			if (e.getActionCommand().equals(IECSocketStatus.IECSocketClose.toString())) {
				ActionEvent clientEvent = new ActionEvent(e.getSource(),ActionEvent.ACTION_PERFORMED,IECClientAction.IECClientDisconnect.toString());
	    		ThreadAction a= new ThreadAction(clientEvent);
//	    		resume();
	    		synchronized(o)
	    		{
			        log.finest("notify()");
			        o.notify();
	    		} 
			}
		}

		@Override
		public void onReceive(IECSocket sender, byte[] b, int len) {
			a= new ThreadAction(sender,b,len);
		}
		
	}

	private IECSocketListener iecSocketListener = new IECSocketListener();
	
	public final static Logger log = Logger.getLogger(IECClient.class .getName()); 
	
	private IIECnetActionListener iecclientListener = null;
	
	public Socket socket = null;
	public String host = "127.0.0.1";
	public int port = 2404;
	
	Object o= new Object();
	
	private IECSocket iecsock = null;
	public IECSocketParameter iecparam =new IECSocketParameter();
	
    public IECClient() {
    }

    public IECClient(String host,int port) {
       this.host = host;
       this.port = port;
    }


   private void waitReconnect() {
		log.fine("Wait for next try: "+String.valueOf(iecparam.T0)+"ms");
    	try {
			sleep(iecparam.T0);
		} catch (InterruptedException e1) {
	        log.fine(e1.getMessage());
			interrupt();
        }
   }
   
   public void start() {
   		iecparam.iecSocketType = IECSocketType.IECClient;
   		ActionEvent clientEvent = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,IECClientAction.IECClientStart.toString());
	    ThreadAction a= new ThreadAction(clientEvent);
	    super.start();
   }
    
   
   public void run() {
    	SocketAddress addr = new InetSocketAddress( host, port );
    	do {
        	try {
				log.fine("try to Connect to "+addr.toString());
		    	socket = new Socket();
        		socket.connect( addr);
        		iecsock = new IECSocket(socket,iecSocketListener);
//            	iecsock.iecSocketParameter = (IECSocketParameter) iecparam.clone();
            	iecsock.iecSocketParameter = iecparam;
				iecsock.start();
				ActionEvent clientEvent = new ActionEvent(iecsock,ActionEvent.ACTION_PERFORMED,IECClientAction.IECClientConnect.toString());
	    		ThreadAction a= new ThreadAction(clientEvent);
	    		log.info("Client has connected! "+ socket.getRemoteSocketAddress().toString());
	    		
//	    		suspend();
	    		synchronized(o)
	    		{
	    			try {
						o.wait();
					} catch (InterruptedException e) {
						log.severe("wait " +e.getMessage());
						interrupt();
					}
	    		}

	    		log.finer("resume");
    		} catch (IOException e) {
    			log.warning(e.getMessage());
   				close();

   				}
			if (!isInterrupted()) waitReconnect();
		}  while ((!isInterrupted())&&(socket.isClosed()));

//       	while (!isInterrupted());
//       	while ((!isInterrupted())&&(!socket.isConnected()));

    	log.finer("exit");
		ActionEvent clientEvent = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,IECClientAction.IECClientStop.toString());
		ThreadAction a= new ThreadAction(clientEvent);
    }
	
    public void setIECClientListener(IIECnetActionListener p) {
	      iecclientListener = p;
	}
    
    private void close() {
 		if (socket.isConnected()){
			ActionEvent clientEvent = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,IECClientAction.IECClientDisconnect.toString());
    		ThreadAction a= new ThreadAction(clientEvent);
 		}

 		if (!socket.isClosed()){
 			log.finest("");
 			try {
 				socket.close();	
 			} catch (IOException e1) {
 				log.severe(e1.getMessage());
 			}
 		}
    }
    
    
    public void interrupt() {
        log.finest("");
        close();
    	super.interrupt();	
    }

	/**
	 * try to send an IECStatus request to the partner socket 
	 * @param startDT= true to send an request for StartDT<br>
	 * 		   false to send an request for StopDT
	 * 
	 * @return True if request could send to the partner socket
	 */
    public boolean setIECDT(boolean startDT) {
		if ((socket != null)&&(iecsock!=null)) {
			if (socket.isConnected()) {
				if (startDT) {
					iecsock.setIECstatus(IECSocketStatus.IECSocketStartDT);
				} else 	iecsock.setIECstatus(IECSocketStatus.IECSocketStopDT);
				return true;
			}
		} else {
			log.warning("Request not send 'socket not connected'");
		}
		return false;
	}
}

