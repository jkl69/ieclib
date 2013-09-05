package jkl.iec.tc.type;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jkl.iec.net.utils.IECFunctions;
import jkl.iec.tc.type.IECMap.IECType;
import jkl.iec.tc.utils.IECSimProperties;

/**
 * DOS not support Block packet TC Streams 
 * 
 * @author jaen
 *
 */

public class IECTCItem {
	
//	public static boolean P_SHORT = true; 
	public static boolean P_SHORT =false; 
	public static boolean Respone_Unknown = true;
	
	public String Name;
	public int ID;
	
	private IECType TYPE= IECType.IEC_NULL_TYPE;
	private int ASDU;
	private int iob=1;
	
	byte[] Stream = new byte[250];
	int StreamLength;
	public Object data = null;
	public boolean flag1 =false;	

	private ArrayList<IECTCObject> IOB;
	

	public final static Logger log = Logger.getLogger(IECTCItem.class .getName()); 
	
	/**
	 * Create default IECStream with ONE IECObject<br><br>
	 * <b>Default Values:</b><br>
	 * IECType = M_SP_NA<br>
	 * ASDU = 1<br>
	 */
	public IECTCItem() {
		IOB = new ArrayList<IECTCObject>();
		Stream[0] = IECType.M_SP_NA.tk();	
		setType(IECType.M_SP_NA);
		setCOT(3);
		setASDU(1);
		IECTCObject o = new IECTCObject(this,iob++);
		addIOB(o);	
//		setIOBCount(1);		
//		addIOB();
	}

	public IECTCItem(IECType iectyp) {
		log.finer(iectyp.name());
		IOB = new ArrayList<IECTCObject>();
		Stream[0] = iectyp.tk();	
		if (iectyp == IECType.IEC_NULL_TYPE) {
			Stream[0] = IECType.M_SP_NA.tk();
		}
		IECTCObject o = new IECTCObject(this,iob++);
		addIOB(o);		
		setCOT(3);
		setASDU(1);
	}

	/**
	 * Create new IECStream with ONE IECObject<br>
	 */
	public IECTCItem(IECType iectype,int ASDU) {
		log.finer(iectype.name());
		IOB = new ArrayList<IECTCObject>();
		Stream[0] = iectype.tk();	
		setType(iectype);
		IECTCObject o = new IECTCObject(this,iob++);
		addIOB(o);	
		setCOT(3);
		setASDU(ASDU);
	}
	
	/**
	 * Create new IECStream from an byte stream<br>
	 */  	
	public IECTCItem(byte[] b,int length) {
//		System.out.println("create item from Stream len: "+length);
		log.finer("create item from Stream len: "+length);
 	//  DECODE an recieved stream 
		IOB = new ArrayList<IECTCObject>();
		Stream[0] = b[0];
		Stream[1] = 0;
		readType();
		IECTCObject o = new IECTCObject(this);
		addIOB(o);	
		setIOBCount(b[1]);		

//		byte[] b_long = new byte[length+20]; 
		
		int index;		
		if (P_SHORT) {
			log.finer("Short profil (P_SHORT) aktiv Delete bytes");
			System.arraycopy(b,0,Stream,0,3);
			Stream[3] =0;
			System.arraycopy(b,3,Stream,4,length-3);
			length++;
			index =5;   
		} else {
			System.arraycopy(b,0,Stream,0,length);
			index=6;  
		}
		
//		Stream = b_long;
		StreamLength= length;
		readASDU();
//		og(Level level, String msg, Object[] params) 
		log.log(Level.FINE,"create item [TYPE:{0} count:{1} ASDU:{2} COT:{3}] ",new Object[]{TYPE,getIOBCount(),ASDU,getCOT()});
		o = iob(0);
		o.buf = Arrays.copyOfRange(b,index, b.length);  // copy rest of stream 
		if (getType() != IECType.IEC_NULL_TYPE) {
			o.readValue();
			o.readTime();
	        crcLength(length);
	    }
	}	

   public int MaxObjects() {
	   return 240/ iob(0).getBufLength();
	  }
   
