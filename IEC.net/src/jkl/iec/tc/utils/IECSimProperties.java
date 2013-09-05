package jkl.iec.tc.utils;


import java.util.Properties;

import jkl.iec.tc.type.IECMap;
import jkl.iec.tc.type.IECTCItem;
import jkl.iec.tc.type.IECMap.IECType;

public class IECSimProperties  {
//public class IECItemSimProperties extends Properties {
  public long NextSimTime;
  private String TimeString="";
  private String BackString="";
  int TimeInc;
  int TimeInc2=-1;
  public String SimStr;
  private double Valinc;
  @SuppressWarnings("unused")
  private double Valinc2=-1;
  public IECType backType;
  public int backASDU;
  public int backIOB;
  public IECTCItem item;
  IECType old_type =null;
  public String lastErrorStr;	
  
  public static String[] getPropNames() {
	  String[] result =new String[2];
	  result[0] ="SIM.PROPERTY";
	  result[1] ="SIM.VAL_INC";
	  return result;
  }
  
  public Properties getProperties() {
		Properties p = new Properties();
		String pre = "ITEM"+item.ID+"."+"SIM.";
		if (IECMap.IEC_M_Type.contains(item.getType())) {
			  p.setProperty(pre+"PROPERTY",TimeString);
		  } else {
			  p.setProperty(pre+"PROPERTY",BackString);			  
		  }
		p.setProperty(pre+"VAL_INC",String.valueOf(Valinc));
		return p;
	}
  
  public String getPString() {
		String s = "";
		if (IECMap.IEC_M_Type.contains(item.getType())) {
			  s=s+TimeString;
		  } else {
			  s=s+BackString;			  
		  }
		s=s+";"+String.valueOf(Valinc);
		return s;
	}
  
  public void setProperties(Properties p) {
	  String pre = "ITEM"+item.ID+"."+"SIM.";
	  setValinc(Double.parseDouble(p.getProperty(pre+"VAL_INC",String.valueOf(Valinc))));
	  if (IECMap.IEC_M_Type.contains(item.getType())) {
		  System.out.print("setSimProperties "+pre +"Value "+Valinc+"  TIMER__:"+TimeString);
		  if (setTimerString(p.getProperty(pre+"PROPERTY",TimeString),true)) {
			  System.out.println(" OK"); 
		  } else System.out.println(lastErrorStr);
	  } else {
		  System.out.print("SET SIM_BACKSTRING: "+p.getProperty(pre+"PROPERTY","??")+" value "+Valinc);
		  if (setBackString(p.getProperty(pre+"PROPERTY",BackString),true)) {
			  System.out.println(" OK"); 
		  } else System.out.println(lastErrorStr);
	  }
	  old_type= item.getType();
	}	
  
  public IECSimProperties(IECTCItem o) {
	  item = o;
	  setDefProps();
	  }

  public void setDefProps() {
	System.out.println("set Default sim props");
	switch (item.getType()) {
	case M_SP_NA : case M_SP_TB :
	case M_DP_NA : case M_DP_TB :{
		  setValinc(1);
		  setTimerString("+10",true);
		  break;
	  }
	  case M_ME_NA : case M_ME_TB : case M_ME_NB : case M_ME_TD :{
		  setValinc(10);
		  setTimerString("+4",true);
		  break;
	  }
	  case M_ME_NC : case M_ME_TF : {
		  setValinc(11.1);
		  setTimerString("4-10",true);
		  break;
	  }
	  case M_IT_NA : case M_IT_TB : {
		  setValinc(11);
		  setTimerString("4",true);
		  break;
	  }
	  default :
		  setValinc(1);
		  setTimerString("",true);
	  }
	  old_type= item.getType();
  }
  public boolean isBackString(String txt) {
	  return setBackString(txt,false);
  }
  public boolean setBackString(String txt,boolean write) {
//		String[] s =txt.split("\\\\");
		int tmpasdu;
		int tmpiob;
		IECType tmptype;
		
		lastErrorStr ="unknown";
		txt =txt.replaceAll(" ","");
		if (txt.isEmpty()) {
			lastErrorStr ="Empty";
			return false;
		}
		if (!txt.contains("/")) {
			lastErrorStr ="seperator '/' not found";
			return false;
		}
		String[] s =txt.split("/");
//		System.out.println("Elements "+s.length);
		if (s.length>3) {
			try {
				if (s[1].equals("")) {
					tmpasdu = item.getASDU();	
				} else {
					tmpasdu = Integer.parseInt(s[1]);
				}
				if (s[2].equals("")) {
					lastErrorStr ="missing Type (pos.2)";
					return false;
				}
				tmptype = IECMap.getType(Byte.parseByte(s[2]));
				if (tmptype== null) {
					lastErrorStr ="invalid Type (pos.2)";
					return false;
				}
				tmpiob = Integer.parseInt(s[3]);
				if (write) {
					backType =tmptype;
					backASDU =tmpasdu;
					backIOB = tmpiob;
					System.out.println("set back ASDU:"+backASDU+"  Type:"+backType+"  IOB:"+backIOB);
					BackString = txt;
				}
				return true;
			} catch (NumberFormatException e) {

				lastErrorStr ="invalid Number for ASDU(pos.1) or IOB(pos.3)";
				System.out.println(e);			
			}
		} else {
			lastErrorStr ="to less parameters !";
			return false;			
		}
		return false;
  }
 
