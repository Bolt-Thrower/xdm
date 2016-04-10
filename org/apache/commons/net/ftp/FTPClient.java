package org.apache.commons.net.ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.net.MalformedServerReplyException;
import org.apache.commons.net.ftp.Configurable;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPCommand;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPFileFilters;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.parser.DefaultFTPFileEntryParserFactory;
import org.apache.commons.net.ftp.parser.FTPFileEntryParserFactory;
import org.apache.commons.net.ftp.parser.MLSxEntryParser;
import org.apache.commons.net.io.CRLFLineReader;
import org.apache.commons.net.io.CopyStreamAdapter;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.FromNetASCIIInputStream;
import org.apache.commons.net.io.SocketInputStream;
import org.apache.commons.net.io.SocketOutputStream;
import org.apache.commons.net.io.ToNetASCIIOutputStream;
import org.apache.commons.net.io.Util;

public class FTPClient extends FTP implements Configurable {
   public static final String FTP_SYSTEM_TYPE = "org.apache.commons.net.ftp.systemType";
   public static final String FTP_SYSTEM_TYPE_DEFAULT = "org.apache.commons.net.ftp.systemType.default";
   public static final String SYSTEM_TYPE_PROPERTIES = "/systemType.properties";
   public static final int ACTIVE_LOCAL_DATA_CONNECTION_MODE = 0;
   public static final int ACTIVE_REMOTE_DATA_CONNECTION_MODE = 1;
   public static final int PASSIVE_LOCAL_DATA_CONNECTION_MODE = 2;
   public static final int PASSIVE_REMOTE_DATA_CONNECTION_MODE = 3;
   private int __dataConnectionMode;
   private int __dataTimeout;
   private int __passivePort;
   private String __passiveHost;
   private final Random __random;
   private int __activeMinPort;
   private int __activeMaxPort;
   private InetAddress __activeExternalHost;
   private InetAddress __reportActiveExternalHost;
   private InetAddress __passiveLocalHost;
   private int __fileType;
   private int __fileFormat;
   private int __fileStructure;
   private int __fileTransferMode;
   private boolean __remoteVerificationEnabled;
   private long __restartOffset;
   private FTPFileEntryParserFactory __parserFactory;
   private int __bufferSize;
   private boolean __listHiddenFiles;
   private boolean __useEPSVwithIPv4;
   private String __systemName;
   private FTPFileEntryParser __entryParser;
   private String __entryParserKey;
   private FTPClientConfig __configuration;
   private CopyStreamListener __copyStreamListener;
   private long __controlKeepAliveTimeout;
   private int __controlKeepAliveReplyTimeout = 1000;
   private static final Pattern __PARMS_PAT = Pattern.compile("(\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,3}),(\\d{1,3}),(\\d{1,3})");
   private boolean __autodetectEncoding = false;
   private HashMap __featuresMap;

   private static Properties getOverrideProperties() {
      return FTPClient.PropertiesSingleton.PROPERTIES;
   }

   public FTPClient() {
      this.__initDefaults();
      this.__dataTimeout = -1;
      this.__remoteVerificationEnabled = true;
      this.__parserFactory = new DefaultFTPFileEntryParserFactory();
      this.__configuration = null;
      this.__listHiddenFiles = false;
      this.__useEPSVwithIPv4 = false;
      this.__random = new Random();
      this.__passiveLocalHost = null;
   }

   private void __initDefaults() {
      this.__dataConnectionMode = 0;
      this.__passiveHost = null;
      this.__passivePort = -1;
      this.__activeExternalHost = null;
      this.__reportActiveExternalHost = null;
      this.__activeMinPort = 0;
      this.__activeMaxPort = 0;
      this.__fileType = 0;
      this.__fileStructure = 7;
      this.__fileFormat = 4;
      this.__fileTransferMode = 10;
      this.__restartOffset = 0L;
      this.__systemName = null;
      this.__entryParser = null;
      this.__entryParserKey = "";
      this.__bufferSize = 1024;
      this.__featuresMap = null;
   }

   private static String __parsePathname(String reply) {
      int begin = reply.indexOf(34);
      if(begin == -1) {
         return reply.substring(4);
      } else {
         int end = reply.lastIndexOf("\" ");
         return end != -1?reply.substring(begin + 1, end).replace("\"\"", "\""):reply.substring(4);
      }
   }

