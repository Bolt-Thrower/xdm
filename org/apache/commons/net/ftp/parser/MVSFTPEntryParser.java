package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import java.util.List;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;
import org.apache.commons.net.ftp.parser.UnixFTPEntryParser;

public class MVSFTPEntryParser extends ConfigurableFTPFileEntryParserImpl {
   static final int UNKNOWN_LIST_TYPE = -1;
   static final int FILE_LIST_TYPE = 0;
   static final int MEMBER_LIST_TYPE = 1;
   static final int UNIX_LIST_TYPE = 2;
   static final int JES_LEVEL_1_LIST_TYPE = 3;
   static final int JES_LEVEL_2_LIST_TYPE = 4;
   private int isType = -1;
   private UnixFTPEntryParser unixFTPEntryParser;
   static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd HH:mm";
   static final String FILE_LIST_REGEX = "\\S+\\s+\\S+\\s+\\S+\\s+\\S+\\s+\\S+\\s+[FV]\\S*\\s+\\S+\\s+\\S+\\s+(PS|PO|PO-E)\\s+(\\S+)\\s*";
   static final String MEMBER_LIST_REGEX = "(\\S+)\\s+\\S+\\s+\\S+\\s+(\\S+)\\s+(\\S+)\\s+\\S+\\s+\\S+\\s+\\S+\\s+\\S+\\s*";
   static final String JES_LEVEL_1_LIST_REGEX = "(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s*";
   static final String JES_LEVEL_2_LIST_REGEX = "(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+).*";

   public MVSFTPEntryParser() {
      super("");
      super.configure((FTPClientConfig)null);
   }

   public FTPFile parseFTPEntry(String entry) {
      boolean isParsed = false;
      FTPFile f = new FTPFile();
      if(this.isType == 0) {
         isParsed = this.parseFileList(f, entry);
      } else if(this.isType == 1) {
         isParsed = this.parseMemberList(f, entry);
         if(!isParsed) {
            isParsed = this.parseSimpleEntry(f, entry);
         }
      } else if(this.isType == 2) {
         isParsed = this.parseUnixList(f, entry);
      } else if(this.isType == 3) {
         isParsed = this.parseJeslevel1List(f, entry);
      } else if(this.isType == 4) {
         isParsed = this.parseJeslevel2List(f, entry);
      }

      if(!isParsed) {
         f = null;
      }

      return f;
   }

   private boolean parseFileList(FTPFile file, String entry) {
      if(this.matches(entry)) {
         file.setRawListing(entry);
         String name = this.group(2);
         String dsorg = this.group(1);
         file.setName(name);
         if("PS".equals(dsorg)) {
            file.setType(0);
         } else {
            if(!"PO".equals(dsorg) && !"PO-E".equals(dsorg)) {
               return false;
            }

            file.setType(1);
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean parseMemberList(FTPFile file, String entry) {
      if(this.matches(entry)) {
         file.setRawListing(entry);
         String name = this.group(1);
         String datestr = this.group(2) + " " + this.group(3);
         file.setName(name);
         file.setType(0);

         try {
            file.setTimestamp(super.parseTimestamp(datestr));
            return true;
         } catch (ParseException var6) {
            var6.printStackTrace();
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean parseSimpleEntry(FTPFile file, String entry) {
      if(entry != null && entry.trim().length() > 0) {
         file.setRawListing(entry);
         String name = entry.split(" ")[0];
         file.setName(name);
         file.setType(0);
         return true;
      } else {
         return false;
      }
   }

   private boolean parseUnixList(FTPFile file, String entry) {
      file = this.unixFTPEntryParser.parseFTPEntry(entry);
      return file != null;
   }

   private boolean parseJeslevel1List(FTPFile file, String entry) {
      if(this.matches(entry) && this.group(3).equalsIgnoreCase("OUTPUT")) {
         file.setRawListing(entry);
         String name = this.group(2);
         file.setName(name);
         file.setType(0);
         return true;
      } else {
         return false;
      }
   }

   private boolean parseJeslevel2List(FTPFile file, String entry) {
      if(this.matches(entry) && this.group(4).equalsIgnoreCase("OUTPUT")) {
         file.setRawListing(entry);
         String name = this.group(2);
         file.setName(name);
         file.setType(0);
         return true;
      } else {
         return false;
      }
   }

   public List preParse(List orig) {
      if(orig != null && orig.size() > 0) {
         String header = (String)orig.get(0);
         if(header.indexOf("Volume") >= 0 && header.indexOf("Dsname") >= 0) {
            this.setType(0);
            super.setRegex("\\S+\\s+\\S+\\s+\\S+\\s+\\S+\\s+\\S+\\s+[FV]\\S*\\s+\\S+\\s+\\S+\\s+(PS|PO|PO-E)\\s+(\\S+)\\s*");
         } else if(header.indexOf("Name") >= 0 && header.indexOf("Id") >= 0) {
            this.setType(1);
            super.setRegex("(\\S+)\\s+\\S+\\s+\\S+\\s+(\\S+)\\s+(\\S+)\\s+\\S+\\s+\\S+\\s+\\S+\\s+\\S+\\s*");
         } else if(header.indexOf("total") == 0) {
            this.setType(2);
            this.unixFTPEntryParser = new UnixFTPEntryParser();
         } else if(header.indexOf("Spool Files") >= 30) {
            this.setType(3);
            super.setRegex("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s*");
         } else if(header.indexOf("JOBNAME") == 0 && header.indexOf("JOBID") > 8) {
            this.setType(4);
            super.setRegex("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+).*");
         } else {
            this.setType(-1);
         }

         if(this.isType != 3) {
            orig.remove(0);
         }
      }

      return orig;
   }

   void setType(int type) {
      this.isType = type;
   }

   protected FTPClientConfig getDefaultConfiguration() {
      return new FTPClientConfig("MVS", "yyyy/MM/dd HH:mm", (String)null, (String)null, (String)null, (String)null);
   }
}
