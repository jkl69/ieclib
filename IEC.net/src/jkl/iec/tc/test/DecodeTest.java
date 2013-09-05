package jkl.iec.tc.test;

import java.util.ArrayList;

import jkl.iec.net.utils.IECFunctions;
import jkl.iec.tc.type.IECMap.IECType;
import jkl.iec.tc.type.IECTCItem;

public class DecodeTest {

	public DecodeTest() {
//		IECTCItem.P_SHORT = true;
		
		System.out.println("create TCItem");
		IECTCItem i = new IECTCItem(IECType.M_ME_NC,100);
		
		System.out.println("Max obj in item "+i.MaxObjects());
		i.setIOBCount(15);
		System.out.println("getStream");
				
		byte[] buf = i.getStream();

		System.out.println("Item_stream: "+IECFunctions.byteArrayToHexString(buf,0,buf.length));

		ArrayList<IECTCItem> itemarray = IECTCItem.Seperate(i);
		
		System.out.println("Item_items: "+itemarray.size());
		for (int x=0;x<itemarray.size();x++) {
			System.out.println(x+": "+itemarray.get(x).printStream());
		}
		
//		byte[] b = new byte[]{0,1,2,3,4};
//		IECTCItem item = new IECTCItem(buf,10);
		
	}

}