   protected void _parsePassiveModeReply(String reply) throws MalformedServerReplyException {
      Matcher m = __PARMS_PAT.matcher(reply);
      if(!m.find()) {
         throw new MalformedServerReplyException("Could not parse passive host information.\nServer Reply: " + reply);
      } else {
         this.__passiveHost = m.group(1).replace(',', '.');

         try {
            int e = Integer.parseInt(m.group(2));
            int remote = Integer.parseInt(m.group(3));
            this.__passivePort = e << 8 | remote;
         } catch (NumberFormatException var7) {
            throw new MalformedServerReplyException("Could not parse passive port information.\nServer Reply: " + reply);
         }

         try {
            InetAddress e1 = InetAddress.getByName(this.__passiveHost);
            if(e1.isSiteLocalAddress()) {
               InetAddress remote1 = this.getRemoteAddress();
               if(!remote1.isSiteLocalAddress()) {
                  String hostAddress = remote1.getHostAddress();
                  this.fireReplyReceived(0, "[Replacing site local address " + this.__passiveHost + " with " + hostAddress + "]\n");
                  this.__passiveHost = hostAddress;
               }
            }

         } catch (UnknownHostException var6) {
            throw new MalformedServerReplyException("Could not parse passive host information.\nServer Reply: " + reply);
         }
      }
   }

   protected void _parseExtendedPassiveModeReply(String reply) throws MalformedServerReplyException {
      reply = reply.substring(reply.indexOf(40) + 1, reply.indexOf(41)).trim();
      char delim1 = reply.charAt(0);
      char delim2 = reply.charAt(1);
      char delim3 = reply.charAt(2);
      char delim4 = reply.charAt(reply.length() - 1);
      if(delim1 == delim2 && delim2 == delim3 && delim3 == delim4) {
         int port;
         try {
            port = Integer.parseInt(reply.substring(3, reply.length() - 1));
         } catch (NumberFormatException var8) {
            throw new MalformedServerReplyException("Could not parse extended passive host information.\nServer Reply: " + reply);
         }

         this.__passiveHost = this.getRemoteAddress().getHostAddress();
         this.__passivePort = port;
      } else {
         throw new MalformedServerReplyException("Could not parse extended passive host information.\nServer Reply: " + reply);
      }
   }

   private boolean __storeFile(int command, String remote, InputStream local) throws IOException {
      return this._storeFile(FTPCommand.getCommand(command), remote, local);
   }

   protected boolean _storeFile(String command, String remote, InputStream local) throws IOException {
      Socket socket = this._openDataConnection_(command, remote);
      if(socket == null) {
         return false;
      } else {
         Object output;
         if(this.__fileType == 0) {
            output = new ToNetASCIIOutputStream(new BufferedOutputStream(socket.getOutputStream(), this.getDefaultedBufferSize()));
         } else {
            output = new BufferedOutputStream(socket.getOutputStream(), this.getDefaultedBufferSize());
         }

         FTPClient.CSL csl = null;
         if(this.__controlKeepAliveTimeout > 0L) {
            csl = new FTPClient.CSL(this, this.__controlKeepAliveTimeout, this.__controlKeepAliveReplyTimeout);
         }

         try {
            Util.copyStream(local, (OutputStream)output, this.getDefaultedBufferSize(), -1L, this.__mergeListeners(csl), false);
         } catch (IOException var8) {
            Util.closeQuietly(socket);
            throw var8;
         }

         ((OutputStream)output).close();
         socket.close();
         if(csl != null) {
            csl.cleanUp();
         }

         boolean ok = this.completePendingCommand();
         return ok;
      }
   }

   private OutputStream __storeFileStream(int command, String remote) throws IOException {
      return this._storeFileStream(FTPCommand.getCommand(command), remote);
   }

   protected OutputStream _storeFileStream(String command, String remote) throws IOException {
      Socket socket = this._openDataConnection_(command, remote);
      if(socket == null) {
         return null;
      } else {
         Object output = socket.getOutputStream();
         if(this.__fileType == 0) {
            BufferedOutputStream output1 = new BufferedOutputStream((OutputStream)output, this.getDefaultedBufferSize());
            output = new ToNetASCIIOutputStream(output1);
         }

         return new SocketOutputStream(socket, (OutputStream)output);
      }
   }

   protected Socket _openDataConnection_(int command, String arg) throws IOException {
      return this._openDataConnection_(FTPCommand.getCommand(command), arg);
   }

