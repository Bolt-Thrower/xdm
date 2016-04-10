package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;

public class MacOsPeterFTPEntryParser extends ConfigurableFTPFileEntryParserImpl {
   static final String DEFAULT_DATE_FORMAT = "MMM d yyyy";
   static final String DEFAULT_RECENT_DATE_FORMAT = "MMM d HH:mm";
   private static final String REGEX = "([bcdelfmpSs-])(((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-])))\\+?\\s+((folder\\s+)|((\\d+)\\s+(\\d+)\\s+))(\\d+)\\s+((?:\\d+[-/]\\d+[-/]\\d+)|(?:\\S{3}\\s+\\d{1,2})|(?:\\d{1,2}\\s+\\S{3}))\\s+(\\d+(?::\\d+)?)\\s+(\\S*)(\\s*.*)";

   public MacOsPeterFTPEntryParser() {
      this((FTPClientConfig)null);
   }

   public MacOsPeterFTPEntryParser(FTPClientConfig config) {
      super("([bcdelfmpSs-])(((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-])))\\+?\\s+((folder\\s+)|((\\d+)\\s+(\\d+)\\s+))(\\d+)\\s+((?:\\d+[-/]\\d+[-/]\\d+)|(?:\\S{3}\\s+\\d{1,2})|(?:\\d{1,2}\\s+\\S{3}))\\s+(\\d+(?::\\d+)?)\\s+(\\S*)(\\s*.*)");
      this.configure(config);
   }

   public FTPFile parseFTPEntry(String entry) {
      FTPFile file = new FTPFile();
      file.setRawListing(entry);
      boolean isDevice = false;
      if(!this.matches(entry)) {
         return null;
      } else {
         String typeStr = this.group(1);
         String hardLinkCount = "0";
         Object usr = null;
         Object grp = null;
         String filesize = this.group(20);
         String datestr = this.group(21) + " " + this.group(22);
         String name = this.group(23);
         String endtoken = this.group(24);

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

         file.setUser((String)usr);
         file.setGroup((String)grp);

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