  public String getBackString() {
//	  String d1 = BackString.replace("\\", "\\\\\\"); 
      System.out.println("getbackString :"+ BackString);
	  return BackString;
  	}  
  
  public boolean isTimerString(String txt) {
//	System.out.println("isTimerStr ? "+txt);
	 return setTimerString(txt,false);
  }
  
  public boolean setTimerString(String txt,boolean write) {
//	System.out.println("set TimerStr _"+txt+"_");	
	int tmp1;
	int tmp2;
	lastErrorStr ="unknown";
	txt =txt.replaceAll(" ","");
	if (txt.isEmpty()) {
		lastErrorStr ="Empty";
		return false;
	}
	// check for time cycle e.g. "+27"  
	if (txt.startsWith("+")) {
		  try {
			  tmp1 = Integer.parseInt(txt.substring(1,txt.length()));
			  if (write) {
				  TimeString =txt;
				  TimeInc = tmp1;
				  TimeInc2 = -1;
			  }
			  return true;
		  } catch (NumberFormatException e) {
			  lastErrorStr ="invalid cycle";
			  System.out.println("cycle "+e);
			  return false;
		  }
	  }
	// check for time interval e.g. "5-10"  
	if (txt.contains("-")) {
		String[] s =txt.split("-");
        System.out.println("TimerStr split length:"+s.length);	
		if (s.length == 2) {
			  try {
//				  System.out.println("TimerStr length 2");	
				  if (!s[0].isEmpty()) {
					  tmp1 = Integer.parseInt(s[0]);
				  } else {
					  lastErrorStr ="invalid interval (missing from)";
					  return false;
				  }
				  if (!s[1].isEmpty()) {
					  tmp2 = Integer.parseInt(s[1]);
				  } else {
					  lastErrorStr ="invalid interval (missing till)";
					  return false;
				  }
				  System.out.println("TimerStr length 2"+tmp2);	
				  if (tmp2 <= tmp1) {
					  lastErrorStr ="invalid interval (from equal,bigger as till)";
					  return false;
				  }
				  if (write) {
					  TimeString =txt;
					  TimeInc = tmp1;
					  TimeInc2 = tmp2;
				  }
				  return true;
			  } catch (NumberFormatException e) {
				  	lastErrorStr ="invalid interval (format)";
					System.out.println("intercval "+e);
//					return false;
			  }		
		} else {
		  	lastErrorStr ="invalid interval (missing times)";
		  	return false;
		}
	} else {
		// check for fix second e.g. "45"  
		  try {
			  tmp1 = Integer.parseInt(txt);
			  if (tmp1>59) {
				  	lastErrorStr ="invalid fix second (second to big)";
				  	return false;
			  }
			  if (write) {
				  TimeString =txt;
				  TimeInc = tmp1*-1;
				  TimeInc2 = -1;
			  }
			  return true;
		  } catch (NumberFormatException e) {
			  	lastErrorStr ="invalid fix second (format)";
				System.out.println("fix " +e);			
		  }		
		
	}
//	  System.out.println("set TimerStr ERROR");	
	  return false;
	}
  

  public String getTimerString() {
	return TimeString;
}

  public double getValinc() {
	return Valinc;
}

  public void setValinc(double valinc) {
	if (valinc > item.iob(0).getMAX_VALUE()) {
		valinc = item.iob(0).getMAX_VALUE();
	}
	if (valinc < item.iob(0).getMIN_VALUE()) {
		switch (item.getType()) {
		case M_SP_NA: case M_SP_TB : 
		case M_DP_NA: case M_DP_TB : 
		case C_SC_NA : System.out.print("BOOL INC now: "+valinc);break; 
		default : valinc = -1;System.out.print("INC now: "+valinc);
		}
	}
	Valinc = valinc;
}

}