   protected Socket _openDataConnection_(String command, String arg) throws IOException {
      if(this.__dataConnectionMode != 0 && this.__dataConnectionMode != 2) {
         return null;
      } else {
         boolean isInet6Address = this.getRemoteAddress() instanceof Inet6Address;
         Socket socket;
         if(this.__dataConnectionMode == 0) {
            ServerSocket attemptEPSV = this._serverSocketFactory_.createServerSocket(this.getActivePort(), 1, this.getHostAddress());

            try {
               if(isInet6Address) {
                  if(!FTPReply.isPositiveCompletion(this.eprt(this.getReportHostAddress(), attemptEPSV.getLocalPort()))) {
                     return null;
                  }
               } else if(!FTPReply.isPositiveCompletion(this.port(this.getReportHostAddress(), attemptEPSV.getLocalPort()))) {
                  return null;
               }

               if(this.__restartOffset > 0L && !this.restart(this.__restartOffset) || !FTPReply.isPositivePreliminary(this.sendCommand(command, arg))) {
                  return null;
               }

               if(this.__dataTimeout >= 0) {
                  attemptEPSV.setSoTimeout(this.__dataTimeout);
               }

               socket = attemptEPSV.accept();
               if(this.__dataTimeout >= 0) {
                  socket.setSoTimeout(this.__dataTimeout);
               }
            } finally {
               attemptEPSV.close();
            }
         } else {
            boolean attemptEPSV1 = this.isUseEPSVwithIPv4() || isInet6Address;
            if(attemptEPSV1 && this.epsv() == 229) {
               this._parseExtendedPassiveModeReply((String)this._replyLines.get(0));
            } else {
               if(isInet6Address) {
                  return null;
               }

               if(this.pasv() != 227) {
                  return null;
               }

               this._parsePassiveModeReply((String)this._replyLines.get(0));
            }

            socket = this._socketFactory_.createSocket();
            if(this.__passiveLocalHost != null) {
               socket.bind(new InetSocketAddress(this.__passiveLocalHost, 0));
            }

            if(this.__dataTimeout >= 0) {
               socket.setSoTimeout(this.__dataTimeout);
            }

            socket.connect(new InetSocketAddress(this.__passiveHost, this.__passivePort), this.connectTimeout);
            if(this.__restartOffset > 0L && !this.restart(this.__restartOffset)) {
               socket.close();
               return null;
            }

            if(!FTPReply.isPositivePreliminary(this.sendCommand(command, arg))) {
               socket.close();
               return null;
            }
         }

         if(this.__remoteVerificationEnabled && !this.verifyRemote(socket)) {
            socket.close();
            throw new IOException("Host attempting data connection " + socket.getInetAddress().getHostAddress() + " is not same as server " + this.getRemoteAddress().getHostAddress());
         } else {
            if(this.__bufferSize > 0) {
               socket.setReceiveBufferSize(this.__bufferSize);
               socket.setSendBufferSize(this.__bufferSize);
            }

            return socket;
         }
      }
   }

   protected void _connectAction_() throws IOException {
      super._connectAction_();
      this.__initDefaults();
      if(this.__autodetectEncoding) {
         ArrayList oldReplyLines = new ArrayList(this._replyLines);
         int oldReplyCode = this._replyCode;
         if(this.hasFeature("UTF8") || this.hasFeature("UTF-8")) {
            this.setControlEncoding("UTF-8");
            this._controlInput_ = new CRLFLineReader(new InputStreamReader(this._input_, this.getControlEncoding()));
            this._controlOutput_ = new BufferedWriter(new OutputStreamWriter(this._output_, this.getControlEncoding()));
         }

         this._replyLines.clear();
         this._replyLines.addAll(oldReplyLines);
         this._replyCode = oldReplyCode;
      }

   }

   public void setDataTimeout(int timeout) {
      this.__dataTimeout = timeout;
   }

   public void setParserFactory(FTPFileEntryParserFactory parserFactory) {
      this.__parserFactory = parserFactory;
   }

   public void disconnect() throws IOException {
      super.disconnect();
      this.__initDefaults();
   }

   public void setRemoteVerificationEnabled(boolean enable) {
      this.__remoteVerificationEnabled = enable;
   }

   public boolean isRemoteVerificationEnabled() {
      return this.__remoteVerificationEnabled;
   }

   public boolean login(String username, String password) throws IOException {
      this.user(username);
      return FTPReply.isPositiveCompletion(this._replyCode)?true:(!FTPReply.isPositiveIntermediate(this._replyCode)?false:FTPReply.isPositiveCompletion(this.pass(password)));
   }

   public boolean login(String username, String password, String account) throws IOException {
      this.user(username);
      if(FTPReply.isPositiveCompletion(this._replyCode)) {
         return true;
      } else if(!FTPReply.isPositiveIntermediate(this._replyCode)) {
         return false;
      } else {
         this.pass(password);
         return FTPReply.isPositiveCompletion(this._replyCode)?true:(!FTPReply.isPositiveIntermediate(this._replyCode)?false:FTPReply.isPositiveCompletion(this.acct(account)));
      }
   }

