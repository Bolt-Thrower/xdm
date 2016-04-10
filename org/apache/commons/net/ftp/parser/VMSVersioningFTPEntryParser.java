package org.apache.commons.net.ftp.parser;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.parser.VMSFTPEntryParser;

public class VMSVersioningFTPEntryParser extends VMSFTPEntryParser {
   private final Pattern _preparse_pattern_;
   private static final String PRE_PARSE_REGEX = "(.*);([0-9]+)\\s*.*";

   public VMSVersioningFTPEntryParser() {
      this((FTPClientConfig)null);
   }

   public VMSVersioningFTPEntryParser(FTPClientConfig config) {
      this.configure(config);

      try {
         this._preparse_pattern_ = Pattern.compile("(.*);([0-9]+)\\s*.*");
      } catch (PatternSyntaxException var3) {
         throw new IllegalArgumentException("Unparseable regex supplied:  (.*);([0-9]+)\\s*.*");
      }
   }

   public List preParse(List original) {
      HashMap existingEntries = new HashMap();
      ListIterator iter = original.listIterator();

      while(true) {
         while(true) {
            MatchResult result;
            Matcher _preparse_matcher_;
            String name;
            String version;
            Integer nv;
            Integer existing;
            do {
               String entry;
               if(!iter.hasNext()) {
                  while(iter.hasPrevious()) {
                     entry = ((String)iter.previous()).trim();
                     result = null;
                     _preparse_matcher_ = this._preparse_pattern_.matcher(entry);
                     if(_preparse_matcher_.matches()) {
                        result = _preparse_matcher_.toMatchResult();
                        name = result.group(1);
                        version = result.group(2);
                        nv = Integer.valueOf(version);
                        existing = (Integer)existingEntries.get(name);
                        if(existing != null && nv.intValue() < existing.intValue()) {
                           iter.remove();
                        }
                     }
                  }

                  return original;
               }

               entry = ((String)iter.next()).trim();
               result = null;
               _preparse_matcher_ = this._preparse_pattern_.matcher(entry);
            } while(!_preparse_matcher_.matches());

            result = _preparse_matcher_.toMatchResult();
            name = result.group(1);
            version = result.group(2);
            nv = Integer.valueOf(version);
            existing = (Integer)existingEntries.get(name);
            if(existing != null && nv.intValue() < existing.intValue()) {
               iter.remove();
            } else {
               existingEntries.put(name, nv);
            }
         }
      }
   }

   protected boolean isVersioning() {
      return true;
   }
}
