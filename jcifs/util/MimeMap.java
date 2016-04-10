package jcifs.util;

import java.io.IOException;
import java.io.InputStream;

public class MimeMap {
   private static final int IN_SIZE = 7000;
   private static final int ST_START = 1;
   private static final int ST_COMM = 2;
   private static final int ST_TYPE = 3;
   private static final int ST_GAP = 4;
   private static final int ST_EXT = 5;
   private byte[] in = new byte[7000];
   private int inLen;

   public MimeMap() throws IOException {
      InputStream is = this.getClass().getClassLoader().getResourceAsStream("jcifs/util/mime.map");

      int n;
      for(this.inLen = 0; (n = is.read(this.in, this.inLen, 7000 - this.inLen)) != -1; this.inLen += n) {
         ;
      }

      if(this.inLen >= 100 && this.inLen != 7000) {
         is.close();
      } else {
         throw new IOException("Error reading jcifs/util/mime.map resource");
      }
   }

   public String getMimeType(String extension) throws IOException {
      return this.getMimeType(extension, "application/octet-stream");
   }

   public String getMimeType(String extension, String def) throws IOException {
      byte[] type = new byte[128];
      byte[] buf = new byte[16];
      byte[] ext = extension.toLowerCase().getBytes("ASCII");
      byte state = 1;
      boolean i = false;
      int x = 0;
      int t = 0;

      for(int off = 0; off < this.inLen; ++off) {
         byte ch = this.in[off];
         switch(state) {
         case 1:
            if(ch == 32 || ch == 9) {
               break;
            }

            if(ch == 35) {
               state = 2;
               break;
            } else {
               state = 3;
            }
         case 3:
            if(ch != 32 && ch != 9) {
               type[t++] = ch;
               break;
            }

            state = 4;
            break;
         case 2:
            if(ch == 10) {
               i = false;
               x = 0;
               t = 0;
               state = 1;
            }
            break;
         case 4:
            if(ch == 32 || ch == 9) {
               break;
            }

            state = 5;
         case 5:
            switch(ch) {
            case 9:
            case 10:
            case 32:
            case 35:
               int var13;
               for(var13 = 0; var13 < x && x == ext.length && buf[var13] == ext[var13]; ++var13) {
                  ;
               }

               if(var13 == ext.length) {
                  return new String(type, 0, t, "ASCII");
               }

               if(ch == 35) {
                  state = 2;
               } else if(ch == 10) {
                  i = false;
                  boolean var12 = false;
                  t = 0;
                  state = 1;
               }

               x = 0;
               break;
            default:
               buf[x++] = ch;
            }
         }
      }

      return def;
   }
}
