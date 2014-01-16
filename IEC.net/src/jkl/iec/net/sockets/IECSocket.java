package jkl.iec.net.sockets;


import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import jkl.iec.net.sockets.IECSocketParameter.IECSocketType;
import jkl.iec.net.utils.IECFunctions;



/**
 * java class for an IEC 60870-5-104 Socket.<br>
 * usable as server or client socket.
 *
 * @author Jaentsch Klaus
 * @version 1.0
 */

public class IECSocket extends Thread {
	
	public enum IECSocketStatus {
		IECSocketINIT,
		IECSocketStartDT,
		IECSocketStopDT,
		IECSocketClose,
		IECSocketRemoteClose
	}
	
	private static int SocketCount=0;
	
	public Logger log;
	
	private class ThreadAction extends Thread {
		ActionEvent a;
		public ThreadAction(ActionEvent a){
        	this.a=a;
        }
		@Override
		public void run() {
	    	if (iecevent!=null) {
	    		iecevent.actionPerformed(a);
	    	}
		}
	}
	
	private class irq extends TimerTask {
	    public void run() {
//	    	System.out.println("TIMER");
//	      trace(TraceLevel.DEBUG,"Timer enter");
	      if (iecSocketParameter.t2 < new Date().getTime()) {
		      log.fine("(t2) expired have to send Ack_ now");	
		      send_SFrame_Ack();
//		      iecSocketParameter.t2=Long.MAX_VALUE;
	      	}
	      if (iecSocketParameter.t1 < new Date().getTime()) {
	    	  log.fine("(t1) expired i Should now get Ack_");	
		      iecSocketParameter.t1=Long.MAX_VALUE;
	      }
//	      if (iecSocketParameter.t0 < new Date().getTime()) {
//		      log.warning("(t0) expired missing polling");	
//		      iecSocketParameter.t0=new Date().getTime()+iecSocketParameter.T0;
//	      }
	      if (iecSocketParameter.iecSocketType == IECSocketType.IECServer) {
		      if (iecSocketParameter.t3 < new Date().getTime()) {
			      log.warning("(t3) expired missing polling");
			      log.finer("(t3) exp. send polling");
			      send_Test();
			      iecSocketParameter.t3=new Date().getTime()+iecSocketParameter.T3+100;
		      }
	      }

	      if (iecSocketParameter.iecSocketType == IECSocketType.IECClient) {
	         if (iecstatus == IECSocketStatus.IECSocketINIT) {
	        	 send_Start();	        	 
	          }
    	      if (iecSocketParameter.t3 < new Date().getTime()) {
			      log.finer("(t3) exp. send polling");
			      send_Test();
			      iecSocketParameter.t3=new Date().getTime()+iecSocketParameter.T3+100;
    	      }
	      }
	      
	      if (iecSocketParameter.k == iecSocketParameter.K) {
		      log.warning("(k) overrun i Should now get Ack_ ");	
		      iecSocketParameter.k=0;
	      }
	    }
	  }
		
//	enum IECSocketStatus {
//		IECStatusNULL,IECStatusStartDT,IECStatusStopDT
//	}
	
	private enum APCIFrame {
		APCI_NULL,APCI_I_Frame,APCI_S_Frame,
		APCI_U_start,APCI_U_start_ack,APCI_U_stop,APCI_U_stop_ack,APCI_U_test,APCI_U_test_ack
		}
	
	private byte[] RXdata = new byte[1024];
    byte[] APDU = new byte[246];
    
//    private Socket socket;
    public Socket socket;
    private boolean Run;
    int id;

	private IECSocketStatus iecstatus= IECSocketStatus.IECSocketINIT;
	
    private int IPcount, APDUPointer, APDUlength=0;
    private IIECnetActionListener iecevent= null;   //Listener for external classes
//    ActionListener acttionlistener=null;     //listener to Server/Client IP-Socket
    
    private InputStream stream;
    private OutputStream data;
    private boolean newAPDU; 
	private String txt;
	
	public IECSocketParameter iecSocketParameter = new IECSocketParameter();
	
	private int vs, vr;
	
	private ActionEvent actionevent; 
	
