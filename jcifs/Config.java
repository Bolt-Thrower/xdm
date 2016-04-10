package jcifs;

import java.util.Properties;

public class Config {
   private static Properties prp = new Properties();

   public static void setProperties(Properties prp) {
      prp = new Properties(prp);

      try {
         prp.putAll(System.getProperties());
      } catch (SecurityException var2) {
         ;
      }

   }

   public static Object setProperty(String key, String value) {
      return prp.setProperty(key, value);
   }

   public static Object get(String key) {
      return prp.get(key);
   }

   public static String getProperty(String key, String def) {
      return prp.getProperty(key, def);
   }

   public static String getProperty(String key) {
      return prp.getProperty(key);
   }

   public static int getInt(String key, int def) {
      String s = prp.getProperty(key);
      if(s != null) {
         try {
            def = Integer.parseInt(s);
         } catch (NumberFormatException var4) {
            ;
         }
      }

      return def;
   }

   public static int getInt(String key) {
      String s = prp.getProperty(key);
      int result = -1;
      if(s != null) {
         try {
            result = Integer.parseInt(s);
         } catch (NumberFormatException var4) {
            ;
         }
      }

      return result;
   }

   public static long getLong(String key, long def) {
      String s = prp.getProperty(key);
      if(s != null) {
         try {
            def = Long.parseLong(s);
         } catch (NumberFormatException var5) {
            ;
         }
      }

      return def;
   }

   public static boolean getBoolean(String key, boolean def) {
      String b = getProperty(key);
      if(b != null) {
         def = b.toLowerCase().equals("true");
      }

      return def;
   }
}
