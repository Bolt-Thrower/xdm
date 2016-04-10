package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;

public class OS400FTPEntryParser extends ConfigurableFTPFileEntryParserImpl {
   private static final String DEFAULT_DATE_FORMAT = "yy/MM/dd HH:mm:ss";
   private static final String REGEX = "(\\S+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\*\\S+)\\s+(\\S+/?)\\s*";

   public OS400FTPEntryParser() {
      this((FTPClientConfig)null);
   }

   public OS400FTPEntryParser(FTPClientConfig config) {
      super("(\\S+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\*\\S+)\\s+(\\S+/?)\\s*");
      this.configure(config);
   }

   public FTPFile parseFTPEntry(String entry) {
      FTPFile file = new FTPFile();
      file.setRawListing(entry);
      if(this.matches(entry)) {
         String usr = this.group(1);
         String filesize = this.group(2);
         String datestr = this.group(3) + " " + this.group(4);
         String typeStr = this.group(5);
         String name = this.group(6);

         try {
            file.setTimestamp(super.parseTimestamp(datestr));
         } catch (ParseException var11) {
            ;
         }

         byte type;
         if(typeStr.equalsIgnoreCase("*STMF")) {
            type = 0;
         } else if(typeStr.equalsIgnoreCase("*DIR")) {
            type = 1;
         } else {
            type = 3;
         }

         file.setType(type);
         file.setUser(usr);

         try {
            file.setSize(Long.parseLong(filesize));
         } catch (NumberFormatException var10) {
            ;
         }

         if(name.endsWith("/")) {
            name = name.substring(0, name.length() - 1);
         }

         int pos = name.lastIndexOf(47);
         if(pos > -1) {
            name = name.substring(pos + 1);
         }

         file.setName(name);
         return file;
      } else {
         return null;
      }
   }

   protected FTPClientConfig getDefaultConfiguration() {
      return new FTPClientConfig("OS/400", "yy/MM/dd HH:mm:ss", (String)null, (String)null, (String)null, (String)null);
   }
}
