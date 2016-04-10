package org.apache.commons.net.ftp.parser;

import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.parser.ParserInitializationException;

public interface FTPFileEntryParserFactory {
   FTPFileEntryParser createFileEntryParser(String var1) throws ParserInitializationException;

   FTPFileEntryParser createFileEntryParser(FTPClientConfig var1) throws ParserInitializationException;
}
