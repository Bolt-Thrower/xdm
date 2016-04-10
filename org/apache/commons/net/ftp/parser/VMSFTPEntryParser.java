package org.apache.commons.net.ftp.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.StringTokenizer;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;

public class VMSFTPEntryParser extends ConfigurableFTPFileEntryParserImpl {
   private static final String DEFAULT_DATE_FORMAT = "d-MMM-yyyy HH:mm:ss";
   private static final String REGEX = "(.*;[0-9]+)\\s*(\\d+)/\\d+\\s*(\\S+)\\s+(\\S+)\\s+\\[(([0-9$A-Za-z_]+)|([0-9$A-Za-z_]+),([0-9$a-zA-Z_]+))\\]?\\s*\\([a-zA-Z]*,([a-zA-Z]*),([a-zA-Z]*),([a-zA-Z]*)\\)";

   public VMSFTPEntryParser() {
      this((FTPClientConfig)null);
   }

   public VMSFTPEntryParser(FTPClientConfig config) {
      super("(.*;[0-9]+)\\s*(\\d+)/\\d+\\s*(\\S+)\\s+(\\S+)\\s+\\[(([0-9$A-Za-z_]+)|([0-9$A-Za-z_]+),([0-9$a-zA-Z_]+))\\]?\\s*\\([a-zA-Z]*,([a-zA-Z]*),([a-zA-Z]*),([a-zA-Z]*)\\)");
      this.configure(config);
   }

   public FTPFile parseFTPEntry(String entry) {
      long longBlock = 512L;
      if(this.matches(entry)) {
         FTPFile f = new FTPFile();
         f.setRawListing(entry);
         String name = this.group(1);
         String size = this.group(2);
         String datestr = this.group(3) + " " + this.group(4);
         String owner = this.group(5);
         String[] permissions = new String[]{this.group(9), this.group(10), this.group(11)};

         try {
            f.setTimestamp(super.parseTimestamp(datestr));
         } catch (ParseException var17) {
            ;
         }

         StringTokenizer t = new StringTokenizer(owner, ",");
         String grp;
         String user;
         switch(t.countTokens()) {
         case 1:
            grp = null;
            user = t.nextToken();
            break;
         case 2:
            grp = t.nextToken();
            user = t.nextToken();
            break;
         default:
            grp = null;
            user = null;
         }

         if(name.lastIndexOf(".DIR") != -1) {
            f.setType(1);
         } else {
            f.setType(0);
         }

         if(this.isVersioning()) {
            f.setName(name);
         } else {
            name = name.substring(0, name.lastIndexOf(";"));
            f.setName(name);
         }

         long sizeInBytes = Long.parseLong(size) * longBlock;
         f.setSize(sizeInBytes);
         f.setGroup(grp);
         f.setUser(user);

         for(int access = 0; access < 3; ++access) {
            String permission = permissions[access];
            f.setPermission(access, 0, permission.indexOf(82) >= 0);
            f.setPermission(access, 1, permission.indexOf(87) >= 0);
            f.setPermission(access, 2, permission.indexOf(69) >= 0);
         }

         return f;
      } else {
         return null;
      }
   }

   public String readNextEntry(BufferedReader reader) throws IOException {
      String line = reader.readLine();
      StringBuilder entry = new StringBuilder();

      while(line != null) {
         if(!line.startsWith("Directory") && !line.startsWith("Total")) {
            entry.append(line);
            if(line.trim().endsWith(")")) {
               break;
            }

            line = reader.readLine();
         } else {
            line = reader.readLine();
         }
      }

      return entry.length() == 0?null:entry.toString();
   }

   protected boolean isVersioning() {
      return false;
   }

   protected FTPClientConfig getDefaultConfiguration() {
      return new FTPClientConfig("VMS", "d-MMM-yyyy HH:mm:ss", (String)null, (String)null, (String)null, (String)null);
   }

   /** @deprecated */
   @Deprecated
   public FTPFile[] parseFileList(InputStream listStream) throws IOException {
      FTPListParseEngine engine = new FTPListParseEngine(this);
      engine.readServerList(listStream, (String)null);
      return engine.getFiles();
   }
}
