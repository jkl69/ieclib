package jkl.iec.net.test;


import java.awt.event.ActionEvent;
import jkl.iec.net.sockets.IECSocket;
import jkl.iec.net.sockets.IIECnetActionListener;
import jkl.iec.net.utils.IECFunctions;
import jkl.iec.tc.type.IIECTCActionListener;

public class IECEventImp implements IIECnetActionListener, IIECTCActionListener{

	
	@Override
	public void onReceive(IECSocket sender,byte[] b, int len) {
		// TODO Auto-generated method stub
    	System.out.println("NEW IEC data arrived");			
	}

	@Override
	public void sendByteArray(byte[] b, int c) {
		// TODO Auto-generated method stub
    	System.out.println("IEC send["+c+"] "+IECFunctions.byteArrayToHexString(b,0,b[0]+1));
	}

	@Override
	public void doUpdate(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		System.out.println(e);
	}

}
