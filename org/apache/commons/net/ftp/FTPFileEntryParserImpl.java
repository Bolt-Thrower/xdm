package org.apache.commons.net.ftp;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import org.apache.commons.net.ftp.FTPFileEntryParser;

public abstract class FTPFileEntryParserImpl implements FTPFileEntryParser {
   public String readNextEntry(BufferedReader reader) throws IOException {
      return reader.readLine();
   }

   public List preParse(List original) {
      return original;
   }
}
