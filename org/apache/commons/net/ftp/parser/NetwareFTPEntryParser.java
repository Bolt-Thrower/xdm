package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;

public class NetwareFTPEntryParser extends ConfigurableFTPFileEntryParserImpl {
   private static final String DEFAULT_DATE_FORMAT = "MMM dd yyyy";
   private static final String DEFAULT_RECENT_DATE_FORMAT = "MMM dd HH:mm";
   private static final String REGEX = "(d|-){1}\\s+\\[(.*)\\]\\s+(\\S+)\\s+(\\d+)\\s+(\\S+\\s+\\S+\\s+((\\d+:\\d+)|(\\d{4})))\\s+(.*)";

   public NetwareFTPEntryParser() {
      this((FTPClientConfig)null);
   }

   public NetwareFTPEntryParser(FTPClientConfig config) {
      super("(d|-){1}\\s+\\[(.*)\\]\\s+(\\S+)\\s+(\\d+)\\s+(\\S+\\s+\\S+\\s+((\\d+:\\d+)|(\\d{4})))\\s+(.*)");
      this.configure(config);
   }

   public FTPFile parseFTPEntry(String entry) {
      FTPFile f = new FTPFile();
      if(this.matches(entry)) {
         String dirString = this.group(1);
         String attrib = this.group(2);
         String user = this.group(3);
         String size = this.group(4);
         String datestr = this.group(5);
         String name = this.group(9);

         try {
            f.setTimestamp(super.parseTimestamp(datestr));
         } catch (ParseException var10) {
            ;
         }

         if(dirString.trim().equals("d")) {
            f.setType(1);
         } else {
            f.setType(0);
         }

         f.setUser(user);
         f.setName(name.trim());
         f.setSize(Long.parseLong(size.trim()));
         if(attrib.indexOf("R") != -1) {
            f.setPermission(0, 0, true);
         }

         if(attrib.indexOf("W") != -1) {
            f.setPermission(0, 1, true);
         }

         return f;
      } else {
         return null;
      }
   }

   protected FTPClientConfig getDefaultConfiguration() {
      return new FTPClientConfig("NETWARE", "MMM dd yyyy", "MMM dd HH:mm", (String)null, (String)null, (String)null);
   }
}
