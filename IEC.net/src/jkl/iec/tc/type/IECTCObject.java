package jkl.iec.tc.type;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import jkl.iec.net.utils.IECFunctions;

public class IECTCObject implements Cloneable {
	
	IECTCItem asdu;
	private double MAX_VALUE;
	private double MIN_VALUE;
	private double Value;
	public int ValueParam;
	double VALUE_TX;
	double VALUE_RX;
	byte[] buf = new byte[16];
	byte QU;
	byte QU_TX;
	byte QU_RX;
	byte SEQ=0;
	private Date Time;
	public Date Time_RX;
	public Date Time_TX;

	public final static Logger log = Logger.getLogger(IECTCObject.class .getName()); 
	
	public IECTCObject(IECTCItem s) {
/*		buf[0]=  IOB1  buf[1]= IOB2   buf[2]= IOB3
		buf[3] = Val1	buf[4]= Val2   buf[5]= Val3	 buf[6]= Val4
		buf[7] = QU    
		buf[8]=  TIME1  buf[9]= TIME2   buf[10]= TIME3  buf[11]= TIME4  buf[12]= TIME5  buf[13]= TIME6   buf[14]= TIME7  */
		asdu = s;
//		System.out.println("IOB OBJ ADDR. "+ getIOB());
		init();
		setIOB(1);
	}
	
	public IECTCObject(IECTCItem s,int iob) {
		asdu = s;

//		System.out.println("IOB OBJ ADDR. "+ getIOB());
		setValues(0,(byte) 0,new Date());
		Time_TX =Time;
		init();
		setIOB(iob);
	}
	
	private void init(){
		log.finest("");
		setDefLimits();
		setValues(0,(byte) 0,new Date());
		Time_TX =Time;	
	}

	public void setIOB(int iob) {
    	if (iob > 0xffffff) {
    		iob = 0xffffff;
		}
    	if (iob < 0) {
    		iob = 0;
		}
    	buf[0] =(byte) ((iob & 0x00ff));
		buf[1] =(byte) ((iob & 0x00ff00)>>8);
		buf[2] =(byte) ((iob & 0xff0000)>>16);
		log.log(Level.FINEST,"{0} [{1}] ",new Object[]{getIOB(),IECFunctions.byteArrayToHexString(buf, 0, 3)});
//		log.log(Level.FINEST,"{3} [{0} {1} {2}] ",new Object[]{buf[0],buf[1],buf[2],getIOB()});
	}
	
	public int getIOB(){
		if (IECTCItem.P_SHORT) {
			return  (buf[0] & 0xFF) | ((buf[1] & 0xFF) << 8) ;
		} else {
			return  (buf[0] & 0xFF) | ((buf[1] & 0xFF) << 8) | ((buf[2] & 0xFF) << 16);
		}
	}

	private void setValues(double v,byte qu,Date d) {
		setValue(v);
//		System.out.println("IOB getVal TIMEIDX. "+ getValue());
		setQU(qu);
		setTime(d);	
	}
	
	public IECTCObject clone() throws CloneNotSupportedException {
		return (IECTCObject) super.clone();
	}

	/**
	 * 
	 * @param o1 = source
	 * @param o2 = destination
	 */
	public static void copy(IECTCObject o1,IECTCObject o2) {
		o2.asdu=o1.asdu;
		o2.setIOB(o1.getIOB());
		o2.setValues(o1.getValue(), o1.getQU(),  o1.getTime());
	}
	
	public double getValue() {
//		readValue();
		return Value;
	}

	public String getValueasString() {
		System.out.println("IOB getValasString "+ Value);
		switch(asdu.getType()) {
		case M_SP_NA : case M_SP_TB : case C_SC_NA :{
			if (Value == 0) return "OFF (0)";
			if (Value == 1) return "ON  (1)";
			return Boolean(Value != 0).toString();
		}
		case M_DP_NA : case M_DP_TB : {
			if (Value == 0) return "FAULT(00)";
			if (Value == 1) return "OFF  (01)";
			if (Value == 2) return "ON   (10)";
			if (Value == 3) return "INT  (11)";
		}
		case C_DC_NA : {
//			ValueParam =  buf[index] & 0x1c;
			if (Value == 1) return "OFF  (01)";
			if (Value == 2) return "ON   (10)";
		}
		case M_ME_NA: case M_ME_TB : case M_ME_NB : case M_ME_TD :  {
			return String.valueOf((int)(Value));
		}
		case M_ME_NC: case M_ME_TF : {
			return String.valueOf(Value);
		}
		case M_IT_NA: case M_IT_TB : {
			return String.valueOf((long)Value);
		}
		case C_SE_NA: case C_SE_NB : {
			return String.valueOf((int)(Value));
		}
		case C_IC_NA : {
			return String.valueOf((int)(Value));
		}
		case C_CS_NA : {
//			Date d = new Date();
//			d.setTime((long) Value);
			SimpleDateFormat df = new SimpleDateFormat("yy.MM.dd   HH:mm:ss,S");
			return df.format((long) Value);
//			return d.getTime();
		}		
		}
		return "??";
	}
	
