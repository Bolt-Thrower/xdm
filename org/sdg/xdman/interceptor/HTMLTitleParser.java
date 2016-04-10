package org.sdg.xdman.interceptor;

import java.io.BufferedReader;
import java.io.IOException;

public class HTMLTitleParser {
   public static String GetTitleFromPage(BufferedReader r) throws IOException {
      boolean pos = false;
      boolean end = false;
      StringBuilder title = new StringBuilder();

      while(true) {
         String line;
         int index;
         int pos1;
         while(true) {
            pos1 = 0;
            line = r.readLine();
            if(line == null) {
               return title.toString();
            }

            if(end) {
               break;
            }

            index = line.indexOf("<title>");
            if(index != -1) {
               pos1 = index + 7;
               end = true;
               break;
            }
         }

         if(end) {
            index = line.indexOf("</title>", pos1);
            if(index != -1) {
               int len = index - pos1;
               if(len < 1) {
                  return null;
               }

               String stitle = line.substring(pos1, pos1 + len);
               title.append(stitle);
               return title.toString();
            }

            title.append(line);
         }
      }
   }
}
