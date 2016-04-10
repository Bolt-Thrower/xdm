package org.sdg.xdman.core.common.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import jcifs.util.Base64;
import org.sdg.xdman.core.common.AuthenticationException;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.core.common.http.NTLMAutheticator;
import org.sdg.xdman.core.common.http.io.ChunkedInputStream;
import org.sdg.xdman.util.HTTPUtil;
import org.sdg.xdman.util.Logger;

public class XDMHttpClient2 {
   public HashMap requestHeaders = new HashMap();
   public HashMap resposeHeaders = new HashMap();
   private ArrayList cookies = new ArrayList();
   private ArrayList setCookies = new ArrayList();
   private SSLContext context;
   public boolean connected = false;
   public boolean sendGet = false;
   public Socket sock;
   public URL url;
   public String host;
   public String path;
   public String protocol;
   public String query;
   public String referer;
   public String ua;
   int port;
   public OutputStream out;
   public InputStream in;
   public String statusLine;
   final int HTTP_PROXY = 10;
   final int HTTPS_PROXY = 20;
   final int NONE = 30;
   int proxyType = 30;
   XDMConfig config;
   long len;
   public static final int NTLM = 10;
   public static final int BASIC = 20;
   public static final int NO_AUTH = 0;
   static boolean flag = false;
   public String user;
   public String pass;

   public void addCookies(ArrayList cookieList) {
      if(cookieList != null) {
         this.cookies.addAll(cookieList);
      }

   }

   public XDMHttpClient2(XDMConfig config) {
      this.config = config;
   }

   public void addRequestHeaders(String key, String value) {
      if(key != null && value != null) {
         this.requestHeaders.put(key, value);
      }
   }

   public String getResponseHeader(String key) {
      return (String)this.resposeHeaders.get(key);
   }

   public void addCookie(String cookie) {
      if(cookie != null) {
         this.cookies.add(cookie);
      }

   }

   public ArrayList getCookies() {
      return this.setCookies;
   }

   private void doConnect() throws IOException, AuthenticationException {
      if(this.protocol.equalsIgnoreCase("http")) {
         this.sock = new Socket();
         this.sock.setTcpNoDelay(true);
         this.sock.setReceiveBufferSize(this.config.tcpBuf * 1024);
         this.sock.setSoTimeout(this.config.timeout * 1000);
         if(this.config.useHttpProxy) {
            this.proxyType = 10;
            String proxyHost = this.config.httpProxyHost;
            int proxyPort = this.config.httpProxyPort;
            this.sock.connect(new InetSocketAddress(proxyHost, proxyPort));
         } else {
            this.proxyType = 30;
            this.sock.connect(new InetSocketAddress(this.host, this.port));
         }
      } else {
         if(!this.protocol.equalsIgnoreCase("https")) {
            throw new IOException("Protocol " + this.protocol + " is not supported");
         }

         if(this.config.useHttpsProxy) {
            this.proxyType = 20;
            this.sock = new Socket();
            this.doTunneling(this.config.httpsProxyHost, this.config.httpsProxyPort);
         } else {
            this.proxyType = 30;
            this.acceptAllCerts();
            this.sock = this.context.getSocketFactory().createSocket();
            this.sock.setTcpNoDelay(true);
            this.sock.setSoTimeout(this.config.timeout * 1000);
            this.sock.setReceiveBufferSize(this.config.tcpBuf * 1024);
            this.sock.connect(new InetSocketAddress(this.host, this.port));
         }
      }

      this.sock.setTcpNoDelay(true);
      this.in = this.sock.getInputStream();
      this.out = this.sock.getOutputStream();
      this.sock.setKeepAlive(true);
      this.connected = true;
   }

   private void doTunnelConnect(String host, int port) throws IOException {
      this.sock = new Socket();
      this.sock.setTcpNoDelay(true);
      this.sock.setSoTimeout(this.config.timeout * 1000);
      this.sock.setReceiveBufferSize(this.config.tcpBuf * 1024);
      this.sock.connect(new InetSocketAddress(host, port));
      this.in = this.sock.getInputStream();
      this.out = this.sock.getOutputStream();
   }

