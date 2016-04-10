package org.apache.commons.net.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public final class FromNetASCIIInputStream extends PushbackInputStream {
   static final boolean _noConversionRequired;
   static final String _lineSeparator = System.getProperty("line.separator");
   static final byte[] _lineSeparatorBytes;
   private int __length = 0;

   static {
      _noConversionRequired = _lineSeparator.equals("\r\n");
      _lineSeparatorBytes = _lineSeparator.getBytes();
   }

   public static final boolean isConversionRequired() {
      return !_noConversionRequired;
   }

   public FromNetASCIIInputStream(InputStream input) {
      super(input, _lineSeparatorBytes.length + 1);
   }

   private int __read() throws IOException {
      int ch = super.read();
      if(ch == 13) {
         ch = super.read();
         if(ch != 10) {
            if(ch != -1) {
               this.unread(ch);
            }

            return 13;
         }

         this.unread(_lineSeparatorBytes);
         ch = super.read();
         --this.__length;
      }

      return ch;
   }

   public int read() throws IOException {
      return _noConversionRequired?super.read():this.__read();
   }

   public int read(byte[] buffer) throws IOException {
      return this.read(buffer, 0, buffer.length);
   }

   public int read(byte[] buffer, int offset, int length) throws IOException {
      if(_noConversionRequired) {
         return super.read(buffer, offset, length);
      } else if(length < 1) {
         return 0;
      } else {
         int ch = this.available();
         this.__length = length > ch?ch:length;
         if(this.__length < 1) {
            this.__length = 1;
         }

         if((ch = this.__read()) == -1) {
            return -1;
         } else {
            int off = offset;

            do {
               buffer[offset++] = (byte)ch;
            } while(--this.__length > 0 && (ch = this.__read()) != -1);

            return offset - off;
         }
      }
   }

   public int available() throws IOException {
      if(this.in == null) {
         throw new IOException("Stream closed");
      } else {
         return this.buf.length - this.pos + this.in.available();
      }
   }
}
