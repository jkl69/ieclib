package jkl.iec.net.applications.panels;

import javax.swing.table.DefaultTableModel;

import jkl.iec.net.applications.Server;

@SuppressWarnings("serial")
public class ClientModel  extends DefaultTableModel{
//public class ClientModel  extends AbstractTableModel{

	String[] columnNames = {"Socket",
            "status",
            "statistc"};
	
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public int getRowCount() {
		if (Server.server!=null) {
			return 1+Server.server.socket.size();
		   } 
		return 1;
	}
	
//	System.out.println("get obj");
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		if (rowIndex==0) {
			if (Server.server!=null){
				
			}
			switch (columnIndex){
			case 0: return "Server";
			case 1: return Server.iecstatus();
			}
		}
		return null;
	}

}
