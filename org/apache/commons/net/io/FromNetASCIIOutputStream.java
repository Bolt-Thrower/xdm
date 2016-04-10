package org.apache.commons.net.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.net.io.FromNetASCIIInputStream;

public final class FromNetASCIIOutputStream extends FilterOutputStream {
   private boolean __lastWasCR = false;

   public FromNetASCIIOutputStream(OutputStream output) {
      super(output);
   }

   private void __write(int ch) throws IOException {
      switch(ch) {
      case 10:
         if(this.__lastWasCR) {
            this.out.write(FromNetASCIIInputStream._lineSeparatorBytes);
            this.__lastWasCR = false;
         } else {
            this.__lastWasCR = false;
            this.out.write(10);
         }
         break;
      case 11:
      case 12:
      default:
         if(this.__lastWasCR) {
            this.out.write(13);
            this.__lastWasCR = false;
         }

         this.out.write(ch);
         break;
      case 13:
         this.__lastWasCR = true;
      }

   }

   public synchronized void write(int ch) throws IOException {
      if(FromNetASCIIInputStream._noConversionRequired) {
         this.out.write(ch);
      } else {
         this.__write(ch);
      }
   }

   public synchronized void write(byte[] buffer) throws IOException {
      this.write(buffer, 0, buffer.length);
   }

   public synchronized void write(byte[] buffer, int offset, int length) throws IOException {
      if(FromNetASCIIInputStream._noConversionRequired) {
         this.out.write(buffer, offset, length);
      } else {
         while(length-- > 0) {
            this.__write(buffer[offset++]);
         }

      }
   }

   public synchronized void close() throws IOException {
      if(FromNetASCIIInputStream._noConversionRequired) {
         super.close();
      } else {
         if(this.__lastWasCR) {
            this.out.write(13);
         }

         super.close();
      }
   }
}
