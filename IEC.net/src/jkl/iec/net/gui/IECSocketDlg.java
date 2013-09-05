package jkl.iec.net.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import jkl.iec.net.sockets.IECSocketParameter;

public class IECSocketDlg extends JDialog {
	
	private ActionListener rbListener =new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent arg0) {
			updateIECParam();
			setVisible(false);		}
		
	};
	
	IECSocketParameter iecparam;
	
	public TitledBorder titledborder;
	private JFormattedTextField TextT0;  //new JFormattedTextField(NumberFormat.getIntegerInstance());
	private JFormattedTextField TextT1;
	private JFormattedTextField TextT2;
	private JFormattedTextField TextT3;
	private JFormattedTextField Textw; 
	private JFormattedTextField Textk; 
	private NumberFormat f = NumberFormat.getNumberInstance(); 
	private NumberFormat s = NumberFormat.getIntegerInstance(); 
	
private void updateIECParam() {
	try {
		iecparam.T0=(Long) f.parse(TextT0.getText());
		iecparam.T1=(Long) f.parse(TextT1.getText());
		iecparam.T2=(Long) f.parse(TextT2.getText());
		iecparam.T3=(Long) f.parse(TextT3.getText());
//		iecparam.K= (Integer) s.parse(Textk.getText());
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
//	iecparam.T0=Integer.parseInt(TextT0.getText());
	iecparam.W=Integer.parseInt(Textw.getText());
	iecparam.K=Integer.parseInt(Textk.getText());
	}

private void updateGUI() {
//	jkl.iec.net.sockets.IECSocketParameter.IECSocketType.IECServer;
    
    TextT0.setText(f.format(iecparam.T0));
	TextT1.setText(f.format(iecparam.T1));
	TextT2.setText(f.format(iecparam.T2));
	TextT3.setText(f.format(iecparam.T3));
//	   TextT0.setText(String.valueOf(iecparam.T0));
	Textw.setText(String.valueOf(iecparam.W));
	Textk.setText(String.valueOf(iecparam.K));
	}

public IECSocketDlg(IECSocketParameter p) {
		setUndecorated(true);
		this.iecparam =p;
//		System.out.println(iecparam.T0);
		getContentPane().setBackground(Color.BLACK);
		setBackground(Color.BLACK);
		setBounds(100, 100, 200, 255);
	    
		getRootPane().registerKeyboardAction(rbListener,
	            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	            JComponent.WHEN_IN_FOCUSED_WINDOW);
		getRootPane().registerKeyboardAction(rbListener,
	            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
	            JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		panel = new JPanel();
		titledborder =new TitledBorder(new LineBorder(new Color(0, 0, 0), 3), "IEC Socketproperties", TitledBorder.CENTER, TitledBorder.BELOW_TOP, null, null);
		panel.setBorder(titledborder);
		getContentPane().add(panel, BorderLayout.CENTER);
		
		f = NumberFormat.getNumberInstance(); 
		f.setMaximumIntegerDigits(5);
		TextT0 = new JFormattedTextField(f);
		TextT0.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), new Object());
		TextT0.setBounds(91, 32, 64, 20);
		TextT0.setToolTipText("server expect polling time out");
		TextT1 = new JFormattedTextField(f);
		TextT1.setToolTipText("The socket expect an acknowledge for transmitted data after this time");
		TextT1.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), new Object());
		TextT1.setBounds(91, 58, 64, 20);
		TextT2 = new JFormattedTextField(f);
		TextT2.setToolTipText("The socket has to acknowledge on received data after this time");
		TextT2.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), new Object());
		TextT2.setBounds(91, 84, 64, 20);
		TextT3 = new JFormattedTextField(f);
		TextT3.setToolTipText("Timeout for polling (testFrames) if no Data transmission");
		TextT3.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), new Object());
		TextT3.setBounds(91, 110, 64, 20);
		s = NumberFormat.getNumberInstance(); 
		s.setMaximumIntegerDigits(2);
		Textw = new JFormattedTextField(s);
		Textw.setToolTipText("The socket has to acknowledge for received data after this amount of messages");
		Textw.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), new Object());
		Textw.setBounds(91, 177, 64, 20);
		Textk = new JFormattedTextField(s);
		Textk.setToolTipText("The socket expect an acknowledge for transmitted data after this amount of messages");
		Textk.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), new Object());
		Textk.setBounds(91, 203, 64, 20);
		
		JLabel LabelT0 = new JLabel("T0");
		LabelT0.setBounds(35, 35, 18, 14);
		LabelT0.setToolTipText("reconnect timeout for Client Sockets\r\n");
		LabelT0.setLabelFor(TextT0);
		
		JLabel labelT1 = new JLabel("T1");
		labelT1.setBounds(35, 61, 18, 14);
		labelT1.setLabelFor(TextT1);
		
		JLabel labelT2 = new JLabel("T2");
		labelT2.setBounds(35, 87, 18, 14);
		
		JLabel labelT3 = new JLabel("T3");
		labelT3.setBounds(35, 113, 18, 14);
		
		JLabel labelw = new JLabel("w");
		labelw.setBounds(35, 180, 18, 14);
		
		JLabel labelk = new JLabel("k");
		labelk.setBounds(35, 206, 18, 14);
		
		panel.setLayout(null);
		panel.add(LabelT0);
		panel.add(labelT1);
		panel.add(labelT2);
		panel.add(labelT3);
		panel.add(labelw);
		panel.add(labelk);
		panel.add(Textk);
		panel.add(Textw);
		panel.add(TextT3);
		panel.add(TextT2);
		panel.add(TextT1);
		panel.add(TextT0);
		
		updateGUI();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel panel;
}
