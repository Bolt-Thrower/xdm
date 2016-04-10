package org.apache.commons.net.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class ToNetASCIIOutputStream extends FilterOutputStream {
   private boolean __lastWasCR = false;

   public ToNetASCIIOutputStream(OutputStream output) {
      super(output);
   }

   public synchronized void write(int ch) throws IOException {
      switch(ch) {
      case 10:
         if(!this.__lastWasCR) {
            this.out.write(13);
         }
      case 11:
      case 12:
      default:
         this.__lastWasCR = false;
         this.out.write(ch);
         return;
      case 13:
         this.__lastWasCR = true;
         this.out.write(13);
      }
   }

   public synchronized void write(byte[] buffer) throws IOException {
      this.write(buffer, 0, buffer.length);
   }

   public synchronized void write(byte[] buffer, int offset, int length) throws IOException {
      while(length-- > 0) {
         this.write(buffer[offset++]);
      }

   }
}
