package jkl.iec.tc.gui;

import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class IECTabedTable extends JTabbedPane  {
	
	public IECTable iectable = new IECTable();
	public IECTable t;
	
	public IECTableModel getIECModel()  {
//indexOfTabComponent(ASDUTab.this)
        t = (IECTable) getComponentAt(getSelectedIndex()); 
		return t.getIECModel();
//		return iectable.getIECModel();
	}
    
	public IECTabedTable() {
		
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
//		TableColumnModel colModel = iectable.table.getColumnModel();
//		colModel.removeColumn(colModel.getColumn(2));

//		addTab("ASDU 3456", null,iectable, null);
//		addTab("ASDU 2", null, new JButton(), null);
		addTab("ASDU", null, null, null);
		setTabComponentAt(0, new NewTab(this));
		}
}
