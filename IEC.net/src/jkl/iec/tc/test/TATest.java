package jkl.iec.tc.test;

import jkl.iec.tc.type.IECTAItem;
import jkl.iec.tc.type.IECTCItem;
import jkl.iec.tc.type.IECMap.IECType;

public class TATest {
	
	public TATest() {
		System.out.println("create TCItem");
		IECTCItem.P_SHORT = true;
		
		IECTCItem i = new IECTCItem(IECType.M_ME_NC,566);
		i.iob(0).setIOB(55);
		i.iob(0).setValue(5555);
		IECTAItem ta = new IECTAItem(i);
		
		IECTAItem.setMask(new int[]{16,16,8});
				
		System.out.println("TCItem "+ta.getAdrString());
	}
	
}
