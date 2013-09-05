package jkl.iec.tc.type;

import java.util.logging.Level;
import java.util.logging.Logger;

public class IECTAItem {
    IECTCItem item =null;
    private static int[] mask =new int[20];
    long[] path =new long[20];
    
	public final static Logger log = Logger.getLogger(IECTAItem.class .getName()); 
	
	public IECTAItem(IECTCItem i) {
    	this.item = i;
    	setDefaultMask();
//    	log.info(String.valueOf((long)getAdr()));
    	log.info(String.valueOf(getAdr().longValue()));
    }
	
    private void setDefaultMask() {
        mask[0] = 8;	
        mask[1] = 8;	
        mask[2] = 8;	
        mask[3] = 8;	
        mask[4] = 8;	
        mask[5] = -1;	
    }
    
    public static boolean setMask(int[] m) {
		int pos=0;
		int x;
//		int maxpos= 40;
		int maxpos =40;
		if (IECTCItem.P_SHORT) {
			maxpos= 36;
		}
		for (x=0; x<m.length;x++) {
			mask[x] =m[x];
			pos=pos+ m[x];
			if (pos > maxpos) {
				mask[x] = pos -maxpos;
				mask[x+1]=-1;
				log.log(Level.WARNING,"mask:{0} {1} {2} {3}",new Object[]{mask[0],mask[1],mask[2],mask[3]});
				return false;
			}
		}
		mask[x] =-1;
		log.log(Level.INFO,"mask:{0} {1} {2} {3}",new Object[]{mask[0],mask[1],mask[2],mask[3]});
    	return true;
     }
    
    private void getPath() {
		long l = getAdr();
		int pos= 40;
		int x= 0;
		long m;
		while (mask[x]>0){
			m = (long) Math.pow(2,mask[x])-1;
			pos = pos - mask[x];
			m =  m  << pos;
	    	log.fine("mask $"+Long.toHexString(m));
			path[x] = (l & m) >> pos;
	    	log.fine("$"+Long.toHexString(path[x]));
	    	x++;
		}
    }
    
    public String getAdrString() {
		String result = "";
		getPath();
		int x=0;
		while (mask[x]>0){
			result = result + path[x];
			x++;
			if (mask[x]>0) result=result+".";
		}
    	log.fine("Path :"+result);
    	return result;
     }
    
    public Long getAdr() {
		Long result =new Long((long)(item.getASDU() * 16777216l)+ item.iob(0).getIOB());
		if (IECTCItem.P_SHORT) {
			result = (long)(item.getASDU() * 65536l)+ item.iob(0).getIOB();
		}
//		Long result3 =new Long( 16777216 );
    	log.fine("$"+Long.toHexString(result));
    	return result;
	
    }
}
