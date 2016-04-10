package org.sdg.xdman.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

public class XDMTransferable implements Transferable {
   protected String plainData;
   protected String htmlData;
   private static DataFlavor[] htmlFlavors;
   private static DataFlavor[] stringFlavors;
   private static DataFlavor[] plainFlavors;

   static {
      try {
         htmlFlavors = new DataFlavor[3];
         htmlFlavors[0] = new DataFlavor("text/html;class=java.lang.String");
         htmlFlavors[1] = new DataFlavor("text/html;class=java.io.Reader");
         htmlFlavors[2] = new DataFlavor("text/html;charset=unicode;class=java.io.InputStream");
         plainFlavors = new DataFlavor[3];
         plainFlavors[0] = new DataFlavor("text/plain;class=java.lang.String");
         plainFlavors[1] = new DataFlavor("text/plain;class=java.io.Reader");
         plainFlavors[2] = new DataFlavor("text/plain;charset=unicode;class=java.io.InputStream");
         stringFlavors = new DataFlavor[2];
         stringFlavors[0] = new DataFlavor("application/x-java-jvm-local-objectref;class=java.lang.String");
         stringFlavors[1] = DataFlavor.stringFlavor;
      } catch (ClassNotFoundException var1) {
         System.err.println("error initializing javax.swing.plaf.basic.BasicTranserable");
      }

   }

   public XDMTransferable(String plainData, String htmlData) {
      this.plainData = plainData;
      this.htmlData = htmlData;
   }

   public DataFlavor[] getTransferDataFlavors() {
      DataFlavor[] richerFlavors = this.getRicherFlavors();
      int nRicher = richerFlavors != null?richerFlavors.length:0;
      int nHTML = this.isHTMLSupported()?htmlFlavors.length:0;
      int nPlain = this.isPlainSupported()?plainFlavors.length:0;
      int nString = this.isPlainSupported()?stringFlavors.length:0;
      int nFlavors = nRicher + nHTML + nPlain + nString;
      DataFlavor[] flavors = new DataFlavor[nFlavors];
      int nDone = 0;
      if(nRicher > 0) {
         System.arraycopy(richerFlavors, 0, flavors, nDone, nRicher);
         nDone += nRicher;
      }

      if(nHTML > 0) {
         System.arraycopy(htmlFlavors, 0, flavors, nDone, nHTML);
         nDone += nHTML;
      }

      if(nPlain > 0) {
         System.arraycopy(plainFlavors, 0, flavors, nDone, nPlain);
         nDone += nPlain;
      }

      if(nString > 0) {
         System.arraycopy(stringFlavors, 0, flavors, nDone, nString);
         int var10000 = nDone + nString;
      }

      return flavors;
   }

   public boolean isDataFlavorSupported(DataFlavor flavor) {
      DataFlavor[] flavors = this.getTransferDataFlavors();

      for(int i = 0; i < flavors.length; ++i) {
         if(flavors[i].equals(flavor)) {
            return true;
         }
      }

      return false;
   }

   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
      if(this.isRicherFlavor(flavor)) {
         return this.getRicherData(flavor);
      } else {
         String data;
         if(this.isHTMLFlavor(flavor)) {
            data = this.getHTMLData();
            data = data == null?"":data;
            if(String.class.equals(flavor.getRepresentationClass())) {
               return data;
            }

            if(Reader.class.equals(flavor.getRepresentationClass())) {
               return new StringReader(data);
            }

            if(InputStream.class.equals(flavor.getRepresentationClass())) {
               return new ByteArrayInputStream(data.getBytes());
            }
         } else if(this.isPlainFlavor(flavor)) {
            data = this.getPlainData();
            data = data == null?"":data;
            if(String.class.equals(flavor.getRepresentationClass())) {
               return data;
            }

            if(Reader.class.equals(flavor.getRepresentationClass())) {
               return new StringReader(data);
            }

            if(InputStream.class.equals(flavor.getRepresentationClass())) {
               return new ByteArrayInputStream(data.getBytes());
            }
         } else if(this.isStringFlavor(flavor)) {
            data = this.getPlainData();
            data = data == null?"":data;
            return data;
         }

         throw new UnsupportedFlavorException(flavor);
      }
   }

   protected boolean isRicherFlavor(DataFlavor flavor) {
      DataFlavor[] richerFlavors = this.getRicherFlavors();
      int nFlavors = richerFlavors != null?richerFlavors.length:0;

      for(int i = 0; i < nFlavors; ++i) {
         if(richerFlavors[i].equals(flavor)) {
            return true;
         }
      }

      return false;
   }

   protected DataFlavor[] getRicherFlavors() {
      return null;
   }

   protected Object getRicherData(DataFlavor flavor) throws UnsupportedFlavorException {
      return null;
   }

   protected boolean isHTMLFlavor(DataFlavor flavor) {
      DataFlavor[] flavors = htmlFlavors;

      for(int i = 0; i < flavors.length; ++i) {
         if(flavors[i].equals(flavor)) {
            return true;
         }
      }

      return false;
   }

   protected boolean isHTMLSupported() {
      return this.htmlData != null;
   }

   protected String getHTMLData() {
      return this.htmlData;
   }

   protected boolean isPlainFlavor(DataFlavor flavor) {
      DataFlavor[] flavors = plainFlavors;

      for(int i = 0; i < flavors.length; ++i) {
         if(flavors[i].equals(flavor)) {
            return true;
         }
      }

      return false;
   }

   protected boolean isPlainSupported() {
      return this.plainData != null;
   }

   protected String getPlainData() {
      return this.plainData;
   }

   protected boolean isStringFlavor(DataFlavor flavor) {
      DataFlavor[] flavors = stringFlavors;

      for(int i = 0; i < flavors.length; ++i) {
         if(flavors[i].equals(flavor)) {
            return true;
         }
      }

      return false;
   }
}
