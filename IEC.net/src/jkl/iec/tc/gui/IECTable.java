package jkl.iec.tc.gui;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import jkl.iec.tc.type.IECMap;
import jkl.iec.tc.type.IECTCItem;
import jkl.iec.tc.utils.IECSIMEditor;
import jkl.iec.tc.utils.IECSimDlg;

@SuppressWarnings("serial")

public class IECTable extends JPanel  {
	
	IECValDlg valdlg = new IECValDlg();
	IECQuDlg qudlg = new IECQuDlg();
	IECSimDlg SimDlg =new IECSimDlg();
	IECComboBox comboBox = new IECComboBox();
	
	IECSIMEditor simdlg = new IECSIMEditor();

	class ButtonTableCellRenderer extends DefaultTableCellRenderer{
		   private JButton button = new JButton();
		   public Component getTableCellRendererComponent(JTable table, Object value,
		         boolean isSelected, boolean hasFocus, int row, int column) {
			   IECTCItem item = data.getItem(row);
//			   System.out.println("Cellrender.item.object:"+item);
			   if (column==10) {
			       button.setText("SIMParam.");
			       return button;
		       } 
			   if ((column==8)&&(IECMap.IEC_M_Type.contains(item.getType()))) {
			       button.setText("Send");  
			       return button;
		       }
			   return null;
		   }
		}
	
	class JTableButtonMouseListener extends MouseAdapter {
		  private final JTable table;
				
		  public JTableButtonMouseListener(JTable table) {
		    this.table = table;
		  }
		  @Override 
		  public void mouseClicked(MouseEvent e) {
		  int column = table.getColumnModel().getColumnIndexAtX(e.getX());
          int row    = e.getY()/table.getRowHeight();
//          data.ieclist.size();
          System.out.println("mouseClicked on"+column+":"+row+ "  dataElemets:"+ data.ieclist.size());
		  if (row >= data.ieclist.size()) {
			  return;
		  }
			IECTCItem it = data.getItem(row);
		    if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
		      Object value = table.getValueAt(row, column);
	        
			 if (column == 10) {
				 SimDlg.setLocation(e.getXOnScreen()-300,e.getYOnScreen());
				 SimDlg.show(it);
//				 table.setValueAt(SimDlg.result,row,column);
			      }

			 if ((column == 6)&&(IECMap.IEC_M_Type.contains(it.getType()))) {
				 qudlg.setLocation(e.getXOnScreen()-300,e.getYOnScreen());
				 qudlg.show(it);
				 table.setValueAt(qudlg.result,row,column);
		      }
	     
			if ((column == 5)&&(IECMap.IEC_M_Type.contains(it.getType()))) {
					valdlg.setLocation(e.getXOnScreen()-300,e.getYOnScreen());
					valdlg.show(it);
					table.setValueAt(valdlg.result,row,column);
			      } 
			if (column == 8) {		//Send Stream Button
				if (IECMap.IEC_M_Type.contains(it.getType())) {
			        ((JButton)value).doClick();
				}
				//}
			    }
		    }
		  }
		}
	
	private IECTableModel data =new IECTableModel();
	public JTable table;
	
	public IECTableModel getIECModel()  {
		return data;
	}
	
	public IECTable() {
//	public IECTable(IECTableModel tm) {

	setLayout( new BorderLayout() );

//	valdlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);	
    
//		IECmodel = new DefaultTableModel(rows,col);
//		IECmodel = new IECTableModel();
	table = new JTable(data);
	
	table.setCellSelectionEnabled(false);
	table.setRowSelectionAllowed(false);
	table.setColumnSelectionAllowed(false);
	table.addMouseListener(new JTableButtonMouseListener(table));
	
	TableColumnModel colModel = table.getColumnModel();

	table.setFillsViewportHeight(true);   //zeigt table bis zum ende;
	colModel.getColumn(0).setCellEditor(new DefaultCellEditor(comboBox));

	ButtonTableCellRenderer buttonbellrenderer = new ButtonTableCellRenderer();
	colModel.getColumn(8).setCellRenderer(buttonbellrenderer);
	colModel.getColumn(10).setCellRenderer(buttonbellrenderer);
//	colModel.getColumn(10).getCellRenderer();

	
	TableColumn column = null;
	for (int i = 0; i < 10; i++) {
	    column = colModel.getColumn(i);
        column.setPreferredWidth(data.getColumnWith(i));
	    }
	
	JScrollPane scrollPane = new JScrollPane(table);
//	JScrollPane scrollPane = new JScrollPane();
	scrollPane.getViewport().add( table );

	add( scrollPane, BorderLayout.CENTER );
	}
}
