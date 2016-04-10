package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileEntryParserImpl;

public class MLSxEntryParser extends FTPFileEntryParserImpl {
   private static final MLSxEntryParser PARSER = new MLSxEntryParser();
   private static final HashMap TYPE_TO_INT = new HashMap();
   private static int[] UNIX_GROUPS;
   private static int[][] UNIX_PERMS;

   static {
      TYPE_TO_INT.put("file", Integer.valueOf(0));
      TYPE_TO_INT.put("cdir", Integer.valueOf(1));
      TYPE_TO_INT.put("pdir", Integer.valueOf(1));
      TYPE_TO_INT.put("dir", Integer.valueOf(1));
      UNIX_GROUPS = new int[]{0, 1, 2};
      UNIX_PERMS = new int[][]{new int[0], {2}, {1}, {2, 1}, new int[1], {0, 2}, {0, 1}, {0, 1, 2}};
   }

   public FTPFile parseFTPEntry(String entry) {
      String[] parts = entry.split(" ", 2);
      if(parts.length != 2) {
         return null;
      } else {
         FTPFile file = new FTPFile();
         file.setRawListing(entry);
         file.setName(parts[1]);
         String[] facts = parts[0].split(";");
         boolean hasUnixMode = parts[0].toLowerCase(Locale.ENGLISH).contains("unix.mode=");
         String[] var9 = facts;
         int var8 = facts.length;

         for(int var7 = 0; var7 < var8; ++var7) {
            String fact = var9[var7];
            String[] factparts = fact.split("=");
            if(factparts.length == 2) {
               String factname = factparts[0].toLowerCase(Locale.ENGLISH);
               String factvalue = factparts[1];
               String valueLowerCase = factvalue.toLowerCase(Locale.ENGLISH);
               if("size".equals(factname)) {
                  file.setSize(Long.parseLong(factvalue));
               } else if("sizd".equals(factname)) {
                  file.setSize(Long.parseLong(factvalue));
               } else if("modify".equals(factname)) {
                  SimpleDateFormat var25;
                  if(factvalue.contains(".")) {
                     var25 = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
                  } else {
                     var25 = new SimpleDateFormat("yyyyMMddHHmmss");
                  }

                  TimeZone var23 = TimeZone.getTimeZone("GMT");
                  var25.setTimeZone(var23);
                  GregorianCalendar var26 = new GregorianCalendar(var23);

                  try {
                     var26.setTime(var25.parse(factvalue));
                  } catch (ParseException var22) {
                     ;
                  }

                  file.setTimestamp(var26);
               } else if("type".equals(factname)) {
                  Integer var24 = (Integer)TYPE_TO_INT.get(valueLowerCase);
                  if(var24 == null) {
                     file.setType(3);
                  } else {
                     file.setType(var24.intValue());
                  }
               } else if(!factname.startsWith("unix.")) {
                  if(!hasUnixMode && "perm".equals(factname)) {
                     this.doUnixPerms(file, valueLowerCase);
                  }
               } else {
                  String unixfact = factname.substring("unix.".length()).toLowerCase(Locale.ENGLISH);
                  if("group".equals(unixfact)) {
                     file.setGroup(factvalue);
                  } else if("owner".equals(unixfact)) {
                     file.setUser(factvalue);
                  } else if("mode".equals(unixfact)) {
                     int off = factvalue.length() - 3;

                     for(int i = 0; i < 3; ++i) {
                        int ch = factvalue.charAt(off + i) - 48;
                        if(ch >= 0 && ch <= 7) {
                           int[] var21;
                           int var20 = (var21 = UNIX_PERMS[ch]).length;

                           for(int var19 = 0; var19 < var20; ++var19) {
                              int p = var21[var19];
                              file.setPermission(UNIX_GROUPS[i], p, true);
                           }
                        }
                     }
                  }
               }
            }
         }

         return file;
      }
   }

   private void doUnixPerms(FTPFile file, String valueLowerCase) {
      char[] var6;
      int var5 = (var6 = valueLowerCase.toCharArray()).length;

      for(int var4 = 0; var4 < var5; ++var4) {
         char c = var6[var4];
         switch(c) {
         case 'a':
            file.setPermission(0, 1, true);
         case 'b':
         case 'f':
         case 'g':
         case 'h':
         case 'i':
         case 'j':
         case 'k':
         case 'n':
         case 'o':
         case 'q':
         case 's':
         case 't':
         case 'u':
         case 'v':
         default:
            break;
         case 'c':
            file.setPermission(0, 1, true);
            break;
         case 'd':
            file.setPermission(0, 1, true);
            break;
         case 'e':
            file.setPermission(0, 0, true);
            break;
         case 'l':
            file.setPermission(0, 2, true);
            break;
         case 'm':
            file.setPermission(0, 1, true);
            break;
         case 'p':
            file.setPermission(0, 1, true);
            break;
         case 'r':
            file.setPermission(0, 0, true);
            break;
         case 'w':
            file.setPermission(0, 1, true);
         }
      }

   }

   public static FTPFile parseEntry(String entry) {
      return PARSER.parseFTPEntry(entry);
   }

   public static MLSxEntryParser getInstance() {
      return PARSER;
   }
}
