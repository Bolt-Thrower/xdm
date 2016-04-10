package org.apache.commons.net.ftp.parser;

import java.util.Locale;
import java.util.regex.Pattern;
import org.apache.commons.net.ftp.Configurable;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.parser.CompositeFileEntryParser;
import org.apache.commons.net.ftp.parser.FTPFileEntryParserFactory;
import org.apache.commons.net.ftp.parser.MVSFTPEntryParser;
import org.apache.commons.net.ftp.parser.MacOsPeterFTPEntryParser;
import org.apache.commons.net.ftp.parser.NTFTPEntryParser;
import org.apache.commons.net.ftp.parser.NetwareFTPEntryParser;
import org.apache.commons.net.ftp.parser.OS2FTPEntryParser;
import org.apache.commons.net.ftp.parser.OS400FTPEntryParser;
import org.apache.commons.net.ftp.parser.ParserInitializationException;
import org.apache.commons.net.ftp.parser.UnixFTPEntryParser;
import org.apache.commons.net.ftp.parser.VMSVersioningFTPEntryParser;

public class DefaultFTPFileEntryParserFactory implements FTPFileEntryParserFactory {
   private static final String JAVA_IDENTIFIER = "\\p{javaJavaIdentifierStart}(\\p{javaJavaIdentifierPart})*";
   private static final String JAVA_QUALIFIED_NAME = "(\\p{javaJavaIdentifierStart}(\\p{javaJavaIdentifierPart})*\\.)+\\p{javaJavaIdentifierStart}(\\p{javaJavaIdentifierPart})*";
   private static final Pattern JAVA_QUALIFIED_NAME_PATTERN = Pattern.compile("(\\p{javaJavaIdentifierStart}(\\p{javaJavaIdentifierPart})*\\.)+\\p{javaJavaIdentifierStart}(\\p{javaJavaIdentifierPart})*");

   public FTPFileEntryParser createFileEntryParser(String key) {
      if(key == null) {
         throw new ParserInitializationException("Parser key cannot be null");
      } else {
         return this.createFileEntryParser(key, (FTPClientConfig)null);
      }
   }

   private FTPFileEntryParser createFileEntryParser(String key, FTPClientConfig config) {
      Object parser = null;
      if(JAVA_QUALIFIED_NAME_PATTERN.matcher(key).matches()) {
         try {
            Class ukey = Class.forName(key);

            try {
               parser = (FTPFileEntryParser)ukey.newInstance();
            } catch (ClassCastException var6) {
               throw new ParserInitializationException(ukey.getName() + " does not implement the interface " + "org.apache.commons.net.ftp.FTPFileEntryParser.", var6);
            } catch (Exception var7) {
               throw new ParserInitializationException("Error initializing parser", var7);
            } catch (ExceptionInInitializerError var8) {
               throw new ParserInitializationException("Error initializing parser", var8);
            }
         } catch (ClassNotFoundException var9) {
            ;
         }
      }

      if(parser == null) {
         String ukey1 = key.toUpperCase(Locale.ENGLISH);
         if(ukey1.indexOf("UNIX") >= 0) {
            parser = new UnixFTPEntryParser(config);
         } else if(ukey1.indexOf("VMS") >= 0) {
            parser = new VMSVersioningFTPEntryParser(config);
         } else if(ukey1.indexOf("WINDOWS") >= 0) {
            parser = this.createNTFTPEntryParser(config);
         } else if(ukey1.indexOf("OS/2") >= 0) {
            parser = new OS2FTPEntryParser(config);
         } else if(ukey1.indexOf("OS/400") < 0 && ukey1.indexOf("AS/400") < 0) {
            if(ukey1.indexOf("MVS") >= 0) {
               parser = new MVSFTPEntryParser();
            } else if(ukey1.indexOf("NETWARE") >= 0) {
               parser = new NetwareFTPEntryParser(config);
            } else if(ukey1.indexOf("MACOS PETER") >= 0) {
               parser = new MacOsPeterFTPEntryParser(config);
            } else {
               if(ukey1.indexOf("TYPE: L8") < 0) {
                  throw new ParserInitializationException("Unknown parser type: " + key);
               }

               parser = new UnixFTPEntryParser(config);
            }
         } else {
            parser = this.createOS400FTPEntryParser(config);
         }
      }

      if(parser instanceof Configurable) {
         ((Configurable)parser).configure(config);
      }

      return (FTPFileEntryParser)parser;
   }

   public FTPFileEntryParser createFileEntryParser(FTPClientConfig config) throws ParserInitializationException {
      String key = config.getServerSystemKey();
      return this.createFileEntryParser(key, config);
   }

   public FTPFileEntryParser createUnixFTPEntryParser() {
      return new UnixFTPEntryParser();
   }

   public FTPFileEntryParser createVMSVersioningFTPEntryParser() {
      return new VMSVersioningFTPEntryParser();
   }

   public FTPFileEntryParser createNetwareFTPEntryParser() {
      return new NetwareFTPEntryParser();
   }

   public FTPFileEntryParser createNTFTPEntryParser() {
      return this.createNTFTPEntryParser((FTPClientConfig)null);
   }

   private FTPFileEntryParser createNTFTPEntryParser(FTPClientConfig config) {
      return (FTPFileEntryParser)(config != null && "WINDOWS".equals(config.getServerSystemKey())?new NTFTPEntryParser(config):new CompositeFileEntryParser(new FTPFileEntryParser[]{new NTFTPEntryParser(config), new UnixFTPEntryParser(config)}));
   }

   public FTPFileEntryParser createOS2FTPEntryParser() {
      return new OS2FTPEntryParser();
   }

   public FTPFileEntryParser createOS400FTPEntryParser() {
      return this.createOS400FTPEntryParser((FTPClientConfig)null);
   }

   private FTPFileEntryParser createOS400FTPEntryParser(FTPClientConfig config) {
      return (FTPFileEntryParser)(config != null && "OS/400".equals(config.getServerSystemKey())?new OS400FTPEntryParser(config):new CompositeFileEntryParser(new FTPFileEntryParser[]{new OS400FTPEntryParser(config), new UnixFTPEntryParser(config)}));
   }

   public FTPFileEntryParser createMVSEntryParser() {
      return new MVSFTPEntryParser();
   }
}
