package org.apache.commons.net.ftp.parser;

import java.util.Calendar;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.RegexFTPFileEntryParserImpl;

public class EnterpriseUnixFTPEntryParser extends RegexFTPFileEntryParserImpl {
   private static final String MONTHS = "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)";
   private static final String REGEX = "(([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z]))(\\S*)\\s*(\\S+)\\s*(\\S*)\\s*(\\d*)\\s*(\\d*)\\s*(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s*((?:[012]\\d*)|(?:3[01]))\\s*((\\d\\d\\d\\d)|((?:[01]\\d)|(?:2[0123])):([012345]\\d))\\s(\\S*)(\\s*.*)";

   public EnterpriseUnixFTPEntryParser() {
      super("(([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z]))(\\S*)\\s*(\\S+)\\s*(\\S*)\\s*(\\d*)\\s*(\\d*)\\s*(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s*((?:[012]\\d*)|(?:3[01]))\\s*((\\d\\d\\d\\d)|((?:[01]\\d)|(?:2[0123])):([012345]\\d))\\s(\\S*)(\\s*.*)");
   }

   public FTPFile parseFTPEntry(String entry) {
      FTPFile file = new FTPFile();
      file.setRawListing(entry);
      if(this.matches(entry)) {
         String usr = this.group(14);
         String grp = this.group(15);
         String filesize = this.group(16);
         String mo = this.group(17);
         String da = this.group(18);
         String yr = this.group(20);
         String hr = this.group(21);
         String min = this.group(22);
         String name = this.group(23);
         file.setType(0);
         file.setUser(usr);
         file.setGroup(grp);

         try {
            file.setSize(Long.parseLong(filesize));
         } catch (NumberFormatException var17) {
            ;
         }

         Calendar cal = Calendar.getInstance();
         cal.set(14, 0);
         cal.set(13, 0);
         cal.set(12, 0);
         cal.set(11, 0);

         try {
            int pos = "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)".indexOf(mo);
            int month = pos / 4;
            if(yr != null) {
               cal.set(1, Integer.parseInt(yr));
            } else {
               int year = cal.get(1);
               if(cal.get(2) < month) {
                  --year;
               }

               cal.set(1, year);
               cal.set(11, Integer.parseInt(hr));
               cal.set(12, Integer.parseInt(min));
            }

            cal.set(2, month);
            cal.set(5, Integer.parseInt(da));
            file.setTimestamp(cal);
         } catch (NumberFormatException var16) {
            ;
         }

         file.setName(name);
         return file;
      } else {
         return null;
      }
   }
}