   private void initConnection(String uri) throws IOException, URISyntaxException {
      String oldHost = this.host;
      int oldPort = this.port;
      uri = uri.trim();
      this.url = new URL(uri);
      this.host = this.url.getHost();
      this.port = this.url.getPort();
      this.protocol = this.url.getProtocol();
      this.path = this.url.getPath();
      this.query = this.url.getQuery();
      if(this.path == null) {
         this.path = "/";
      }

      if(!this.path.startsWith("/")) {
         this.path = "/" + this.path;
      }

      if(this.query != null) {
         this.path = this.path + "?" + this.query;
      }

      if(this.port < 0) {
         if(this.protocol.equals("http")) {
            this.port = 80;
         }

         if(this.protocol.equals("https")) {
            this.port = 443;
         }
      }

      if(oldHost != null && (!this.host.equals(oldHost) || this.port != oldPort)) {
         throw new IOException("ReUse Error: Remote address is incompatiable: New Host: " + this.host + " Old Host: " + oldHost);
      }
   }

   private void parseResponse() throws IOException {
      int in2;
      if(flag) {
         this.in = this.sock.getInputStream();
         System.out.println("***AVAIL: " + this.in.available());

         for(int tenc = 0; tenc < 100; ++tenc) {
            in2 = this.in.read();
            if(in2 == 13) {
               System.out.println("\'\\r\'");
            } else if(in2 == 10) {
               System.out.println("\'\\n\'");
            } else {
               System.out.println((char)in2);
            }
         }
      }

      this.statusLine = HTTPUtil.readLine(this.in);
      System.out.println("Status: " + this.statusLine + " " + this.statusLine.length());
      Logger.log(this.statusLine);
      this.resposeHeaders.clear();

      while(true) {
         String enc;
         String in3;
         do {
            while(true) {
               String var5 = HTTPUtil.readLine(this.in);
               if(var5.length() < 1) {
                  var5 = (String)this.resposeHeaders.get("transfer-encoding");
                  Object var6 = this.in;
                  if(var5 != null) {
                     if(!var5.equalsIgnoreCase("chunked")) {
                        throw new IOException("Transfer Encoding not supported: " + var5);
                     }

                     var6 = new ChunkedInputStream(this.in);
                  }

                  enc = (String)this.resposeHeaders.get("content-encoding");
                  Object var7 = var6;
                  if(enc != null) {
                     if(enc.equalsIgnoreCase("gzip")) {
                        var7 = new GZIPInputStream((InputStream)var6);
                     } else {
                        if(!enc.equalsIgnoreCase("none")) {
                           throw new IOException("Content Encoding not supported: " + enc);
                        }

                        var7 = var6;
                     }
                  }

                  this.in = (InputStream)var7;
                  return;
               }

               Logger.log(var5);
               System.out.println("RESP: " + var5);
               in2 = var5.indexOf(":");
               enc = var5.substring(0, in2).trim().toLowerCase();
               in3 = var5.substring(in2 + 1).trim();
               if(enc.equals("proxy-authenticate")) {
                  break;
               }

               if(enc.equals("www-authenticate")) {
                  if((in3.toLowerCase().indexOf("ntlm") != -1 || in3.toLowerCase().indexOf("basic") != -1) && this.resposeHeaders.get(enc) == null) {
                     this.resposeHeaders.put(enc, in3);
                  }
               } else if(enc.equals("set-cookie")) {
                  this.setCookies.add(in3);
               } else {
                  this.resposeHeaders.put(enc, in3);
               }
            }
         } while(in3.toLowerCase().indexOf("ntlm") == -1 && in3.toLowerCase().indexOf("basic") == -1);

         if(this.resposeHeaders.get(enc) == null) {
            this.resposeHeaders.put(enc, in3);
         }
      }
   }

