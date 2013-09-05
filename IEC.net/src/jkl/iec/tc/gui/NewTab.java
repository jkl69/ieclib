package jkl.iec.tc.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

@SuppressWarnings("serial")
public class NewTab extends JPanel {

    private final JTabbedPane pane;
    public NewTab(final JTabbedPane pane) {
        //unset default FlowLayout' gaps
//        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        this.pane = pane;
        setOpaque(false);
  
        JButton button = new TabButton();
        add(button);
        //add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        
    }

    private class TabButton extends JButton implements ActionListener {
        public TabButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("create new tab");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(true);
            //Making nice rollover effect
            //we use the same listener for all buttons
//            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(NewTab.this);
            if (i != -1) {
        		pane.setTabComponentAt(i, new ASDUTab(pane));
        		pane.setComponentAt(i,new IECTable());
        		pane.addTab("ASDU 2", null, new JPanel(), null);
//        		pane.addTab("ASDU 2", null, new IECTabedTable().new ScrollTable(), null);
        		pane.setTabComponentAt(i+1,new NewTab(pane));
//        		pane.remove(i);
        		pane.setSelectedIndex(i); 
            }
        }

        //we don't want to update UI for this button
        public void updateUI() {
        }

        //paint the cross
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            //shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.GRAY);
            if (getModel().isRollover()) {
                g2.setColor(Color.MAGENTA);
            }
//            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
//            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.drawLine(getWidth() /2 ,3, getWidth() /2 , getHeight()-3);
            g2.drawLine(3,getHeight() /2 , getWidth()-3, getHeight() / 2);
            g2.dispose();
        }
    }

}
