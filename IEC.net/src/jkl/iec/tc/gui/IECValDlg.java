package jkl.iec.tc.gui;


import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.CardLayout;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JFormattedTextField;
import javax.swing.JCheckBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.Color;
import java.awt.Font;
import javax.swing.border.MatteBorder;

import jkl.iec.tc.type.IECTCItem;

@SuppressWarnings("serial")
public class IECValDlg extends JDialog {

    ActionListener rbListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
//			System.out.println(e);
			if (e.getActionCommand()==null) {
				setVisible(false);
				return;
			}
			if (e.getActionCommand().equals("off")|(e.getActionCommand().equals("00"))) {
				result= 0.0;
			}
			if (e.getActionCommand().equals("on")|(e.getActionCommand().equals("01"))) {
				result= 1.0;
			}
			if (e.getActionCommand().equals("10")) {
				result= 2.0;
			}
			if (e.getActionCommand().equals("11")) {
				result= 3.0;
			}
//			if (e.getActionCommand().equals("Exclude IV-States")) {
//				result= 3.0;
//			}
			setVisible(false);
        }
    };
	
    public final JPanel contentPanel = new JPanel();
	Double result=1.0;
	
	private final JPanel buttonPane; 
	private final JButton okButton;
	CardLayout layout =new CardLayout();
	JRadioButton rb_off = new JRadioButton("off");
	JRadioButton rb_on = new JRadioButton("on");
	JSlider ME_slider = new JSlider();
	private final Component horizontalStrut = Box.createHorizontalStrut(55);
	private final Component horizontalStrut_1 = Box.createHorizontalStrut(30);
	private final JLabel max_lable = new JLabel("MAX");
	private final JFormattedTextField ME_textValue = new JFormattedTextField();
	private final JPanel limit_Panel = new JPanel();
	private final JLabel min_lable = new JLabel("MIN");
	private final JPanel v_Panel = new JPanel();
	private final JLabel v_lable = new JLabel("Value");
	private final Component horizontalStrut_2 = Box.createHorizontalStrut(20);
	private final JFormattedTextField min_text = new JFormattedTextField();
	private final JFormattedTextField max_text = new JFormattedTextField();
	private IECTCItem item;
	private final JPanel DP_Panel = new JPanel();
	private final JRadioButton rb_10 = new JRadioButton("10");
	private final JRadioButton rb_00 = new JRadioButton("00");
	private final JRadioButton rb_01 = new JRadioButton("01");
	private final JRadioButton rb_11 = new JRadioButton("11");
	private final JCheckBox cb_likeSP = new JCheckBox("Exclude IV-States");
	private final JPanel panel = new JPanel();
	private final JPanel panel_1 = new JPanel();
	private final Component verticalStrut = Box.createVerticalStrut(25);
	private final JPanel IT_Panel = new JPanel();
	private final JFormattedTextField it_text = new JFormattedTextField();
	private final JPanel IEEE_panel = new JPanel();
	private final JPanel IEEE_Limit = new JPanel();
	private final JLabel label = new JLabel("MIN");
	private final JFormattedTextField min_text_ieee = new JFormattedTextField(new Float(0.0));
	private final JLabel label_1 = new JLabel("MAX");
	private final JFormattedTextField max_text_ieee = new JFormattedTextField(new Float(0.0));
	private final JSlider IEEE_slider = new JSlider();
	private final JPanel IEEE_Value = new JPanel();
	private final JLabel label_2 = new JLabel("Value");
	private final JFormattedTextField v_text_ieee = new JFormattedTextField(new Float(0.0));
	
	public IECValDlg() {
		setUndecorated(true);
		setTitle("Set Value");
		getContentPane().setBackground(Color.BLACK);
		setBackground(Color.BLACK);
		setBounds(100, 100, 297, 173);
	    
		getRootPane().registerKeyboardAction(rbListener,
	            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	            JComponent.WHEN_IN_FOCUSED_WINDOW);

	    BorderLayout borderLayout = new BorderLayout();
	    borderLayout.setHgap(1);
	    getContentPane().setLayout(borderLayout);
		contentPanel.setBorder(new MatteBorder(3, 3, 0, 3, (Color) new Color(0, 0, 0)));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(layout);
		
		contentPanel.add(IEEE_panel, "IEEE");
		IEEE_panel.setLayout(new BorderLayout(0, 0));
		IEEE_slider.setPaintTicks(true);
		IEEE_slider.setPaintLabels(true);
		IEEE_slider.setVisible(false);
		IEEE_slider.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
					setVisible(false);							
				}
			});
		
		IEEE_slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
//						System.out.println(e);
					result = (double) IEEE_slider.getValue();
