package org.apache.commons.net.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public final class DotTerminatedMessageReader extends BufferedReader {
   private static final char LF = '\n';
   private static final char CR = '\r';
   private static final int DOT = 46;
   private boolean atBeginning = true;
   private boolean eof = false;
   private boolean seenCR;

   public DotTerminatedMessageReader(Reader reader) {
      super(reader);
   }

   public int read() throws IOException {
      Object var1 = this.lock;
      synchronized(this.lock) {
         if(this.eof) {
            return -1;
         } else {
            int chint = super.read();
            if(chint == -1) {
               this.eof = true;
               return -1;
            } else {
               if(this.atBeginning) {
                  this.atBeginning = false;
                  if(chint == 46) {
                     this.mark(2);
                     chint = super.read();
                     if(chint == -1) {
                        this.eof = true;
                        return 46;
                     }

                     if(chint == 46) {
                        return chint;
                     }

                     if(chint == 13) {
                        chint = super.read();
                        if(chint == -1) {
                           this.reset();
                           return 46;
                        }

                        if(chint == 10) {
                           this.atBeginning = true;
                           this.eof = true;
                           return -1;
                        }
                     }

                     this.reset();
                     return 46;
                  }
               }

               if(this.seenCR) {
                  this.seenCR = false;
                  if(chint == 10) {
                     this.atBeginning = true;
                  }
               }

               if(chint == 13) {
                  this.seenCR = true;
               }

               return chint;
            }
         }
      }
   }

   public int read(char[] buffer) throws IOException {
      return this.read(buffer, 0, buffer.length);
   }

   public int read(char[] buffer, int offset, int length) throws IOException {
      if(length < 1) {
         return 0;
      } else {
         Object var5 = this.lock;
         synchronized(this.lock) {
            int ch;
            if((ch = this.read()) == -1) {
               return -1;
            } else {
               int off = offset;

               do {
                  buffer[offset++] = (char)ch;
                  --length;
               } while(length > 0 && (ch = this.read()) != -1);

               return offset - off;
            }
         }
      }
   }

   public void close() throws IOException {
      Object var1 = this.lock;
      synchronized(this.lock) {
         if(!this.eof) {
            while(this.read() != -1) {
               ;
            }
         }

         this.eof = true;
         this.atBeginning = false;
      }
   }

   public String readLine() throws IOException {
      StringBuilder sb = new StringBuilder();
      Object string = this.lock;
      int intch;
      synchronized(this.lock) {
         while((intch = this.read()) != -1) {
            if(intch == 10 && this.atBeginning) {
               return sb.substring(0, sb.length() - 1);
            }

            sb.append((char)intch);
         }
      }

      String string1 = sb.toString();
      return string1.length() == 0?null:string1;
   }
}