	public void readValue() {
		double result = 0;
		int index = 3;
		if (IECTCItem.P_SHORT) {
			index=2;
		} 
		switch(asdu.getType()) {
		case M_SP_NA : case M_SP_TB : {
			result = buf[index] & 0x01;
			break;
		}
		case C_SC_NA : {
			result = buf[index] & 0x03;
			ValueParam =  buf[index] & 0x1c;
			break;
		}	
		case M_DP_NA : case M_DP_TB : {
			result = buf[index] & 0x03;
			break;
		}
		case C_DC_NA : {
			result = buf[index] & 0x03;
			ValueParam =  buf[index] & 0x1c;
			break;
		}
		case M_ME_NA: case M_ME_TB : case M_ME_NB : case M_ME_TD :  {
			result = buf[index] + buf[index+1]*256;
			break;
		}
		case M_ME_NC: case M_ME_TF : {
			result =buf[index] +
					buf[index+1]<<8+
					buf[index+2]<<16+
					buf[index+3]<<24 ;
			break;
		}
		case M_IT_NA: case M_IT_TB : {
			result = buf[index] +
					 buf[index+1]<<8+
					 buf[index+2]<<16+
					 buf[index+3]<<24 ;
			break;
		}
		case C_SE_NA: case C_SE_NB : {
			result = buf[index] + buf[4]*256;
			break;
		}
		case C_SE_NC : {
			int bits = 0;
			bits = buf[index] +
				   buf[index+1]*256+
				   buf[index+2]*65535+
				   (buf[index+3] & (0xef))*16777216 ;
			log.fine("*BITS:"+bits);
			result = Float.intBitsToFloat(bits);
			break;
		}
		case C_IC_NA : {
			result = buf[index];
			break;
		}
		case C_CS_NA : {
			result = readTime(index).getTime();
			break;
		}
    	}
		log.finer("buf["+index+"]["+Integer.toHexString(buf[index])+"..]  Value := "+result);
		System.out.println(result);
		Value = result;
	}
	 