//					v_text_ieee.setText(String.valueOf(IEEE_slider.getValue()));
					v_text_ieee.setValue(IEEE_slider.getValue());
				}
			});
		
		IEEE_panel.add(IEEE_slider, BorderLayout.CENTER);
		
		IEEE_panel.add(IEEE_Value, BorderLayout.NORTH);
		
		IEEE_Value.add(label_2);
		v_text_ieee.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				float f = (Float) v_text_ieee.getValue();
				result = (double) f;
//				result = (double) Integer.parseInt(ME_textValue.getText());
				setVisible(false);	
				}
		});
		v_text_ieee.setColumns(12);
		
		IEEE_Value.add(v_text_ieee);
		FlowLayout flowLayout_1 = (FlowLayout) IEEE_Limit.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		
		IEEE_panel.add(IEEE_Limit, BorderLayout.SOUTH);
		
		IEEE_Limit.add(label);
		min_text_ieee.setColumns(8);
		
		IEEE_Limit.add(min_text_ieee);
		
		IEEE_Limit.add(label_1);
		max_text_ieee.setColumns(8);
		
		IEEE_Limit.add(max_text_ieee);

		JPanel SP_panel = new JPanel();
		contentPanel.add(SP_panel, "SP");
		SP_panel.setLayout(new BoxLayout(SP_panel, BoxLayout.X_AXIS));
		ButtonGroup group = new ButtonGroup();
		group.add(rb_off);
		group.add(rb_on);
		
		ButtonGroup groupDP = new ButtonGroup();
				
		SP_panel.add(horizontalStrut);
		rb_off.setFont(new Font("Tahoma", Font.BOLD, 12));
		SP_panel.add(rb_off);
		rb_off.addActionListener(rbListener) ;
		SP_panel.add(horizontalStrut_1);
		rb_on.setFont(new Font("Tahoma", Font.BOLD, 12));
		SP_panel.add(rb_on);
		rb_on.addActionListener(rbListener) ;

		JPanel ME_panel = new JPanel();
		contentPanel.add(ME_panel, "ME");
		ME_panel.setLayout(new BorderLayout(0, 0));
		ME_slider.setPaintTicks(true);
		ME_slider.setPaintLabels(true);
		ME_slider.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
					setVisible(false);							
				}
			});
		
		ME_slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
//						System.out.println(e);
					result = (double) ME_slider.getValue();
					ME_textValue.setText(String.valueOf(ME_slider.getValue()));
				}
			});
			

		ME_panel.add(ME_slider);
		ME_panel.add(limit_Panel, BorderLayout.SOUTH);
		ME_panel.add(v_Panel, BorderLayout.NORTH);

		v_Panel.add(v_lable);
		v_Panel.add(ME_textValue);
		ME_textValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				result = (double) Integer.parseInt(ME_textValue.getText());
				setVisible(false);						
			}
		});
		
		ME_textValue.setColumns(10);
		limit_Panel.add(min_lable);
		min_text.setColumns(6);
		limit_Panel.add(min_text);
		limit_Panel.add(horizontalStrut_2);
		limit_Panel.add(max_lable);
		max_text.setColumns(6);
		limit_Panel.add(max_text);

		contentPanel.add(DP_Panel, "DP");
		DP_Panel.setLayout(new BorderLayout(0, 0));
		
		DP_Panel.add(panel, BorderLayout.SOUTH);
				panel.add(cb_likeSP);
				
		DP_Panel.add(panel_1, BorderLayout.CENTER);
		groupDP.add(rb_00);
		panel_1.add(rb_00);
		groupDP.add(rb_01);
		panel_1.add(rb_01);
		groupDP.add(rb_10);
		panel_1.add(rb_10);
		groupDP.add(rb_11);
		panel_1.add(rb_11);
				
		DP_Panel.add(verticalStrut, BorderLayout.NORTH);
		FlowLayout flowLayout = (FlowLayout) IT_Panel.getLayout();
		flowLayout.setVgap(55);
				
		contentPanel.add(IT_Panel, "IT");
		it_text.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
					result = (double) Long.parseLong(it_text.getText());
					setVisible(false);											
					}
			});
		
		it_text.setColumns(10);
		IT_Panel.add(it_text);
		rb_11.addActionListener(rbListener);
		rb_10.addActionListener(rbListener);
		rb_01.addActionListener(rbListener);
		rb_00.addActionListener(rbListener);
		
		cb_likeSP.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				System.out.println(e);
				System.out.println(e.getStateChange());  //2 = unchecked
				if (e.getStateChange() == 1) {
					if ((item.iob(0).getValue() == 0) | (item.iob(0).getValue() == 3)) {
						item.iob(0).setValue(1);
						rb_01.setSelected(true);
						}
					item.iob(0).setMAX_VALUE(2);
					item.iob(0).setMIN_VALUE(1);
					rb_00.setEnabled(false);
					rb_11.setEnabled(false);
				} else {
					item.iob(0).setMAX_VALUE(3);
					item.iob(0).setMIN_VALUE(0);
					rb_00.setEnabled(true);						
					rb_11.setEnabled(true);
				}
			}
		});
				
		cb_likeSP.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