   private String parseProxyResponse(HashMap proxyResponse) throws IOException {
      String stat = HTTPUtil.readLine(this.in);
      Logger.log(stat);
      proxyResponse.clear();

      while(true) {
         String enc;
         String in3;
         do {
            while(true) {
               String tenc = HTTPUtil.readLine(this.in);
               if(tenc.length() < 1) {
                  tenc = (String)proxyResponse.get("transfer-encoding");
                  Object in21 = this.in;
                  if(tenc != null) {
                     if(!tenc.equalsIgnoreCase("chunked")) {
                        throw new IOException("Transfer Encoding not supported: " + tenc);
                     }

                     in21 = new ChunkedInputStream(this.in);
                  }

                  enc = (String)proxyResponse.get("content-encoding");
                  Object in31 = in21;
                  if(enc != null) {
                     if(enc.equalsIgnoreCase("gzip")) {
                        in31 = new GZIPInputStream((InputStream)in21);
                     } else {
                        if(!enc.equalsIgnoreCase("none")) {
                           throw new IOException("Content Encoding not supported: " + enc);
                        }

                        in31 = in21;
                     }
                  }

                  this.in = (InputStream)in31;
                  return stat;
               }

               Logger.log(tenc);
               System.out.println(tenc);
               int in2 = tenc.indexOf(":");
               enc = tenc.substring(0, in2).trim().toLowerCase();
               in3 = tenc.substring(in2 + 1).trim();
               if(enc.equals("proxy-authenticate")) {
                  break;
               }

               if(enc.equals("www-authenticate")) {
                  if((in3.toLowerCase().indexOf("ntlm") != -1 || in3.toLowerCase().indexOf("basic") != -1) && proxyResponse.get(enc) == null) {
                     proxyResponse.put(enc, in3);
                  }
               } else {
                  proxyResponse.put(enc, in3);
               }
            }
         } while(in3.toLowerCase().indexOf("ntlm") == -1 && in3.toLowerCase().indexOf("basic") == -1);

         if(proxyResponse.get(enc) == null) {
            proxyResponse.put(enc, in3);
         }
      }
   }

   public void reConnect(String uri) throws UnknownHostException, IOException, URISyntaxException, AuthenticationException {
      this.initConnection(uri);
      this.connected = true;
   }

   public void connect(String uri) throws UnknownHostException, IOException, URISyntaxException, AuthenticationException {
      if(!this.connected) {
         this.initConnection(uri);
         this.doConnect();
         this.connected = true;
      }
   }

   public void close() {
      try {
         this.in.close();
      } catch (Exception var4) {
         ;
      }

      try {
         this.out.close();
      } catch (Exception var3) {
         ;
      }

      try {
         this.sock.close();
      } catch (Exception var2) {
         ;
      }

   }

   public String getHostString() {
      return this.host + ":" + this.port;
   }

   private int getAuthenticationMethod(String auth) throws IOException {
      if(auth != null) {
         auth = auth.toLowerCase();
         if(auth.indexOf("basic") != -1) {
            return 20;
         } else if(auth.indexOf("ntlm") != -1) {
            return 10;
         } else {
            throw new IOException("Authentication method not supported");
         }
      } else {
         return 0;
      }
   }

   private String getNTChallage(String value) {
      if(value == null) {
         return null;
      } else {
         int index = value.indexOf(32);
         return index < 0?null:value.substring(index + 1).trim();
      }
   }