   public boolean logout() throws IOException {
      return FTPReply.isPositiveCompletion(this.quit());
   }

   public boolean changeWorkingDirectory(String pathname) throws IOException {
      return FTPReply.isPositiveCompletion(this.cwd(pathname));
   }

   public boolean changeToParentDirectory() throws IOException {
      return FTPReply.isPositiveCompletion(this.cdup());
   }

   public boolean structureMount(String pathname) throws IOException {
      return FTPReply.isPositiveCompletion(this.smnt(pathname));
   }

   boolean reinitialize() throws IOException {
      this.rein();
      if(!FTPReply.isPositiveCompletion(this._replyCode) && (!FTPReply.isPositivePreliminary(this._replyCode) || !FTPReply.isPositiveCompletion(this.getReply()))) {
         return false;
      } else {
         this.__initDefaults();
         return true;
      }
   }

   public void enterLocalActiveMode() {
      this.__dataConnectionMode = 0;
      this.__passiveHost = null;
      this.__passivePort = -1;
   }

   public void enterLocalPassiveMode() {
      this.__dataConnectionMode = 2;
      this.__passiveHost = null;
      this.__passivePort = -1;
   }

   public boolean enterRemoteActiveMode(InetAddress host, int port) throws IOException {
      if(FTPReply.isPositiveCompletion(this.port(host, port))) {
         this.__dataConnectionMode = 1;
         this.__passiveHost = null;
         this.__passivePort = -1;
         return true;
      } else {
         return false;
      }
   }

   public boolean enterRemotePassiveMode() throws IOException {
      if(this.pasv() != 227) {
         return false;
      } else {
         this.__dataConnectionMode = 3;
         this._parsePassiveModeReply((String)this._replyLines.get(0));
         return true;
      }
   }

   public String getPassiveHost() {
      return this.__passiveHost;
   }

   public int getPassivePort() {
      return this.__passivePort;
   }

   public int getDataConnectionMode() {
      return this.__dataConnectionMode;
   }

   private int getActivePort() {
      return this.__activeMinPort > 0 && this.__activeMaxPort >= this.__activeMinPort?(this.__activeMaxPort == this.__activeMinPort?this.__activeMaxPort:this.__random.nextInt(this.__activeMaxPort - this.__activeMinPort + 1) + this.__activeMinPort):0;
   }

   private InetAddress getHostAddress() {
      return this.__activeExternalHost != null?this.__activeExternalHost:this.getLocalAddress();
   }

   private InetAddress getReportHostAddress() {
      return this.__reportActiveExternalHost != null?this.__reportActiveExternalHost:this.getHostAddress();
   }

   public void setActivePortRange(int minPort, int maxPort) {
      this.__activeMinPort = minPort;
      this.__activeMaxPort = maxPort;
   }

   public void setActiveExternalIPAddress(String ipAddress) throws UnknownHostException {
      this.__activeExternalHost = InetAddress.getByName(ipAddress);
   }

   public void setPassiveLocalIPAddress(String ipAddress) throws UnknownHostException {
      this.__passiveLocalHost = InetAddress.getByName(ipAddress);
   }

   public void setPassiveLocalIPAddress(InetAddress inetAddress) {
      this.__passiveLocalHost = inetAddress;
   }

   public InetAddress getPassiveLocalIPAddress() {
      return this.__passiveLocalHost;
   }

   public void setReportActiveExternalIPAddress(String ipAddress) throws UnknownHostException {
      this.__reportActiveExternalHost = InetAddress.getByName(ipAddress);
   }

   public boolean setFileType(int fileType) throws IOException {
      if(FTPReply.isPositiveCompletion(this.type(fileType))) {
         this.__fileType = fileType;
         this.__fileFormat = 4;
         return true;
      } else {
         return false;
      }
   }

   public boolean setFileType(int fileType, int formatOrByteSize) throws IOException {
      if(FTPReply.isPositiveCompletion(this.type(fileType, formatOrByteSize))) {
         this.__fileType = fileType;
         this.__fileFormat = formatOrByteSize;
         return true;
      } else {
         return false;
      }
   }

   public boolean setFileStructure(int structure) throws IOException {
      if(FTPReply.isPositiveCompletion(this.stru(structure))) {
         this.__fileStructure = structure;
         return true;
      } else {
         return false;
      }
   }

   public boolean setFileTransferMode(int mode) throws IOException {
      if(FTPReply.isPositiveCompletion(this.mode(mode))) {
         this.__fileTransferMode = mode;
         return true;
      } else {
         return false;
      }
   }

   public boolean remoteRetrieve(String filename) throws IOException {
      return this.__dataConnectionMode != 1 && this.__dataConnectionMode != 3?false:FTPReply.isPositivePreliminary(this.retr(filename));
   }

