package org.apache.commons.net.ftp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.net.MalformedServerReplyException;
import org.apache.commons.net.ProtocolCommandSupport;
import org.apache.commons.net.SocketClient;
import org.apache.commons.net.ftp.FTPCommand;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CRLFLineReader;

public class FTP extends SocketClient {
   public static final int DEFAULT_DATA_PORT = 20;
   public static final int DEFAULT_PORT = 21;
   public static final int ASCII_FILE_TYPE = 0;
   public static final int EBCDIC_FILE_TYPE = 1;
   public static final int BINARY_FILE_TYPE = 2;
   public static final int LOCAL_FILE_TYPE = 3;
   public static final int NON_PRINT_TEXT_FORMAT = 4;
   public static final int TELNET_TEXT_FORMAT = 5;
   public static final int CARRIAGE_CONTROL_TEXT_FORMAT = 6;
   public static final int FILE_STRUCTURE = 7;
   public static final int RECORD_STRUCTURE = 8;
   public static final int PAGE_STRUCTURE = 9;
   public static final int STREAM_TRANSFER_MODE = 10;
   public static final int BLOCK_TRANSFER_MODE = 11;
   public static final int COMPRESSED_TRANSFER_MODE = 12;
   public static final String DEFAULT_CONTROL_ENCODING = "ISO-8859-1";
   public static final int REPLY_CODE_LEN = 3;
   private static final String __modes = "AEILNTCFRPSBC";
   protected int _replyCode;
   protected ArrayList _replyLines;
   protected boolean _newReplyString;
   protected String _replyString;
   protected String _controlEncoding;
   protected ProtocolCommandSupport _commandSupport_;
   protected boolean strictMultilineParsing = false;
   protected BufferedReader _controlInput_;
   protected BufferedWriter _controlOutput_;

   public FTP() {
      this.setDefaultPort(21);
      this._replyLines = new ArrayList();
      this._newReplyString = false;
      this._replyString = null;
      this._controlEncoding = "ISO-8859-1";
      this._commandSupport_ = new ProtocolCommandSupport(this);
   }

   private boolean __strictCheck(String line, String code) {
      return !line.startsWith(code) || line.charAt(3) != 32;
   }

   private boolean __lenientCheck(String line) {
      return line.length() <= 3 || line.charAt(3) == 45 || !Character.isDigit(line.charAt(0));
   }

   private void __getReply() throws IOException {
      this.__getReply(true);
   }

   protected void __getReplyNoReport() throws IOException {
      this.__getReply(false);
   }

   private void __getReply(boolean reportReply) throws IOException {
      this._newReplyString = true;
      this._replyLines.clear();
      String line = this._controlInput_.readLine();
      if(line == null) {
         throw new FTPConnectionClosedException("Connection closed without indication.");
      } else {
         int length = line.length();
         if(length < 3) {
            throw new MalformedServerReplyException("Truncated server reply: " + line);
         } else {
            String code = null;

            try {
               code = line.substring(0, 3);
               this._replyCode = Integer.parseInt(code);
            } catch (NumberFormatException var6) {
               throw new MalformedServerReplyException("Could not parse response code.\nServer Reply: " + line);
            }

            this._replyLines.add(line);
            if(length > 3 && line.charAt(3) == 45) {
               while(true) {
                  line = this._controlInput_.readLine();
                  if(line == null) {
                     throw new FTPConnectionClosedException("Connection closed without indication.");
                  }

                  this._replyLines.add(line);
                  if(this.isStrictMultilineParsing()) {
                     if(!this.__strictCheck(line, code)) {
                        break;
                     }
                  } else if(!this.__lenientCheck(line)) {
                     break;
                  }
               }
            }

            this.fireReplyReceived(this._replyCode, this.getReplyString());
            if(this._replyCode == 421) {
               throw new FTPConnectionClosedException("FTP response 421 received.  Server closed connection.");
            }
         }
      }
   }

   protected void _connectAction_() throws IOException {
      super._connectAction_();
      this._controlInput_ = new CRLFLineReader(new InputStreamReader(this._input_, this.getControlEncoding()));
      this._controlOutput_ = new BufferedWriter(new OutputStreamWriter(this._output_, this.getControlEncoding()));
      if(this.connectTimeout > 0) {
         int original = this._socket_.getSoTimeout();
         this._socket_.setSoTimeout(this.connectTimeout);

         try {
            this.__getReply();
            if(FTPReply.isPositivePreliminary(this._replyCode)) {
               this.__getReply();
            }
         } catch (SocketTimeoutException var7) {
            IOException ioe = new IOException("Timed out waiting for initial connect reply");
            ioe.initCause(var7);
            throw ioe;
         } finally {
            this._socket_.setSoTimeout(original);
         }
      } else {
         this.__getReply();
         if(FTPReply.isPositivePreliminary(this._replyCode)) {
            this.__getReply();
         }
      }

   }