   public void sendRequest() throws IOException, AuthenticationException {
      this.requestHeaders.put("accept-encoding", "gzip");
      boolean proxyAuthMethod = false;
      boolean httpAuthMethod = false;
      int proxyNtlmStage = -1;
      int ntlmStage = -1;
      int basicHttpStage = -1;
      int basicProxyStage = -1;
      int prevResponse = 0;
      NTLMAutheticator ntAuth = null;
      NTLMAutheticator proxyNtAuth = null;
      if(this.proxyType == 10) {
         this.path = this.url.toString();
      }

      while(prevResponse != 401 || this.user != null && this.user.length() >= 1) {
         int var13 = this.getAuthenticationMethod((String)this.resposeHeaders.get("www-authenticate"));
         int var12 = this.getAuthenticationMethod((String)this.resposeHeaders.get("proxy-authenticate"));
         String requestBuffer;
         String tenc;
         if(var13 == 10) {
            if(ntlmStage > 0) {
               throw new AuthenticationException("Authentication Failed!");
            }

            if(ntAuth == null) {
               requestBuffer = this.getUser(this.user);
               tenc = this.getDomain(this.user);
               ntAuth = new NTLMAutheticator(tenc, requestBuffer, this.pass);
            }

            requestBuffer = (String)this.resposeHeaders.get("www-authenticate");
            ++ntlmStage;
            this.requestHeaders.put("authorization", "NTLM " + ntAuth.getNTLMString(this.getNTChallage(requestBuffer)));
         } else if(var13 == 20) {
            if(basicHttpStage > -1) {
               throw new AuthenticationException("Authentication Failed!");
            }

            requestBuffer = "Basic " + Base64.encode((this.user + ":" + this.pass).getBytes());
            this.requestHeaders.put("authorization", requestBuffer);
            ++basicHttpStage;
         } else if(var13 != 0) {
            throw new IOException("Authentication method not supported");
         }

         if(var12 == 10) {
            if(proxyNtlmStage > 1) {
               throw new AuthenticationException("Proxy Authentication Failed!");
            }

            if(proxyNtAuth == null) {
               proxyNtAuth = new NTLMAutheticator(System.getenv("USERDOMAIN"), this.config.httpUser, this.config.httpPass);
            }

            requestBuffer = (String)this.resposeHeaders.get("proxy-authenticate");
            ++proxyNtlmStage;
            this.requestHeaders.put("proxy-authorization", "NTLM " + proxyNtAuth.getNTLMString(this.getNTChallage(requestBuffer)));
         } else if(var12 == 20) {
            if(basicProxyStage > -1) {
               throw new AuthenticationException("Proxy Authentication Failed!");
            }

            requestBuffer = "Basic " + Base64.encode((this.config.httpUser + ":" + this.config.httpPass).getBytes());
            this.requestHeaders.put("proxy-authorization", requestBuffer);
            ++basicProxyStage;
         } else if(var12 != 0) {
            throw new IOException("Proxy Authentication method not supported");
         }

         StringBuffer var14 = new StringBuffer();
         this.appendHeaders(var14, this.requestHeaders, false);
         var14.append("\r\n");
         if(!this.sock.isConnected()) {
            this.doConnect();
         } else if(this.sock.isClosed()) {
            this.doConnect();
         }

         this.out = this.sock.getOutputStream();
         this.in = this.sock.getInputStream();
         this.out.write(var14.toString().getBytes());
         this.out.flush();
         System.out.println(var14);
         this.parseResponse();
         prevResponse = this.getResponseCode();
         System.out.println("SERVER_RESP: " + prevResponse + " " + this.statusLine);
         if(prevResponse == 407 || prevResponse == 401) {
            tenc = (String)this.resposeHeaders.get("transfer-encoding");
            this.skipRemainingStream("chunked".equalsIgnoreCase(tenc), this.resposeHeaders);
            if("Close".equalsIgnoreCase((String)this.resposeHeaders.get("connection")) || "Close".equalsIgnoreCase((String)this.resposeHeaders.get("proxy-connection"))) {
               this.close();
            }
         }

         if(prevResponse != 407 && prevResponse != 401) {
            this.sendGet = true;
            return;
         }
      }

      throw new AuthenticationException("Authentication required!");
   }

