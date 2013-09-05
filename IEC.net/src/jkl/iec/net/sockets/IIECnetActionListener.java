package jkl.iec.net.sockets;

import java.awt.event.ActionListener;

/**
 * Lister  extends ActionListener <br>
 * to receive ActionEvent from an IECSocket or IECServer 
 * <br><br>
 * Possible values for ActionEvent.getActionCommand()'s :<br>
 * e.g<br>
 *IECServerAction."IECServerClinentConnect"<br>
 *IECSocketStatus."IECSocketStartDT"<br>
 * <br>
 *<b>new Method</b><br>
 * onReceive(IECSocket sender,byte[] b,int len)
 * @param sender = IECSocket fired this event
 * @param b      = Byte Stream the sender received   
 * @param len    = length of Stream the sender received   
 *  
 */
public interface IIECnetActionListener extends ActionListener {
	
	void onReceive(IECSocket sender,byte[] b,int len);
	
}
