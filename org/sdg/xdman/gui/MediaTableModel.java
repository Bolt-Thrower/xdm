package org.sdg.xdman.gui;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import org.sdg.xdman.gui.MediaInfo;
import org.sdg.xdman.gui.StringResource;

public class MediaTableModel extends AbstractTableModel {
   private static final long serialVersionUID = 3687589857430853297L;
   ArrayList list = new ArrayList();
   String[] cols = new String[]{StringResource.getString("FILE_NAME"), StringResource.getString("INFO"), StringResource.getString("URL")};

   public Class getColumnClass(int arg0) {
      return String.class;
   }

   public int getColumnCount() {
      return this.cols.length;
   }

   public int getRowCount() {
      return this.list.size();
   }

   public Object getValueAt(int arg0, int arg1) {
      MediaInfo info = (MediaInfo)this.list.get(arg0);
      switch(arg1) {
      case 0:
         return info.name;
      case 1:
         return (info.type == null?"":info.type) + " " + (info.size == null?"":info.size);
      case 2:
         return info.url;
      default:
         return "";
      }
   }

   public synchronized void add(MediaInfo info) {
      for(int i = 0; i < this.list.size(); ++i) {
         MediaInfo mi = (MediaInfo)this.list.get(i);
         if(mi.url.equals(info.url)) {
            return;
         }
      }

      ArrayList var5 = this.list;
      synchronized(this.list) {
         this.list.add(info);
      }

      this.fireTableDataChanged();
   }

   public void remove(int index) {
      ArrayList var2 = this.list;
      synchronized(this.list) {
         this.list.remove(index);
      }

      this.fireTableDataChanged();
   }

   public void clear() {
      ArrayList var1 = this.list;
      synchronized(this.list) {
         this.list.clear();
      }

      this.fireTableDataChanged();
   }

   public String getColumnName(int c) {
      return this.cols[c];
   }
}
