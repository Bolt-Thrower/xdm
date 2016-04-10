package org.apache.commons.net.ftp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPFileFilters;

public class FTPListParseEngine {
   private List entries = new LinkedList();
   private ListIterator _internalIterator;
   private final FTPFileEntryParser parser;

   public FTPListParseEngine(FTPFileEntryParser parser) {
      this._internalIterator = this.entries.listIterator();
      this.parser = parser;
   }

   public void readServerList(InputStream stream, String encoding) throws IOException {
      this.entries = new LinkedList();
      this.readStream(stream, encoding);
      this.parser.preParse(this.entries);
      this.resetIterator();
   }

   private void readStream(InputStream stream, String encoding) throws IOException {
      BufferedReader reader;
      if(encoding == null) {
         reader = new BufferedReader(new InputStreamReader(stream));
      } else {
         reader = new BufferedReader(new InputStreamReader(stream, encoding));
      }

      for(String line = this.parser.readNextEntry(reader); line != null; line = this.parser.readNextEntry(reader)) {
         this.entries.add(line);
      }

      reader.close();
   }

   public FTPFile[] getNext(int quantityRequested) {
      LinkedList tmpResults = new LinkedList();

      for(int count = quantityRequested; count > 0 && this._internalIterator.hasNext(); --count) {
         String entry = (String)this._internalIterator.next();
         FTPFile temp = this.parser.parseFTPEntry(entry);
         tmpResults.add(temp);
      }

      return (FTPFile[])tmpResults.toArray(new FTPFile[tmpResults.size()]);
   }

   public FTPFile[] getPrevious(int quantityRequested) {
      LinkedList tmpResults = new LinkedList();

      for(int count = quantityRequested; count > 0 && this._internalIterator.hasPrevious(); --count) {
         String entry = (String)this._internalIterator.previous();
         FTPFile temp = this.parser.parseFTPEntry(entry);
         tmpResults.add(0, temp);
      }

      return (FTPFile[])tmpResults.toArray(new FTPFile[tmpResults.size()]);
   }

   public FTPFile[] getFiles() throws IOException {
      return this.getFiles(FTPFileFilters.NON_NULL);
   }

   public FTPFile[] getFiles(FTPFileFilter filter) throws IOException {
      ArrayList tmpResults = new ArrayList();
      Iterator iter = this.entries.iterator();

      while(iter.hasNext()) {
         String entry = (String)iter.next();
         FTPFile temp = this.parser.parseFTPEntry(entry);
         if(filter.accept(temp)) {
            tmpResults.add(temp);
         }
      }

      return (FTPFile[])tmpResults.toArray(new FTPFile[tmpResults.size()]);
   }

   public boolean hasNext() {
      return this._internalIterator.hasNext();
   }

   public boolean hasPrevious() {
      return this._internalIterator.hasPrevious();
   }

   public void resetIterator() {
      this._internalIterator = this.entries.listIterator();
   }

   /** @deprecated */
   @Deprecated
   public void readServerList(InputStream stream) throws IOException {
      this.readServerList(stream, (String)null);
   }
}
