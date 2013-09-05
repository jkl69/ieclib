package jkl.iec.tc.gui;


import javax.swing.JComboBox;

import jkl.iec.tc.type.IECMap;
import jkl.iec.tc.type.IECMap.IECType;

@SuppressWarnings("serial")
public class IECComboBox extends JComboBox<String> {
	public IECComboBox() {
		for (IECType it : IECType.values()) {
//			if (IECMap.IEC_M_Type.contains(it)) {
				addItem(IECMap.getTypeDescription(it));
//			}
		}
	}
	
}