	/**
  	 * 
  	 * @param length
  	 * @return
  	 */
	 public boolean crcLength(int length) {
		 int index=6;
		 if (P_SHORT) {
			 length--;
			 index =5; 
		 }
		 IECTCObject o = iob(0);
		 int l =  o.getBufLength();
		 if (length == l * getIOBCount() *+index) {
			 log.log(Level.FINE,"Stream Length:{2} (Head.length[{0}] + IOB.count[{3}]*IOB.length[{1}])",new Object[]{index,l,l+index,getIOBCount()});
			 return true;
		 } 
		 log.log(Level.SEVERE,"Stream Length:{3} should {2} (Head.length[{0}] + IOB.count[{4}]*IOB.length[{1}])",new Object[]{index,l,l * getIOBCount()+index,length,getIOBCount()});
		 return false;
	}

	public void addIOB() {
		 IECTCObject item = new IECTCObject(this);
		 addIOB(item);
	 }

	public void addIOB(IECTCObject item) {
		log.finest(String.valueOf(item.getIOB()));
		IOB.add(item);
 /// increment iob number],
//		if (item.getIOB() == iob) {
//			item.setIOB(iob++);
	//	}
 /// save number of items in stream[1],
		Stream[1]= (byte) IOB.size();
//		 setIOBCount(IOB.size());
	 }
	 
	 public IECTCObject iob(int index) {
		if (index <= IOB.size()) {
			return IOB.get(index);
		}
		return null;
	}
	
	public void setType(IECType t) {
		Stream[0] = t.tk();	
		
		for (int it=0;it<IOB.size();it++) {
			IOB.get(it).setDefLimits();   //Reset The Limits
			IOB.get(it).setQU((byte) 0);  //Reset The Quality
		}
//		System.out.println("IOBCount :"+IOB.size());		
//		StreamLength =6+ IOB.get(0).getBufLength();
		TYPE = t;
	}
	
	private void readType() {
		TYPE = IECMap.getType(Stream[0]);
		if (TYPE == IECType.IEC_NULL_TYPE) {
			log.warning("Stream-Type: "+TYPE);
		} else {
			log.finest("Stream-Type: "+TYPE);
		}
	}
	
	public IECType getType(){
//		return TYPE;
		return IECMap.getType(Stream[0]);
	}
	
	/**
	 * Ceate 'c' IECTCObject s for this stream
	 * @param c = Number of Objects to create
	 * @throws Exception 
	 */
	
	public void setIOBCount(int c) {
		if (c > MaxObjects()) {
			throw new IllegalArgumentException("Number of Elements exides Max Element numbers: ");
		}
		byte count = (byte) c;
//		System.out.println("setIOBCount "+c);
		if (count!=Stream[1]) {
			while (IOB.size()< count) {
				IECTCObject item = new IECTCObject(this,iob++);
				addIOB(item);
			}
			while (IOB.size()> count) {
// ??				IOB.get(IOB.size()-1) =null;
				IOB.remove(IOB.size()-1);
			}
		}
		if (count!=Stream[1]) {
			Stream[1]= count;
		}
	}
	public int getIOBCount() {
		log.finest(String.valueOf(Stream[1]));
		return Stream[1];
	}
	
	public void setCOT(int cot) {
    	if (cot > 65535) {
			cot = 65535;
		}
    	if (cot < 1) {
			cot = 1;
		}
    	Stream[2] =	(byte) (cot % 256);
		Stream[3] = (byte) (cot / 256);	
	}
	public int getCOT() {
		if (P_SHORT) {
			return  (Stream[2] & 0xFF);       	
		} else {
			return  ((Stream[3] & 0xFF) << 8) | (Stream[2] & 0xFF);   	
		}
	}
	
	public void setASDU(int asdu) {
    	if (asdu > 65535) {
			asdu = 65535;
		}
    	if (asdu < 1) {
			asdu = 1;
		}
		
    	this.ASDU = asdu;
    	
		int index;
		if (P_SHORT) {
			index =3;    	
		} else {
			index=4;    	
		}
		Stream[index] =	(byte) (ASDU % 256);
		Stream[index +1] = (byte) (ASDU / 256);					
	}

	public int getASDU() {
		return ASDU;
	}
	/**
	 *  set IECStream-ASDU by reading out of the Stream
	 */
	private void readASDU() {
//		System.out.println(c+"[4]"+ (int) Stream[4] +"[5]"+ (int) Stream[5] +"getASDU "+re);
		int index=4;    	
		ASDU = ((Stream[index+1] & 0xFF) << 8) | (Stream[index] & 0xFF);
		log.finest("Stream-ASDU: "+ASDU);
	}
	