//					System.out.println(e);
//					cb_likeSP.
					}
				});

		buttonPane = new JPanel();
		buttonPane.setBorder(new MatteBorder(0, 3, 3, 3, (Color) new Color(0, 0, 0)));
		FlowLayout fl_buttonPane = new FlowLayout(FlowLayout.CENTER);
		fl_buttonPane.setAlignOnBaseline(true);
		buttonPane.setLayout(fl_buttonPane);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		okButton = new JButton("set Limits");
		okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						switch (item.getType()) {
						case M_DP_NA :case M_DP_TB : {
							if (cb_likeSP.isSelected()) {
								item.iob(0).setMIN_VALUE(1);							
								item.iob(0).setMAX_VALUE(2);	
							} else {
								item.iob(0).setMIN_VALUE(0);							
								item.iob(0).setMAX_VALUE(3);	
							}
							break;
						}
						case M_ME_NA : case M_ME_TB : case M_ME_NB :case M_ME_TD :{ 
							item.iob(0).setMIN_VALUE((Double) min_text.getValue());							
							item.iob(0).setMAX_VALUE((Double) max_text.getValue());	
							break;
							}
						case M_ME_NC : case M_ME_TF :{ 
							item.iob(0).setMIN_VALUE((Double) min_text_ieee.getValue());							
							item.iob(0).setMAX_VALUE((Double) max_text_ieee.getValue());							
							break;
							}
						default:
							break;
						}
						setVisible(false);
					}
				});

		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		}


	public void show(IECTCItem i) {
		item = i;
		result = item.iob(0).getValue();
		System.out.println("Value :"+item.iob(0).getValue());
		switch (item.getType()) {
		case M_SP_NA :case M_SP_TB : {
			layout.show(contentPanel,"SP");
			okButton.setVisible(false);
			if (result ==1.0) {
				rb_on.setSelected(true);
//				rb_off.setSelected(false);
			} else {
//				rb_on.setSelected(false);
				rb_off.setSelected(true);
			}
			break;  
		}		
		case M_DP_NA :case M_DP_TB : {
			layout.show(contentPanel,"DP");
			okButton.setVisible(true);
			if (item.iob(0).getMAX_VALUE() ==2) {
				cb_likeSP.setSelected(true);
			} else {
				cb_likeSP.setSelected(false);
			}
			switch (result.intValue()) {
			case 0 : {
				rb_00.setSelected(true);
				break;  
				} 
			case 1 : {
				rb_01.setSelected(true);
				break;  
				} 
			case 2 : {
				rb_10.setSelected(true);
				break;  
				} 
			case 3 : {
				rb_11.setSelected(true);
				break;  
				} 
			}
			break;
		}
			case M_ME_NA : case M_ME_TB : case M_ME_NB : case M_ME_TD : {
				layout.show(contentPanel,"ME");
				okButton.setVisible(true);
				ME_textValue.setText(String.valueOf(result.intValue()));
				ME_slider.setValue(result.intValue());
				min_text.setValue(item.iob(0).getMIN_VALUE());
				ME_slider.setMinimum((int) item.iob(0).getMIN_VALUE());
				max_text.setValue(item.iob(0).getMAX_VALUE());
				ME_slider.setMaximum((int) item.iob(0).getMAX_VALUE());
				ME_textValue.selectAll();
				break;  
			}
			case M_ME_NC : case M_ME_TF : {
				layout.show(contentPanel,"IEEE");
				okButton.setVisible(true);
				System.out.println("Value: "+result);
//				v_text_ieee.setValue(0.0);
//				v_text_ieee.setValue((Double)result);
//				v_text_ieee.setText(String.valueOf(result.floatValue()));
//				IEEE_slider.setValue(result.intValue());
				min_text_ieee.setValue(item.iob(0).getMIN_VALUE());
//				IEEE_slider.setMinimum((int) item.iob(0).getMIN_VALUE());
				max_text_ieee.setValue(item.iob(0).getMAX_VALUE());
//				IEEE_slider.setMaximum((int) item.iob(0).getMAX_VALUE());
				v_text_ieee.setValue(result);
				v_text_ieee.setText(v_text_ieee.getText());
				v_text_ieee.selectAll();
				break;  
			}
			case M_IT_NA : case M_IT_TB : {
				layout.show(contentPanel,"IT");
				okButton.setVisible(false);
				it_text.setText(String.valueOf(result.intValue()));
				it_text.selectAll();
				break;  
			}
		default:
			break;		
		}

		setModal(true);
		setVisible(true);
		
	}
}