   public boolean remoteStore(String filename) throws IOException {
      return this.__dataConnectionMode != 1 && this.__dataConnectionMode != 3?false:FTPReply.isPositivePreliminary(this.stor(filename));
   }

   public boolean remoteStoreUnique(String filename) throws IOException {
      return this.__dataConnectionMode != 1 && this.__dataConnectionMode != 3?false:FTPReply.isPositivePreliminary(this.stou(filename));
   }

   public boolean remoteStoreUnique() throws IOException {
      return this.__dataConnectionMode != 1 && this.__dataConnectionMode != 3?false:FTPReply.isPositivePreliminary(this.stou());
   }

   public boolean remoteAppend(String filename) throws IOException {
      return this.__dataConnectionMode != 1 && this.__dataConnectionMode != 3?false:FTPReply.isPositivePreliminary(this.appe(filename));
   }

   public boolean completePendingCommand() throws IOException {
      return FTPReply.isPositiveCompletion(this.getReply());
   }

   public boolean retrieveFile(String remote, OutputStream local) throws IOException {
      return this._retrieveFile(FTPCommand.getCommand(13), remote, local);
   }

   protected boolean _retrieveFile(String command, String remote, OutputStream local) throws IOException {
      Socket socket = this._openDataConnection_(command, remote);
      if(socket == null) {
         return false;
      } else {
         Object input;
         if(this.__fileType == 0) {
            input = new FromNetASCIIInputStream(new BufferedInputStream(socket.getInputStream(), this.getDefaultedBufferSize()));
         } else {
            input = new BufferedInputStream(socket.getInputStream(), this.getDefaultedBufferSize());
         }

         FTPClient.CSL csl = null;
         if(this.__controlKeepAliveTimeout > 0L) {
            csl = new FTPClient.CSL(this, this.__controlKeepAliveTimeout, this.__controlKeepAliveReplyTimeout);
         }

         try {
            Util.copyStream((InputStream)input, local, this.getDefaultedBufferSize(), -1L, this.__mergeListeners(csl), false);
         } finally {
            Util.closeQuietly(socket);
         }

         if(csl != null) {
            csl.cleanUp();
         }

         boolean ok = this.completePendingCommand();
         return ok;
      }
   }

   public InputStream retrieveFileStream(String remote) throws IOException {
      return this._retrieveFileStream(FTPCommand.getCommand(13), remote);
   }

   protected InputStream _retrieveFileStream(String command, String remote) throws IOException {
      Socket socket = this._openDataConnection_(command, remote);
      if(socket == null) {
         return null;
      } else {
         Object input = socket.getInputStream();
         if(this.__fileType == 0) {
            BufferedInputStream input1 = new BufferedInputStream((InputStream)input, this.getDefaultedBufferSize());
            input = new FromNetASCIIInputStream(input1);
         }

         return new SocketInputStream(socket, (InputStream)input);
      }
   }

   public boolean storeFile(String remote, InputStream local) throws IOException {
      return this.__storeFile(14, remote, local);
   }

   public OutputStream storeFileStream(String remote) throws IOException {
      return this.__storeFileStream(14, remote);
   }

   public boolean appendFile(String remote, InputStream local) throws IOException {
      return this.__storeFile(16, remote, local);
   }

   public OutputStream appendFileStream(String remote) throws IOException {
      return this.__storeFileStream(16, remote);
   }

   public boolean storeUniqueFile(String remote, InputStream local) throws IOException {
      return this.__storeFile(15, remote, local);
   }

   public OutputStream storeUniqueFileStream(String remote) throws IOException {
      return this.__storeFileStream(15, remote);
   }

   public boolean storeUniqueFile(InputStream local) throws IOException {
      return this.__storeFile(15, (String)null, local);
   }

   public OutputStream storeUniqueFileStream() throws IOException {
      return this.__storeFileStream(15, (String)null);
   }

   public boolean allocate(int bytes) throws IOException {
      return FTPReply.isPositiveCompletion(this.allo(bytes));
   }

   public boolean features() throws IOException {
      return FTPReply.isPositiveCompletion(this.feat());
   }

   public String[] featureValues(String feature) throws IOException {
      if(!this.initFeatureMap()) {
         return null;
      } else {
         Set entries = (Set)this.__featuresMap.get(feature.toUpperCase(Locale.ENGLISH));
         return entries != null?(String[])entries.toArray(new String[entries.size()]):null;
      }
   }

   public String featureValue(String feature) throws IOException {
      String[] values = this.featureValues(feature);
      return values != null?values[0]:null;
   }

