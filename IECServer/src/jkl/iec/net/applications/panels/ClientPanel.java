package jkl.iec.net.applications.panels;

import jkl.iec.net.applications.Server;
import jkl.iec.net.gui.IECSocketDlg;
import jkl.iec.net.sockets.IECServer;
import jkl.iec.net.sockets.IECSocket;
import java.awt.Color;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * 
 */

//public class ClientPanel extends JTable implements ActionListener {
public class ClientPanel extends JScrollPane implements ActionListener {

	private final static Logger log = Logger.getLogger(ClientPanel.class .getName()); 
	
	private static final long serialVersionUID = 1L;
	
	private int row ;

	private IECSocket iecsock;
//	JScrollPane scrollPane = new JScrollPane(table);
	public JTable table ;//= new JTable();
	public TableModel clientmodel;
	
	private Logger logger; //logger for tabel row object
	
	final JPopupMenu popmen = new JPopupMenu();
	final JRadioButtonMenuItem tdMenuItem;
	final JRadioButtonMenuItem tiMenuItem;
	final JRadioButtonMenuItem teMenuItem;
	JMenuItem ex;
	
	public ClientPanel() {
//		log.severe("CLIENT");
		ButtonGroup group = new ButtonGroup();
		final JPopupMenu popmen = new JPopupMenu();
		
		tdMenuItem =new JRadioButtonMenuItem("FINE");
		group.add(tdMenuItem);
		JMenuItem mc=new JMenuItem("CONFIG");
		mc.addActionListener(this);
		popmen.add(mc);
		popmen.addSeparator();
		popmen.add(tdMenuItem);
		tiMenuItem =new JRadioButtonMenuItem("INFO");
		group.add(tiMenuItem);
		popmen.add(tiMenuItem);
		teMenuItem =new JRadioButtonMenuItem("SEVERE");
		group.add(teMenuItem);
		popmen.add(teMenuItem);
		popmen.addSeparator();
		ex=new JMenuItem("CLOSE");
		ex.addActionListener(this);
		popmen.add(ex);
		tdMenuItem.addActionListener(this);
		tiMenuItem.addActionListener(this);
		teMenuItem.addActionListener(this);
		
		
		setBackground(Color.DARK_GRAY);
//		clientmodel = new ClientModel();
//		table = new JTable(clientmodel);
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  
		
//		setPreferredSize(new Dimension(20000,20));
		
		getViewport().add( table );
		table.setFillsViewportHeight(true); 
//		table.setFillsViewportHeight(true); 
		String[] columnNames = {"ID","Socket",
                "status",
                "statistc"};
		
		table.setFont(new Font("Tahoma", Font.BOLD, 12));

		((DefaultTableModel) table.getModel()).setColumnIdentifiers(columnNames); 
		((DefaultTableModel) table.getModel()).addRow(new String[]{"","Server","stop",""});
//		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		table.getColumnModel().getColumn(2).setPreferredWidth(300);
//		table.getColumnModel().getColumn(3).setPreferredWidth(getWidth()-220);
		table.getColumnModel().getColumn(3).setPreferredWidth(500);
		
		table.setForeground(Color.ORANGE);
		table.setBackground(Color.DARK_GRAY);
		table.addMouseListener(new MouseListener(){
			@Override
			public void mouseReleased(MouseEvent me) {
     	         int column = table.getColumnModel().getColumnIndexAtX(me.getX());
	             row    = me.getY()/ table.getRowHeight();
     			 log.finest("mouseClicked on "+column+":"+row);
				 if (row < table.getRowCount()) {
					 if (row ==0) {
			     		 logger = IECServer.log;
			     		 while (logger.getLevel()==null) {
			     			 logger= logger.getParent();
			     		 }
						 if (Server.server == null) {
							ex.setText("START");
					    } else {
							ex.setText("STOP");
					    	}
					   } else {
    						ex.setText("CLOSE");
    						iecsock=Server.server.getSocket((Integer.parseInt((String) table.getValueAt( row,0))));
    						logger = iecsock.log;
    						log.finer("set client log"+iecsock.socket.getRemoteSocketAddress().toString()+"  "+logger.getName()+"  "+logger.getLevel());
    						Logger tmplogger = logger;
    						while (logger.getLevel()==null) {
    							tmplogger=tmplogger.getParent();
    							logger.setLevel(tmplogger.getLevel());
    						}
					   }
    			setLevel(logger.getLevel());
           		if ( me.isPopupTrigger() )
					        popmen.show( me.getComponent(), me.getX(), me.getY() );
					 }
				 }

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
	});
}

