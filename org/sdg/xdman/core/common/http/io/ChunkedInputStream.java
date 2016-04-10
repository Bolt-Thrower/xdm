package org.sdg.xdman.core.common.http.io;

import java.io.IOException;
import java.io.InputStream;

public class ChunkedInputStream extends InputStream {
   private static final int CHUNK_LEN = 1;
   private static final int CHUNK_DATA = 2;
   private static final int CHUNK_CRLF = 3;
   private static final int BUFFER_SIZE = 2048;
   private final InputStream in;
   private StringBuffer buffer;
   private int state;
   private int chunkSize;
   private int pos;
   private boolean eof = false;
   private boolean closed = false;

   public ChunkedInputStream(InputStream in) {
      if(in == null) {
         throw new IllegalArgumentException("Session input buffer may not be null");
      } else {
         this.in = in;
         this.pos = 0;
         this.buffer = new StringBuffer(16);
         this.state = 1;
      }
   }

   public int read() throws IOException {
      if(this.closed) {
         throw new IOException("Attempted read from closed stream.");
      } else if(this.eof) {
         return -1;
      } else {
         if(this.state != 2) {
            this.nextChunk();
            if(this.eof) {
               return -1;
            }
         }

         int b = this.in.read();
         if(b != -1) {
            ++this.pos;
            if(this.pos >= this.chunkSize) {
               this.state = 3;
            }
         }

         return b;
      }
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if(this.closed) {
         throw new IOException("Attempted read from closed stream.");
      } else if(this.eof) {
         return -1;
      } else {
         if(this.state != 2) {
            this.nextChunk();
            if(this.eof) {
               return -1;
            }
         }

         len = Math.min(len, this.chunkSize - this.pos);
         int bytesRead = this.in.read(b, off, len);
         if(bytesRead != -1) {
            this.pos += bytesRead;
            if(this.pos >= this.chunkSize) {
               this.state = 3;
            }

            return bytesRead;
         } else {
            this.eof = true;
            throw new IllegalArgumentException("Truncated chunk ( expected size: " + this.chunkSize + "; actual size: " + this.pos + ")");
         }
      }
   }

   public int read(byte[] b) throws IOException {
      return this.read(b, 0, b.length);
   }

   private void nextChunk() throws IOException {
      this.chunkSize = this.getChunkSize();
      if(this.chunkSize < 0) {
         throw new IllegalArgumentException("Negative chunk size");
      } else {
         this.state = 2;
         this.pos = 0;
         if(this.chunkSize == 0) {
            this.eof = true;
            this.parseTrailerHeaders();
         }

      }
   }

   private int getChunkSize() throws IOException {
      int st = this.state;
      int i;
      switch(st) {
      case 2:
      default:
         throw new IllegalStateException("Inconsistent codec state");
      case 3:
         this.buffer = new StringBuffer();
         i = readLine(this.in, this.buffer);
         if(i == -1) {
            return 0;
         } else if(this.buffer.length() != 0) {
            throw new IllegalArgumentException("Unexpected content at the end of chunk");
         } else {
            this.state = 1;
         }
      case 1:
         this.buffer = new StringBuffer();
         i = readLine(this.in, this.buffer);
         if(i == -1) {
            return 0;
         } else {
            int separator = this.buffer.toString().indexOf(59);
            if(separator < 0) {
               separator = this.buffer.length();
            }

            try {
               return Integer.parseInt(this.buffer.substring(0, separator).trim(), 16);
            } catch (NumberFormatException var5) {
               throw new IllegalArgumentException("Bad chunk header");
            }
         }
      }
   }

   private void parseTrailerHeaders() throws IOException {
      StringBuffer buf;
      int i;
      do {
         buf = new StringBuffer();
         i = readLine(this.in, buf);
      } while(i != -1 && buf.length() >= 1);

   }

   public void close() throws IOException {
      if(!this.closed) {
         try {
            if(!this.eof) {
               byte[] buffer = new byte[2048];

               while(this.read(buffer) >= 0) {
                  ;
               }
            }
         } finally {
            this.eof = true;
            this.closed = true;
         }
      }

   }

   public static final int readLine(InputStream in, StringBuffer buf) throws IOException {
      boolean gotCR = false;

      while(true) {
         int x = in.read();
         if(x == -1) {
            return buf.length() > 0?buf.length():-1;
         }

         if(x == 10 && gotCR) {
            return buf.length();
         }

         if(x == 13) {
            gotCR = true;
         } else {
            gotCR = false;
         }

         if(x != 13) {
            buf.append((char)x);
         }
      }
   }
}