   public boolean hasFeature(String feature) throws IOException {
      return !this.initFeatureMap()?false:this.__featuresMap.containsKey(feature.toUpperCase(Locale.ENGLISH));
   }

   public boolean hasFeature(String feature, String value) throws IOException {
      if(!this.initFeatureMap()) {
         return false;
      } else {
         Set entries = (Set)this.__featuresMap.get(feature.toUpperCase(Locale.ENGLISH));
         return entries != null?entries.contains(value):false;
      }
   }

   private boolean initFeatureMap() throws IOException {
      if(this.__featuresMap == null) {
         boolean success = FTPReply.isPositiveCompletion(this.feat());
         this.__featuresMap = new HashMap();
         if(!success) {
            return false;
         }

         String[] var5;
         int var4 = (var5 = this.getReplyStrings()).length;

         for(int var3 = 0; var3 < var4; ++var3) {
            String l = var5[var3];
            if(l.startsWith(" ")) {
               String value = "";
               int varsep = l.indexOf(32, 1);
               String key;
               if(varsep > 0) {
                  key = l.substring(1, varsep);
                  value = l.substring(varsep + 1);
               } else {
                  key = l.substring(1);
               }

               key = key.toUpperCase(Locale.ENGLISH);
               Object entries = (Set)this.__featuresMap.get(key);
               if(entries == null) {
                  entries = new HashSet();
                  this.__featuresMap.put(key, entries);
               }

               ((Set)entries).add(value);
            }
         }
      }

      return true;
   }

   public boolean allocate(int bytes, int recordSize) throws IOException {
      return FTPReply.isPositiveCompletion(this.allo(bytes, recordSize));
   }

   public boolean doCommand(String command, String params) throws IOException {
      return FTPReply.isPositiveCompletion(this.sendCommand(command, params));
   }

   public String[] doCommandAsStrings(String command, String params) throws IOException {
      boolean success = FTPReply.isPositiveCompletion(this.sendCommand(command, params));
      return success?this.getReplyStrings():null;
   }

   public FTPFile mlistFile(String pathname) throws IOException {
      boolean success = FTPReply.isPositiveCompletion(this.sendCommand(39, pathname));
      if(success) {
         String entry = this.getReplyStrings()[1].substring(1);
         return MLSxEntryParser.parseEntry(entry);
      } else {
         return null;
      }
   }

   public FTPFile[] mlistDir() throws IOException {
      return this.mlistDir((String)null);
   }

   public FTPFile[] mlistDir(String pathname) throws IOException {
      FTPListParseEngine engine = this.initiateMListParsing(pathname);
      return engine.getFiles();
   }

   public FTPFile[] mlistDir(String pathname, FTPFileFilter filter) throws IOException {
      FTPListParseEngine engine = this.initiateMListParsing(pathname);
      return engine.getFiles(filter);
   }

   protected boolean restart(long offset) throws IOException {
      this.__restartOffset = 0L;
      return FTPReply.isPositiveIntermediate(this.rest(Long.toString(offset)));
   }

   public void setRestartOffset(long offset) {
      if(offset >= 0L) {
         this.__restartOffset = offset;
      }

   }

   public long getRestartOffset() {
      return this.__restartOffset;
   }

   public boolean rename(String from, String to) throws IOException {
      return !FTPReply.isPositiveIntermediate(this.rnfr(from))?false:FTPReply.isPositiveCompletion(this.rnto(to));
   }

   public boolean abort() throws IOException {
      return FTPReply.isPositiveCompletion(this.abor());
   }

   public boolean deleteFile(String pathname) throws IOException {
      return FTPReply.isPositiveCompletion(this.dele(pathname));
   }

   public boolean removeDirectory(String pathname) throws IOException {
      return FTPReply.isPositiveCompletion(this.rmd(pathname));
   }

   public boolean makeDirectory(String pathname) throws IOException {
      return FTPReply.isPositiveCompletion(this.mkd(pathname));
   }

   public String printWorkingDirectory() throws IOException {
      return this.pwd() != 257?null:__parsePathname((String)this._replyLines.get(this._replyLines.size() - 1));
   }

   public boolean sendSiteCommand(String arguments) throws IOException {
      return FTPReply.isPositiveCompletion(this.site(arguments));
   }

   public String getSystemType() throws IOException {
      if(this.__systemName == null) {
         if(FTPReply.isPositiveCompletion(this.syst())) {
            this.__systemName = ((String)this._replyLines.get(this._replyLines.size() - 1)).substring(4);
         } else {
            String systDefault = System.getProperty("org.apache.commons.net.ftp.systemType.default");
            if(systDefault == null) {
               throw new IOException("Unable to determine system type - response: " + this.getReplyString());
            }

            this.__systemName = systDefault;
         }
      }

      return this.__systemName;
   }