	private void setLevel(Level l) {
		 log.finest("Log.Level: "+l);
	     teMenuItem.setSelected(true) ;
		 if (l.intValue()<=Level.INFO.intValue()) {
		     tiMenuItem.setSelected(true) ;
		 }
		 if (l.intValue()<=Level.FINE.intValue()) {
		     tdMenuItem.setSelected(true) ;
		 }
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		 log.finest("actionEvent: "+e);
		 if (e.getActionCommand().equals("SEVERE")) {
			 logger.setLevel(Level.SEVERE);
		 }
		 if (e.getActionCommand().equals("INFO")) {
			 logger.setLevel(Level.INFO);
		 }
    	 if (e.getActionCommand().equals("FINE")) {
			 logger.setLevel(Level.FINE);
		 }
    	 if (e.getActionCommand().equals("START")) {
  		    Server.start();
 		 }
    	 if (e.getActionCommand().equals("STOP")) {
  		    Server.server.interrupt();
 		 }
    	 if (e.getActionCommand().equals("CLOSE")) {
			if (iecsock!=null) {
				iecsock.interrupt();
			}
		 }
    	 if (e.getActionCommand().equals("CONFIG")) {
    		IECSocketDlg dlg = null;
			log.fine("dlg");
			if (row == 0) {
				dlg = new IECSocketDlg(IECServer.iecSocketParameter);
				dlg.titledborder.setTitle("Default");

			}
			if (iecsock!=null) {
				dlg = new IECSocketDlg(iecsock.iecSocketParameter);
			}
			if (dlg!=null) {
				dlg.setLocation(MouseInfo.getPointerInfo().getLocation().x,MouseInfo.getPointerInfo().getLocation().y-150);
//				dlg.setLocation(e.getXOnScreen()-300,e.getYOnScreen());
				dlg.setModal(true);
			    dlg.setVisible(true);
			}
		 }
	}

	public void removeClient(IECSocket iecSock) {
		log.finer("ClientID: "+String.valueOf(iecSock.getSockID()));
		int rows=((DefaultTableModel) table.getModel()).getRowCount();
		log.finest("scan "+String.valueOf(rows)+" table rows");
		for (int row=1;row<=rows-1;row++) {
			int id=(Integer.parseInt((String) table.getValueAt(row, 0)));
			log.finest("ckeck row "+String.valueOf(row)+" table value: "+String.valueOf(id));
			if (id==iecSock.getSockID()){
				((DefaultTableModel) table.getModel()).removeRow(row);
				return;
			}
		}
	}
	
	public void addClient(IECSocket iecSock) {
		log.finest(iecSock.toString());
		log.finest("ClientID: "+String.valueOf(iecSock.getSockID()));
		((DefaultTableModel) table.getModel()).addRow(new String[]{String.valueOf(iecSock.getSockID()),iecSock.socket.getRemoteSocketAddress().toString(),"??",""});
	}
	public void update(IECSocket iecSock) {
		int rows=((DefaultTableModel) table.getModel()).getRowCount();
		log.finest("ClientID:"+String.valueOf(iecSock.getSockID())+"  rows:"+String.valueOf(rows));
		for (int row=1;row<=rows-1;row++) {
			int id=(Integer.parseInt((String) table.getValueAt(row, 0)));
			if (id==iecSock.getSockID()){
				table.setValueAt(iecSock.getIECstatus(), row, 2);
			}
		}
		
	}
	
}