	private Object Boolean(boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean setValue(double v) {
    	if (v > MAX_VALUE) {
			v = MAX_VALUE;
		}
		if (v < MIN_VALUE) {
			System.out.println(v+"< MinValue : "+MIN_VALUE);
			v = MIN_VALUE;
		}
		if (v != getValue()) {
			Value = v;
			System.out.println("setValue(newValue : "+v+")");
			Time = new Date();
			return true;
		}
		return false;
	}
	
	private int getTimeLength(){
		if (isTimeType()) {
			return 7;
		} 
		return 0;
	}

	public int getBufLength(){
		return getTimeIndex()+getTimeLength();
	}
	
	private int getIOBLength() {
		int result =0;
		switch(asdu.getType()) {
		case M_SP_NA : case M_SP_TB : 
		case M_DP_NA : case M_DP_TB : 
		case C_SC_NA : case C_DC_NA : 
		case C_IC_NA : case C_CI_NA :{
			result= 1;
			break;
		}		
		case M_ME_NA: case M_ME_TB : case M_ME_NB : case M_ME_TD :
		case C_SE_NA: case C_SE_NB :  {
			result= 3;
			break;
		}
		case M_ME_NC: case M_ME_TF :
		case C_SE_NC :{
			result= 5;
			break;
		}
		case M_IT_NA: case M_IT_TB : {
			result= 5;
			break;
		}
		case C_CS_NA: {
			result= 7;
			break;
		}
    	}
        log.finest(String.valueOf(result));
		return result;		
	}
	
	private int getTimeIndex(){
		if (IECTCItem.P_SHORT) {
			return getIOBLength()+2;
		}
		return getIOBLength()+3;
	}
	
	private boolean isTimeType(){
		switch(asdu.getType()) {
		case M_SP_TB : case M_DP_TB :
		case M_ME_TB : case M_ME_TD : case M_ME_TF : case M_IT_TB : {
			return true;
		}
    	}
		return false;
	}	
	
	private void writeValue() {
//		System.out.println("IOB writeVal :"+ Value);
		int index = 3;
		switch(asdu.getType()) {
		case M_SP_NA : case M_SP_TB : {
			buf[index]= (byte) (((byte) Value)& 0x01) ;
			break;
		}
		case C_SC_NA : {
			buf[index]= (byte) (((byte) Value)& 0x01) ;
			buf[index]= (byte) (buf[index] |(byte) ValueParam);
			break;
		}
		case M_DP_NA : case M_DP_TB : {
			buf[index]= (byte) (((byte) Value)& 0x03) ;
			break;
		}
		case C_DC_NA : {
//			System.out.println("C_DC_ValueParam "+ ValueParam);
			buf[index]= (byte) (((byte) Value) & 0x03);
			buf[index]= (byte) (buf[index] |(byte) ValueParam);
			break;
		}
		case M_ME_NA: case M_ME_TB : case M_ME_NB : case M_ME_TD :  {
			short value=(short)Value;
//			System.out.println("M_ValueParam "+ value);
			buf[index+1]= (byte) (value  >> 8);
			buf[index]= (byte)  value ;
//			buf[index]=(byte) (value % 256);
//			buf[index+1]=(byte) (value / 256);
			break;
		}
		case C_SE_NA: case C_SE_NB :  {
			short value=(short)Value;
			buf[index+1]= (byte) (value  >> 8);
			buf[index]= (byte)  value ;
			break;
		}
		case M_ME_NC: case M_ME_TF : {
			int value=Float.floatToRawIntBits((float) Value);
			log.severe("*BITS:"+value);
			buf[index+3] =(byte) ((value & 0xff000000)>>24);
			buf[index+2] =(byte) ((value & 0x00ff0000)>>16);
			buf[index+1] =(byte) ((value & 0x0000ff00)>>8);
			buf[index] =(byte) ((value & 0x000000ff));
			break;
		}
		case C_SE_NC : {
			int value=Float.floatToRawIntBits((float) Value);
			buf[index+3] =(byte) ((value & 0xff000000)>>24);
			buf[index+2] =(byte) ((value & 0x00ff0000)>>16);
			buf[index+1] =(byte) ((value & 0x0000ff00)>>8);
			buf[index] =(byte) ((value & 0x000000ff));
			break;
		}
		case M_IT_NA: case M_IT_TB : {
			int value=(int)Value;
			buf[index+3] =(byte) ((value & 0xff000000)>>24);
			buf[index+2] =(byte) ((value & 0x00ff0000)>>16);
			buf[index+1] =(byte) ((value & 0x0000ff00)>>8);
			buf[index] =(byte) ((value & 0x000000ff));
			break;
		}
		case C_IC_NA : {
			buf[index]= (byte) Value;
			break;
		}	
		case C_CS_NA : {
			writeTime(index);
			break;
		}
    	}
	}
	
	public byte getQU() {
		return QU;
	}
	
	public void readQU() {
		int index = 3;
		switch(asdu.getType()) {
		case M_SP_NA : case M_SP_TB : {
			QU = (byte) (buf[index]& ((byte) 0xfe)) ;
			break;
		}
		case C_SC_NA : {
			QU = (byte) (buf[index]& ((byte) 0xfe)) ;
			break;
		}
		case M_DP_NA : case M_DP_TB : {
			QU = (byte) (buf[index]& ((byte) 0xfc)) ;
			break;
		}	
		case C_DC_NA : {
			QU = (byte) (buf[index]& ((byte) 0xfc)) ;
			break;
		}	
		case M_ME_NA: case M_ME_TB : case M_ME_NB : case M_ME_TD :  {
			QU =  buf[index+2];
			break;
		}
		case C_SE_NA : case C_SE_NB :  {
			QU =  buf[index+2];
			break;
		}
		case M_ME_NC: case M_ME_TF : {
			QU = buf[index+4];
			break;
		}
		case C_SE_NC : {
			QU = buf[index+4];
			break;
		}
		case M_IT_NA: case M_IT_TB : {
			QU = (byte) (buf[index+4] & (byte)0xe0);
			SEQ = (byte) (buf[index+4] & (byte)0x1f);
			break;
		}
    	}
	}
	
	public void setQU(byte qu){
		QU = qu;
	}
	public void setSEQ(byte seq) {
		SEQ =seq;
	}
	public byte getSEQ() {
		return SEQ;
	}	
	private void writeQU() {
		int index = 3;
		switch(asdu.getType()) {
		case M_SP_NA : case M_SP_TB : {
			buf[index]= (byte) (((byte) QU)|((byte) Value)& 0x01) ;
			break;
		}
		case C_SC_NA : {
			buf[index]= (byte) (((byte) QU)|  buf[index]) ;
			break;
		}		
		case M_DP_NA : case M_DP_TB : {
			buf[index]= (byte) ((byte) QU | ((byte) Value)& 0x03) ;
			break;
		}	
		case C_DC_NA : {
			buf[index]= (byte) ((byte) QU | buf[index]);
			break;
		}	
		case M_ME_NA: case M_ME_TB : case M_ME_NB : case M_ME_TD :  {
			buf[index+2]= QU;
			break;
		}
		case C_SE_NA: case C_SE_NB :  {
			buf[index+2]= QU;
			break;
		}
		case M_ME_NC: case M_ME_TF : {
			buf[index+4] = QU;
			break;
		}
		case C_SE_NC : {
			buf[index+4] = QU;
			break;
		}
		case M_IT_NA: case M_IT_TB : {
//			buf[7] = (byte) (buf[7]| qu);
			buf[index+4] = (byte) (SEQ| QU);
			log.finer("SEQ: "+SEQ);
			SEQ++;
			if (SEQ>31) {
				SEQ=0;
			}
			break;
		}
    	}
	}
	
	public void setTime(Date dt) {
	  Time=dt;
	}
	public Date getTime() {
		  return Time;
		}
			
	private void writeTime() {
		writeTime(getTimeIndex());
	}
	
	private void writeTime(int TimeIndex) {
//		System.out.println("IOB TIMEIDX. "+ TimeIndex);
		Calendar d = Calendar.getInstance();
		d.setTime(Time);
		int msec = d.get(Calendar.SECOND)*1000 + d.get(Calendar.MILLISECOND);
//		buf[TimeIndex]= (byte) (d.get(Calendar.MILLISECOND)%256);
//		buf[TimeIndex+1]= (byte) (d.get(Calendar.MILLISECOND)/256);
		buf[TimeIndex]= (byte) (msec %256);
		buf[TimeIndex+1]= (byte) (msec /256);
		buf[TimeIndex+2]= (byte) (d.get(Calendar.MINUTE));
		buf[TimeIndex+3]= (byte) (d.get(Calendar.HOUR_OF_DAY));
		if (TimeZone.getDefault().inDaylightTime(d.getTime())) {
			buf[TimeIndex+3]=(byte) (buf[TimeIndex+3]+0x80) ;
		}
		buf[TimeIndex+4]= (byte) (d.get(Calendar.DAY_OF_MONTH));
		buf[TimeIndex+5]= (byte) (d.get(Calendar.MONTH)+1);
		buf[TimeIndex+6]= (byte) (d.get(Calendar.YEAR)-2000);
	}
	
	public Date readTime() {
		int TimeIndex=getTimeIndex();
		return readTime(TimeIndex);
	}

	private Date readTime(int index) {
		Calendar d = Calendar.getInstance();
		String txt =" Sytem TIME ";
		if (isTimeType()) {
			txt = " IOB TIME ";
			d.set(Calendar.MILLISECOND,((buf[index+1] & 0xFF) << 8) | (buf[index] & 0xFF));
			d.set(Calendar.MINUTE,(buf[index+2] & 0xFF));
			d.set(Calendar.HOUR_OF_DAY,(buf[index+3] & 0x7F));
			d.set(Calendar.DAY_OF_MONTH,(buf[index+4] & 0xFF));
			d.set(Calendar.MONTH,(buf[index+5] & 0xFF)-1);
			d.set(Calendar.YEAR,(buf[index+6] & 0xFF)+2000);
		} 
		log.finer(txt+ d.getTime());
		return d.getTime();
	}
	
	private double getDef_MAX() {
		if (asdu.getType()== null) {
		   return MAX_VALUE;
		}
		switch(asdu.getType()) {
    	case M_SP_NA : case M_SP_TB :{	
    		return 1;
    	}
    	case C_SC_NA :{	
    		return 1;
    	}
    	case M_DP_NA : case M_DP_TB :{	
    		return 3;
    	}
    	case C_DC_NA :{	
    		return 3;
    	}
    	case M_ME_NA : case M_ME_TB : case M_ME_NB : case M_ME_TD :{	
    		return Short.MAX_VALUE;
    	}
    	case C_SE_NA : case C_SE_NB :{	
    		return Short.MAX_VALUE;
    	}
    	case M_ME_NC : case M_ME_TF :{	
//    		return Float.MAX_VALUE;
    		return +1000000000000.0;
    	}   
    	case C_SE_NC :{	
    		return Float.MAX_VALUE;
    	}   
    	case M_IT_NA : case  M_IT_TB :{	
    		return Integer.MAX_VALUE;
    	}   
    	case C_IC_NA : case  C_CI_NA :{	
    		return 255;
    	} 
    	}
		return MAX_VALUE;
	}
	
	private double getDef_MIN() {
		if (asdu.getType()== null) {
			   return MIN_VALUE;
			}
		switch(asdu.getType()) {
    	case M_SP_NA : case M_SP_TB :{	
    		return 0;
    	}
    	case C_SC_NA : case C_DC_NA :{	
    		return 0;
    	}
    	case M_DP_NA : case M_DP_TB :{	
    		return 0;
    	}
    	case M_ME_NA : case M_ME_TB : case M_ME_NB : case M_ME_TD :{	
    		return Short.MIN_VALUE;
    	}
    	case C_SE_NA : case C_SE_NB :{	
    		return Short.MIN_VALUE;
    	}
    	case M_ME_NC : case M_ME_TF :{	
//    		return Float.MIN_VALUE ;
    		return -1000000000000.0;
    	}   
    	case C_SE_NC :{	
    		return Float.MIN_VALUE;
    	}   
    	case M_IT_NA : case  M_IT_TB :{	
    		return Integer.MIN_VALUE;
    	}   
    	case C_IC_NA : case  C_CI_NA :{	
    		return 0;
    	} 
    	}
		return MIN_VALUE;
	}
	
	public void setDefLimits() {
   		MAX_VALUE =getDef_MAX();
   		MIN_VALUE =getDef_MIN();
//    	System.out.println("Limits  "+MIN_VALUE+":"+MAX_VALUE);
    	
	}
	
	public double getMAX_VALUE() {
		return MAX_VALUE;
	}

	public void setMAX_VALUE(double mAX_VALUE) {
    	if (mAX_VALUE > getDef_MAX()) {
    		mAX_VALUE = getDef_MAX();
		}
    	MAX_VALUE = mAX_VALUE;
	}

	public double getMIN_VALUE() {
		return MIN_VALUE;
	}

	public void setMIN_VALUE(double mIN_VALUE) {
    	if (mIN_VALUE < getDef_MIN()) {
    		mIN_VALUE = getDef_MIN();
		}		MIN_VALUE = mIN_VALUE;
	}
	
	public String printStream() {
//		System.out.println("IEC mesage "+Functionsn.byteArrayToHexString(Stream,0,StreamLength));
		return IECFunctions.byteArrayToHexString(buf,0,getBufLength());
	}	
	
	public byte[] getStream() {
		writeValue();
		writeQU();
		if (isTimeType()) {
			writeTime();
		}
		int l= getBufLength();
		byte[] result = Arrays.copyOf(buf, l);
		if (IECTCItem.P_SHORT) {
			//creates "short array"
			byte[] result_s =  Arrays.copyOf(buf, l); 
			System.arraycopy(buf, 3, result_s, 2, l-3);
			log.finer("s:"+l+"[ "+IECFunctions.byteArrayToHexString(result_s,0,result_s.length)+"]");
			return result_s;
		} else {
			log.finer("S:"+l+"[ "+IECFunctions.byteArrayToHexString(result,0,result.length)+"]");
			return result;
		}
//			System.out.println("IOB_buffer"+Functionsn.byteArrayToHexString(buf,0,15));
//		System.out.print("IOB: get_Stream "+getTimeIndex()+" ");
//		System.out.println(Functionsn.byteArrayToHexString(buf,0, getTimeIndex()));
	}
}
