package jkl.iec.tc.utils;


import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.CardLayout;

import java.awt.Color;

import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jkl.iec.tc.type.IECMap;
import jkl.iec.tc.type.IECTCItem;
import javax.swing.JTextPane;

@SuppressWarnings("serial")
public class IECSimDlg extends JDialog {

    ActionListener rbListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {   //close dialog by ESC or ENTER
        	if (! isfielderror()) {
    			System.out.println("Sim fields OK -> save");
        		if (IECMap.IEC_M_Type.contains(item.getType())) {
        			simworker.setTimerString(timeField.getText(),true);
        			simworker.setValinc(valinc);
        		}
        		if (IECMap.IEC_C_Type.contains(item.getType())) {
//                	if (simworker.isBackFile) {
//                    	}
        			simworker.setBackString(itemField.getText(),true);
        			simworker.setValinc(valinc);
        		}
    		} else {
    			System.out.println("Sim fields NOT OK -> NOT save");
    		}	
			setVisible(false);
        }
    };
	
    boolean isfielderror() {
    	boolean fielderror = false;
    	if (itemField.getText().startsWith("@")) return fielderror;
		if (timeField.getBackground()== Color.YELLOW) fielderror =true; 
		if (ValueField.getBackground()== Color.YELLOW) fielderror =true; 
		if (itemField.getBackground()== Color.YELLOW) fielderror =true; 
		if (itemvalueField.getBackground()== Color.YELLOW) fielderror =true; 
		return fielderror;
    }
    
    public final JPanel contentPanel = new JPanel();
    CardLayout layout =new CardLayout();
    private IECSimProperties simworker;
    public Object result = null;
	private IECTCItem item;
	Double valinc;
	
	private final JPanel M_Type_panel = new JPanel();
	private final JLabel timeLabel = new JLabel("Time Property");
	private JTextField timeField;
	private JTextField ValueField;
	private final JLabel timeErrorLabel = new JLabel(".");
	private final JLabel valuErrorLabel = new JLabel(".");
	private final JPanel C_Type_panel = new JPanel();
	private final JLabel itemlabel = new JLabel("Item adress");
	private final JTextField itemField = new JTextField();
	private final JTextField itemvalueField = new JTextField();
	private final JLabel itemvalueLabel = new JLabel("set item Value");
	private final JLabel itemErorrLabel = new JLabel(".");
	private final JLabel itemvalueErorrLabel = new JLabel(".");
	private final JTextPane CSimulText = new JTextPane();
	
	public IECSimDlg() {
		setUndecorated(true);
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
		
		contentPanel.add(M_Type_panel, "M_Type");
		C_Type_panel.setLayout(null);
		
		contentPanel.add(C_Type_panel, "C_Type");
		itemlabel.setBounds(10, 12, 101, 24);
		
		C_Type_panel.add(itemlabel);
		itemField.setToolTipText("/ASDU/TK/IOB");
		itemField.setColumns(10);
		itemField.setBounds(148, 15, 86, 20);
		
		C_Type_panel.add(itemField);
		itemField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent arg0) {
				warn();				
			}
			public void insertUpdate(DocumentEvent arg0) {
				warn();
			}
			public void removeUpdate(DocumentEvent arg0) {
				warn();
			}
			private void warn() {
				if (itemvalueField.getText().startsWith("@")) {
					return;
				}
				if (simworker.isBackString(itemField.getText())) {
					itemField.setBackground(Color.WHITE);					
					itemErorrLabel.setText("");	
				} else {
					itemField.setBackground(Color.YELLOW);
					itemErorrLabel.setText(simworker.lastErrorStr);	
				}
			}
		});
		itemvalueField.setToolTipText("Value Increment or  0 (set equal) ");
		itemvalueField.setColumns(10);
		itemvalueField.setBounds(148, 47, 86, 20);
		
		C_Type_panel.add(itemvalueField);
		itemvalueField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent arg0) {
				warn();				
			}
			public void insertUpdate(DocumentEvent arg0) {
				warn();
			}
			public void removeUpdate(DocumentEvent arg0) {
				warn();
			}
			private void warn() {
				itemvalueField.setBackground(Color.WHITE);					
				itemvalueErorrLabel.setText("");	
				try {
					valinc = Double.parseDouble(itemvalueField.getText());
					itemvalueField.setBackground(Color.WHITE);					
					itemvalueErorrLabel.setText("");	
				} catch (NumberFormatException e) {
					itemvalueField.setBackground(Color.YELLOW);
					itemvalueErorrLabel.setText("Number format error");	
				}
			}
		});
		itemvalueLabel.setBounds(10, 48, 109, 17);
		
		C_Type_panel.add(itemvalueLabel);
		itemErorrLabel.setForeground(Color.RED);
		itemErorrLabel.setBounds(10, 63, 259, 14);
		
		C_Type_panel.add(itemErorrLabel);
		itemvalueErorrLabel.setForeground(Color.RED);
		itemvalueErorrLabel.setBounds(10, 127, 259, 14);
		
		C_Type_panel.add(itemvalueErorrLabel);
		CSimulText.setText("test1\ntest2\ntest3");
		CSimulText.setBounds(30, 79, 239, 76);
		
		C_Type_panel.add(CSimulText);
		M_Type_panel.setLayout(null);
		timeLabel.setBounds(10, 35, 101, 24);
		M_Type_panel.add(timeLabel);
		
		timeField = new JTextField();
		timeField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent arg0) {
				warn();				
			}
			public void insertUpdate(DocumentEvent arg0) {
				warn();
			}
			public void removeUpdate(DocumentEvent arg0) {
				warn();
			}
			private void warn() {
				if (simworker.isTimerString(timeField.getText())) {
					timeField.setBackground(Color.WHITE);					
					timeErrorLabel.setText("");	
				} else {
					timeField.setBackground(Color.YELLOW);
					timeErrorLabel.setText(simworker.lastErrorStr);	
				}
			}
		});
		
