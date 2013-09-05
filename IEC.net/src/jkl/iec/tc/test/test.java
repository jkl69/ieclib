package jkl.iec.tc.test;


import java.util.Scanner;

import jkl.iec.net.sockets.IIECnetActionListener;
import jkl.iec.net.test.IECEventImp;
import jkl.iec.net.utils.IECFunctions;
import jkl.iec.tc.type.IECList;
import jkl.iec.tc.type.IECTCItem;
import jkl.iec.tc.type.IIECTCActionListener;
import jkl.iec.tc.type.IECMap.IECType;
import jkl.iec.tc.utils.IECSimPlayer;

public class test {

	/**
	 * @param args
	 */
	static IIECnetActionListener tr =new IECEventImp();
	
	private static Scanner scanner = new Scanner( System.in );
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("chosse: ");
		System.out.println("\t1: IECTC Decode test");
		System.out.println("\t2: IECTC Pack test");
		System.out.println("\t3: IECTA test");
//		String input = scanner.nextLine();  // from console input example above.
		int number = scanner.nextInt();  // from console input example above.
//		System.out.println("input int: "+number);
		
		switch (number) {
		 case 1: {
				System.out.println("Start DecodeTest");
				DecodeTest t1 = new DecodeTest();
				break;
		 		}
		 case 2: {
				System.out.println("Start Pack Test");
				PackTest t2 = new PackTest();
				break;
		 		}
		 case 3: {
				System.out.println("Start Pack Test");
				TATest t3 = new TATest();
				break;
		 		}
		}
		
/**      	try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
**/		
      	
    
      	System.out.println("Main ENDE ");		
	}
}