   private void appendHeaders(StringBuffer requestBuffer, HashMap requestHeaders, boolean tunnel) {
      if(tunnel) {
         requestBuffer.append("CONNECT " + this.host + ":" + this.port + " HTTP/1.1\r\nHost: " + this.host + ":" + this.port + "\r\n");
      } else {
         String it = this.host;
         if(this.port != 80) {
            it = it + ":" + this.port;
         }

         requestBuffer.append("GET " + this.path + " HTTP/1.1\r\nHost: " + it + "\r\n");
      }

      Iterator var8 = requestHeaders.keySet().iterator();

      while(var8.hasNext()) {
         String i = (String)var8.next();
         String value = (String)requestHeaders.get(i);
         requestBuffer.append(i + ": " + value + "\r\n");
      }

      if(this.cookies != null) {
         for(int var7 = 0; var7 < this.cookies.size(); ++var7) {
            requestBuffer.append("cookie: " + (String)this.cookies.get(var7) + "\r\n");
         }
      }

      Logger.log(requestBuffer);
   }

   private String getDomain(String userdom) {
      int idx = userdom.lastIndexOf(64);
      return idx < 0?null:userdom.substring(idx + 1);
   }

   private String getUser(String userdom) {
      int idx = userdom.lastIndexOf(64);
      return idx < 0?userdom:userdom.substring(0, idx);
   }

   public void doTunneling(String host, int port) throws IOException, AuthenticationException {
      boolean authMethod = false;
      boolean prevResponse = false;
      int proxyNtlmStage = -1;
      int basicProxyStage = -1;
      NTLMAutheticator ntAuth = null;
      HashMap proxyResponse = new HashMap();
      HashMap proxyRequest = new HashMap();

      int var14;
      do {
         int var13 = this.getAuthenticationMethod((String)proxyResponse.get("proxy-authenticate"));
         String sock2;
         if(var13 == 10) {
            if(proxyNtlmStage > 1) {
               throw new AuthenticationException("Proxy Authentication Failed!");
            }

            if(ntAuth == null) {
               ntAuth = new NTLMAutheticator(System.getenv("USERDOMAIN"), this.config.httpsUser, this.config.httpsPass);
            }

            sock2 = (String)proxyResponse.get("proxy-authenticate");
            ++proxyNtlmStage;
            proxyRequest.put("proxy-authorization", "NTLM " + ntAuth.getNTLMString(this.getNTChallage(sock2)));
         } else if(var13 == 20) {
            if(basicProxyStage > -1) {
               throw new AuthenticationException("Proxy Authentication Failed!");
            }

            sock2 = "Basic " + Base64.encode((this.config.httpsUser + ":" + this.config.httpsPass).getBytes());
            proxyRequest.put("proxy-authorization", sock2);
            ++basicProxyStage;
         } else if(var13 != 0) {
            throw new IOException("Proxy Authentication method not supported");
         }

         StringBuffer var16 = new StringBuffer();
         this.appendHeaders(var16, proxyRequest, true);
         if(!this.sock.isConnected()) {
            this.doTunnelConnect(host, port);
         } else if(this.sock.isClosed()) {
            this.doTunnelConnect(host, port);
         }

         var16.append("\r\n");
         this.out.write(var16.toString().getBytes());
         this.out.flush();
         String statusLine = this.parseProxyResponse(proxyResponse);
         System.out.println(statusLine);
         var14 = HTTPUtil.getResponseCode(statusLine);
         if(var14 == 407 || var14 == 401) {
            String tenc = (String)proxyResponse.get("transfer-encoding");
            this.skipRemainingStream("chunked".equalsIgnoreCase(tenc), proxyResponse);
         }

         if("close".equalsIgnoreCase((String)proxyResponse.get("proxy-connection"))) {
            this.close();
         }
      } while(var14 == 407 || var14 == 401);

      if(var14 != 200) {
         System.out.println(var14);
         throw new IOException("Proxy tunnelling failed!");
      } else {
         this.acceptAllCerts();
         SSLSocket var15 = (SSLSocket)this.context.getSocketFactory().createSocket(this.sock, this.host, this.port, true);
         var15.startHandshake();
         this.sock = var15;
      }
   }

