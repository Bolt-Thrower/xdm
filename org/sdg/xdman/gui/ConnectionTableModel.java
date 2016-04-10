package org.sdg.xdman.gui;

import javax.swing.table.AbstractTableModel;

public class ConnectionTableModel extends AbstractTableModel {
   private static final long serialVersionUID = -3901959955250134521L;
   String[] stat = new String[0];
   String[] dwn = new String[0];
   String[] cols = new String[]{"#", "Downloaded", "info"};

   public String getColumnName(int col) {
      return this.cols[col];
   }

   public Class getColumnClass(int arg0) {
      return String.class;
   }

   public int getColumnCount() {
      return this.cols.length;
   }

   public int getRowCount() {
      return this.dwn.length;
   }

   public Object getValueAt(int row, int col) {
      try {
         switch(col) {
         case 0:
            return Integer.valueOf(row);
         case 1:
            return this.dwn[row];
         case 2:
            return this.stat[row];
         default:
            return "";
         }
      } catch (Exception var4) {
         return "";
      }
   }

   public void update(String[] d, String[] s) {
      this.dwn = d;
      this.stat = s;
      this.fireTableDataChanged();
   }
}
