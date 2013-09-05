package jkl.iec.tc.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import jkl.iec.tc.type.IECList;
import jkl.iec.tc.type.IECMap;
import jkl.iec.tc.type.IECMap.IECType;
import jkl.iec.tc.type.IECTCItem;

public class IECTreeModel implements TreeModel {
	
	public final static Logger log = Logger.getLogger(IECTreeModel.class .getName()); 
	
	public ArrayList<IECList> ieclist =new ArrayList<IECList>() ;

	public static boolean OnlyBaseTypes= true;

	public String name;

	static int c;
		
	public IECTreeModel() {
		log.finest("");
		name = "IECTree_"+String.valueOf(c++);
		if (OnlyBaseTypes) {
			Iterator<String> it= IECMap.IEC_M_BaseType.iterator();
			IECList l;
			while (it.hasNext()) {
				String txt = IECMap.getBaseTypeDescription(it.next());
				log.fine("create "+txt);	
				l = new IECList();
				l.name = txt;
				ieclist.add(l);
			}

		} else {
			Iterator<IECType> it= IECMap.IEC_M_Type.iterator();
			IECList l;
			while (it.hasNext()) {
				String txt = IECMap.getTypeDescription(IECMap.getType(it.next().tk()));
				log.fine("create "+txt);	
				l = new IECList();
//				l.name = text[i];
				l.name = txt;
				ieclist.add(l);
			}
		}
	}
	
	private IECList getByName(String name) {
		for (IECList list :ieclist) {
			if (list.name==name) return list;
		}
		return null;
	}
	
	@Override
	public void addTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getChild(Object o, int index) {
		// TODO Auto-generated method stub
		log.finest("for object "+o+" index "+index);
//		return c++;
		if (o == getRoot()) {
//			return ieclist.get(index).name;
			IECList list =ieclist.get(index);
			log.finer("return "+list.toString());
			log.finer("return(def) "+list);
			return list;
		}
//		IECList l = getByName((String) o);
//		return l.get(index).printStream();
		if (o.getClass()==IECList.class) {
			IECList list =ieclist.get(ieclist.indexOf(o));
			return list.get(index);
		}
//		return ieclist.get(ieclist.indexOf(o)).size();
		return index;
	}

	@Override
	public int getChildCount(Object o) {
		log.finest("for object "+o);
		if (o!=getRoot()) {
//			int c =ieclist.get(ieclist.indexOf(o)).size();
//			int c =getByName((String) o).size();
			int c =ieclist.get(ieclist.indexOf(o)).size();
			log.finest("result "+c);
			return c;
		}

		return ieclist.size()-1;
	}

	@Override
	public int getIndexOfChild(Object o, Object o1) {
		// TODO Auto-generated method stub
		log.finest("");
		return 0;
	}

	@Override
	public Object getRoot() {
		return name;
	}

	@Override
	public boolean isLeaf(Object o) {
		// TODO Auto-generated method stub
		log.finest(""+o.getClass());
		if (o.getClass()==IECTCItem.class) return true;
//		return true;
		return false;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}


}
