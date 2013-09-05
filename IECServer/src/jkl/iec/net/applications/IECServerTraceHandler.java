package jkl.iec.net.applications;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import jkl.iec.logger.impl.IECLoggFormatter;

public class IECServerTraceHandler extends ConsoleHandler {
	
	JTextArea textArea =null;

	public IECServerTraceHandler(JTextArea obj) {
		textArea = obj;
		setFormatter(new IECLoggFormatter());
		setLevel(Level.FINE);
	}
	
	
	public boolean isLoggable(LogRecord record) {
		if (textArea == null) 
			return false;
		return super.isLoggable(record);
	}
	
	public void publish(final LogRecord record) {
		Runnable  runnable = new Runnable() {
            public void run(){
               	String formattedMessage ;
        		if (!isLoggable(record)) {
        			return;
        		}
        		formattedMessage = getFormatter().format(record);
        		textArea.append(formattedMessage);
        		textArea.setCaretPosition(textArea.getDocument().getLength());           	
               }};
         SwingUtilities.invokeLater(runnable);
	}

}
