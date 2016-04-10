package org.apache.commons.net.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ToNetASCIIInputStream extends FilterInputStream {
   private static final int __NOTHING_SPECIAL = 0;
   private static final int __LAST_WAS_CR = 1;
   private static final int __LAST_WAS_NL = 2;
   private int __status = 0;

   public ToNetASCIIInputStream(InputStream input) {
      super(input);
   }

   public int read() throws IOException {
      if(this.__status == 2) {
         this.__status = 0;
         return 10;
      } else {
         int ch = this.in.read();
         switch(ch) {
         case 10:
            if(this.__status != 1) {
               this.__status = 2;
               return 13;
            }
         case 11:
         case 12:
         default:
            this.__status = 0;
            return ch;
         case 13:
            this.__status = 1;
            return 13;
         }
      }
   }

   public int read(byte[] buffer) throws IOException {
      return this.read(buffer, 0, buffer.length);
   }

   public int read(byte[] buffer, int offset, int length) throws IOException {
      if(length < 1) {
         return 0;
      } else {
         int ch = this.available();
         if(length > ch) {
            length = ch;
         }

         if(length < 1) {
            length = 1;
         }

         if((ch = this.read()) == -1) {
            return -1;
         } else {
            int off = offset;

            do {
               buffer[offset++] = (byte)ch;
               --length;
            } while(length > 0 && (ch = this.read()) != -1);

            return offset - off;
         }
      }
   }

   public boolean markSupported() {
      return false;
   }

   public int available() throws IOException {
      int result = this.in.available();
      return this.__status == 2?result + 1:result;
   }
}
