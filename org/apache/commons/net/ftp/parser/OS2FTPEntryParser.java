package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;

public class OS2FTPEntryParser extends ConfigurableFTPFileEntryParserImpl {
   private static final String DEFAULT_DATE_FORMAT = "MM-dd-yy HH:mm";
   private static final String REGEX = "\\s*([0-9]+)\\s*(\\s+|[A-Z]+)\\s*(DIR|\\s+)\\s*(\\S+)\\s+(\\S+)\\s+(\\S.*)";

   public OS2FTPEntryParser() {
      this((FTPClientConfig)null);
   }

   public OS2FTPEntryParser(FTPClientConfig config) {
      super("\\s*([0-9]+)\\s*(\\s+|[A-Z]+)\\s*(DIR|\\s+)\\s*(\\S+)\\s+(\\S+)\\s+(\\S.*)");
      this.configure(config);
   }

   public FTPFile parseFTPEntry(String entry) {
      FTPFile f = new FTPFile();
      if(this.matches(entry)) {
         String size = this.group(1);
         String attrib = this.group(2);
         String dirString = this.group(3);
         String datestr = this.group(4) + " " + this.group(5);
         String name = this.group(6);

         try {
            f.setTimestamp(super.parseTimestamp(datestr));
         } catch (ParseException var9) {
            ;
         }

         if(!dirString.trim().equals("DIR") && !attrib.trim().equals("DIR")) {
            f.setType(0);
         } else {
            f.setType(1);
         }

         f.setName(name.trim());
         f.setSize(Long.parseLong(size.trim()));
         return f;
      } else {
         return null;
      }
   }

   protected FTPClientConfig getDefaultConfiguration() {
      return new FTPClientConfig("OS/2", "MM-dd-yy HH:mm", (String)null, (String)null, (String)null, (String)null);
   }
}
