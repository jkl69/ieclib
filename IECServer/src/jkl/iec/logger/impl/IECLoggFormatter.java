package jkl.iec.logger.impl;

import java.text.DateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

//public class IECLoggFormatter  extends Formatter {
public class IECLoggFormatter  extends SimpleFormatter {

	public String format(LogRecord record) {
		// TODO Auto-generated method stub
		DateFormat dateFormat;
		StringBuilder buf = new StringBuilder(1000);
		dateFormat = DateFormat.getDateTimeInstance();
		buf.append(dateFormat.format(new Date(record.getMillis())));
		buf.append(String.format(" [%-7s]  ", record.getLevel()));//, " ["+record.getLevel()+"]\t");
		if (record.getLoggerName().lastIndexOf("IEC")!=-1) {
			buf.append(record.getLoggerName().substring(record.getLoggerName().lastIndexOf("IEC"))+" ");
		} else {
			buf.append(record.getLoggerName()+" ");
		}
//		buf.append(record.getSourceMethodName()+" ");
		buf.append(record.getMessage()+"\n");
//		builder.append(record.getSourceClassName());
		return buf.toString();
	}

}
