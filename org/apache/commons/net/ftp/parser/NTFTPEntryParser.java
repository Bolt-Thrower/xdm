package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import org.apache.commons.net.ftp.Configurable;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;
import org.apache.commons.net.ftp.parser.FTPTimestampParser;
import org.apache.commons.net.ftp.parser.FTPTimestampParserImpl;

public class NTFTPEntryParser extends ConfigurableFTPFileEntryParserImpl {
   private static final String DEFAULT_DATE_FORMAT = "MM-dd-yy hh:mma";
   private static final String DEFAULT_DATE_FORMAT2 = "MM-dd-yy kk:mm";
   private FTPTimestampParser timestampParser;
   private static final String REGEX = "(\\S+)\\s+(\\S+)\\s+(?:(<DIR>)|([0-9]+))\\s+(\\S.*)";

   public NTFTPEntryParser() {
      this((FTPClientConfig)null);
   }

   public NTFTPEntryParser(FTPClientConfig config) {
      super("(\\S+)\\s+(\\S+)\\s+(?:(<DIR>)|([0-9]+))\\s+(\\S.*)");
      this.configure(config);
      FTPClientConfig config2 = new FTPClientConfig("WINDOWS", "MM-dd-yy kk:mm", (String)null, (String)null, (String)null, (String)null);
      config2.setDefaultDateFormatStr("MM-dd-yy kk:mm");
      this.timestampParser = new FTPTimestampParserImpl();
      ((Configurable)this.timestampParser).configure(config2);
   }

   public FTPFile parseFTPEntry(String entry) {
      FTPFile f = new FTPFile();
      f.setRawListing(entry);
      if(this.matches(entry)) {
         String datestr = this.group(1) + " " + this.group(2);
         String dirString = this.group(3);
         String size = this.group(4);
         String name = this.group(5);

         try {
            f.setTimestamp(super.parseTimestamp(datestr));
         } catch (ParseException var10) {
            try {
               f.setTimestamp(this.timestampParser.parseTimestamp(datestr));
            } catch (ParseException var9) {
               ;
            }
         }

         if(name != null && !name.equals(".") && !name.equals("..")) {
            f.setName(name);
            if("<DIR>".equals(dirString)) {
               f.setType(1);
               f.setSize(0L);
            } else {
               f.setType(0);
               if(size != null) {
                  f.setSize(Long.parseLong(size));
               }
            }

            return f;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public FTPClientConfig getDefaultConfiguration() {
      return new FTPClientConfig("WINDOWS", "MM-dd-yy hh:mma", (String)null, (String)null, (String)null, (String)null);
   }
}