	/**
	 * Return an Byte buffer that contains an ready to send Stream of this object
	 */
	public byte[] getStream() {
//		System.out.println("getIOB_Buffer");
		if (getType()==null) {  // Type NOT supported
		   if (Respone_Unknown) {
			   setCOT(0x44);
				if (P_SHORT) {
					byte[] Stream_s = Arrays.copyOf(Stream,StreamLength);
					System.arraycopy(Stream,4,Stream_s,3,StreamLength-4);    	
					return Arrays.copyOf(Stream_s,StreamLength-1);
				} else {
					return Arrays.copyOf(Stream,StreamLength);
				}
		   } else {
			   return null;
		   }
		}  		
//  Known Type 
		byte[] Stream_s = Arrays.copyOf(Stream,StreamLength+200);
		Stream[5] =	(byte) (ASDU % 256);
		Stream[5] = (byte) (ASDU / 256);			
		Stream_s[3] =	(byte) (ASDU % 256);
		Stream_s[4] = (byte) (ASDU / 256);			
		int index =6;
		if (P_SHORT) {
			index =5;    	
		}
		int indexIOB = index;
		byte[] b = null;
		int bl=0;
		int i=getIOBCount();
		for (int it=0;it<i;it++) {
			b =iob(it).getStream();
//			System.out.println("IOB_index "+indexIOB+"   IOB_Stream "+IECFunctions.byteArrayToHexString(b,0,b.length));
			if (P_SHORT) {
				System.arraycopy(b,0,Stream_s,indexIOB,b.length);    	
			} else {
				System.arraycopy(b,0,Stream,indexIOB,b.length);    	
			}
			indexIOB =indexIOB +b.length;
			bl = b.length;
		}
//		System.out.println("Stream "+Functionsn.byteArrayToHexString(Stream,0, 6+ getIOBCount()*(bl)));
//		System.out.println("_STREAM: "+StreamLength+printStream());
		int l = index+ i *(bl);
		byte[] result;
		String s;
		if (P_SHORT) {
			s="s";
			result=Arrays.copyOf(Stream_s,l);
//			return Arrays.copyOf(Stream_s,l);
		} else {
			s="S";
			result=Arrays.copyOf(Stream,l);
//			System.out.println("b.length "+b.length+"  LENGTH "+l);
//			return Arrays.copyOf(Stream,l);
		}
		log.finer(s+":"+l+"[ "+IECFunctions.byteArrayToHexString(result,0,result.length)+"]");
		return result;
	}
	
	/**
	 * !! If the Requested TCType is an counter this will increase the Sequence !!
	 * @return HEX String of the IECTCItem
	 */
	public String printStream() {
//		System.out.println("IEC mesage "+Functionsn.byteArrayToHexString(Stream,0,StreamLength));
		byte [] buf = getStream();
		return IECFunctions.byteArrayToHexString(buf,0,buf.length);
	}
	
	public void setProperties(Properties p,int idoffset) {
//		p.list(System.out);
		String pre = "ITEM"+String.valueOf(ID-idoffset)+".";
		System.out.println("Properties for: "+pre);
		try {
			Name = p.getProperty(pre+"NAME",Name);
			setType(IECType.valueOf(p.getProperty(pre+"TYPE",getType().toString())));
			setASDU(Integer.parseInt(p.getProperty(pre+"ASDU",String.valueOf(getASDU()))));
			setCOT(Integer.parseInt(p.getProperty(pre+"COT",String.valueOf(getCOT()))));
			iob(0).setIOB(Integer.parseInt(p.getProperty(pre+"IOB",String.valueOf(iob(0).getIOB()))));
			iob(0).setValue(Double.parseDouble(p.getProperty(pre+"VALUE",String.valueOf(iob(0).getValue()))));
			flag1 = Boolean.parseBoolean(p.getProperty(pre+"SIMULATE",String.valueOf(flag1)));
		} catch (Exception e) {
		}
		if (data.getClass()== IECSimProperties.class) {
//			System.out.println("sim.setProperties");
			IECSimProperties sim = (IECSimProperties) data;
			 sim.setProperties(p);   				
			}
//		System.out.println(IECType.valueOf(txt));
	}

	public static String[] getPropNames() {
		  String[] result =new String[7];
		  result[0] ="NAME";
		  result[1] ="TYPE";
		  result[2] ="ASDU";
		  result[3] ="COT";
		  result[4] ="IOB";
		  result[5] ="VALUE";
		  result[6] ="SIMULATE";
		  return result;
	  }
	  