//		timeField.setInputVerifier(verifier);
		timeField.setBounds(148, 37, 109, 20);
		M_Type_panel.add(timeField);
		timeField.setColumns(10);
		
		ValueField = new JTextField();
		ValueField.setToolTipText("");
		ValueField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent arg0) {
				warn();				
			}
			public void insertUpdate(DocumentEvent arg0) {
				warn();
			}
			public void removeUpdate(DocumentEvent arg0) {
				warn();
			}
			private void warn() {
				try {
					valinc = Double.parseDouble(ValueField.getText());
					ValueField.setBackground(Color.WHITE);					
					valuErrorLabel.setText("");	
				} catch (NumberFormatException e) {
					ValueField.setBackground(Color.YELLOW);
					valuErrorLabel.setText("Number format error");	
				}
			}
		});

		ValueField.setBounds(148, 96, 109, 20);
		M_Type_panel.add(ValueField);
		ValueField.setColumns(10);
		
		JLabel valueLabel = new JLabel("Value Property");
		valueLabel.setBounds(10, 99, 109, 17);
		M_Type_panel.add(valueLabel);
		timeErrorLabel.setForeground(Color.RED);
		timeErrorLabel.setBounds(10, 63, 259, 14);
		
		M_Type_panel.add(timeErrorLabel);
		valuErrorLabel.setForeground(Color.RED);
		valuErrorLabel.setBounds(10, 127, 259, 14);
		
		M_Type_panel.add(valuErrorLabel);

		}


	public void show(IECTCItem i) {
		item = i;
//		result = item.data;
		simworker = (IECSimProperties) i.data;
		timeField.setBackground(Color.WHITE);	
		ValueField.setBackground(Color.WHITE);	
		itemField.setBackground(Color.WHITE);	
		itemvalueField.setBackground(Color.WHITE);
		
		if (IECMap.IEC_M_Type.contains(item.getType())) {
			timeField.setText(simworker.getTimerString());
			ValueField.setText(String.valueOf(simworker.getValinc()));
			layout.show(contentPanel,"M_Type");
		}
		if (IECMap.IEC_C_Type.contains(item.getType())) {
			itemField.setText(simworker.getBackString());
			itemvalueField.setText(String.valueOf(simworker.getValinc()));
			layout.show(contentPanel,"C_Type");
		}

		setModal(true);
		setVisible(true);

	}
}
