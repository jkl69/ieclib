package jkl.iec.tc.utils;


import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import jkl.iec.tc.type.IECList;
import jkl.iec.tc.type.IECMap;
import jkl.iec.tc.type.IECTCItem;

@SuppressWarnings("serial")
public class IECSimulator extends ArrayList<IECTCItem>{

	IECSimProperties simprop=null;
	IECList ieclist=null;
	public Boolean enabled = true;
	
	class irq extends TimerTask {
	    public void run() {
	    	if (enabled) {
	    		IECList.log.finest("SIMULATOR_IRQ");
//			    System.out.println("IRQ");	
			    for (int it=0;it<size();it++) {
			    	simprop = (IECSimProperties) get(it).data;
			    	// on type change set back to defaults.
			    	if (simprop.old_type != get(it).getType()) {
			    		simprop.setDefProps();
//			    		System.out.println("TimerStr = "+simprop.getTimerString());	
			    	}
			    	// if simulation enabled execute SimItem(()
			    	if (get(it).flag1) {
			    		simItem(get(it));
			    	}
			    }

	    	}  // end if;
	    }
	}
	    
	private void sim_M_Item(IECTCItem item) {
	    if (simprop.NextSimTime <= new Date().getTime()) {
    		System.out.print("SIMUL: "+ item.Name+"  INC:"+ simprop.getValinc()+" " );
    		if (!item.iob(0).setValue(item.iob(0).getValue()+simprop.getValinc())) {
	    		System.out.print("New Item val > MAX_VAL: ");	
	    		simprop.setValinc(-1 * simprop.getValinc());
//		    		System.out.print("New INC: "+simprop.getValinc() + " ");	
    			item.iob(0).setValue(item.iob(0).getValue()+simprop.getValinc());
    		}
    		System.out.print("New Item val: "+item.iob(0).getValue() + " ");	
    		calcNextSimTime();
    		}
    	}

    private void simItem(IECTCItem item) {
      	 if (simprop != null) {
      		if (IECMap.IEC_M_Type.contains(item.getType())) {
      			sim_M_Item(item);
      		}
   	    	if (IECMap.IEC_C_Type.contains(item.getType())) {
      			sim_C_Item(item);
      		}
      	 }
      }
   	    
    private void sim_C_Item(IECTCItem item) {
//    	System.out.println("**** C_SIM ****");
    			//"time"+item.iob(0).getTime()+"  time_RX"+item.iob(0).Time_RX);	
   	   	String backfile;
   	   	if (item.iob(0).getTime() != item.iob(0).Time_RX) {   //
   	    	if (simprop.isBackFile) {
				backfile =simprop.getBackString();
				IECSimPlayer player = new IECSimPlayer();
				player.setIeclist(ieclist);
				if (! player.play(backfile.substring(1, backfile.length()))) {
//					"D:\\workspace\\IEC.net\\src\\iec\\tc\\utils\\sim.txt")) {
			    	System.out.println("MAIN_Player INIT ERROR !");
			    }
			    item.iob(0).Time_RX = item.iob(0).getTime();

/*
					FileReader fileReader = null;
				}
				try {
					backfile =simprop.getBackString();
					String bf = backfile.substring(1, backfile.length());
//					System.out.println("backfile:"+backfile.substring(1, backfile.length()));
					System.out.println("backfile:"+bf);
					fileReader = new FileReader(new File(bf));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				    item.iob(0).Time_RX = item.iob(0).getTime();
					return;
				}
    	    	 BufferedReader br = new BufferedReader(fileReader);
    	    	 String line = null;
    	    	 try {
					while ((line = br.readLine()) != null) {
    			    	System.out.println("**** C_SIM ****"+line);
    			    	simprop.setBackString(line,true);
    		   	    	sim_C_item_checkback(item);
	        	    }
				} catch (IOException e) {
					e.printStackTrace();
				    item.iob(0).Time_RX = item.iob(0).getTime();
					return;
				}
    	     simprop.setBackString(backfile,false);
*/
    	    } 
   	    	else   	sim_C_item_checkback(item);
   	  	}
   	}

    private void sim_C_item_checkback(IECTCItem item) {
	    	IECTCItem simitem =ieclist.getIECStream(simprop.backType,simprop.backASDU,simprop.backIOB);
   	    	String tr = "Simul  reaction trigger "+item.Name+" Search item Type:"+simprop.backType+"  asdu:"+simprop.backASDU+" IOB:"+simprop.backIOB;
	    	if (simitem != null) {
	   	    	tr += " --> FOUND";
	    		if (simprop.getValinc()==0) {
	    			simitem.iob(0).setValue(item.iob(0).getValue());	
	    		} 
	    		if ((simprop.getValinc()!=0)& (!simitem.iob(0).setValue(simitem.iob(0).getValue()+simprop.getValinc()))) {
	    			simprop.setValinc(-1 * simprop.getValinc());
			    	simitem.iob(0).setValue(simitem.iob(0).getValue()+simprop.getValinc());
			    	}
			    }
			    System.out.println(tr);
			    item.iob(0).Time_RX = item.iob(0).getTime();
    }
    
    @SuppressWarnings("deprecation")
	private void calcNextSimTime() {
    	Date now =new Date();
    	System.out.println("calcNextSimTime:"+simprop.getTimerString()+" inc:"+simprop.TimeInc);
    	if (simprop.TimeInc < 0) {
       		System.out.println("Once a min at sec: "+ simprop.TimeInc *-1);
   			now.setMinutes(now.getMinutes()+1);
   			now.setSeconds(simprop.TimeInc*-1);
       		simprop.NextSimTime = now.getTime();
   			return;
   		}
   		if (simprop.TimeInc2 == -1) {
       		System.out.println("New Time sec Inc: "+ simprop.TimeInc*1000);
   			simprop.NextSimTime = now.getTime() + simprop.TimeInc*1000;
   			return;
    		}
   		int r= (int)((Math.random()*(simprop.TimeInc2-simprop.TimeInc))+simprop.TimeInc)*1000;
   		System.out.println("New Timer Random Inc: "+ r);
   		simprop.NextSimTime = now.getTime() + r;
	    }

	private Timer timer = new Timer();
		 
	public IECSimulator(IECList l) {
		this.ieclist = l; 
		timer.scheduleAtFixedRate(new irq(), 1000,500);
		 System.out.println("Simulator Create ");	
	 }
}