    /**
     * return the Properties of IEC steam including properties of first IEC Object 
     */
	public Properties getProperties() {
		Properties p = new Properties();
		String pre = "ITEM"+String.valueOf(ID)+".";
		p.setProperty(pre+"NAME",Name);
		p.setProperty(pre+"TYPE",getType().toString());
		p.setProperty(pre+"ASDU",String.valueOf(getASDU()));
		p.setProperty(pre+"COT",String.valueOf(getCOT()));
		p.setProperty(pre+"IOB",String.valueOf(iob(0).getIOB()));
		p.setProperty(pre+"VALUE",String.valueOf(iob(0).getValue()));
		p.setProperty(pre+"SIMULATE",String.valueOf(flag1));
		if (data.getClass()== IECSimProperties.class) {
   				IECSimProperties sim = (IECSimProperties) data;
   				p.putAll(sim.getProperties());   				
   			}
//		p.list(System.out);
		return p;
	}
	public String getPString() {
		String s = "";
//		String pre = "ITEM"+String.valueOf(ID)+".";
		s=s+Name+";";
		s=s+getType().tk()+";";
		s=s+String.valueOf(getASDU())+";";
		s=s+String.valueOf(getCOT())+";";
		s=s+String.valueOf(iob(0).getIOB())+";";
		s=s+String.valueOf(iob(0).getValue())+";";
		s=s+String.valueOf(flag1);
		if (data.getClass()== IECSimProperties.class) {
   				IECSimProperties sim = (IECSimProperties) data;
   				s=s+";"+sim.getPString();   				
   			}
//		p.list(System.out);
		return s;
	}

	public static ArrayList<IECTCItem> Seperate(IECTCItem i) {
		log.finest("");
		IECType type = i.getType();
		int asdu =i.getASDU();
		int cot =i.getCOT();
		ArrayList<IECTCItem> result = new ArrayList<IECTCItem>();
		IECTCItem t;
		IECTCObject o;
		int c = i.getIOBCount();
		log.finest("Object count"+c);
		for (int x=0; x<c ;x++) {
			t =new IECTCItem(type,asdu);
			t.setCOT(cot);
			o = i.iob(x);
			t.iob(0).setIOB(o.getIOB());
			t.iob(0).setQU(o.getQU());
			t.iob(0).setValue(o.getValue());
			t.iob(0).setTime(o.getTime());
//			Seperate.add(t);
			result.add(t);
		}
		return result;
	}

	public static ArrayList<IECTCItem> Pack(ArrayList<IECTCItem> itemarray) {
		int TCItemcount = itemarray.size();
		log.info(TCItemcount+" TCItems in Input array");
		if (TCItemcount ==0) return null;
		ArrayList<IECTCItem> result = new ArrayList<IECTCItem>();
		IECTCItem i = itemarray.get(0);
		IECType iectype = IECType.IEC_NULL_TYPE;
		int asdu = 0 ;
		int cot = 0 ;
		int MAX = 0 ;
		IECTCItem i2 = null;
		boolean clone;
		for (int x=0 ; x < TCItemcount; x++) {
			clone= true;
			i = itemarray.get(x);
			log.finest("TCItem "+x);
			if ((i.getType()!= iectype)||(i.getASDU()!=asdu)||(i.getCOT()!=cot)) {
				if (i2 != null) result.add(i2);
				iectype = i.getType();
				asdu =i.getASDU();
				cot =i.getCOT();
//				MAX = 8;
				MAX = i.MaxObjects();

				i2 = new IECTCItem(iectype,asdu);
				i2.setCOT(cot);
				IECTCObject.copy(i.iob(0),i2.iob(0));
				log.info("Paramter changed create new TCItem "+iectype.name());	
				clone= false;
			}
			if (i2.getIOBCount()>=MAX) {
				if (i2 != null) result.add(i2);
				i2 = new IECTCItem(iectype,asdu);
				i2.setCOT(cot);
				IECTCObject.copy(i.iob(0),i2.iob(0));
				log.info("MAX Objects >"+MAX+" create new TCItem "+iectype.name());
				clone= false;
			}
			if (clone) {
				try {
					i2.addIOB(i.iob(0).clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
		result.add(i2);
		log.fine("TCItems in result "+result.size());
		return result;
	}
}


