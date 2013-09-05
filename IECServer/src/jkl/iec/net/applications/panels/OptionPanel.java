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
	private final JButton btnNewButton = new JButton("t");
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
		SimfileName.setColumns(33);
		
		JButton btnPlaySim = new JButton("Start Simulation");
		panel.add(btnPlaySim);
		
		panel.add(btnNewButton);
		
//		add(panel_1);
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
