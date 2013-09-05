package jkl.iec.net.utils;

public class IECFunctions {
	
	public static String byteArrayToHexString(byte[] b,int offset, int count) {
	    StringBuffer sb = new StringBuffer(count * 2);
	    for (int i = offset; i < offset+count; i++) {
	      int v = b[i] & 0xff;
	      if (v < 16) {
	        sb.append('0');
	      }
	      sb.append(Integer.toHexString(v)+" ");
	    }
	    return sb.toString().toUpperCase();
	  }

}
