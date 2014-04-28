package jkl.iec.net.applications;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class Convbase {
	
	final String fids[] = {"measured_values.csv","signals.csv","set_points.csv","commands.csv","counter_values.csv"};
	File ff;
	Properties p;
	
	class IECServerProperties {
//		ITEM.PROPERTIES=NAME;TYPE;ASDU;COT;IOB;VALUE;SIMULATE;SIM.PROPERTY;SIM.VAL_INC
//		ITEM.PROPERTIES=NAME;TYPE;ASDU;COT;IOB;VALUE;MIN;MAX
	    int index;
	    String Name;
	    String type;
	    String ASDU;
	    int COT=3;
	    String IOB;
	    float Value = 0;
		float Min;
		float Max;
	    IECServerProperties(int index) {
	    	this.index = index;
	    }
	    
	    public String key() {
			return "ITEM"+index;
	    }
	    public String value() {
			return Name+";"+type+";"+ASDU+";"+IOB+";"+COT+";"+Min+";"+Max;
	    }
	    
	    public String toString() {
			return "ITEM"+index+"="+Name+";"+type+";"+ASDU+";"+IOB+";"+COT+";"+Min+";"+Max;
	    }
	}
	
//    String HEADER ="ITEM.PROPERTIES=NAME;TYPE;ASDU;COT;IOB;MIN;MAX";
    String HEADER ="ITEM.PROPERTIES=NAME;TYPE;ASDU;IOB;MIN;MAX";
	int ItemCount,LineNo =0;

	private String getFooter() {
		return "ITEMS.COUNT="+ItemCount;
	}
	
    String[] Headline;
	
	private void parseHeadLine(String line) {

		Headline = line.split(",");
	    for (int x=0; x < Headline.length; x++) {
	    	 String sts = Headline[x];
	    	 int pos =sts.indexOf("(");
	    	 String na = sts.substring(pos+1);
	    	 na = na.substring(0, na.length()-1);
	    	 Headline[x]=na;
//	    	 System.out.println(x+" "+na);
	     }
		System.out.println("HEAD_Length:"+Headline.length);
		LineNo ++;
	}
	
	private int getIndex(String e) {
	    for (int x=0; x < Headline.length; x++) {
	    	 if (Headline[x].equals(e)) {
	    		return x;	    	 }
	    }
	    return -1;
	}

	private float getMin(String[] entrys) {
		if (getIndex("ContainsMeasuredValueParam.PLL") !=-1) {
			return Float.parseFloat(entrys[getIndex("ContainsMeasuredValueParam.PLL")]);
		}
		return 0;
	}

	private float getMax(String[] entrys) {
		if (getIndex("ContainsMeasuredValueParam.PLH") !=-1) {
			return Float.parseFloat(entrys[getIndex("ContainsMeasuredValueParam.PLH")]);
		}
		return 0;
	}

	private String getAsdu(String[] entrys) {
		if (getIndex("ASDUA") !=-1) {
			return entrys[getIndex("ASDUA")];
		}
		return "1";
	}

	private String getIob(String[] entrys) {
		if (getIndex("IPA") !=-1) {
			return entrys[getIndex("IPA")];
		}
		return "0";
	}

	private String getName(String[] entrys) {
		if (getIndex("name") !=-1) {
			return entrys[getIndex("name")];
		}
		return null;
	}
	
	private String getType(String[] entrys) {
		String t;
		if (getIndex("MeasuredValueType") !=-1) {
			t = entrys[getIndex("MeasuredValueType")];
			if (t.equals("MWNORM")) return "9";
			if (t.equals("MWSCAL")) return "10";
			if (t.equals("MWIEEE")) return "11";
	}
		if (getIndex("SignalType") !=-1) {
			t = entrys[getIndex("SignalType")];
			if (t.equals("EML")) return "1";
			if (t.equals("DML")) return "3";
		}
		if (getIndex("SetPointType") !=-1) {
			t = entrys[getIndex("SetPointType")];
			if (t.equals("NORM")) return "48";
			if (t.equals("SCAL")) return "49";
			if (t.equals("IEEE")) return "50";
		}
		if (getIndex("CommandType") !=-1) {
			t = entrys[getIndex("CommandType")];
			if (t.equals("EBF")) return "45";
			if (t.equals("DBF")) return "46";
		}
		if (getIndex("IntegratedTotalType") !=-1) {
			t = entrys[getIndex("IntegratedTotalType")];
			if (t.equals("ZW")) return "15";
		}
		return "15"; //IntegratedTotalType
	}
	
	private IECServerProperties setIECData(String[] entrys) {
		IECServerProperties iecprop = new IECServerProperties(ItemCount);
		iecprop.Name = getName(entrys);
		iecprop.type = getType(entrys);
		iecprop.ASDU = getAsdu(entrys);
		iecprop.IOB = getIob(entrys);
//	    System.out.println("***iecprop.type:"+iecprop.type);

//	    iecprop.Min = getMin(entrys);
//		iecprop.Max = getMax(entrys);
		return iecprop;
	}
	
    private void parseLine(String line) {
         Boolean sstring = false;

         if (LineNo == 0) {
        	 parseHeadLine(line);
        	 return;
         }
         String n;
         if (line.contains("\",")) {
        	 n = line.substring(1,line.indexOf("\"",2));
             line = line.substring(n.length()); //cut the first , 
        	 sstring = true;
//        	 System.out.println("name with \" found "+n);
         } else {
        	 n = line.substring(0,line.indexOf(","));
//        	 System.out.println("name with found "+n);
         }
        String[] result = line.split(",");
        ItemCount++;
        if (sstring) result[0]=n;  // if name contains " then replace Wrong name
//     	for (int x=0; x < result.length; x++) { System.out.println(Headline[x]+"="+result[x]);   }
        IECServerProperties iecprop = setIECData(result);
   		p.setProperty(iecprop.key(),iecprop.value());
//	    System.out.println(iecprop.toString());
	}
	
    public void unrar(String s) {
		String exec ="unrar e -y "+s;
		System.out.println("exec:"+exec);
		Process process=null;
		try { process = Runtime.getRuntime().exec(exec);
		} catch (IOException e) { Server.log.severe(e.getMessage()); 	e.printStackTrace();}
		try { process.waitFor(); } catch (InterruptedException e) {e.printStackTrace();}
   }
    
	public Properties readFile(String f) {
	   p =new Properties();
	   FileInputStream fis = null;
	   ff = new File(f);
	
	   try {
		fis = new FileInputStream(f);
	} catch (FileNotFoundException e) {
		Server.log.severe(e.getMessage());
		e.printStackTrace();
	}
	  
	   BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line;
    	LineNo = 0;
    	ItemCount =0;
       try {
//   		System.out.println(HEADER);
//      		p.setProperty("ITEM.PROPERTIES","NAME;TYPE;ASDU;COT;IOB;MIN;MAX");
//  		p.setProperty("ITEM.PROPERTIES","NAME;TYPE;ASDU;IOB;MIN;MAX");
  		p.setProperty("ITEM.PROPERTIES","NAME;TYPE;ASDU;IOB");
		while((line = br.readLine()) != null) {
			parseLine(line);
		    LineNo ++;
		    }
   		p.setProperty("ITEMS.COUNT",String.valueOf(ItemCount));
	} catch (IOException e) {
		e.printStackTrace();
	}
      try {
		fis.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
      return p; 
   }
  
  public void delFiles() {
	  for (int x=0 ;x < fids.length; x++) {
		  ff = new File(fids[x]);
		  System.out.println("Delete "+ff.getAbsolutePath());
		  if(ff.delete()){
				System.out.println(ff.getName() + " is deleted!");
			}else{
				System.out.println(x+" Delete operation is failed.");
			}
	  }
  }
	
}
