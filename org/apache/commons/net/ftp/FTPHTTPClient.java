package org.apache.commons.net.ftp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Inet6Address;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.util.Base64;

public class FTPHTTPClient extends FTPClient {
   private final String proxyHost;
   private final int proxyPort;
   private final String proxyUsername;
   private final String proxyPassword;
   private static final byte[] CRLF = new byte[]{(byte)13, (byte)10};
   private final Base64 base64;

   public FTPHTTPClient(String proxyHost, int proxyPort, String proxyUser, String proxyPass) {
      this.base64 = new Base64();
      this.proxyHost = proxyHost;
      this.proxyPort = proxyPort;
      this.proxyUsername = proxyUser;
      this.proxyPassword = proxyPass;
   }

   public FTPHTTPClient(String proxyHost, int proxyPort) {
      this(proxyHost, proxyPort, (String)null, (String)null);
   }

   protected Socket _openDataConnection_(int command, String arg) throws IOException {
      return super._openDataConnection_(command, arg);
   }

   protected Socket _openDataConnection_(String command, String arg) throws IOException {
      if(this.getDataConnectionMode() != 2) {
         throw new IllegalStateException("Only passive connection mode supported");
      } else {
         boolean isInet6Address = this.getRemoteAddress() instanceof Inet6Address;
         boolean attemptEPSV = this.isUseEPSVwithIPv4() || isInet6Address;
         if(attemptEPSV && this.epsv() == 229) {
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

         Socket socket = new Socket(this.proxyHost, this.proxyPort);
         InputStream is = socket.getInputStream();
         OutputStream os = socket.getOutputStream();
         this.tunnelHandshake(this.getPassiveHost(), this.getPassivePort(), is, os);
         if(this.getRestartOffset() > 0L && !this.restart(this.getRestartOffset())) {
            socket.close();
            return null;
         } else if(!FTPReply.isPositivePreliminary(this.sendCommand(command, arg))) {
            socket.close();
            return null;
         } else {
            return socket;
         }
      }
   }

   public void connect(String host, int port) throws SocketException, IOException {
      this._socket_ = new Socket(this.proxyHost, this.proxyPort);
      this._input_ = this._socket_.getInputStream();
      this._output_ = this._socket_.getOutputStream();

      try {
         this.tunnelHandshake(host, port, this._input_, this._output_);
      } catch (Exception var5) {
         IOException ioe = new IOException("Could not connect to " + host + " using port " + port);
         ioe.initCause(var5);
         throw ioe;
      }

      super._connectAction_();
   }

   private void tunnelHandshake(String host, int port, InputStream input, OutputStream output) throws IOException, UnsupportedEncodingException {
      String connectString = "CONNECT " + host + ":" + port + " HTTP/1.1";
      String hostString = "Host: " + host + ":" + port;
      output.write(connectString.getBytes("UTF-8"));
      output.write(CRLF);
      output.write(hostString.getBytes("UTF-8"));
      output.write(CRLF);
      if(this.proxyUsername != null && this.proxyPassword != null) {
         String response = this.proxyUsername + ":" + this.proxyPassword;
         String reader = "Proxy-Authorization: Basic " + this.base64.encodeToString(response.getBytes("UTF-8"));
         output.write(reader.getBytes("UTF-8"));
      }

      output.write(CRLF);
      ArrayList response1 = new ArrayList();
      BufferedReader reader1 = new BufferedReader(new InputStreamReader(input));

      for(String size = reader1.readLine(); size != null && size.length() > 0; size = reader1.readLine()) {
         response1.add(size);
      }

      int size1 = response1.size();
      if(size1 == 0) {
         throw new IOException("No response from proxy");
      } else {
         String code = null;
         String resp = (String)response1.get(0);
         if(resp.startsWith("HTTP/") && resp.length() >= 12) {
            code = resp.substring(9, 12);
            if(!"200".equals(code)) {
               StringBuilder msg = new StringBuilder();
               msg.append("HTTPTunnelConnector: connection failed\r\n");
               msg.append("Response received from the proxy:\r\n");
               Iterator var14 = response1.iterator();

               while(var14.hasNext()) {
                  String line = (String)var14.next();
                  msg.append(line);
                  msg.append("\r\n");
               }

               throw new IOException(msg.toString());
            }
         } else {
            throw new IOException("Invalid response from proxy: " + resp);
         }
      }
   }
}
