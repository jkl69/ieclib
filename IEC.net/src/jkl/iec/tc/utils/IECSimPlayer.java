package jkl.iec.tc.utils;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import jkl.iec.tc.type.IECList;
import jkl.iec.tc.type.IECMap;
import jkl.iec.tc.type.IECTCItem;
import jkl.iec.tc.type.IECMap.IECType;

public class IECSimPlayer {
	
	public final static Logger log = Logger.getLogger(IECSimPlayer.class.getName()); 

	private Timer timer = null;
	private BufferedReader br;
	private FileReader fr ;
	private String itemname = "";	
	private IECList ieclist =null; 
	int lineNo=0;
	
	class player extends TimerTask {
//		Properties p;
		private int newTime =2000; 
		private Boolean PlayFileEnd = false;
		private int ASDU = 0;
		private int IOB = 0;
		private IECType iectype = null;
		private int qu = 0;
		private Float Value = null;
		private Float inc = null;
		
		public void run() {
			System.out.println("Player Create ");
//			p = new Properties();
			doline();
			while(!PlayFileEnd) {
//				System.out.println("Player SLEEP("+newTime+")");
				try {
					Thread.sleep(newTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				doline();
			} 
//			System.out.println("LAST LINE -> EXIT");
			try {
				if (fr!=null) {fr.close();}
				log.fine("Player destroyed ");	
			} catch (IOException e) {
				log.severe("Player destroy ERROR: "+e);	
				System.out.println("Player destroy ERROR: "+e);	
			} 
	    	timer.cancel();
			}
	    
// EXAMPLE of an IECSimFile Line
//
//" item=/9/1/4097;value=1 "
//		
// only created items will be Simulated
		
  private boolean isValidLine(String txt) {
	String[] param;
	String[] keyval;
	String Stream = null;
	
	newTime =1000;
	ASDU = 0;
	IOB = 0;
	iectype = null;
	qu = 0;
	Value = (float) 0;
	inc = (float) 0;
			
//	System.out.println("ValidateLine_"+txt);
	if (txt == null) {
		log.info("PLAYER EOF:");
    	PlayFileEnd = true;
		}
				
	if(txt != null) {
		param =txt.split(";");
//		System.out.println("LINE_parms:"+param.length);
		itemname="";
		for (int i =0; i < param.length ; i++){
			keyval = param[i].split("=");
			log.finer("Found_param:"+keyval[0]);
		    if (keyval[0].equals("sleep")) {
			    	newTime =Integer.parseInt(keyval[1]);
		    }
		    if (keyval[0].equals("value")) {
			    	Value =Float.parseFloat(keyval[1]);
		    }
		    if (keyval[0].equals("inc")) {
			    	inc =Float.parseFloat(keyval[1]);
		    }
		    if (keyval[0].equals("QU")) {
			    	qu =Integer.parseInt(keyval[1]);
		    }
		    if (keyval[0].equals("item")) {
		    	Stream = keyval[1];
//				System.out.println("KEYVal:"+keyval[1]); 	
		    	keyval = Stream.split("/");
		    	if (keyval.length==1) {  //item Name 
		    		itemname = keyval[0];
//					System.out.println("Isitemname:"); 	
		    	} else {
//					System.out.println("IsItemAdr:"); 	
					iectype = IECMap.getType((Byte.parseByte(keyval[1])));
			    	ASDU = Integer.parseInt(keyval[2]);
			    	IOB = Integer.parseInt(keyval[3]);
		    	}
		    }
		 }
	   if (itemname.isEmpty()) {
//		   System.out.println("Validate : Type "+iectype+" ASDU "+ASDU+" IOB "+IOB+ "  Val "+Value+"  QU "+qu+" Player SLEEP("+newTime+")"); 	
		   return ((iectype != null)&&(ASDU != 0)&&(IOB !=0));
	   } 
//       System.out.println("Validate: itemname "+itemname); 	
	   return true;
	}
return false;	
 }

		
   private void doline() {
		String txt = "";
		IECTCItem item =null;
		try {
			while (!isValidLine(txt)&&(PlayFileEnd == false)) {
					txt = br.readLine();
					log.fine("LINE_"+lineNo+ ": "+txt);
					lineNo++;
			}
			if (PlayFileEnd == false) {
				if (ieclist !=null) {
					if (itemname.isEmpty()) {
						log.info("Player: Type="+iectype+" ASDU="+ASDU+" IOB="+IOB+"  inc="+inc+"  Val="+Value+"  QU="+qu+" SLEEP="+newTime); 	
//						System.out.println("Player: Type="+iectype+" ASDU="+ASDU+" IOB="+IOB+"  inc="+inc+"  Val="+Value+"  QU="+qu+" SLEEP="+newTime); 	
						item = ieclist.getIECStream(iectype, ASDU, IOB);
					} else {
						log.info("Player: Itemname="+itemname+"  inc="+inc+"  Val="+Value+"  QU="+qu+" SLEEP="+newTime); 	
					    item = ieclist.getIECStream(itemname);
					}
					if (item !=null) {
						System.out.println("Type in LIST --> SIMULATE");
						if (inc != 0) {
				    		if (item.iob(0).getValue()+inc > item.iob(0).getMAX_VALUE()) {
								    item.iob(0).setValue(item.iob(0).getMIN_VALUE());
				    		  } else {
								    item.iob(0).setValue(item.iob(0).getValue()+inc);
					    	  }
							} else {
								item.iob(0).setValue(Value);
							}
							item.iob(0).setQU((byte) qu);
//							item.iob(0).s
						}
					}
    			}
			} catch (IOException e) {
				System.out.println(e);
			}
	    }
	}
	
	public boolean play(String filename){
		try {
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
//			System.out.println("Player Create ");
			timer = new Timer();
			timer.schedule(new player(), 10);
			return  true;
		} catch (FileNotFoundException e) {
			System.out.println("Player Not Create : ERROR "+e);
			return  false;
		}
  }

  public boolean play(StringReader sr){
		fr=null;
		this.br = new BufferedReader(sr);
//		System.out.println("Player Create ");
		timer = new Timer();
		timer.schedule(new player(), 10);
		return  true;
  }

	public IECList getIeclist() {
		return ieclist;
	}

	public void setIeclist(IECList ieclist) {
		this.ieclist = ieclist;
	}
}