package org.sdg.xdman.gui;

import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.sdg.xdman.gui.BatchItem;
import org.sdg.xdman.gui.StringResource;

public class BatchTableModel extends AbstractTableModel {
   private static final long serialVersionUID = 1897735268013212330L;
   String[] cols = new String[]{"#", StringResource.getString("FILE"), StringResource.getString("SAVE_IN"), StringResource.getString("URL")};
   List batchList = new LinkedList();

   public Class getColumnClass(int c) {
      return c == 0?Boolean.class:String.class;
   }

   public String getColumnName(int c) {
      return this.cols[c];
   }

   public int getColumnCount() {
      return this.cols.length;
   }

   public int getRowCount() {
      return this.batchList.size();
   }

   public Object getValueAt(int r, int c) {
      try {
         BatchItem e = (BatchItem)this.batchList.get(r);
         switch(c) {
         case 0:
            return Boolean.valueOf(e.selected);
         case 1:
            return e.fileName;
         case 2:
            return e.dir;
         case 3:
            return e.url;
         default:
            return "";
         }
      } catch (Exception var4) {
         return "";
      }
   }
}
