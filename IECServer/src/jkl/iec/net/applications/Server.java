package jkl.iec.net.applications;

/*
02.03.14 jkl
  Version 1.0.2-28: Item MIn-MAX Values added to file  
*/

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextArea;

import java.awt.Color;
import java.awt.Font;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import java.awt.Toolkit;

import jkl.iec.event.impl.IECEventListener;
import jkl.iec.net.applications.panels.ButtonPanel;
import jkl.iec.net.applications.panels.ClientPanel;
import jkl.iec.net.applications.panels.OptionPanel;
import jkl.iec.net.applications.panels.PorpertiesTable;
import jkl.iec.net.sockets.IECServer;
import jkl.iec.tc.gui.IECTabedTable;
import jkl.iec.tc.gui.IECTable;
import jkl.iec.tc.gui.IECTableModel;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;

@SuppressWarnings("serial")
public class Server extends JFrame {
	
	public final static Logger log = Logger.getLogger(Server.class .getName()); 
	
	private static String VersionFile = "/iec/net/applications/Images/version.ser"; 
	
	private Properties VERSION;
	
	public static IECTable iectable;
	static IECTabedTable iecttable;
	static boolean usettable= false;
//	static boolean usettable= true;


	public static IECServer server = null;
	static IECEventListener actionlist;
	
	public static Server ServerDlg;
	static serveraction ServerAction;
	public static ButtonPanel BP;
	static IECFile iecfile;
	static OptionPanel oPanel = new OptionPanel();
		
    public static JTextArea textField =new JTextArea();

//	public JTable table;
	public static ClientPanel Clientpanel =new ClientPanel();
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {

    	actionlist =new IECEventListener();
    	ServerDlg = new Server();
		iecfile = new IECFile(iectable.getIECModel());		

		ServerDlg.setVisible(true);
		ServerDlg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		System.out.println("Args "+args.length);
		if (args.length > 0) {
			iecfile.load(args[0]);
		}
	}

	public static void start() {
		server =new IECServer();
		server.setIECServerListener(actionlist);
		server.IECPort= BP.getPort();
		server.start();
		log.fine("IEC Server startet");		
		}
	
	public class serveraction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
//			System.out.println("Action "+e);
			if (e.getActionCommand()=="StartServer") {
				start();
			}
			if (e.getActionCommand()=="StopServer") {
				server.interrupt();
			}
			if (e.getActionCommand()=="Save") {
				System.out.println("SAVE");
//				iecfile.save();
				iecfile.saveV2();
			}
			if (e.getActionCommand()=="Load") {
				iecfile.load();
//				setTitle("IEC-TestServer  LOAD");
			}
			if(e.getActionCommand()=="Add") {
//				System.out.println("ADD "+e);
				IECTableModel tm = iectable.getIECModel();
				tm.addRow();
			}		
			if(e.getActionCommand()=="Clear") {
				IECTableModel tm = iectable.getIECModel();
				tm.clear();
			}	
	
		}
	}

	private Properties getVersionProperties(){
		Properties result= new Properties();
	    URL url = Server.class.getResource(VersionFile); 
//	    URL url2 =Server.class.getResource("/iec/net/applications/Images/IEC.PNG"); 
		log.fine("URL_V.Info "+url);	
//		log.info("URL_V2.Info "+url2);	
		if (url != null) {
			File f=null;
			try {
				f= IECFile.URLFileCopier(url,"version.ser");
			} catch (Exception e) {
				e.printStackTrace();
			}
			InputStream fis = null;
			try
				{
		    	  fis = new FileInputStream( f );
			      ObjectInputStream o = new ObjectInputStream( fis );
				  result =(Properties) o.readObject();
				}
				catch ( IOException e ) { System.err.println( e ); }
				catch ( ClassNotFoundException e ) { System.err.println( e ); }
				finally { try { fis.close(); }
							catch ( Exception e ) { } }		
	    		
			f.delete();
//			log.info("Props from ser.File "+ result);
		}
		return result;
	}
	
