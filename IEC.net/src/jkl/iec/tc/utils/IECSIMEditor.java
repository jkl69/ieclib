package jkl.iec.tc.utils;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.JTextField;
import javax.swing.JLabel;

import jkl.iec.tc.type.IECMap;
import jkl.iec.tc.type.IECMap.IECType;


@SuppressWarnings("serial")
public class IECSIMEditor extends AbstractCellEditor 
						  implements TableCellEditor, ActionListener  {
	
	private static final String EDIT = "edit";
	private String result="";
	private final JPanel contentPanel = new JPanel();
	private IECSimProperties sim;
	
	JButton button;
	JDialog dialog;

	JButton okButton;
	JButton cancelButton;
	private JTextField propertyText;
	private JLabel propertyLabel;
	private JTextField valueText;
	private JLabel valueLabel;

	/**
	 * Create the dialog.
	 */
	public IECSIMEditor() {
        button = new JButton("SIMParam.");
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(true);
        
        dialog = new JDialog();
        dialog.setResizable(false);
        dialog.setTitle("Simulation Properties");
		dialog.setBounds(400, 400, 248, 153);
		dialog.getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		dialog.getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		propertyLabel = new JLabel("Time Property");
		propertyLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		contentPanel.add(propertyLabel);
		propertyText = new JTextField();
		contentPanel.add(propertyText);
		propertyText.setColumns(10);
		
		valueLabel = new JLabel("Value Property");
		valueLabel.setToolTipText("Value Increment that is added each simulation cycle");
		contentPanel.add(valueLabel);
		
		valueText = new JTextField();
		contentPanel.add(valueText);
		valueText.setColumns(10);
		valueText.setToolTipText("Value Increment that is added each simulation cycle");

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			dialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);

				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(this);
				buttonPane.add(okButton);
				dialog.getRootPane().setDefaultButton(okButton);

				cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(this);
				buttonPane.add(cancelButton);
		}
	}  // End Dialog Create
	
	private void setDialogProperties() {
		System.out.print("setDialogProperties: ");
		if (IECMap.IEC_M_Type.contains(sim.item.getType())) {
			set_M_Type_dialog();
		} else {
			set_C_Type_dialog();
		}
	}
	private void set_M_Type_dialog() {
		propertyLabel.setText("Time Property");
		propertyText.setText(sim.getTimerString());
//		valueLabel.setVisible(true);
//		valueText.setVisible(true);
		if ((sim.item.getType() == IECType.M_SP_NA)||
			(sim.item.getType() == IECType.M_SP_TB)) {
			valueText.setEditable(false);
		} else {
			valueText.setEditable(true);			
		}
		valueText.setText(String.valueOf(sim.getValinc()));
		System.out.println("Value: "+sim.getValinc());
	}
	private void set_C_Type_dialog() {
		propertyLabel.setText("Reaction Property");
		propertyText.setText(sim.getBackString());
		valueText.setText(String.valueOf(sim.getValinc()));
//		valueLabel.setVisible(false);
//		valueText.setVisible(false);
	}
	
	private boolean set_M_Type_Properties() {
		result="";
		boolean timeOK = sim.setTimerString(propertyText.getText(),true);
		if (!timeOK) {
			  result = "Time Property is not valid ";	
		}
		try {
			sim.setValinc(Double.parseDouble(valueText.getText()));
		} catch (NumberFormatException e) {
		  result = "Value Property is not valid ";
		  return false;	
		}
		return timeOK;
	}
	private boolean set_C_Type_Properties() {
		result="";
		boolean backOK = sim.setBackString(propertyText.getText(),true);
		if (!backOK) {
			  result = "Reaction Property is not valid ";	
		}
		try {
			sim.setValinc(Double.parseDouble(valueText.getText()));
		} catch (NumberFormatException e) {
		  result = "Value Property is not valid ";
		  return false;	
		}
		return backOK;
	}
	
	private boolean setSimProperties() {
		if (IECMap.IEC_M_Type.contains(sim.item.getType())) {
			return set_M_Type_Properties()	; 
		} else {
			return set_C_Type_Properties()	; 
		}
	}
	
	@Override
	public Object getCellEditorValue() {
		return sim;
//		return result;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
											boolean isSelected, int row, int col) {
//		System.out.println("getTableCellEditorComponent: "+button);
		sim = (IECSimProperties) value;
		System.out.println("SIMValue: "+sim.getValinc());
		int posX = table.getLocationOnScreen().x+table.getCellRect(row,col,false).getLocation().x;
		int posY = table.getLocationOnScreen().y+ table.getCellRect(row,col,false).getLocation().y;
		System.out.println("CellPos X "+posX+"/Y "+posY);  //248, 153
		dialog.setLocation(posX-250,posY-70);
		return button;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
//		System.out.println("actionPerformed: "+e);
		if (EDIT.equals(e.getActionCommand())) {
            //The user has clicked the cell, so bring up the dialog.
			setDialogProperties();
			dialog.setModal(true);
        	dialog.setVisible(true);
            fireEditingStopped(); //Make the renderer reappear.
        } 
		if  (okButton.getActionCommand().equals(e.getActionCommand())) {
            //The user has clicked the cell, so bring up the dialog.
			System.out.print("OK Button: ");
        	if (setSimProperties()) {
    			System.out.println(" Properties OK");
    			dialog.setVisible(false);
        	} else {
    			System.out.println(" Properties NOT-OK"); 
    			JOptionPane.showMessageDialog(dialog, result,"Message",JOptionPane.WARNING_MESSAGE);
    			setDialogProperties();
        	}
//            fireEditingStopped(); //Make the renderer reappear.
        } 
		if  (cancelButton.getActionCommand().equals(e.getActionCommand())) {
            //The user has clicked the cell, so bring up the dialog.
			System.out.println("chancel Button: ");
			dialog.setVisible(false);
//            fireEditingStopped(); //Make the renderer reappear.
        }
	}

}
