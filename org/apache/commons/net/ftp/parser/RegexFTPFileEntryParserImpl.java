package org.apache.commons.net.ftp.parser;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.net.ftp.FTPFileEntryParserImpl;

public abstract class RegexFTPFileEntryParserImpl extends FTPFileEntryParserImpl {
   private Pattern pattern = null;
   private MatchResult result = null;
   protected Matcher _matcher_ = null;

   public RegexFTPFileEntryParserImpl(String regex) {
      this.setRegex(regex);
   }

   public boolean matches(String s) {
      this.result = null;
      this._matcher_ = this.pattern.matcher(s);
      if(this._matcher_.matches()) {
         this.result = this._matcher_.toMatchResult();
      }

      return this.result != null;
   }

   public int getGroupCnt() {
      return this.result == null?0:this.result.groupCount();
   }

   public String group(int matchnum) {
      return this.result == null?null:this.result.group(matchnum);
   }

   public String getGroupsAsString() {
      StringBuilder b = new StringBuilder();

      for(int i = 1; i <= this.result.groupCount(); ++i) {
         b.append(i).append(") ").append(this.result.group(i)).append(System.getProperty("line.separator"));
      }

      return b.toString();
   }

   public boolean setRegex(String regex) {
      try {
         this.pattern = Pattern.compile(regex);
      } catch (PatternSyntaxException var3) {
         throw new IllegalArgumentException("Unparseable regex supplied: " + regex);
      }

      return this.pattern != null;
   }
}