   private void skipRemainingStream(boolean isChunked, HashMap map) throws IOException {
      long cLen = HTTPUtil.getContentLength(map);
      byte[] buf = new byte[8192];
      int x;
      if(cLen > 0L) {
         while(cLen > 0L) {
            x = (int)(cLen > (long)buf.length?(long)buf.length:cLen);
            int x1 = this.in.read(buf, 0, x);
            if(x1 == -1) {
               break;
            }

            cLen -= (long)x1;
         }
      } else if(isChunked) {
         do {
            x = this.in.read(buf);
         } while(x != -1);
      } else {
         do {
            x = this.in.available();
            if(x <= 0) {
               break;
            }

            if(x > buf.length) {
               x = buf.length;
            }
         } while(this.in.read(buf, 0, x) >= 1);
      }

   }

   public long getContentLength() {
      return HTTPUtil.getContentLength(this.resposeHeaders);
   }

   public int getResponseCode() {
      return HTTPUtil.getResponseCode(this.statusLine);
   }

   public String getContentName() {
      try {
         String cd = (String)this.resposeHeaders.get("content-disposition");
         if(cd == null) {
            return null;
         }

         cd = cd.toLowerCase();
         if(cd.startsWith("attachment")) {
            String fm = cd.split(";")[1].trim();
            int index = fm.indexOf("=");
            if(index < 0) {
               return null;
            }

            return fm.substring(index + 1).trim();
         }
      } catch (Exception var4) {
         ;
      }

      return null;
   }

   private void acceptAllCerts() {
      try {
         try {
            this.context = SSLContext.getInstance("TLS");
         } catch (Exception var2) {
            this.context = SSLContext.getInstance("SSL");
         }

         TrustManager[] e = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
               return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
         }};
         this.context.init((KeyManager[])null, e, (SecureRandom)null);
      } catch (NoSuchAlgorithmException var3) {
         var3.printStackTrace();
      } catch (KeyManagementException var4) {
         var4.printStackTrace();
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public void finishStream() throws IOException {
      String ts = (String)this.resposeHeaders.get("transfer-encoding");
      if(ts != null && ts.equalsIgnoreCase("chunked")) {
         this.in.close();
      }

   }

   public static void main(String[] args) throws Exception {
      XDMConfig conf = XDMConfig.load(new File(""));
      conf.useHttpsProxy = true;
      conf.useHttpProxy = true;
      conf.httpsPass = "May@2013";
      conf.httpsUser = "SD00109548";
      conf.httpUser = "SD00109548";
      conf.httpPass = "May@2013";
      conf.httpsProxyHost = "10.254.40.57";
      conf.httpsProxyPort = 80;
      conf.httpProxyHost = "10.254.40.57";
      conf.httpProxyPort = 80;
      XDMHttpClient2 client = new XDMHttpClient2(conf);
      client.user = "Techmahindra\\sd00109548";
      client.pass = "May@2013";
      client.requestHeaders.put("proxy-connection", "keep-alive");
      client.requestHeaders.put("user-agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");
      client.connect("http://10.254.40.54:15871/cgi-bin/blockpage.cgi?ws-session=3171572021");
      client.sendRequest();
      InputStream in = client.in;

      while(true) {
         int a = in.read();
         if(a < 0) {
            client.finishStream();
            System.out.println("AVAILLLL: " + client.sock.getInputStream().available());
            System.exit(0);
            in = client.sock.getInputStream();
            in.skip((long)in.available());
            System.out.println("=================");
            client.requestHeaders.clear();
            client.requestHeaders.put("user-agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");
            client.reConnect("http://xdman.sourceforge.net/xdmanlogo.png");
            client.sendRequest();
            in = client.in;

            while(true) {
               a = in.read();
               if(a < 0) {
                  return;
               }

               System.out.print((char)a);
            }
         }

         System.out.print((char)a);
      }
   }
}
