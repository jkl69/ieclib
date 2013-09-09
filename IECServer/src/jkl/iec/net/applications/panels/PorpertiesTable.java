package jkl.iec.net.applications.panels;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

//public class PorpertiesTable extends JTable  implements MouseListener {
public class PorpertiesTable extends JScrollPane implements MouseListener {
	
	private Properties properties;
	private JTable table= null;
	
	private boolean isURL(String value ){
		return value.startsWith("http");
	}

	class MTR extends DefaultTableCellRenderer {
		  @Override
		  public void setValue( Object value )
		  {
		    
//		    if (value.toString().startsWith("http"))
		    if (isURL(value.toString()))  {
		      setForeground(Color.BLUE);
		      setText( value.toString() );
		    }
		    else
		       setForeground(Color.BLACK);
		       setText ( value.toString() );
		  }

		  }

    
	public PorpertiesTable(Properties p) {
	   this.properties = p;
	   table = new JTable(createPropertiesTableModel());
	   getViewport().add( table );
	   table.setRowSelectionAllowed(false);
//	   table.setModel(createPropertiesTableModel());
	   table.getColumnModel().getColumn(1).setCellRenderer(new MTR());
	   table.addMouseListener(this);
	}
	
	private DefaultTableModel createPropertiesTableModel() {
        DefaultTableModel model = new DefaultTableModel();
 
        model.addColumn("Property");
        model.addColumn("Value");
 
        Set<Object> keys = properties.keySet();
        SortedSet<Object> sortedKeys = new TreeSet<Object>(keys);
        Iterator<Object> iter = sortedKeys.iterator();
 
        while (iter.hasNext()) {
            String key = iter.next().toString();
            String value = properties.getProperty(key);
            String[] row = { key, value };
            model.addRow(row);
        	}
 
        return model;
    }

	@Override
	public void mouseClicked(MouseEvent e) {
//		    JTable table = (JTable)e.getSource();
		    Point pt = e.getPoint();
		    int ccol = table.columnAtPoint(pt);
		    int crow = table.rowAtPoint(pt);
//		    System.out.println(crow+":"+ccol);
		    String urltxt =table.getValueAt(crow, ccol).toString();

		    if(isURL(urltxt)) { // && pointInsidePrefSize(table, pt)) {
		    	   URL url = null;
				try {
					url = new URL(urltxt);
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		      System.out.println(url);
		      try{
		        //Web Start
		        //BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
		        //bs.showDocument(url);
		        if(Desktop.isDesktopSupported()) { // JDK 1.6.0
		          Desktop.getDesktop().browse(url.toURI());
		        }
		      }catch(Exception ex) {
		        ex.printStackTrace();
		      }
		    }
		}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
