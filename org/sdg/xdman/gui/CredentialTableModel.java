package org.sdg.xdman.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.table.AbstractTableModel;
import org.sdg.xdman.core.common.Authenticator;
import org.sdg.xdman.core.common.Credential;
import org.sdg.xdman.gui.StringResource;

public class CredentialTableModel extends AbstractTableModel implements Observer {
   private static final long serialVersionUID = -4277859942575427821L;
   String[] cols = new String[]{StringResource.getString("HOST"), StringResource.getString("LBL_PROXY_USER")};
   List list = new ArrayList();

   public int getColumnCount() {
      return this.cols.length;
   }

   public int getRowCount() {
      return this.list.size();
   }

   public Object getValueAt(int row, int col) {
      Credential c = (Credential)this.list.get(row);
      switch(col) {
      case 0:
         return c.host;
      case 1:
         return c.user;
      default:
         return "";
      }
   }

   public String getColumnName(int col) {
      return this.cols[col];
   }

   public Class getColumnClass(int arg0) {
      return String.class;
   }

   public void update(Observable arg0, Object arg1) {
      this.list.clear();
      Iterator it = Authenticator.auth.values().iterator();

      while(it.hasNext()) {
         this.list.add((Credential)it.next());
      }

      this.fireTableDataChanged();
   }

   void load() {
      this.list.clear();
      Iterator it = Authenticator.auth.values().iterator();

      while(it.hasNext()) {
         this.list.add((Credential)it.next());
      }

      this.fireTableDataChanged();
   }
}
