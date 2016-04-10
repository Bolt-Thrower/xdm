package org.apache.commons.net.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public final class CRLFLineReader extends BufferedReader {
   private static final char LF = '\n';
   private static final char CR = '\r';

   public CRLFLineReader(Reader reader) {
      super(reader);
   }

   public String readLine() throws IOException {
      StringBuilder sb = new StringBuilder();
      boolean prevWasCR = false;
      Object string = this.lock;
      int intch;
      synchronized(this.lock) {
         for(; (intch = this.read()) != -1; sb.append((char)intch)) {
            if(prevWasCR && intch == 10) {
               return sb.substring(0, sb.length() - 1);
            }

            if(intch == 13) {
               prevWasCR = true;
            } else {
               prevWasCR = false;
            }
         }
      }

      String string1 = sb.toString();
      return string1.length() == 0?null:string1;
   }
}
