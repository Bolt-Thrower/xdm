package org.sdg.xdman.interceptor;

import java.util.HashMap;
import java.util.Iterator;

public class HTMLEscapeUtil {
   static HashMap escapeList = null;

   static void init() {
      escapeList = new HashMap();
      escapeList.put("&nbsp;", " ");
      escapeList.put("&quot;", "\"");
      escapeList.put("&amp;", "&");
      escapeList.put("&lt;", "<");
      escapeList.put("&gt;", ">");
      escapeList.put("&iexcl;", "!");
      escapeList.put("&copy;", "(c)");
      escapeList.put("&reg;", "(R)");
   }

   public static String escapeHTMLLine(String line) {
      if(escapeList == null) {
         init();
      }

      String key;
      for(Iterator var2 = escapeList.keySet().iterator(); var2.hasNext(); line = line.replace(key, (String)escapeList.get(key))) {
         key = (String)var2.next();
      }

      return line;
   }
}
