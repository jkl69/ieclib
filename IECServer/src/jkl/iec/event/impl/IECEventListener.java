package jkl.iec.event.impl;


import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import jkl.iec.net.applications.IECServerTraceHandler;
import jkl.iec.net.applications.Server;
import jkl.iec.net.sockets.IECServer.IECServerAction;
import jkl.iec.net.sockets.IECSocket;
import jkl.iec.net.sockets.IECSocket.IECSocketStatus;
import jkl.iec.net.sockets.IIECnetActionListener;
import jkl.iec.net.utils.IECFunctions;
import jkl.iec.tc.gui.IECTableModel;
import jkl.iec.tc.type.IIECTCActionListener;


public class IECEventListener implements IIECnetActionListener,IIECTCActionListener{
	
	JTextArea textArea =null;
	IECTableModel iecmodell=null;
	IECSocket iecSock;

	public IECEventListener() {
//		iecmodell = Server.iectable.getIECModel();// iECdata;
		textArea = Server.textField;
	}
	

	@Override
	public void onReceive(IECSocket sender,byte[] b, int c) {
		iecmodell = Server.iectable.getIECModel();
		iecmodell.ieclist.getByteArray(b, c);
	}
	
	
	@Override
	public void sendByteArray(byte[] b, int c) {
		System.out.println("IEC mesage "+IECFunctions.byteArrayToHexString(b,0,c));
		if (Server.server!=null) {
			for (int it=0;it < Server.server.socket.size();it++) {
				Server.server.socket.get(it).sendIFrame(b,c);
			}
//			if (server.iecSocket != null) {
//				server.iecSocket.sendIFrame(b, c);
//			}
		}
	}
	
	public void doUpdate(int index) {
		iecmodell = Server.iectable.getIECModel();
		if ( iecmodell !=null) {	
			iecmodell.fireTableRowsUpdated(index, index);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e+"ID:"+e.getID());
		if (e.getActionCommand().equals(IECServerAction.IECServerStart.toString())) {
			System.out.println("SERVER START on Port "+Server.server.IECPort);
			Server.BP.StopButton.setEnabled(true);
			Server.BP.StartButton.setEnabled(false);
			Server.BP.PortNo.setEnabled(false);
			Server.Clientpanel.table.setValueAt("run",0,2);
		}
		if (e.getActionCommand().equals(IECServerAction.IECServerStop.toString())) {
			Server.BP.StopButton.setEnabled(false);
			Server.BP.StartButton.setEnabled(true);
			Server.BP.PortNo.setEnabled(true);
			Server.Clientpanel.table.setValueAt("Stop",0,2);
		}
		if (e.getActionCommand().equals(IECServerAction.IECServerClientConnect.toString())) {
			iecSock = (IECSocket) e.getSource();
			iecSock.log.addHandler((new IECServerTraceHandler(Server.textField)));
//			iecSock.log.setLevel(Level.ALL);
			Server.Clientpanel.addClient(iecSock);
			}
		if (e.getActionCommand().equals(IECSocketStatus.IECSocketClose.toString())) {
			if(e.getID()!=-1) {
				iecSock = (IECSocket) e.getSource();
				System.out.println("iecsock id:"+iecSock.getId());
				Server.Clientpanel.removeClient(iecSock);
			}
		}
		if ((e.getActionCommand().equals(IECSocketStatus.IECSocketStartDT.toString())) ||
			(e.getActionCommand().equals(IECSocketStatus.IECSocketStopDT.toString()))) {
			iecSock = (IECSocket) e.getSource();
			Server.Clientpanel.update(iecSock);//
//			table.getModel().setValueAt("StartDT",e.getID(),1);
		}
	}

   
}
