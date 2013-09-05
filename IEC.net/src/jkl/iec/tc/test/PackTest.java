package jkl.iec.tc.test;

import java.util.ArrayList;

import jkl.iec.tc.type.IECTCItem;
import jkl.iec.tc.type.IECMap.IECType;

public class PackTest {
	ArrayList<IECTCItem> itemarray = new ArrayList<IECTCItem>();
	ArrayList<IECTCItem> parray = null;

	public PackTest() {
		System.out.println("create TCItem");
		
		IECTCItem i = null;
		for (int x=0 ;x<5;x++) {
			i = new IECTCItem(IECType.M_DP_TB,100);
			i.iob(0).setIOB(4097+x);
//			i.iob(0).setValue(5.5);
			i.iob(0).setQU((byte) 0);
			itemarray.add(i);
		}
		for (int x=0 ;x<15;x++) {
			i = new IECTCItem(IECType.M_IT_NA,30);
			i.iob(0).setIOB(12289+x);
			i.iob(0).setValue(100*x);
//			i.iob(0).setQU((byte) 0);
			itemarray.add(i);
		}

		System.out.println("TCItems in array: "+itemarray.size());
		
		parray = IECTCItem.Pack(itemarray);
		System.out.println("Packed array size: "+parray.size());
		
		for (int x=0 ; x<parray.size();x++) {
			System.out.println(x+" Packed item obj count: "+parray.get(x).getIOBCount());
			System.out.println(x+" Packed item : "+parray.get(x).printStream());
			
		}
		for (int x=0 ; x<parray.size();x++) {
			System.out.println(x+" Packed item obj count: "+parray.get(x).getIOBCount());
			System.out.println(x+" Packed item : "+parray.get(x).printStream());
			
		}
   }
}
