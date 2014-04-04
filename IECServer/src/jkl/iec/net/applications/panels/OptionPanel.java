package jkl.iec.net.applications.panels;

import jkl.iec.net.applications.Server;
import jkl.iec.tc.type.IECList;
import jkl.iec.tc.type.IECTCItem;
import jkl.iec.tc.utils.IECSimPlayer;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.awt.Rectangle;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.io.StringReader;

@SuppressWarnings("serial")
public class OptionPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	JRadioButton Respone_Unknown = new JRadioButton("Respone for Unknown types");
	public JRadioButton IEC_small = new JRadioButton("activate Short IEC PRofile (1/2/2)");
	
	public class Oaction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
//			System.out.println("Option_action :"+e);	
			if (e.getActionCommand() == "activate Short IEC PRofile (1/2/2)") {
				IECTCItem.P_SHORT = IEC_small.isSelected();
			}
			if (e.getActionCommand() == "Respone for Unknown types") {
				IECTCItem.Respone_Unknown = Respone_Unknown.isSelected();	
			}
			if(e.getActionCommand()=="t") {
				Server.server.socket.get(0).resetIFrameSq();
			}
		}
		
	}
	
	Oaction oa =new Oaction();
	public JTextField SimfileName;
	
	private IECList ieclist;
	private final JPanel panel = new JPanel();
	private final JButton btnLoadSimFile = new JButton("LoadSimFile");
	private final JButton btnPlaySim = new JButton("Start File");
	private final JButton btnNewButton = new JButton("t");
	private final JScrollPane scrollPane;// = new JScrollPane();
	private final JTextArea playtext = new JTextArea();
	private final JButton btnStartSimul = new JButton("Start Simul");
//	private final JPanel panel_1 = new JPanel();
	
	public void setList(IECList ieclist){
		this.ieclist=ieclist;
	}
	
	public OptionPanel() {
		IEC_small.addActionListener(oa);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(IEC_small);
		btnNewButton.addActionListener(oa);
		
		Respone_Unknown.addActionListener(oa);
		Respone_Unknown.setSelected(true);
		add(Respone_Unknown);
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		
		add(panel);
		btnLoadSimFile.setSize(new Dimension(200, 100));
		btnLoadSimFile.addActionListener(new ActionListener() {
			JFileChooser fc = new JFileChooser();
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = fc.showOpenDialog(null);
				if (returnVal==JFileChooser.APPROVE_OPTION) {
					SimfileName.setText(fc.getSelectedFile().toString());
				}
			}
		});
		
		panel.add(btnLoadSimFile);
		
		SimfileName = new JTextField();
		panel.add(SimfileName);
//		SimfileName.setText("D:\\\\workspace\\\\IEC.net\\\\src\\\\iec\\\\tc\\\\utils\\\\sim.txt");
		SimfileName.setColumns(20);
		
		panel.add(btnPlaySim);
		playtext.setText("item=item0;inc=1;sleep=5000\nitem=item1;inc=1");
		scrollPane = new JScrollPane(playtext);
		scrollPane.setBounds(new Rectangle(0, 0, 200, 0));
		
		panel.add(scrollPane);
		playtext.setColumns(20);
		playtext.setRows(4);
		playtext.setBounds(new Rectangle(0, 0, 100, 100));
		btnStartSimul.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				IECSimPlayer player = new IECSimPlayer();
				player.setIeclist(ieclist);

				StringReader sr = new StringReader(playtext.getText());
				player.play(sr);
			}
		});
		
		panel.add(btnStartSimul);
//		panel.add(btnNewButton);
		
		btnPlaySim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				IECSimPlayer player = new IECSimPlayer();
				player.setIeclist(ieclist);
				if (! player.play(SimfileName.getText())) {
//					"D:\\workspace\\IEC.net\\src\\iec\\tc\\utils\\sim.txt")) {
			    	System.out.println("MAIN_Player INIT ERROR !");
			    } 
			}
		});

	}

}
