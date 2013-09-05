package jkl.iec.tc.type;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import jkl.iec.tc.type.IECMap.IECType;
import jkl.iec.tc.utils.IECSimProperties;
import jkl.iec.tc.utils.IECSimulator;

@SuppressWarnings("serial")
public class IECList extends ArrayList<IECTCItem> {
	
	private static int counter;
	private static boolean SimEnabled =false;
	public final static Logger log = Logger.getLogger(IECList.class .getName()); 

	public String name;
	
	public IECType iectype =null;
	public boolean AutoItemCreate = true;
	private int old_size;
	private int NextIOB=1;
		
	public IECSimulator iecsimulator =null;
	
	private IIECTCActionListener iectcactionlistener = null;
	
	class irq extends TimerTask {
	    @SuppressWarnings("deprecation")
		public void run() {
	    Date now =new Date();
	    byte[] buf =new byte[32];
	    boolean send;
//	    System.out.println("IRQ");	
	    for (int it=0;it<size();it++) {
//	    	System.out.println("Check "+get(it).getType());
	    	send=false;
	    	if (IECMap.IEC_M_Type.contains(get(it).getType())) {
	    		if((get(it).getType()!=IECMap.IECType.M_IT_NA)&&(get(it).getType()!=IECMap.IECType.M_IT_TB)) {
			    	boolean Valuechange = get(it).iob(0).getValue() != get(it).iob(0).VALUE_TX;
		    	    boolean QUchange = get(it).iob(0).getQU() != get(it).iob(0).QU_TX;
	    			if (Valuechange | QUchange) {  // send an M_Type on change
	    				send =true;
	    			}
	    		} else {  // send an M_Type Counter only once a minute
//	    	    	System.out.print("Counter to send ? last send min:"+get(it).iob(0).Time_TX.getMinutes());
	    	    	if (now.getMinutes()!=get(it).iob(0).Time_TX.getMinutes()) {
		    	    	send = true;
		    	    }
	    		}
	    	
	    	    if (iectcactionlistener !=null) {
    				if (send) {
   		    		buf = get(it).getStream();
    	    		iectcactionlistener.sendByteArray(buf,buf.length);	    		
    	    	    get(it).iob(0).VALUE_TX = get(it).iob(0).getValue();
    	    	    get(it).iob(0).QU_TX = get(it).iob(0).getQU();
    	    	    get(it).iob(0).Time_TX = now;
    				}
    	    		iectcactionlistener.doUpdate(it);
	    	    }
	    	}
		}
	    
	    if (old_size != size()) {
	    	old_size=size();
	    }
	    }
	}
	    
	 private Timer timer = new Timer();
	 
	 public IECList() {
		 log.finest("");
		 name="IECList_"+String.valueOf(counter++);
		 old_size=size();
		 timer.scheduleAtFixedRate(new irq(), 1000,500);
	 }
	 
	 public void setSimEnabled(boolean b) {
		 if (b && iecsimulator==null) {
			 iecsimulator = new IECSimulator(this);   
		 }
		 SimEnabled =b;
	 }
	 
	 public void setIECTCActionListener(IIECTCActionListener l) {
		 iectcactionlistener = l;
	 }
	 
	 public String toString() {
		return name;
	 }
	 
	 /**
	  * 
	  * @return 
	  */
	 public boolean add() {
		 IECTCItem item =new IECTCItem();
		 item.Name = "Item"+size();
		 if (iectype!= null) {
			 item.setType(iectype);
		 }
		 if ((iectype == IECType.C_IC_NA) |
		 	(iectype == IECType.C_CI_NA) |
		 	(iectype == IECType.C_CS_NA)){
			 item.iob(0).setIOB(0);
		 } else {
			 item.iob(0).setIOB(NextIOB);
			 NextIOB++;
		 }
		 return add(item);
	 }
	 
     public boolean	add(IECTCItem o) {
 	    boolean result=super.add(o);
 	    if (SimEnabled) {
 	    	iecsimulator.add(o);
 	    	o.data = new IECSimProperties(o);
 	    }
 	    log.fine("Create Items size:"+size());
   	 	o.ID = size();
 	    return result;    	 
     }

     public IECTCItem remove(int index) {
    	 System.out.println("Delete Item "+index);
  	    if (SimEnabled) {
  	    	System.out.println("Delete Item from simulator index"+iecsimulator.indexOf(get(index)));
  	    	iecsimulator.remove(iecsimulator.indexOf(get(index)));
 	  	    get(index).data = null;
 	    }
    	 get(index).setIOBCount(0);
    	 return super.remove(index);
     }
     