	/**
	 * return the current socket status
	 */
	public IECSocketStatus getIECstatus() {
		return iecstatus;
	}

	/**
	 * works only for client sockets<br>
	 * send the request for status s to the server  
	 * @param s = Requested status
	 */
	public void setIECstatus(IECSocketStatus s) {
	    if (iecSocketParameter.iecSocketType == IECSocketType.IECClient) {
  		  log.finest("current status "+iecstatus.toString());
  		  if (s!= iecstatus) {
        		  log.finest(s.toString());
	       		  if (s==IECSocketStatus.IECSocketStartDT) {
	    	    	  send_Start();
	    		  }
	    		  if (s==IECSocketStatus.IECSocketStopDT) {
	    	    	  send_Stop();
	    		  }
	    	  }
	    }
	}
	
	/**
	 * resets the counter for received and sender massages <br>  
	 * <b>DANGERUS! </B>only for test cases partner socket should close connection
	 */
	public void resetIFrameSq() {
		vs =0 ;
		vr =0 ;
	}
	
	private void init(){
		log = Logger.getLogger("jkl.iec.net.sockets.IECSocket."+String.valueOf(SocketCount));
		id = SocketCount++;
//		System.out.println("LOGGER name  "+log.getName());
		log.fine("");
	
	}

	public IECSocket() {
        init();
	}

	public IECSocket(Socket sock ) {
		this.socket = sock;
        init();
	}
	
	/**
	 * create an IECSocket 
	 * @param sock = java.net.socket instance
	 * @param a    = Listener 
	 */
	public IECSocket(Socket sock,IIECnetActionListener a) {
		this.socket = sock;
        this.iecevent = a;
        init();
	}
	

	 public void setSocket(Socket s) {
		 socket = s;
	 }
	 
	 public void setIECEvent(IIECnetActionListener p) {
	      iecevent = p;
	 }
	 
	/**
	 * IECSocket starts ONLY if socket isConnected
	 */
	public void start() {
		if (socket.isConnected()){
				try {
				   	stream = socket.getInputStream();
					data =socket.getOutputStream();
					super.start();
				} catch (IOException e) {
					log.severe(e.getMessage());	
					e.printStackTrace();
					} 
		} else {
			log.severe("not connected Cancel start");	
		}
	 }
	 
