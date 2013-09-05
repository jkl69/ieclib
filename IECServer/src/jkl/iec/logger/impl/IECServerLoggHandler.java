package jkl.iec.logger.impl;

import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;

public class IECServerLoggHandler extends ConsoleHandler {

	public void publish(LogRecord record) {
//		System.out.print(getLevel()+" "+record.getLevel()+" * ");
		String formattedMessage ;
		if (!isLoggable(record)) {
			return;
		}
		
		formattedMessage = getFormatter().format(record);
		System.out.print(formattedMessage);
	}

}
