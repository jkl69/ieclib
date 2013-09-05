package jkl.iec.tc.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;


import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import jkl.iec.tc.type.IECList;
import jkl.iec.tc.type.IECMap;
import jkl.iec.tc.type.IECTCItem;

@SuppressWarnings("serial")
public class IECTableModel extends AbstractTableModel{

	public IECList ieclist =new IECList();
	int rc = 3;
	
	private String[] columnNames = {"Type","Name","ASDU","COT","IOB","Value","QU","TIME","","Sim","SimProp."};
	private int[] columnWith =     {150,    100,    20,   8,    30,    40,    5,  130,  10,   8,     70};
	
	public int getColumnWith(int col) {
		return columnWith[col];
	}
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	public String getColumnName(int col) {
		return columnNames[col];
    }
	
	public int getRowCount() {
//		System.out.println("getRowcount: "+ieclist.size());
		return ieclist.size();
	}
	
	public IECTCItem getItem(int r) {
		return ieclist.get(r);
	}
	
	@Override
	public Object getValueAt(int r, int c) {
		IECTCItem item = ieclist.get(r);
		final int row =r;
//		System.out.println("get obj");
		switch (c) {
		case 0 : return IECMap.getTypeDescription(item.getType());
		case 1 : return item.Name;
		case 2 : return item.getASDU();
		case 3 : return item.getCOT();
		case 4 : return item.iob(0).getIOB();
		case 5 : return item.iob(0).getValue();
//		case 5 : return item.iob(0).getValueasString();
		case 6 : {
		    StringBuilder sb = new StringBuilder();
	        sb.append(String.format("(0x%02X)",item.iob(0).getQU())); 
//		    return item.iob(0).getQU();
		    return sb.toString();
		}
		case 7 : {
			SimpleDateFormat df = new SimpleDateFormat("yy.MM.dd   HH:mm:ss,S");
			return df.format(item.iob(0).getTime());
		}
		case 8:  final JButton button = new JButton("tt");
				       button.addActionListener(new ActionListener() {
					   public void actionPerformed(ActionEvent e) {
//					    	JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(button),"Button clicked for row "+row);
					    	byte[] b =ieclist.get(row).getStream();
						    ieclist.sendByteArray(b,b.length);
					}
					});
				return button;
		case 9 : return item.flag1 ;
		case 10 : return item.data;
		}
		
		return null;  
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		
		System.out.println("CHANGE "+columnIndex+":"+rowIndex+" VAL: "+ aValue);
		IECTCItem item = ieclist.get(rowIndex);
		switch (columnIndex) {
		case 0 : {
			item.setType(IECMap.getType((String) aValue));
			break;
		}
		case 1 : {
			item.Name= ((String) aValue); 
			break;
			}
		case 2 : {
/*			try {
				int asdu = Integer.parseInt((String) aValue);
				item.setASDU(asdu); 
			}  catch (NumberFormatException e) {
				System.out.println(e);			
			}*/
			item.setASDU((Integer) aValue); 
			break;
			}
		case 3 : {
			item.setCOT((Integer) aValue); 
			break;
			}
		case 4 : {
/*			try {
				int iob = Integer.parseInt((String) aValue);
				item.setIOB(iob);
				}  catch (NumberFormatException e) {
				System.out.println(e);			
			}*/
			item.iob(0).setIOB((Integer) aValue); 
			break;
			}
		case 5 : {
			item.iob(0).setValue((Double) aValue);
			break;
			}
		case 6 : {
//			System.out.println("setQU "+aValue);	
			Integer qu = (Integer) aValue;
			item.iob(0).setQU(qu.byteValue());
			break;
			}
		case 9 : {
			if (item.data!= null) {
				item.flag1= (Boolean) aValue;
			}
		}
		}
	}
	
	public boolean isCellEditable(int r,int c) {
		if ((c==5)||(c==6)) {
			return false;
		}
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int c) {
		String s = columnNames[c];
//		System.out.println("getColumnClass for column  "+s);	
		if (s.equals("Type")) return String.class;
		if (s.equals("Name")) return String.class;
		if (s.equals("ASDU")) return Integer.class;
		if (s.equals("COT")) return Integer.class;
		if (s.equals("IOB")) return Integer.class;
		if (s.equals("Value")) return Double.class;
//		if (s.equals("Value")) return String.class;
//		if (s.equals("QU")) return Byte.class;
		if (s.equals("QU")) return String.class;
		if (s.equals("TIME")) return String.class;
		if (s.equals("Sim")) return Boolean.class;

		return Object.class;
	}

	public void addRow()  {
		ieclist.add();
//		System.out.println("listsize "+ieclist.size());		
    	fireTableRowsInserted(0, 0);
	}
	
	public void clear()  {
		int c = ieclist.size();
//		ieclist.remove(0);
		ieclist.clear();
	 	fireTableRowsDeleted(0,c);
	}
}
