package org.sdg.xdman.gui;

import javax.swing.AbstractListModel;
import javax.swing.Icon;
import org.sdg.xdman.core.common.DownloadInfo;
import org.sdg.xdman.gui.DownloadList;
import org.sdg.xdman.gui.DownloadListItem;

public class MainListModel extends AbstractListModel {
   private static final long serialVersionUID = 5930766243148331109L;
   DownloadList list = null;
   Icon q;

   public MainListModel(DownloadList list) {
      this.list = list;
   }

   public Object getElementAt(int row) {
      DownloadListItem item = this.list.get(row);
      return item;
   }

   void fireListItemUpdated(int index) {
      this.fireContentsChanged(this, index, index);
   }

   public int getSize() {
      return this.list == null?0:this.list.size();
   }

   public void setType(String type) {
      this.list.setType(type);
   }

   public void updateItem(DownloadInfo info) {
      DownloadListItem item = this.list.getByID(info.id);
      if(item != null) {
         item.updateData(info);
         int index = this.list.getIndex(item);
         if(index >= 0) {
            this.fireContentsChanged(this, index, index);
         }
      }
   }
}