     public void clear() {
    	 int c =size();
    	 for (int it=0;it<c;it++) {
    		 remove(0);
    	 }
     }
    	 
     private void doICType(IECTCItem i,Set<IECType> set) {
    	int hit =0;
    	log.fine("IECTypes "+set);
    	for (int it=0 ; it<size(); it++) {
			if ((set.contains(get(it).getType())) &&
				( get(it).getASDU() == i.getASDU())) {
//				System.out.println("GS ITEM Found!");
				hit++;
				if (hit>1) {
					i.addIOB();
				}
				log.finest("Item found add as Obj. No:"+String.valueOf(hit)); 
				i.iob(hit-1).setIOB(get(it).iob(0).getIOB());
				i.iob(hit-1).setValue(get(it).iob(0).getValue());
				i.iob(hit-1).setQU(get(it).iob(0).getQU());
				i.iob(hit-1).setSEQ(get(it).iob(0).getSEQ());
			}
		}
		log.fine("Items in GS answer "+String.valueOf(hit)); 
		//send only if items in stream (hit >0)
		if (hit >0 ) {
			sendItem(i);
		}
     }

     private void doCI(IECTCItem gs) {
    	log.info("General Scan Counter started!");
//    	System.out.println("General Scan started!"); 
 		Set<IECType> set;
 		IECTCItem i;
 		i = new IECTCItem();
 		i.Name ="GS_IT_DUMMY";
		i.setType(IECType.M_IT_NA);
	    i.setCOT((int) gs.iob(0).getValue());
	    i.setASDU(gs.getASDU());
	    i.setIOBCount(1);
	    set = EnumSet.of(IECType.M_IT_NA,IECType.M_IT_TB);
		
	    doICType(i,set); 
		
	    gs.setCOT(10);
     	log.fine("General Scan regular end!");
		sendItem(i); 
	 }
     
     private void doIC(IECTCItem gs) {
//System.out.println("General Scan started!"); 
		Set<IECType> set;
		IECTCItem i= new IECTCItem();
	    i.setCOT((int) gs.iob(0).getValue());
	    i.setASDU(gs.getASDU());
     	log.info("General Scan regular started!  COT/ASDU "+i.getCOT()+"/"+i.getASDU());

	    i.Name ="GS_SP_DUMMY";
	    i.setType(IECType.M_SP_NA);
		set = EnumSet.of(IECType.M_SP_NA,IECType.M_SP_TB);
		doICType(i,set);

		i.Name ="GS_DP_DUMMY";
	    i.setType(IECType.M_DP_NA);
		set = EnumSet.of(IECType.M_DP_NA,IECType.M_DP_TB);
		doICType(i,set);

		i.Name ="GS_MEn_DUMMY";
	    i.setType(IECType.M_ME_NA);
	    i.setIOBCount(1);
	    set = EnumSet.of(IECType.M_ME_NA,IECType.M_ME_TB);
		doICType(i,set);

		i.Name ="GS_MEs_DUMMY";
		i.setType(IECType.M_ME_NB);
	    i.setIOBCount(1);
	    set = EnumSet.of(IECType.M_ME_NB,IECType.M_ME_TD);
		doICType(i,set);

		i.Name ="GS_MEiee_DUMMY";
		i.setType(IECType.M_ME_NC);
	    i.setIOBCount(1);
	    set = EnumSet.of(IECType.M_ME_NC,IECType.M_ME_TF);
		doICType(i,set);
		
		gs.setCOT(10);
     	log.fine("General Scan regular end!");
		sendItem(gs); 
		
     }
     
     public IECTCItem getIECStream(IECType t,int asdu,int iob) {
    	 log.fine("Search Item "+t+"_"+asdu+"_"+iob);
       	 for (int it=0;it<size();it++) {
        		 if ((get(it).getType() == t) &&
        				 (get(it).getASDU()== asdu) &&
        				 (get(it).iob(0).getIOB() == iob)) {
        			 return get(it);
        		 }
    	 }
    	 return null;
     }
     
     public boolean containsITem(IECTCItem i){
    	 return (getIECStream(i.getType(),i.getASDU(),i.iob(0).getIOB()) != null);
     }
     
     /**
      * Copy IEC Object properties Value, QU , Time 
      * @param o1 = source Object
      * @param o2 = destination Object
      */
     private void copyIOB(IECTCObject o1,IECTCObject o2) {
   		 o2.setValue(o1.getValue());
   		 o2.setQU(o1.getQU());
		 o2.setTime(o1.getTime());
     }
     