   public void setControlEncoding(String encoding) {
      this._controlEncoding = encoding;
   }

   public String getControlEncoding() {
      return this._controlEncoding;
   }

   public void disconnect() throws IOException {
      super.disconnect();
      this._controlInput_ = null;
      this._controlOutput_ = null;
      this._newReplyString = false;
      this._replyString = null;
   }

   public int sendCommand(String command, String args) throws IOException {
      if(this._controlOutput_ == null) {
         throw new IOException("Connection is not open");
      } else {
         String message = this.__buildMessage(command, args);
         this.__send(message);
         this.fireCommandSent(command, message);
         this.__getReply();
         return this._replyCode;
      }
   }

   private String __buildMessage(String command, String args) {
      StringBuilder __commandBuffer = new StringBuilder();
      __commandBuffer.append(command);
      if(args != null) {
         __commandBuffer.append(' ');
         __commandBuffer.append(args);
      }

      __commandBuffer.append("\r\n");
      return __commandBuffer.toString();
   }

   private void __send(String message) throws IOException, FTPConnectionClosedException, SocketException {
      try {
         this._controlOutput_.write(message);
         this._controlOutput_.flush();
      } catch (SocketException var3) {
         if(!this.isConnected()) {
            throw new FTPConnectionClosedException("Connection unexpectedly closed.");
         } else {
            throw var3;
         }
      }
   }

   protected void __noop() throws IOException {
      String msg = this.__buildMessage(FTPCommand.getCommand(32), (String)null);
      this.__send(msg);
      this.__getReplyNoReport();
   }

   public int sendCommand(int command, String args) throws IOException {
      return this.sendCommand(FTPCommand.getCommand(command), args);
   }

   public int sendCommand(String command) throws IOException {
      return this.sendCommand(command, (String)null);
   }

   public int sendCommand(int command) throws IOException {
      return this.sendCommand(command, (String)null);
   }

   public int getReplyCode() {
      return this._replyCode;
   }

   public int getReply() throws IOException {
      this.__getReply();
      return this._replyCode;
   }

   public String[] getReplyStrings() {
      return (String[])this._replyLines.toArray(new String[this._replyLines.size()]);
   }

   public String getReplyString() {
      if(!this._newReplyString) {
         return this._replyString;
      } else {
         StringBuilder buffer = new StringBuilder(256);
         Iterator var3 = this._replyLines.iterator();

         while(var3.hasNext()) {
            String line = (String)var3.next();
            buffer.append(line);
            buffer.append("\r\n");
         }

         this._newReplyString = false;
         return this._replyString = buffer.toString();
      }
   }

   public int user(String username) throws IOException {
      return this.sendCommand(0, username);
   }

   public int pass(String password) throws IOException {
      return this.sendCommand(1, password);
   }

   public int acct(String account) throws IOException {
      return this.sendCommand(2, account);
   }

   public int abor() throws IOException {
      return this.sendCommand(21);
   }

   public int cwd(String directory) throws IOException {
      return this.sendCommand(3, directory);
   }

   public int cdup() throws IOException {
      return this.sendCommand(4);
   }

   public int quit() throws IOException {
      return this.sendCommand(7);
   }

   public int rein() throws IOException {
      return this.sendCommand(6);
   }

   public int smnt(String dir) throws IOException {
      return this.sendCommand(5, dir);
   }

   public int port(InetAddress host, int port) throws IOException {
      StringBuilder info = new StringBuilder(24);
      info.append(host.getHostAddress().replace('.', ','));
      int num = port >>> 8;
      info.append(',');
      info.append(num);
      info.append(',');
      num = port & 255;
      info.append(num);
      return this.sendCommand(8, info.toString());
   }

   public int eprt(InetAddress host, int port) throws IOException {
      StringBuilder info = new StringBuilder();
      String h = host.getHostAddress();
      int num = h.indexOf("%");
      if(num > 0) {
         h = h.substring(0, num);
      }

      info.append("|");
      if(host instanceof Inet4Address) {
         info.append("1");
      } else if(host instanceof Inet6Address) {
         info.append("2");
      }

      info.append("|");
      info.append(h);
      info.append("|");
      info.append(port);
      info.append("|");
      return this.sendCommand(37, info.toString());
   }

