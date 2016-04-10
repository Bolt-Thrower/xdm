package org.sdg.xdman.gui;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;
import org.sdg.xdman.core.common.DownloadInfo;
import org.sdg.xdman.gui.DownloadList;
import org.sdg.xdman.gui.DownloadListItem;
import org.sdg.xdman.gui.StringResource;

public class MainTableModel extends AbstractTableModel {
   DownloadList list = null;
   Icon q;
   private static final long serialVersionUID = -8936395745120671317L;
   final String[] cols = new String[]{""};

   public void setList(DownloadList list) {
      this.list = list;
      this.fireTableDataChanged();
   }

   void setType(String type) {
      this.list.setType(type);
   }

   public Class getColumnClass(int col) {
      return DownloadListItem.class;
   }

   public String getColumnName(int col) {
      return this.cols[col];
   }

   public int getColumnCount() {
      return this.cols.length;
   }

   public int getRowCount() {
      return this.list == null?0:this.list.size();
   }

   public Object getValueAt(int row, int col) {
      DownloadListItem item = this.list.get(row);
      return item;
   }

   public void updateItem(DownloadInfo info) {
      DownloadListItem item = this.list.getByID(info.id);
      if(item != null) {
         item.updateData(info);
         int index = this.list.getIndex(item);
         if(index >= 0) {
            this.fireTableRowsUpdated(index, index);
         }
      }
   }

   String getString(String id) {
      return StringResource.getString(id);
   }
}
