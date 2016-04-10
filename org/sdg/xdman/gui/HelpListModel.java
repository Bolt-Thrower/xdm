package org.sdg.xdman.gui;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.AbstractListModel;

public class HelpListModel extends AbstractListModel {
   private static final long serialVersionUID = 5679536364566414367L;
   HashMap map = new HashMap();

   public URL getLinkURL(String pageName) {
      return (URL)this.map.get(pageName);
   }

   public Iterator listPages() {
      return this.map.keySet().iterator();
   }

   public Object getElementAt(int r) {
      return "" + this.map.keySet().toArray()[r];
   }

   public int getSize() {
      return this.map.size();
   }
}