     public void activateItemRecived(IECTCItem receivedItem) {
 		IECTCItem listitem = getIECStream(receivedItem.getType(),receivedItem.getASDU(),receivedItem.iob(0).getIOB());
 		if (listitem != null) {
 			log.info("ITEM in List --> actcon");
 			copyIOB(receivedItem.iob(0),listitem.iob(0));
 		    if (iectcactionlistener !=null) {
 	 			log.finer("UPDATE ITEM Index "+indexOf(listitem));
 	 			iectcactionlistener.doUpdate(indexOf(listitem));	    		
 		    }
 			receivedItem.setCOT(7);
 			sendItem(receivedItem);
	    	
	    	if (receivedItem.getType()==IECType.C_IC_NA) {
				doIC(receivedItem);
			}
	    	if (receivedItem.getType()==IECType.C_CI_NA) {
				doCI(receivedItem);
			}
	    	if (receivedItem.iob(0).ValueParam != 0) {  //used to send actend
 	 			System.out.println("ValueParam != 0");	    		
 		    	receivedItem.setCOT(0x0a);
 	 			sendItem(receivedItem);
	    	} else {
 	 			System.out.println("ValueParam == 0");	    		
	    	}
		} else {  //Item not in List
			log.warning("ITEM NOT IN LIST --> nactcon");
			receivedItem.setCOT(0x47);
 			sendItem(receivedItem);
	    	}
     }
     
     /**
      *  sends a Byte Stream with the length: l  to the listener
      */	    
     public void sendByteArray(byte[] buf,int l) {
    	 if (iectcactionlistener !=null) {
    		 	log.finest(" length: "+l);
        		iectcactionlistener.sendByteArray(buf, l);	    		
//    		iectcactionlistener.doUpdate(it);
    	}
     }
	    
     public void getByteArray(byte[] b,int length) {
    	 log.finest(" ["+length+"]");
    	 IECTCItem i = new IECTCItem(b, length);
    	 i.Name ="DUMMY";
    	 if (i.getType()==null) {
    		 log.warning("received unsupported Type!: ");
//    		 System.out.println("received unsupported Type!: ");
//     	  	 sendByteArray(b, length);	    		
    		 return;
    	 }
//		System.out.println("RX_ITEM_Type: "+i.getType());
		if (i.getCOT()==6) {
    		activateItemRecived(i);
    		return;
    	 }
		 System.out.println("COT NOT 6 nothing to do! ");
		 i.setCOT(0x45);
		 sendItem(i);
//		 byte[] buf = i.getStream();
// 	  	 sendByteArray(buf, buf.length);	    		
	 }
     
   /**
    *  Creates a Stream of the Item <br>
    *  and send to the listener
    */
   public void sendItem(IECTCItem i) {
	   byte[] buf = i.getStream();
  	   sendByteArray(buf, buf.length);	
	}

	public static String[] getPropNames() {
 		 String[] result = new String[IECTCItem.getPropNames().length +
 		                              IECSimProperties.getPropNames().length];
 		 System.arraycopy(IECTCItem.getPropNames(),0,result,0,IECTCItem.getPropNames().length);
 		if (SimEnabled) {
 			System.arraycopy(IECSimProperties.getPropNames(),0,result,IECTCItem.getPropNames().length,IECSimProperties.getPropNames().length);
 		}
 		 return result;
 	 }
 	 
    /**
     * return the Properties of all Items in the list 
     */
	public Properties getItemProperties() {
		 Properties p =new Properties();
		 p.setProperty("ITEMS_",String.valueOf(size()));
		 for (int it=0;it< size();it++) {
    		 p.putAll(get(it).getProperties());
    	 }
//    	 p.list(System.out);   
    	 return p;
     }
    
     public void setItemProperties(Properties p,double version) {
//    	 p.list(System.out); 
    	 int idoffset = size();
    	 int c =0;
    	 if (version==1) {
    		 c = Integer.parseInt(p.getProperty("ITEMS_"))+idoffset;
    	 } 
    	 if (version==2) {
    		 c = Integer.parseInt(p.getProperty("ITEMS.COUNT","0"))+idoffset;
    	 } 
    	 log.fine("Create "+c+" Items from "+idoffset);
    	 for (int i=idoffset;i<c;i++) {
    		 add();  //add an new IECStream 
    		 get(i).setProperties(p,idoffset);
    	 }
     }
     
}
