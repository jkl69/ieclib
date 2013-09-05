package jkl.iec.tc.gui;


import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.CardLayout;



import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.Box;

import javax.swing.JCheckBox;
import java.awt.Color;
import java.awt.Font;
import javax.swing.border.MatteBorder;

import jkl.iec.tc.type.IECTCItem;

@SuppressWarnings("serial")
public class IECQuDlg extends JDialog {

    ActionListener rbListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
    		result =0;
    		switch (item.getType()) {
    		case M_IT_NA :case M_IT_TB : {
 //   			System.out.println("Quality IT Changed");
    			if (cb_it_IV.isSelected()) result = result + 0x80;
    			if (cb_it_CA.isSelected()) result = result + 0x40;
    			if (cb_it_CY.isSelected()) result = result + 0x20;
    			break;  
    		}
    		default: 
//    			System.out.println("Quality DEF Changed");
    			if (cb_IV.isSelected()) result = result + 0x80;
    			if (cb_NT.isSelected()) result = result + 0x40;
    			if (cb_SB.isSelected()) result = result + 0x20;
    			if (cb_BL.isSelected()) result = result + 0x10;
    			if (cb_OV.isSelected()) result = result + 0x01;
    			break;
    		}
			System.out.println("Quality result: "+result);
			setVisible(false);
        }
    };
	

    public final JPanel contentPanel = new JPanel();
	public int result=0;
	
	private CardLayout layout =new CardLayout();
	private IECTCItem item;
	
	private final JPanel DEF_panel = new JPanel();
	private final JPanel IT_Panel = new JPanel();
	
	private final JCheckBox cb_IV = new JCheckBox("IV (0x80)");
	private final JCheckBox cb_NT = new JCheckBox("NT (0x40)");
	private final JCheckBox cb_SB = new JCheckBox("SB (0x20)");
	private final JCheckBox cb_BL = new JCheckBox("BL (0x10)");
	private final Component verticalStrut_1 = Box.createVerticalStrut(10);
	private final JCheckBox cb_OV = new JCheckBox("OV (0x01)");
	private final Component verticalStrut = Box.createVerticalStrut(10);
	private final JCheckBox cb_it_IV = new JCheckBox("IV (0x80)");
	private final JCheckBox cb_it_CA = new JCheckBox("CA (0x40)");
	private final JCheckBox cb_it_CY = new JCheckBox("CY (0x20)");
	private final Component verticalStrut_2 = Box.createVerticalStrut(20);
	
	public IECQuDlg() {
		setUndecorated(true);
		setTitle("Set Quality");
		getContentPane().setBackground(Color.BLACK);
		setBackground(Color.BLACK);
		setBounds(100, 100, 297, 173);
	    
		getRootPane().registerKeyboardAction(rbListener,
	            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	            JComponent.WHEN_IN_FOCUSED_WINDOW);
		getRootPane().registerKeyboardAction(rbListener,
	            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
	            JComponent.WHEN_IN_FOCUSED_WINDOW);

	    BorderLayout borderLayout = new BorderLayout();
	    borderLayout.setHgap(1);
	    getContentPane().setLayout(borderLayout);
		contentPanel.setBorder(new MatteBorder(3, 3, 3, 3, (Color) new Color(0, 0, 0)));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(layout);
		
		contentPanel.add(DEF_panel, "DEF");
		contentPanel.add(IT_Panel, "IT");
		
		IT_Panel.setLayout(new BoxLayout(IT_Panel, BoxLayout.Y_AXIS));
		
		IT_Panel.add(verticalStrut_2);
		cb_it_IV.setAlignmentX(Component.CENTER_ALIGNMENT);
		cb_it_IV.setFont(new Font("Tahoma", Font.BOLD, 12));
		IT_Panel.add(cb_it_IV);
		cb_it_CA.setAlignmentX(Component.CENTER_ALIGNMENT);
		cb_it_CA.setFont(new Font("Tahoma", Font.BOLD, 12));
		IT_Panel.add(cb_it_CA);
		cb_it_CY.setAlignmentX(Component.CENTER_ALIGNMENT);
		cb_it_CY.setFont(new Font("Tahoma", Font.BOLD, 12));
		IT_Panel.add(cb_it_CY);
		
		
		DEF_panel.setLayout(new BoxLayout(DEF_panel, BoxLayout.Y_AXIS));
		DEF_panel.add(verticalStrut_1);
				
		cb_IV.setAlignmentX(Component.CENTER_ALIGNMENT);
		cb_IV.setFont(new Font("Tahoma", Font.BOLD, 12));
		DEF_panel.add(cb_IV);
				
		cb_NT.setAlignmentX(Component.CENTER_ALIGNMENT);
		cb_NT.setFont(new Font("Tahoma", Font.BOLD, 12));
		DEF_panel.add(cb_NT);
				
		cb_SB.setFont(new Font("Tahoma", Font.BOLD, 12));
		cb_SB.setAlignmentX(0.5f);
		DEF_panel.add(cb_SB);
				
		cb_BL.setFont(new Font("Tahoma", Font.BOLD, 12));
		cb_BL.setAlignmentX(0.5f);
		DEF_panel.add(cb_BL);
				
		DEF_panel.add(verticalStrut);
		
		cb_OV.setAlignmentX(Component.CENTER_ALIGNMENT);
		cb_OV.setFont(new Font("Tahoma", Font.BOLD, 12));
		DEF_panel.add(cb_OV);

		}


	public void show(IECTCItem i) {
		item = i;
		result = item.iob(0).getQU();
//		System.out.println("Quality :"+item.iob(0).getQU());
		switch (item.getType()) {
		case M_IT_NA : case M_IT_TB : {
			layout.show(contentPanel,"IT");
			cb_it_IV.setSelected((result & 0x80) == 0x80);
			cb_it_CA.setSelected((result & 0x40) == 0x40);
			cb_it_CY.setSelected((result & 0x20) == 0x20);
			break;  
		}
		default: {
			layout.show(contentPanel,"DEF");
			cb_IV.setSelected((result & 0x80) == 0x80);
			cb_NT.setSelected((result & 0x40) == 0x40);
			cb_SB.setSelected((result & 0x20) == 0x20);
			cb_BL.setSelected((result & 0x10) == 0x10);
			switch (item.getType()) {
			case M_SP_NA :case M_SP_TB : case M_DP_NA :case M_DP_TB :{
				cb_OV.setSelected(false);
				cb_OV.setVisible(false);
				break;  
			}
			default: {
				cb_OV.setSelected((result & 0x01) == 0x01);
				cb_OV.setVisible(true);				
				break;
			}
			}
			break;
			}
		}

		setModal(true);
		setVisible(true);

	}
}