   public String listHelp() throws IOException {
      return FTPReply.isPositiveCompletion(this.help())?this.getReplyString():null;
   }

   public String listHelp(String command) throws IOException {
      return FTPReply.isPositiveCompletion(this.help(command))?this.getReplyString():null;
   }

   public boolean sendNoOp() throws IOException {
      return FTPReply.isPositiveCompletion(this.noop());
   }

   public String[] listNames(String pathname) throws IOException {
      Socket socket = this._openDataConnection_(27, this.getListArguments(pathname));
      if(socket == null) {
         return null;
      } else {
         BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), this.getControlEncoding()));
         ArrayList results = new ArrayList();

         String line;
         while((line = reader.readLine()) != null) {
            results.add(line);
         }

         reader.close();
         socket.close();
         if(this.completePendingCommand()) {
            String[] names = new String[results.size()];
            return (String[])results.toArray(names);
         } else {
            return null;
         }
      }
   }

   public String[] listNames() throws IOException {
      return this.listNames((String)null);
   }

   public FTPFile[] listFiles(String pathname) throws IOException {
      FTPListParseEngine engine = this.initiateListParsing((String)null, pathname);
      return engine.getFiles();
   }

   public FTPFile[] listFiles() throws IOException {
      return this.listFiles((String)null);
   }

   public FTPFile[] listFiles(String pathname, FTPFileFilter filter) throws IOException {
      FTPListParseEngine engine = this.initiateListParsing((String)null, pathname);
      return engine.getFiles(filter);
   }

   public FTPFile[] listDirectories() throws IOException {
      return this.listDirectories((String)null);
   }

   public FTPFile[] listDirectories(String parent) throws IOException {
      return this.listFiles(parent, FTPFileFilters.DIRECTORIES);
   }

   public FTPListParseEngine initiateListParsing() throws IOException {
      return this.initiateListParsing((String)null);
   }

   public FTPListParseEngine initiateListParsing(String pathname) throws IOException {
      return this.initiateListParsing((String)null, pathname);
   }

   public FTPListParseEngine initiateListParsing(String parserKey, String pathname) throws IOException {
      if(this.__entryParser == null || !this.__entryParserKey.equals(parserKey)) {
         if(parserKey != null) {
            this.__entryParser = this.__parserFactory.createFileEntryParser(parserKey);
            this.__entryParserKey = parserKey;
         } else if(this.__configuration != null) {
            this.__entryParser = this.__parserFactory.createFileEntryParser(this.__configuration);
            this.__entryParserKey = this.__configuration.getServerSystemKey();
         } else {
            String systemType = System.getProperty("org.apache.commons.net.ftp.systemType");
            if(systemType == null) {
               systemType = this.getSystemType();
               Properties override = getOverrideProperties();
               if(override != null) {
                  String newType = override.getProperty(systemType);
                  if(newType != null) {
                     systemType = newType;
                  }
               }
            }

            this.__entryParser = this.__parserFactory.createFileEntryParser(systemType);
            this.__entryParserKey = systemType;
         }
      }

      return this.initiateListParsing(this.__entryParser, pathname);
   }

   private FTPListParseEngine initiateListParsing(FTPFileEntryParser parser, String pathname) throws IOException {
      Socket socket = this._openDataConnection_(26, this.getListArguments(pathname));
      FTPListParseEngine engine = new FTPListParseEngine(parser);
      if(socket == null) {
         return engine;
      } else {
         try {
            engine.readServerList(socket.getInputStream(), this.getControlEncoding());
         } finally {
            Util.closeQuietly(socket);
         }

         this.completePendingCommand();
         return engine;
      }
   }

   private FTPListParseEngine initiateMListParsing(String pathname) throws IOException {
      Socket socket = this._openDataConnection_(38, pathname);
      FTPListParseEngine engine = new FTPListParseEngine(MLSxEntryParser.getInstance());
      if(socket == null) {
         return engine;
      } else {
         try {
            engine.readServerList(socket.getInputStream(), this.getControlEncoding());
         } finally {
            Util.closeQuietly(socket);
            this.completePendingCommand();
         }

         return engine;
      }
   }

   protected String getListArguments(String pathname) {
      if(this.getListHiddenFiles()) {
         if(pathname != null) {
            StringBuilder sb = new StringBuilder(pathname.length() + 3);
            sb.append("-a ");
            sb.append(pathname);
            return sb.toString();
         } else {
            return "-a";
         }
      } else {
         return pathname;
      }
   }

   public String getStatus() throws IOException {
      return FTPReply.isPositiveCompletion(this.stat())?this.getReplyString():null;
   }

   public String getStatus(String pathname) throws IOException {
      return FTPReply.isPositiveCompletion(this.stat(pathname))?this.getReplyString():null;
   }

   public String getModificationTime(String pathname) throws IOException {
      return FTPReply.isPositiveCompletion(this.mdtm(pathname))?this.getReplyString():null;
   }

   public boolean setModificationTime(String pathname, String timeval) throws IOException {
      return FTPReply.isPositiveCompletion(this.mfmt(pathname, timeval));
   }

   public void setBufferSize(int bufSize) {
      this.__bufferSize = bufSize;
   }

   public int getBufferSize() {
      return this.__bufferSize;
   }

   private int getDefaultedBufferSize() {
      return this.__bufferSize > 0?this.__bufferSize:1024;
   }

   public void configure(FTPClientConfig config) {
      this.__configuration = config;
   }

   public void setListHiddenFiles(boolean listHiddenFiles) {
      this.__listHiddenFiles = listHiddenFiles;
   }

   public boolean getListHiddenFiles() {
      return this.__listHiddenFiles;
   }

   public boolean isUseEPSVwithIPv4() {
      return this.__useEPSVwithIPv4;
   }

   public void setUseEPSVwithIPv4(boolean selected) {
      this.__useEPSVwithIPv4 = selected;
   }

   public void setCopyStreamListener(CopyStreamListener listener) {
      this.__copyStreamListener = listener;
   }

   public CopyStreamListener getCopyStreamListener() {
      return this.__copyStreamListener;
   }

   public void setControlKeepAliveTimeout(long controlIdle) {
      this.__controlKeepAliveTimeout = controlIdle * 1000L;
   }

   public long getControlKeepAliveTimeout() {
      return this.__controlKeepAliveTimeout / 1000L;
   }

   public void setControlKeepAliveReplyTimeout(int timeout) {
      this.__controlKeepAliveReplyTimeout = timeout;
   }

   public int getControlKeepAliveReplyTimeout() {
      return this.__controlKeepAliveReplyTimeout;
   }

   private CopyStreamListener __mergeListeners(CopyStreamListener local) {
      if(local == null) {
         return this.__copyStreamListener;
      } else if(this.__copyStreamListener == null) {
         return local;
      } else {
         CopyStreamAdapter merged = new CopyStreamAdapter();
         merged.addCopyStreamListener(local);
         merged.addCopyStreamListener(this.__copyStreamListener);
         return merged;
      }
   }

   public void setAutodetectUTF8(boolean autodetect) {
      this.__autodetectEncoding = autodetect;
   }

   public boolean getAutodetectUTF8() {
      return this.__autodetectEncoding;
   }

   /** @deprecated */
   @Deprecated
   public String getSystemName() throws IOException {
      if(this.__systemName == null && FTPReply.isPositiveCompletion(this.syst())) {
         this.__systemName = ((String)this._replyLines.get(this._replyLines.size() - 1)).substring(4);
      }

      return this.__systemName;
   }

   private static class CSL implements CopyStreamListener {
      private final FTPClient parent;
      private final long idle;
      private final int currentSoTimeout;
      private long time = System.currentTimeMillis();
      private int notAcked;

      CSL(FTPClient parent, long idleTime, int maxWait) throws SocketException {
         this.idle = idleTime;
         this.parent = parent;
         this.currentSoTimeout = parent.getSoTimeout();
         parent.setSoTimeout(maxWait);
      }

      public void bytesTransferred(CopyStreamEvent event) {
         this.bytesTransferred(event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());
      }

      public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
         long now = System.currentTimeMillis();
         if(now - this.time > this.idle) {
            try {
               this.parent.__noop();
            } catch (SocketTimeoutException var9) {
               ++this.notAcked;
            } catch (IOException var10) {
               ;
            }

            this.time = now;
         }

      }

      void cleanUp() throws IOException {
         while(this.notAcked-- > 0) {
            this.parent.__getReplyNoReport();
         }

         this.parent.setSoTimeout(this.currentSoTimeout);
      }
   }

   private static class PropertiesSingleton {
      static final Properties PROPERTIES;

      static {
         InputStream resourceAsStream = FTPClient.class.getResourceAsStream("/systemType.properties");
         Properties p = null;
         if(resourceAsStream != null) {
            p = new Properties();

            try {
               p.load(resourceAsStream);
            } catch (IOException var11) {
               ;
            } finally {
               try {
                  resourceAsStream.close();
               } catch (IOException var10) {
                  ;
               }

            }
         }

         PROPERTIES = p;
      }
   }
}
