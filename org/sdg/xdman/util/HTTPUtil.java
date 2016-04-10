package org.sdg.xdman.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class HTTPUtil {
   public static byte[] getBytes(String str) {
      return str.getBytes();
   }

   public static final String readLine(InputStream in) throws IOException {
      StringBuffer buf = new StringBuffer();

      while(true) {
         int x = in.read();
         if(x == -1) {
            throw new IOException("Unexpected EOF while reading header line");
         }

         if(x == 10) {
            return buf.toString();
         }

         if(x != 13) {
            buf.append((char)x);
         }
      }
   }

   public static final int getResponseCode(String statusLine) {
      String[] arr = statusLine.split(" ");
      return arr.length < 2?400:Integer.parseInt(arr[1]);
   }

   public static long getContentLength(HashMap map) {
      try {
         String e = (String)map.get("content-length");
         if(e != null) {
            return Long.parseLong(e);
         } else {
            e = (String)map.get("content-range");
            if(e != null) {
               String str = e.split(" ")[1];
               str = str.split("/")[0];
               String[] arr = str.split("-");
               return Long.parseLong(arr[1]) - Long.parseLong(arr[0]) + 1L;
            } else {
               return -1L;
            }
         }
      } catch (Exception var4) {
         return -1L;
      }
   }
}
