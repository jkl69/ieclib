package jkl.iec.tc.type;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@SuppressWarnings("serial")
public class IECMap extends HashMap<Object, Object> {
	
	public static enum IECType {IEC_NULL_TYPE ((byte)0x00),
		M_SP_NA ((byte)0x01),M_DP_NA ((byte)0x03),M_ME_NA((byte)0x09),M_ME_NB((byte)0x0a),M_ME_NC((byte)0x0d),M_IT_NA((byte)0x0f),
		M_SP_TB((byte)0x1e,(byte)0x01),M_DP_TB((byte)0x1f,(byte)0x03), M_ME_TB((byte)0x22,(byte)0x09),M_ME_TD((byte)0x23,(byte)0x0a),M_ME_TF((byte)0x24,(byte)0x0d),
		M_IT_TB((byte)0x25,(byte)0x0f),
		C_IC_NA ((byte)0x64), C_CI_NA ((byte)0x65),
		C_SE_NA ((byte)48), C_SE_NB ((byte)49), C_SE_NC ((byte)50) ,
		C_SC_NA ((byte)0x2d), C_DC_NA ((byte)0x2e),C_CS_NA((byte)0x67);
		
		// additional Type-Byte property of each enum 
		private byte TK;
		// additional Base-Type property of each enum 
		private byte BT;
		// set TK for each enum
		IECType (byte tk) {
			this.TK = tk;
			this.BT = tk;
			}	
		IECType (byte tk,byte bt) {
			this.TK = tk;
			this.BT = bt;
			}	
		
		public byte tk() {return TK;}
		public byte bt() {return BT;}
	}
	
    public static Set<IECType> IEC_M_Type = EnumSet.of(IECType.M_SP_NA,IECType.M_DP_NA,IECType.M_ME_NA,IECType.M_ME_NB,IECType.M_ME_NC,IECType.M_IT_NA,
			IECType.M_SP_TB,IECType.M_DP_TB,IECType.M_ME_TB,IECType.M_ME_TD,IECType.M_ME_TF,IECType.M_IT_TB);
    public static Set<IECType> IEC_C_Type = EnumSet.of(IECType.C_IC_NA,IECType.C_CI_NA,
    		IECType.C_SC_NA,IECType.C_DC_NA,IECType.C_SE_NA,IECType.C_SE_NB,IECType.C_SE_NC);

    public static Set<String> IEC_M_BaseType =new HashSet<String>();
    public static Set<String> IEC_C_BaseType =new HashSet<String>();
    
	public static IECMap map = new IECMap();

	private static String Type2BaseType(String s) {
		String tmp = s.substring(0, s.length()-3);
		String ext = s.substring(s.length()-3, s.length());
		return tmp+"_"+ext.charAt(ext.length()-1);
	}
	

 	private void setBaseType (Set<IECType> s) {
		String tmp,ext;
		Iterator<IECType> iter = s.iterator();
		while (iter.hasNext()) {
			tmp = iter.next().toString();
			ext = tmp.substring(tmp.length()-3, tmp.length()-1); 
			if (ext.equals("_N")) {
				if (s==IEC_M_Type) {
					IEC_M_BaseType.add(Type2BaseType(tmp));
				}
				if (s==IEC_C_Type) {
					IEC_C_BaseType.add(Type2BaseType(tmp));
				}
				}
		}	
	}

	
	public IECMap() {
		for (IECType it:IECType.values()) {
//			System.out.println(it);
			put(it,it.tk());
			put("tk_"+it.tk(),it);
		}
		setBaseType(IEC_M_Type);
		setBaseType(IEC_C_Type);
		setTypeDescription();
	}
	
	public static IECType getType(String description) {
		return (IECType) map.get("Description."+description);
	}
	
	public static IECType getType(byte b) {
		if ((IECType) map.get("tk_"+b)==null) {
			return IECType.IEC_NULL_TYPE;
		}
		return (IECType) map.get("tk_"+b);
	}
		
	public static String getBaseType(byte b) {
		IECType t = (IECType) map.get("tk_"+b);
		byte bt= t.BT;
		t= (IECType) map.get("tk_"+bt);
		String tmp = t.toString();
		return Type2BaseType(tmp);
	}
	public static String getBaseTypeDescription(String s) {
		return (String) map.get(s+".BaseDescription");
	}
	
	public static String getTypeDescription(IECType t) {
		return (String) map.get(t.toString()+".Description");
	}
	
	private void setBaseDescription(Set<String> s,Properties p) {
		String tmp;
		Iterator<String> iter = s.iterator();
		while (iter.hasNext()) {
			tmp = iter.next();
			put(tmp+".BaseDescription",p.getProperty(tmp,tmp));
			put("BaseDescription."+p.getProperty(tmp,tmp),tmp);
			}
	}
	
	private void setTypeDescription() {
		Properties description = new Properties();
		String PropertiesFile = "IECTypeDescription.properties";
		BufferedInputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(PropertiesFile));
			description.load(stream);
			stream.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		for (IECType it:IECType.values()) {
			put(it.toString()+".Description",description.getProperty(it.toString(),it.toString()));
			put("Description."+description.getProperty(it.toString(),it.toString()),it);
		}
		setBaseDescription(IEC_M_BaseType,description);
//		System.out.println("M_BASE "+IEC_M_BaseType);
		setBaseDescription(IEC_C_BaseType,description);
	}
	
	public void list() {
		System.out.println("IEC_MAP:");
		for (Map.Entry<Object,Object> entry : entrySet()) System.out.println(entry.getKey() +"="+ entry.getValue());
	}

}
