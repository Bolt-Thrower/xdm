package org.sdg.xdman.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.gui.DownloadList;
import org.sdg.xdman.gui.DownloadListItem;
import org.sdg.xdman.gui.XDMTransferHandler;
import org.sdg.xdman.gui.XDMTransferable;

public class FileTransferHandler extends XDMTransferHandler {
   private static final long serialVersionUID = -339416811193865790L;
   DownloadList list;

   public FileTransferHandler(DownloadList list, DownloadStateListner mgr) {
      super(mgr);
      this.list = list;
   }

   protected Transferable createTransferable(JComponent c) {
      Object[] values = (Object[])null;
      int i;
      if(c instanceof JList) {
         values = ((JList)c).getSelectedValues();
      } else if(c instanceof JTable) {
         JTable plainBuf = (JTable)c;
         int[] htmlBuf = plainBuf.getSelectedRows();
         if(htmlBuf != null) {
            values = new Object[htmlBuf.length];

            for(i = 0; i < htmlBuf.length; ++i) {
               int obj = htmlBuf[i];
               DownloadListItem val = this.list.get(obj);
               File file = new File(val.saveto, val.filename);
               values[i] = file;
            }
         }
      }

      if(values != null && values.length != 0) {
         StringBuffer var9 = new StringBuffer();
         StringBuffer var10 = new StringBuffer();
         var10.append("<html>\n<body>\n<ul>\n");

         for(i = 0; i < values.length; ++i) {
            Object var11 = values[i];
            String var12 = var11 == null?"":var11.toString();
            var9.append(var12 + "\n");
            var10.append("  <li>" + var12 + "\n");
         }

         var9.deleteCharAt(var9.length() - 1);
         var10.append("</ul>\n</body>\n</html>");
         return new FileTransferHandler.FileTransferable(var9.toString(), var10.toString(), values);
      } else {
         return null;
      }
   }

   public int getSourceActions(JComponent c) {
      return 1;
   }

   static class FileTransferable extends XDMTransferable {
      Object[] fileData;

      FileTransferable(String plainData, String htmlData, Object[] fileData) {
         super(plainData, htmlData);
         this.fileData = fileData;
      }

      protected DataFlavor[] getRicherFlavors() {
         DataFlavor[] flavors = new DataFlavor[]{DataFlavor.javaFileListFlavor};
         return flavors;
      }

      protected Object getRicherData(DataFlavor flavor) {
         ArrayList files;
         int i;
         if(DataFlavor.javaFileListFlavor.equals(flavor)) {
            files = new ArrayList();

            for(i = 0; i < this.fileData.length; ++i) {
               files.add(this.fileData[i]);
            }

            return files;
         } else if(!DataFlavor.stringFlavor.equals(flavor)) {
            return null;
         } else {
            files = new ArrayList();

            for(i = 0; i < this.fileData.length; ++i) {
               files.add(this.fileData[i]);
            }

            return files;
         }
      }
   }
}
