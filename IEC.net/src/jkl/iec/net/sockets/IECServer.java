package jkl.iec.net.sockets;


import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

import jkl.iec.net.sockets.IECSocket.IECSocketStatus;
import jkl.iec.net.sockets.IECSocketParameter.IECSocketType;

/**
 * java class for an IEC 60870-5-104 Server.<br>
 * uses an IEC 60870-5-104 socket.
 *
 * @author Jaentsch Klaus
 * @version 1.0
 */

//public class IECServer extends Thread {
public class IECServer extends Thread {
	
	/** These are the possible ActionEvent Strings<br>
	 *  IECServer sends to an ActionListener **/
	public enum IECServerAction {
		/** Identifies an IEC Server start Event **/
		IECServerStart,
		/** Identifies that an client has connected to the IEC Server **/
		IECServerClientConnect,
		/** Identifies that an client has Disconnected from the IEC Server **/
		IECServerClientDisconnect, 
		/** Identifies an IEC Server stop Event **/
		IECServerStop
	}
	
	public final static Logger log = Logger.getLogger(IECServer.class .getName()); 
	/** java.net.Socket **/
	public ServerSocket serverSocket;
	/** array of connected IEC clients */
	public ArrayList<IECSocket> socket;
	/** 
	 * defines default IEC socket parameters that are handled to new created 
	 * iecsockets on client connect */
	public static IECSocketParameter iecSocketParameter = new IECSocketParameter();
		
	private  IECSocket iecSocket =null;
	/** TCP_Port No. server is listen */
	public int IECPort = 2404;
	/** IEC_server run status */
    public 	boolean Run;

    byte[] TXdata = new byte[1024];
    
    private IIECnetActionListener iecserverlistener = null;
	
//    private class runAction implements Runnable {
    public class ThreadAction extends Thread {
		ActionEvent ev;
		public ThreadAction(ActionEvent ev){
        	this.ev=ev;
        	start();
        }
		@Override
		public void run() {
	    	if (iecserverlistener!=null) {
	    		iecserverlistener.actionPerformed(ev);
	    	}
		}
	}
	
    private class IECSocketListener implements IIECnetActionListener {
    	public void actionPerformed(ActionEvent e) {
    		log.finest("Client Action! "+e);
			IECSocket s = (IECSocket) e.getSource();
			
			if (e.getActionCommand().equals(IECSocketStatus.IECSocketRemoteClose.toString())) {
				log.fine("Client No. "+s.socket.getRemoteSocketAddress().toString()+ " remote closed.");
			}
			
			if (e.getActionCommand().equals(IECSocketStatus.IECSocketClose.toString())) {
    			log.info("Client No. "+s.socket.getRemoteSocketAddress().toString()+ " closed.");
        		if (s!=null) {
        			if (socket.indexOf(s) != -1) {
            			log.finest("remove from clientlist");
        				socket.remove(socket.indexOf(s));    		
        				}
        		}
        	log.finer("Clientcount "+socket.size());
        	}
    		
    		ThreadAction a= new ThreadAction(e);
    		}

		@Override
		public void onReceive(IECSocket sender, byte[] b, int len) {
	    	if (iecserverlistener!=null) {
	    		iecserverlistener.onReceive(sender, b, len);
	    	}
		}
    }
    
    IECSocketListener iecSocketListener =null;
    
  public IECServer() {
    	socket = new ArrayList<IECSocket>();
		iecSocketListener = new IECSocketListener();    	
		iecSocketParameter.iecSocketType = IECSocketType.IECServer;
		}

private void close() {
		if (serverSocket!= null){
			log.fine("IECServer close");	
			try {
				serverSocket.close();
			} catch (IOException e) {
				log.severe("IECServer close "+e.getMessage());	
			}
		}	
 	}

public void start() {
	ActionEvent ievent;
	log.finest("");
	try {
		serverSocket = new ServerSocket(IECPort);
		if (iecserverlistener!= null) {
			ievent = new ActionEvent(this,ActionEvent.ACTION_FIRST,IECServerAction.IECServerStart.toString());
				iecserverlistener.actionPerformed(ievent);
			}
		super.start();
		} catch (IOException e) {
			/* port in use */
			log.severe(e.getMessage());
			return;
		}	
}


public void run() {
	ActionEvent serverEvent;
	Run=true;
	log.info("listen on port "+String.valueOf(IECPort));
 //	while (!isInterrupted()&&serverSocket!=null) {
	while (!isInterrupted()&&Run) {
//			System.out.println("Server Interrupted:"+isInterrupted());
			log.fine("Server Wait for connect");
			Socket skt = null;
			try {
				skt = serverSocket.accept();
				log.info("Client has connected!"+ skt.getRemoteSocketAddress().toString());
				iecSocket = new IECSocket(skt,iecSocketListener);
				iecSocket.iecSocketParameter.iecSocketType = IECSocketParameter.IECSocketType.IECServer;
				iecSocket.iecSocketParameter = (IECSocketParameter) iecSocketParameter.clone();
				iecSocket.log.setLevel(log.getLevel());
				iecSocket.start();
				socket.add(iecSocket);
				serverEvent = new ActionEvent(iecSocket,ActionEvent.ACTION_PERFORMED,IECServerAction.IECServerClientConnect.toString());
	    		ThreadAction a= new ThreadAction(serverEvent);
			} catch (IOException e) {
				log.severe(" Accept "+ e.getMessage());
				Run =false;
				log.finest("interuptstate "+String.valueOf(isInterrupted()));
//				interrupt();
			}
		} 
	
	serverEvent = new ActionEvent(this,ActionEvent.ACTION_LAST,IECServerAction.IECServerStop.toString());
	log.finest("Stop");
	iecserverlistener.actionPerformed(serverEvent);
}
	
public IECSocket getSocket(int id) {
	for (IECSocket s : socket){
		if (s.getSockID()==id) {
			return s;
		}
	}
	return null;
}

public void setIECServerListener(IIECnetActionListener l) {
	      iecserverlistener = l;
	 }
	 
/**
 * stops and destroy all client sockets<br>
 * and stop the IEC server.<p>
 * !! Server can NOT been restart again !!
 */
public void interrupt() {
 		log.finest("");
		for (int it=socket.size()-1;it>=0;it--) {
			log.finer("go Interrupt IECSocket "+it);
	 		socket.get(it).interrupt();
		}
        close();
		super.interrupt();
   }

}
