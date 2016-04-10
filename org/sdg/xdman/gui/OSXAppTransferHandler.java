package org.sdg.xdman.gui;

import java.awt.datatransfer.Transferable;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import org.sdg.xdman.gui.FileTransferHandler;

public class OSXAppTransferHandler extends TransferHandler {
   private static final long serialVersionUID = -339416811193865790L;
   File[] list = new File[1];

   public void setAppFolderLocation(File f) {
      this.list[0] = f;
   }

   protected Transferable createTransferable(JComponent c) {
      Object[] values = new Object[this.list.length];

      for(int plainBuf = 0; plainBuf < this.list.length; ++plainBuf) {
         values[plainBuf] = this.list[plainBuf];
      }

      StringBuffer var8 = new StringBuffer();
      StringBuffer htmlBuf = new StringBuffer();
      htmlBuf.append("<html>\n<body>\n<ul>\n");

      for(int i = 0; i < values.length; ++i) {
         Object obj = values[i];
         String val = obj == null?"":obj.toString();
         var8.append(val + "\n");
         htmlBuf.append("  <li>" + val + "\n");
      }

      var8.deleteCharAt(var8.length() - 1);
      htmlBuf.append("</ul>\n</body>\n</html>");
      return new FileTransferHandler.FileTransferable(var8.toString(), htmlBuf.toString(), values);
   }

   public int getSourceActions(JComponent c) {
      return 1;
   }
}