public Server()  {
	
	IECServer.log.addHandler(new IECServerTraceHandler(textField));
	final JSplitPane splitPane;
//	log.setUseParentHandlers(false);
//	IECServerTraceHandler th =new IECServerTraceHandler(null);
	IECServerTraceHandler th =new IECServerTraceHandler(textField);
//    th.setLevel(Level.INFO);
	log.addHandler(th);
//    log.addHandler(new IECServerTraceHandler(textField));

	log.fine("IEC Server start");	

	VERSION = getVersionProperties();
	setIconImage(Toolkit.getDefaultToolkit().getImage(Server.class.getResource("/iec/net/applications/Images/IEC.PNG")));
	setTitle("  IEC-TestServer");
	log.config("setTitle");	
	
	ServerAction =new serveraction();
	
	setBounds(100, 100, 1150, 450);
	getContentPane().setLayout(new BorderLayout(0, 0));
		
	iecttable =new IECTabedTable();
	if (usettable) {
		iectable = iecttable.iectable;		
	} else {
		iectable =new IECTable();
	}
	
	IECTableModel IECdata = iectable.getIECModel();
	
	IECdata.ieclist.setIECTCActionListener(actionlist);
	IECdata.ieclist.setSimEnabled(true);

//	textField = new JTextArea();
//	getContentPane().add(textField,BorderLayout.SOUTH);
	textField.setFont(new Font("Tahoma", Font.BOLD, 12));
	textField.setForeground(Color.YELLOW);
	textField.setBackground(Color.DARK_GRAY);
	textField.setRows(6);
			
	JScrollPane scrollPane = new JScrollPane(textField);
	scrollPane.setViewportView(textField);
//			getContentPane().add( scrollPane, BorderLayout.SOUTH );
			
	JTabbedPane tabbPane = new JTabbedPane(JTabbedPane.TOP);
	tabbPane.addTab("Trace", null, scrollPane, null);
			
	// Add Table to Split Panel 
//	if (usettable) {
//		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,iecttable,tabbPane);
//	} else {
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,iectable,tabbPane);
//	}
	
//    tabbPane.setBackground(Color.DARK_GRAY);
	tabbPane.addTab("Clients", null,Clientpanel, null);

//	OptionPanel oPanel = new OptionPanel();
	tabbPane.addTab("Options", null, oPanel, null);
	oPanel.setList(IECdata.ieclist);
	
//	 OptionPanel aPanel = new OptionPanel();
	 PorpertiesTable aPanel = new PorpertiesTable(VERSION);
	tabbPane.addTab("About", null, aPanel, null);

	getContentPane().add(splitPane, BorderLayout.CENTER);
	splitPane.setOneTouchExpandable(true);
	splitPane.setDividerLocation(200);
			
//	BP = new ButtonPanel(ServerAction,tf.data);
	BP = new ButtonPanel(ServerAction,IECdata);
	getContentPane().add( BP,BorderLayout.NORTH);
	
	JPanel Statuspanel = new JPanel();
	FlowLayout flowLayout = (FlowLayout) Statuspanel.getLayout();
	flowLayout.setAlignment(FlowLayout.LEFT);
	getContentPane().add(Statuspanel, BorderLayout.SOUTH);
	
	JLabel Statustext = new JLabel("V"+VERSION.getProperty("Version","??")+" by jkl");
	Statustext.setFont(new Font("Tahoma", Font.PLAIN, 10));
	Statuspanel.add(Statustext);
	
	}

	public void setProperties(Properties p) {
		log.info("set Properties");
		setTitle(p.getProperty("SERVER.TITLE",getTitle()));
		oPanel.IEC_small.setSelected(Boolean.parseBoolean(p.getProperty("SERVER.OPTIONS.IEC_SMALL",String.valueOf(oPanel.IEC_small.isSelected()))));
	}

	public static boolean iecstatus() {
		if (server==null){
			return false;
		}
		return server.Run;
	}
}