	 public void run() {
		log.finest("");
		Timer timer =new Timer();
		timer.scheduleAtFixedRate(new irq(), 1000,1000);
		newAPDU=true;
        iecSocketParameter.t0=new Date().getTime()+iecSocketParameter.T0;
        iecSocketParameter.t3=new Date().getTime()+iecSocketParameter.T3;
        Run=true;
		while (!isInterrupted()&&Run) {
		   	actionevent=null;
			 try {
		    	IPcount = stream.read(RXdata);
//				trace(TraceLevel.DEBUG,"Socket Received : "+IPcount+ "bytes");
		    	if (IPcount !=-1) {
		    		getAPDU();
		    		} 
		    	else {   //Socked close by Client
			    	log.warning("?remote closed?");   // closed by remote socket
		    		actionevent = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,IECSocketStatus.IECSocketRemoteClose.toString());
		    		ThreadAction a= new ThreadAction(actionevent);
		    		a.start();
		    		interrupt();  
		    		}
		    	} catch (IOException e) {
		    		if (e.getMessage().equals("Connection reset")) {  // closed by remote socket
			    		log.warning(e.getMessage());
			    		actionevent = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,IECSocketStatus.IECSocketRemoteClose.toString());
			    		ThreadAction a= new ThreadAction(actionevent);
			    		a.start();
//			    		acttionlistener.actionPerformed(actionevent);
		    		}
		    		if (e.getMessage().equals("socket closed")) {  // closed by calling close() 
			    		log.fine(e.getMessage());
		    		}
		    		if (!isInterrupted()) {
		    			interrupt();
		    		}
	    		}
        	}

		timer.cancel();
	 	timer.purge();
	 	timer=null;
	 	log.fine("stop");
	 	}
	 
	private void close() {
 		if (!socket.isClosed()) {
 			log.finest("");
 			try {
 				socket.close();
				actionevent = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,IECSocketStatus.IECSocketClose.toString());
	    		ThreadAction a= new ThreadAction(actionevent);
	    		a.start();
	    		//acttionlistener.actionPerformed(actionevent);
 			} catch (IOException e) {
 				log.severe(e.getMessage());
 			}
 		}
	}
	
	 public void interrupt() {
 		log.finest("");
		super.interrupt();
	    Run=false;
	    close();
	 }
	 
	 private void doAPCI(APCIFrame apci) {
		 if ((apci == APCIFrame.APCI_U_start)||(apci == APCIFrame.APCI_U_start_ack)) {
			if (apci == APCIFrame.APCI_U_start) {
					send_StartAck();
			}
			iecstatus = IECSocketStatus.IECSocketStartDT;
	    	actionevent = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,IECSocketStatus.IECSocketStartDT.toString());
    		ThreadAction a= new ThreadAction(actionevent);
    		a.start();
    		//acttionlistener.actionPerformed(e1);
	    	}
		 if (apci == APCIFrame.APCI_U_test){
			 send_TestAck();
		 	}
		 if ((apci == APCIFrame.APCI_U_stop)||(apci == APCIFrame.APCI_U_stop_ack)) {
			if (apci == APCIFrame.APCI_U_stop) {
				send_StopAck();
			}
			iecstatus = IECSocketStatus.IECSocketStopDT;
			actionevent = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,IECSocketStatus.IECSocketStopDT.toString());
    		ThreadAction a= new ThreadAction(actionevent);
    		a.start();
    		//acttionlistener.actionPerformed(e1);
		 	}

		 //reload the polling wait time  on all receives
		iecSocketParameter.t0=new Date().getTime()+iecSocketParameter.T0; 
		iecSocketParameter.t3=new Date().getTime()+iecSocketParameter.T3; 

		//check if number of IFrames increased to ack.		
		if (iecSocketParameter.w >= iecSocketParameter.W) {
		      log.warning("Should now Ack_ (w)"+iecSocketParameter.w);	
		      send_SFrame_Ack();
		}

	 	}
	 
	 private APCIFrame getAPCI() {
			switch (APDU[2]) {  
			case 0x07 : return APCIFrame.APCI_U_start; 				
			case 0x0b : return APCIFrame.APCI_U_start_ack; 
			case 0x13 : return APCIFrame.APCI_U_stop; 
			case 0x23 : return APCIFrame.APCI_U_stop_ack; 
			case 0x43 : return APCIFrame.APCI_U_test; 
			case (byte) 0x83 : return APCIFrame.APCI_U_test_ack; 
			}
		
		int tvs=(APDU[4]&0x00ff) + ((APDU[5]& 0x00ff) <<8); //read confirmation from partner
//		System.out.println("tvs:"+tvs+"  vs:"+vs);
		if (tvs/2 >= vs) {
	    	iecSocketParameter.t1=Long.MAX_VALUE;
	    	log.fine("my TX was correkt Ackl. reset t1" );
	    }
		if ((APDU[2] & 0x1)==0x0) {
			vr= (APDU[2]& 0x00ff) +(( APDU[3] & 0x00ff) <<8) ; 
//	    	trace(TraceLevel.INFO,"vr is now "+vr );
			if (iecSocketParameter.t2==Long.MAX_VALUE)  {
				iecSocketParameter.t2=new Date().getTime()+iecSocketParameter.T2; // i have to send an ack. afer t2.
			}
		    iecSocketParameter.w++;
			iecSocketParameter.k=0;	 
//	    	trace(TraceLevel.INFO,"w is now "+iecSocketParameter.w +"  vr is now "+vr);
			return APCIFrame.APCI_I_Frame;
		 }
		
		if ((APDU[2] & 0x2)==0x0){
			iecSocketParameter.k=0;
//			iecSocketParameter.t0=new Date().getTime()+iecSocketParameter.T0; // also interpret as polling and reset wait poll timer
			return APCIFrame.APCI_S_Frame;			 
		 }

		return APCIFrame.APCI_NULL;
	 }
	 
	 private void doAPDU() {
	// Determine what type of 104 Frame (APCI) is received  and notify App. if I_Frame received;
		 byte[] ASDU =new byte[250];
		 APCIFrame apci = getAPCI();    // what frame i have received ??
			if (apci== APCIFrame.APCI_I_Frame) {
				txt=String.format("RX_%s [%05d] %s","_i_",vr,IECFunctions.byteArrayToHexString(APDU,0,APDUlength));
				log.fine(txt);	
				}
			if (apci== APCIFrame.APCI_S_Frame)  {
				txt= String.format("RX_%s [%05d] %s","_s_",vs,IECFunctions.byteArrayToHexString(APDU,0,APDUlength));
				log.fine(txt);
			}
			
			// apci is an U_Frame
			if ((apci!= APCIFrame.APCI_I_Frame)&&(apci!= APCIFrame.APCI_S_Frame))  {
				switch (apci) {
				case APCI_U_start  : txt="_u_s_A_"; break;
				case APCI_U_stop  : txt="_u_e_A_"; break;
				case APCI_U_test  : txt="_u_t_A_"; break;
				default:
					break;
				}
				log.fine("RX_"+txt+IECFunctions.byteArrayToHexString(APDU,0,APDUlength));
			}
			if (apci== APCIFrame.APCI_I_Frame) {
				int ASDULength=0;
				// I_Frame received prepare ASDU for the app.
				for (int x=6;x<APDUlength;x++){
					ASDU[ASDULength]=APDU[x];
					ASDULength++;
					}
				log.info("RX_"+IECFunctions.byteArrayToHexString(ASDU,0,ASDULength));	
				// transfer ASDU to the app.
				if (iecevent !=null) {
					iecevent.onReceive(this,ASDU, ASDULength);
				}
			}
			doAPCI(apci);
			newAPDU =true;
	 }
	 
	 private void getAPDU() { 
	// Extract one 104  message (APDU) out of the IP Stream
		 for (int it=0;it<IPcount;it++) {
			 if (newAPDU) {
					APDUPointer=0;
					APDUlength = RXdata[it+1]+2;
					newAPDU= false;
//					trace("_ADPU length:"+APDUlength);
					}
			 	APDU[APDUPointer]=RXdata[it];
				
			 	if (APDUPointer==APDUlength-1) {   //APDU is  Complied
			 		doAPDU();
			 		}
				APDUPointer++;
			}
	 }
	 
	 private void send_Start() {
		byte[] APDU_TX = new byte[10];
	 	APDU_TX[0]=0x68;
	 	APDU_TX[1]=04;
	 	APDU_TX[2]=0x07;
	 	APDU_TX[3]=00;
	 	APDU_TX[4]=0;
	 	APDU_TX[5]=0;
		txt="_u_s_C_";
	 	send(APDU_TX,6);		 
	 }
	 private void send_StartAck() {
		byte[] APDU_TX = new byte[10];
	 	APDU_TX[0]=0x68;
	 	APDU_TX[1]=04;
	 	APDU_TX[2]=0x0b;
	 	APDU_TX[3]=00;
	 	APDU_TX[4]=0;
	 	APDU_TX[5]=0;
		txt="_u_s_C_";
	 	send(APDU_TX,6);		 
	 }
	 private void send_Stop() {
		byte[] APDU_TX = new byte[10];
	 	APDU_TX[0]=0x68;
	 	APDU_TX[1]=04;
	 	APDU_TX[2]=(byte) 0x13;
	 	APDU_TX[3]=00;
	 	APDU_TX[4]=0;
	 	APDU_TX[5]=0;
		txt="_u_e_C_";
	 	send(APDU_TX,6);		 
	 }

	 private void send_StopAck() {
		byte[] APDU_TX = new byte[10];
	 	APDU_TX[0]=0x68;
	 	APDU_TX[1]=04;
	 	APDU_TX[2]=(byte) 0x23;
	 	APDU_TX[3]=00;
	 	APDU_TX[4]=0;
	 	APDU_TX[5]=0;
		txt="_u_e_C_";
	 	send(APDU_TX,6);		 
	 }
	 
	 private void send_Test() {
		byte[] APDU_TX = new byte[10];
	 	APDU_TX[0]=0x68;
	 	APDU_TX[1]=04;
	 	APDU_TX[2]=(byte) 0x43;
	 	APDU_TX[3]=00;
	 	APDU_TX[4]=0;
	 	APDU_TX[5]=0;
		txt="_u_t_A_";
		send(APDU_TX,6);		 
	 }
	 
	 private void send_TestAck() {
		byte[] APDU_TX = new byte[10];
	 	APDU_TX[0]=0x68;
	 	APDU_TX[1]=04;
	 	APDU_TX[2]=(byte) 0x83;
	 	APDU_TX[3]=00;
	 	APDU_TX[4]=0;
	 	APDU_TX[5]=0;
		txt="_u_t_C_";
		send(APDU_TX,6);		 
	 }
	 
	 private void send_SFrame_Ack() {
		byte[] APDU_TX = new byte[10];
		int  tvr = ((vr) )+2   ;
	 	APDU_TX[0]=0x68;
	 	APDU_TX[1]=04;
	 	APDU_TX[2]=01;
	 	APDU_TX[3]=00;
	 	APDU_TX[4]=(byte) (tvr % 256);
	 	APDU_TX[5]=(byte) (tvr / 256);
		txt=String.format("%s [%05d] ","_s_",vr+1);
		send(APDU_TX,6);		 
		iecSocketParameter.t2=Long.MAX_VALUE;
	    iecSocketParameter.w=0;
//	    trace(TraceLevel.DEBUG,"reset w,t2");
	    }


	 /** 
	   * Sends Hex Stream.<br>
	   * @param b =Hex Stream buffer
	   * @param len =length of buffer
	   */
	 public void sendIFrame(byte[] b, int len) {
		 byte[] APDU_TX = new byte[250];
		 int tvs = vs <<1 ;
    	 int tvr = (vr+2) ;
	     APDU_TX[0] = 0x68;
	     APDU_TX[1] = (byte) (len+4);
	     
	     APDU_TX[2] = (byte) (tvs % 256);
	     APDU_TX[3] = (byte) (tvs / 256);
	
	     APDU_TX[4] = (byte) (tvr % 256);
	     APDU_TX[5] = (byte) (tvr / 256);
//	     trace(TraceLevel.INFO," BUFFER_SIZE_"+b.length);
	     for (int i=0;i<len;i++) {
		     APDU_TX[6+i] =b[i];
	     }
	     len = len+6;
	     vs++;      //variable send
		 iecSocketParameter.k++;
//		 txt="Update_k("+iecSocketParameter.k+")   vs("+vs+")";
		 if (iecSocketParameter.t1==Long.MAX_VALUE) {
			 iecSocketParameter.t1=new Date().getTime()+iecSocketParameter.T1;
			 txt=txt+" Update_(t1)";
		 }
	     txt=String.format("%s [%05d] ","_i_",vs);
		 send(APDU_TX,len);	 
	 }
	 


	  /** 
	   * Sends Hex Stream.<br>
	   * Hex Stream Length has to be stored on b[0].<p>
	   * ! b[0] will NOT be send !
	   * 
	   * @param b =Hex Stream buffer
	   */

	 public void sendIFrame(byte[] b) {
			 int len=b[0];
		     for (int i=1;i<=len;i++) {
			     b[i-1] =b[i];
		     }
		     sendIFrame(b,len);
		}
	 
	 private void send(byte[] b, int len) {
		 	String tx=null;
	    	try {
				if (!socket.isClosed()) {
					data.write(b,0,len);
					tx= "TX_";
				} else	tx= "tx_";
			} catch (IOException e) {
	    		interrupt();
	    		log.severe(e.getMessage());
	    		e.printStackTrace();
			}		 
		 	log.fine(tx+txt+IECFunctions.byteArrayToHexString(b,0,len));
	    	if (len>6) {
	    		log.info(tx+IECFunctions.byteArrayToHexString(b,6,len-6));
	    	}
	 }
	 
	public int getSockID() {
		return id;
	 }

}
