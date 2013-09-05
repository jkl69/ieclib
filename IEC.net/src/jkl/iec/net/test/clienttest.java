package jkl.iec.net.test;

import jkl.iec.net.sockets.IECClient;
import jkl.iec.net.sockets.IECSocket.IECSocketStatus;


public class clienttest {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IECClient c;
		
		IECEventImp iecevent = new IECEventImp();
		c = new IECClient("L60948-JAEN",2404);
		
		c.setIECClientListener(iecevent);
//		c.host = "192.168.0.32";
//		c.host ="L60948-JAEN";
//		c.port = 2405;
		c.iecparam.T0=5000;
		
		c.start();
		
		System.out.println("Try startDT on IECSocket ");	
		c.setIECDT(true);
	   
		try {
		   		Thread.sleep(2000);
		   	} catch (InterruptedException e) {	e.printStackTrace();	}

	c.setIECDT(true);
		
 	   try {
	   		Thread.sleep(45000);
	   	} catch (InterruptedException e) {	e.printStackTrace();	}
			
	c.interrupt();
   	
   	try {
   		Thread.sleep(1000);
	} catch (InterruptedException e) {	e.printStackTrace();	}
		
   	System.out.println("____Try to start a new client ");	
	
   	c = new IECClient();
   	c.setIECClientListener(iecevent);
   	c.start();
   	
	try {
   		Thread.sleep(35000);
	} catch (InterruptedException e) {	e.printStackTrace();	}
		
   	c.interrupt();

   	System.out.println("_____Main ENDE ");	
	}

}
