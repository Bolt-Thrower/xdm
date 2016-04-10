package org.sdg.xdman.gui;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StringResource {
   static Properties resourceMap = new Properties();

   public static String getString(String id) {
      return (String)resourceMap.get(id);
   }

   public static void loadResource(String lang) throws FileNotFoundException, IOException {
      try {
         String e = "/lang/en.txt";
         resourceMap.load(StringResource.class.getResourceAsStream(e));
      } catch (Exception var3) {
         String langFile = "lang/en.txt";
         resourceMap.load(new FileReader(langFile));
      }

   }
}
