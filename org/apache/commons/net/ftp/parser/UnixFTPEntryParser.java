package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;

public class UnixFTPEntryParser extends ConfigurableFTPFileEntryParserImpl {
   static final String DEFAULT_DATE_FORMAT = "MMM d yyyy";
   static final String DEFAULT_RECENT_DATE_FORMAT = "MMM d HH:mm";
   static final String NUMERIC_DATE_FORMAT = "yyyy-MM-dd HH:mm";
   public static final FTPClientConfig NUMERIC_DATE_CONFIG = new FTPClientConfig("UNIX", "yyyy-MM-dd HH:mm", (String)null, (String)null, (String)null, (String)null);
   private static final String REGEX = "([bcdelfmpSs-])(((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-])))\\+?\\s*(\\d+)\\s+(?:(\\S+(?:\\s\\S+)*?)\\s+)?(?:(\\S+(?:\\s\\S+)*)\\s+)?(\\d+(?:,\\s*\\d+)?)\\s+((?:\\d+[-/]\\d+[-/]\\d+)|(?:\\S{3}\\s+\\d{1,2})|(?:\\d{1,2}\\s+\\S{3}))\\s+(\\d+(?::\\d+)?)\\s+(\\S*)(\\s*.*)";

   public UnixFTPEntryParser() {
      this((FTPClientConfig)null);
   }

   public UnixFTPEntryParser(FTPClientConfig config) {
      super("([bcdelfmpSs-])(((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-])))\\+?\\s*(\\d+)\\s+(?:(\\S+(?:\\s\\S+)*?)\\s+)?(?:(\\S+(?:\\s\\S+)*)\\s+)?(\\d+(?:,\\s*\\d+)?)\\s+((?:\\d+[-/]\\d+[-/]\\d+)|(?:\\S{3}\\s+\\d{1,2})|(?:\\d{1,2}\\s+\\S{3}))\\s+(\\d+(?::\\d+)?)\\s+(\\S*)(\\s*.*)");
      this.configure(config);
   }

   public List preParse(List original) {
      ListIterator iter = original.listIterator();

      while(iter.hasNext()) {
         String entry = (String)iter.next();
         if(entry.matches("^total \\d+$")) {
            iter.remove();
         }
      }

      return original;
   }

   public FTPFile parseFTPEntry(String entry) {
      FTPFile file = new FTPFile();
      file.setRawListing(entry);
      boolean isDevice = false;
      if(!this.matches(entry)) {
         return null;
      } else {
         String typeStr = this.group(1);
         String hardLinkCount = this.group(15);
         String usr = this.group(16);
         String grp = this.group(17);
         String filesize = this.group(18);
         String datestr = this.group(19) + " " + this.group(20);
         String name = this.group(21);
         String endtoken = this.group(22);

         try {
            file.setTimestamp(super.parseTimestamp(datestr));
         } catch (ParseException var18) {
            ;
         }

         byte type;
         switch(typeStr.charAt(0)) {
         case '-':
         case 'f':
            type = 0;
            break;
         case 'b':
         case 'c':
            isDevice = true;
            type = 0;
            break;
         case 'd':
            type = 1;
            break;
         case 'e':
            type = 2;
            break;
         case 'l':
            type = 2;
            break;
         default:
            type = 3;
         }

         file.setType(type);
         int g = 4;

         int end;
         for(end = 0; end < 3; g += 4) {
            file.setPermission(end, 0, !this.group(g).equals("-"));
            file.setPermission(end, 1, !this.group(g + 1).equals("-"));
            String execPerm = this.group(g + 2);
            if(!execPerm.equals("-") && !Character.isUpperCase(execPerm.charAt(0))) {
               file.setPermission(end, 2, true);
            } else {
               file.setPermission(end, 2, false);
            }

            ++end;
         }

         if(!isDevice) {
            try {
               file.setHardLinkCount(Integer.parseInt(hardLinkCount));
            } catch (NumberFormatException var17) {
               ;
            }
         }

         file.setUser(usr);
         file.setGroup(grp);

         try {
            file.setSize(Long.parseLong(filesize));
         } catch (NumberFormatException var16) {
            ;
         }

         if(endtoken == null) {
            file.setName(name);
         } else {
            name = name + endtoken;
            if(type == 2) {
               end = name.indexOf(" -> ");
               if(end == -1) {
                  file.setName(name);
               } else {
                  file.setName(name.substring(0, end));
                  file.setLink(name.substring(end + 4));
               }
            } else {
               file.setName(name);
            }
         }

         return file;
      }
   }

   protected FTPClientConfig getDefaultConfiguration() {
      return new FTPClientConfig("UNIX", "MMM d yyyy", "MMM d HH:mm", (String)null, (String)null, (String)null);
   }
}