   public int pasv() throws IOException {
      return this.sendCommand(9);
   }

   public int epsv() throws IOException {
      return this.sendCommand(36);
   }

   public int type(int fileType, int formatOrByteSize) throws IOException {
      StringBuilder arg = new StringBuilder();
      arg.append("AEILNTCFRPSBC".charAt(fileType));
      arg.append(' ');
      if(fileType == 3) {
         arg.append(formatOrByteSize);
      } else {
         arg.append("AEILNTCFRPSBC".charAt(formatOrByteSize));
      }

      return this.sendCommand(10, arg.toString());
   }

   public int type(int fileType) throws IOException {
      return this.sendCommand(10, "AEILNTCFRPSBC".substring(fileType, fileType + 1));
   }

   public int stru(int structure) throws IOException {
      return this.sendCommand(11, "AEILNTCFRPSBC".substring(structure, structure + 1));
   }

   public int mode(int mode) throws IOException {
      return this.sendCommand(12, "AEILNTCFRPSBC".substring(mode, mode + 1));
   }

   public int retr(String pathname) throws IOException {
      return this.sendCommand(13, pathname);
   }

   public int stor(String pathname) throws IOException {
      return this.sendCommand(14, pathname);
   }

   public int stou() throws IOException {
      return this.sendCommand(15);
   }

   public int stou(String pathname) throws IOException {
      return this.sendCommand(15, pathname);
   }

   public int appe(String pathname) throws IOException {
      return this.sendCommand(16, pathname);
   }

   public int allo(int bytes) throws IOException {
      return this.sendCommand(17, Integer.toString(bytes));
   }

   public int feat() throws IOException {
      return this.sendCommand(34);
   }

   public int allo(int bytes, int recordSize) throws IOException {
      return this.sendCommand(17, Integer.toString(bytes) + " R " + Integer.toString(recordSize));
   }

   public int rest(String marker) throws IOException {
      return this.sendCommand(18, marker);
   }

   public int mdtm(String file) throws IOException {
      return this.sendCommand(33, file);
   }

   public int mfmt(String pathname, String timeval) throws IOException {
      return this.sendCommand(35, timeval + " " + pathname);
   }

   public int rnfr(String pathname) throws IOException {
      return this.sendCommand(19, pathname);
   }

   public int rnto(String pathname) throws IOException {
      return this.sendCommand(20, pathname);
   }

   public int dele(String pathname) throws IOException {
      return this.sendCommand(22, pathname);
   }

   public int rmd(String pathname) throws IOException {
      return this.sendCommand(23, pathname);
   }

   public int mkd(String pathname) throws IOException {
      return this.sendCommand(24, pathname);
   }

   public int pwd() throws IOException {
      return this.sendCommand(25);
   }

   public int list() throws IOException {
      return this.sendCommand(26);
   }

   public int list(String pathname) throws IOException {
      return this.sendCommand(26, pathname);
   }

   public int mlsd() throws IOException {
      return this.sendCommand(38);
   }

   public int mlsd(String path) throws IOException {
      return this.sendCommand(38, path);
   }

   public int mlst() throws IOException {
      return this.sendCommand(39);
   }

   public int mlst(String path) throws IOException {
      return this.sendCommand(39, path);
   }

   public int nlst() throws IOException {
      return this.sendCommand(27);
   }

   public int nlst(String pathname) throws IOException {
      return this.sendCommand(27, pathname);
   }

   public int site(String parameters) throws IOException {
      return this.sendCommand(28, parameters);
   }

   public int syst() throws IOException {
      return this.sendCommand(29);
   }

   public int stat() throws IOException {
      return this.sendCommand(30);
   }

   public int stat(String pathname) throws IOException {
      return this.sendCommand(30, pathname);
   }

   public int help() throws IOException {
      return this.sendCommand(31);
   }

   public int help(String command) throws IOException {
      return this.sendCommand(31, command);
   }

   public int noop() throws IOException {
      return this.sendCommand(32);
   }

   public boolean isStrictMultilineParsing() {
      return this.strictMultilineParsing;
   }

   public void setStrictMultilineParsing(boolean strictMultilineParsing) {
      this.strictMultilineParsing = strictMultilineParsing;
   }

   protected ProtocolCommandSupport getCommandSupport() {
      return this._commandSupport_;
   }
}
