package org.apache.commons.net.ftp.parser;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.FTPFileEntryParserImpl;

public class CompositeFileEntryParser extends FTPFileEntryParserImpl {
   private final FTPFileEntryParser[] ftpFileEntryParsers;
   private FTPFileEntryParser cachedFtpFileEntryParser = null;

   public CompositeFileEntryParser(FTPFileEntryParser[] ftpFileEntryParsers) {
      this.ftpFileEntryParsers = ftpFileEntryParsers;
   }

   public FTPFile parseFTPEntry(String listEntry) {
      if(this.cachedFtpFileEntryParser != null) {
         FTPFile ftpFileEntryParser = this.cachedFtpFileEntryParser.parseFTPEntry(listEntry);
         if(ftpFileEntryParser != null) {
            return ftpFileEntryParser;
         }
      } else {
         FTPFileEntryParser[] var5 = this.ftpFileEntryParsers;
         int var4 = this.ftpFileEntryParsers.length;

         for(int var3 = 0; var3 < var4; ++var3) {
            FTPFileEntryParser var7 = var5[var3];
            FTPFile matched = var7.parseFTPEntry(listEntry);
            if(matched != null) {
               this.cachedFtpFileEntryParser = var7;
               return matched;
            }
         }
      }

      return null;
   }
}
