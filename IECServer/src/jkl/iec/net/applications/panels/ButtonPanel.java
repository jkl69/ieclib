package jkl.iec.net.applications.panels;

import jkl.iec.tc.gui.IECComboBox;
import jkl.iec.tc.gui.IECTableModel;
import jkl.iec.tc.type.IECMap;

import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ButtonPanel extends JPanel{
	
	public JButton StartButton;
	public JButton StopButton;
	public JTextField PortNo;
	
	public ButtonPanel(ActionListener e,final IECTableModel t) {
	 setLayout(new FlowLayout(FlowLayout.CENTER, 50, 5));
	
	JPanel itemPanel = new JPanel();
	FlowLayout flowLayout = (FlowLayout) itemPanel.getLayout();
	flowLayout.setAlignment(FlowLayout.LEFT);
	add(itemPanel);
	
	IECComboBox iecBox = new IECComboBox();
	itemPanel.add(iecBox);
	iecBox.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			IECComboBox cb = (IECComboBox)e.getSource();
			t.ieclist.iectype= IECMap.getType((String) cb.getSelectedItem()); 
		}
	});
	
	// Create some function buttons
	JButton addButton = new JButton( "Add" );
	itemPanel.add(addButton);
	addButton.addActionListener(e);
	JButton delButton = new JButton( "Clear" );
	itemPanel.add(delButton);
	delButton.addActionListener(e);
	JCheckBox mastersim = new JCheckBox("Sim");
	itemPanel.add(mastersim);
	mastersim.setSelected(true);
	
	mastersim.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == 1) {
				System.out.println("MasterSimulaten Enabled");
				t.ieclist.iecsimulator.enabled=true;
			} else {
				System.out.println("MasterSimulaten Disabled");
				t.ieclist.iecsimulator.enabled=false;
			}
		}
	});	

	JPanel serverPanel = new JPanel();
	add(serverPanel);
			
	StartButton = new JButton( "StartServer" );
	serverPanel.add(StartButton);
	StartButton.addActionListener(e);
			
	PortNo = new JTextField();
	serverPanel.add(PortNo);
	PortNo.setText("2404");
	PortNo.setColumns(10);
			
	StopButton = new JButton("StopServer");
	serverPanel.add(StopButton);
	StopButton.addActionListener(e);	
	StopButton.setEnabled(false);
	
	JPanel filePanel = new JPanel();
	FlowLayout flowLayout_1 = (FlowLayout) filePanel.getLayout();
	flowLayout_1.setAlignment(FlowLayout.RIGHT);
	add(filePanel);
			
	JButton saveButton = new JButton("Save");
	filePanel.add(saveButton);
	saveButton.addActionListener(e);	
			
	JButton loadButton = new JButton("Load");
	filePanel.add(loadButton);
	loadButton.addActionListener(e);
	}
	public int getPort() {
	try {
		return Integer.parseInt(PortNo.getText());
	} catch (NumberFormatException ex) {
		return 2404;	
	}
	}

}
