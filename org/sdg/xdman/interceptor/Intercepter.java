package org.sdg.xdman.interceptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.core.common.ProxyHelper;
import org.sdg.xdman.core.common.WebProxy;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.core.common.http.io.ChunkedInputStream;
import org.sdg.xdman.interceptor.DownloadIntercepterInfo;
import org.sdg.xdman.interceptor.HTMLEscapeUtil;
import org.sdg.xdman.interceptor.HTMLTitleParser;
import org.sdg.xdman.interceptor.IMediaGrabber;
import org.sdg.xdman.interceptor.SocketPool;
import org.sdg.xdman.plugin.youtube.JSONParser;
import org.sdg.xdman.plugin.youtube.YTVideoInfo;
import org.sdg.xdman.plugin.youtube.YouTubeFmtMap;
import org.sdg.xdman.util.HTTPUtil;
import org.sdg.xdman.util.Logger;
import org.sdg.xdman.util.MIMEUtil;
import org.sdg.xdman.util.XDMUtil;

public class Intercepter implements Runnable {
   String error = "HTTP/1.1 502 Bad Gateway\r\n\r\nERROR";
   String captureString = "HTTP/1.1 204 No Content\r\nConnection: Close\r\nProxy-Connection: Close\r\n\r\n";
   Socket sockLocal;
   Socket sockRemote;
   InputStream sLocalIn;
   InputStream sRemoteIn;
   OutputStream sLocalOut;
   OutputStream sRemoteOut;
   String requestLine;
   String responseLine;
   String url;
   HashMap requestHeaders;
   HashMap responseHeaders;
   String host;
   String path;
   int port;
   byte[] b = new byte[8192];
   XDMConfig config;
   ArrayList cookies;
   ArrayList setCookies;
   boolean stop = false;
   Thread t;
   DownloadStateListner mgr;
   IMediaGrabber media_grabber;
   String method;
   String proxy_pac = "function FindProxyForURL(url, host){\nvar proxy_yes = \"PROXY 127.0.0.1:9614; DIRECT\";\nif(url.indexOf(\"http://127.0.0.1:9614/\")==0)return \"DIRECT\";\nif(url.indexOf(\"http://\")==0)return proxy_yes; \nelse return \"DIRECT\";}";

   public Intercepter(Socket sock, XDMConfig conf, DownloadStateListner mgr, IMediaGrabber mg) {
      this.sockLocal = sock;
      this.config = conf;
      this.mgr = mgr;
      this.media_grabber = mg;
   }

   public void run() {
      while(true) {
         try {
            if(!this.stop) {
               this.acceptRequest();
               continue;
            }
         } catch (Exception var5) {
            Logger.log(var5);
         } finally {
            this.closeAll();
         }

         return;
      }
   }

   private void acceptRequest() throws Exception {
      this.sLocalIn = this.sockLocal.getInputStream();
      this.sLocalOut = this.sockLocal.getOutputStream();
      this.requestLine = HTTPUtil.readLine(this.sLocalIn);
      Logger.log(this.requestLine);
      if(this.requestLine != null && this.requestLine.length() >= 1) {
         if(this.requestLine.equals("PARAM")) {
            this.handlePARAMRequest(this.sLocalIn);
         } else {
            String[] arr = this.requestLine.split(" ");
            if(arr.length < 3) {
               this.closeAll();
            } else {
               this.url = arr[1].trim();
               if(!this.url.startsWith("/") && !this.url.startsWith("http://127.0.0.1:9614")) {
                  if(!this.requestLine.startsWith("CONNECT")) {
                     if(!this.crackURL(this.url)) {
                        throw new Exception("Crack url failed for " + this.url);
                     } else {
                        Logger.log("HandleRequest call");
                        this.handleRequest();
                     }
                  }
               } else {
                  this.handleLocalRequest(this.url, this.sLocalIn, this.sLocalOut);
                  this.closeAll();
               }
            }
         }
      } else {
         this.closeAll();
      }
   }

   private void handleRequest() throws Exception {
      this.requestHeaders = new HashMap();
      this.responseHeaders = new HashMap();
      this.cookies = new ArrayList();
      this.setCookies = new ArrayList();
      StringBuffer reqBuf = new StringBuffer();
      WebProxy proxy = ProxyHelper.getProxyForURL(this.url, this.config);
      System.out.println("using proxy for: " + this.url);
      boolean useProxy = proxy != null;
      boolean reuse = true;
      this.method = this.requestLine.split(" ")[0];
      if(useProxy) {
         System.out.println("using proxy: " + proxy.host);
         reqBuf.append(this.requestLine + "\r\n");
      } else {
         reqBuf.append(this.method + " " + this.path + " HTTP/1.1\r\n");
      }

      boolean isRequestChunked = false;
      boolean compress = false;
      long requestBodySize = -1L;

      while(true) {
         String tmpFile = HTTPUtil.readLine(this.sLocalIn);
         String responseStatus;
         if(tmpFile == null || tmpFile.length() < 1) {
            if(compress) {
               reqBuf.append("Accept-Encoding: gzip\r\n");
            }

            reqBuf.append("\r\n");
            Logger.log(reqBuf);

            try {
               if(this.sockRemote == null) {
                  if(useProxy) {
                     this.sockRemote = SocketPool.getSocket(proxy.host, proxy.port);
                  } else {
                     this.sockRemote = SocketPool.getSocket(this.host, this.port);
                  }
               }

               if(this.sockRemote == null) {
                  Logger.log("Create new for: " + this.requestLine);
                  this.establishConnection(proxy);
                  reuse = false;
               } else {
                  Logger.log("Re-use for: " + this.requestLine + " **********************************************************");
               }
            } catch (Exception var59) {
               Logger.log(var59);
               var59.printStackTrace();
               this.writeString(this.sLocalOut, this.error);
               this.sLocalOut.flush();
               this.closeAll();
               throw new IOException(var59);
            }

            try {
               this.sRemoteIn = this.sockRemote.getInputStream();
               this.sRemoteOut = this.sockRemote.getOutputStream();
               this.writeString(this.sRemoteOut, reqBuf.toString());
            } catch (Exception var54) {
               Logger.log("Unable to re-use socket already closed. Create new for: " + this.requestLine);

               try {
                  this.establishConnection(proxy);
                  this.sRemoteIn = this.sockRemote.getInputStream();
                  this.sRemoteOut = this.sockRemote.getOutputStream();
                  reuse = false;
                  this.writeString(this.sRemoteOut, reqBuf.toString());
               } catch (Exception var53) {
                  Logger.log(var53);
                  this.writeString(this.sLocalOut, this.error);
                  this.sLocalOut.flush();
                  this.closeAll();
                  throw new IOException(var53);
               }
            }

            this.sRemoteOut.flush();
            requestBodySize = HTTPUtil.getContentLength(this.requestHeaders);
            if(this.requestHeaders.get("transfer-encoding") != null) {
               isRequestChunked = "chunked".equals((String)this.requestHeaders.get("transfer-encoding") + "".toLowerCase());
            }

            if(requestBodySize < 0L && !isRequestChunked) {
               requestBodySize = 0L;
            }

            File tmpFile1 = new File(this.config.tempdir, String.valueOf(System.currentTimeMillis()));
            FileOutputStream tmpOut1 = new FileOutputStream(tmpFile1);
            int code1;
            int resBuf;
            if(isRequestChunked) {
               ChunkedInputStream responseStatus1 = new ChunkedInputStream(this.sLocalIn);

               while(true) {
                  int tmpIn1 = responseStatus1.read(this.b, 0, this.b.length);
                  if(tmpIn1 == -1) {
                     this.writeString(this.sRemoteOut, "0\r\n\r\n");
                     this.writeString(tmpOut1, "0\r\n\r\n");
                     responseStatus1.close();
                     tmpOut1.close();
                     this.sRemoteOut.flush();
                     break;
                  }

                  String code = Integer.toHexString(tmpIn1) + "\r\n";
                  this.writeString(this.sRemoteOut, code);
                  this.sRemoteOut.write(this.b, 0, tmpIn1);
                  this.writeString(this.sRemoteOut, "\r\n");
                  this.writeString(tmpOut1, code);
                  tmpOut1.write(this.b, 0, tmpIn1);
                  this.writeString(tmpOut1, "\r\n");
               }
            } else {
               for(long responseStatus2 = requestBodySize; responseStatus2 >= 1L; responseStatus2 -= (long)resBuf) {
                  code1 = (int)(responseStatus2 > 8192L?8192L:responseStatus2);
                  resBuf = this.sLocalIn.read(this.b, 0, code1);
                  if(resBuf == -1) {
                     break;
                  }

                  this.sRemoteOut.write(this.b, 0, resBuf);
                  tmpOut1.write(this.b, 0, resBuf);
               }

               this.sRemoteOut.flush();
               tmpOut1.close();
            }

            responseStatus = null;
            FileInputStream tmpIn2 = null;

            try {
               responseStatus = HTTPUtil.readLine(this.sRemoteIn);
               if(responseStatus.length() < 1) {
                  throw new Exception("NO_DATA");
               }
            } catch (Exception var58) {
               Logger.log("Unable to re-use socket already closed. Create new for: " + this.requestLine + " " + var58);

               try {
                  try {
                     this.sockRemote.close();
                  } catch (Exception var52) {
                     ;
                  }

                  this.establishConnection(proxy);
                  this.sRemoteIn = this.sockRemote.getInputStream();
                  this.sRemoteOut = this.sockRemote.getOutputStream();
                  this.writeString(this.sRemoteOut, reqBuf.toString());
                  Logger.log("Resending post data...");
                  tmpIn2 = new FileInputStream(tmpFile1);

                  while(true) {
                     resBuf = tmpIn2.read(this.b, 0, this.b.length);
                     if(resBuf == -1) {
                        this.sRemoteOut.flush();
                        tmpIn2.close();
                        responseStatus = HTTPUtil.readLine(this.sRemoteIn);
                        break;
                     }

                     this.sRemoteOut.write(this.b, 0, resBuf);
                  }
               } catch (Exception var57) {
                  Logger.log(var57);
                  this.writeString(this.sLocalOut, this.error);
                  this.sLocalOut.flush();
                  this.closeAll();

                  try {
                     tmpFile1.delete();
                  } catch (Exception var45) {
                     ;
                  }

                  throw new IOException(var57);
               }
            }

            try {
               tmpIn2.close();
            } catch (Exception var51) {
               ;
            }

            try {
               tmpOut1.close();
            } catch (Exception var50) {
               ;
            }

            try {
               tmpFile1.delete();
            } catch (Exception var49) {
               ;
            }

            this.responseLine = responseStatus;
            code1 = HTTPUtil.getResponseCode(responseStatus);
            Logger.log("Reuse " + (reuse?"SUCCESS":"FAIL") + " for " + this.requestLine);
            StringBuilder resBuf1 = new StringBuilder();
            resBuf1.append(responseStatus + "\r\n");
            if(code1 == 100) {
               while(true) {
                  String sessionBasedAuth = HTTPUtil.readLine(this.sRemoteIn);
                  if(sessionBasedAuth == null || sessionBasedAuth.length() < 1) {
                     resBuf1.append("\r\n");
                     responseStatus = HTTPUtil.readLine(this.sRemoteIn);
                     this.responseLine = responseStatus;
                     code1 = HTTPUtil.getResponseCode(responseStatus);
                     break;
                  }

                  resBuf1.append(sessionBasedAuth + "\r\n");
               }
            }

            boolean sessionBasedAuth1 = false;

            while(true) {
               String size = HTTPUtil.readLine(this.sRemoteIn);
               String ts;
               String cn;
               if(size == null || size.length() < 1) {
                  long size1 = HTTPUtil.getContentLength(this.responseHeaders);
                  ts = (String)this.responseHeaders.get("transfer-encoding");
                  boolean close1 = false;
                  if(size1 < 0L) {
                     resBuf1.append("transfer-encoding: chunked\r\n");
                  } else {
                     resBuf1.append("content-length: " + size1 + "\r\n");
                  }

                  cn = (String)this.responseHeaders.get("connection");
                  Logger.log("#### Connection: " + cn);
                  if(cn != null && cn.toLowerCase().equals("close")) {
                     close1 = true;
                  }

                  if(this.responseHeaders.get("transfer-encoding") == null && this.responseHeaders.get("content-length") == null) {
                     close1 = true;
                  }

                  String keepAliveTimeout = (String)this.responseHeaders.get("keep-alive");
                  if(keepAliveTimeout != null && keepAliveTimeout.toLowerCase().indexOf("timeout") != -1) {
                     Logger.log("No keep alive due to timeout");
                     close1 = true;
                  }

                  if(!close1) {
                     if(!sessionBasedAuth1) {
                        resBuf1.append("keep-alive: timeout=300\r\n");
                     }

                     resBuf1.append("connection: keep-alive\r\n");
                     resBuf1.append("proxy-connection: keep-alive\r\n");
                  } else {
                     resBuf1.append("connection: close\r\n");
                     resBuf1.append("proxy-connection: close\r\n");
                  }

                  if(this.interceptDownload(code1)) {
                     this.writeString(this.sLocalOut, this.captureString);
                     this.sLocalOut.flush();
                     this.closeAll();
                     this.stop = true;
                     return;
                  } else {
                     resBuf1.append("\r\n");
                     Logger.log("Response for: " + this.requestLine + "\n" + resBuf1 + "\n----------------------------------");
                     this.writeString(this.sLocalOut, resBuf1.toString());
                     this.sLocalOut.flush();
                     this.sRemoteIn = this.createChunkedStreamsIfAvailable(this.responseHeaders, this.sRemoteIn);
                     String conn;
                     if(code1 != 204) {
                        conn = null;
                        FileOutputStream sock = null;
                        boolean yt = false;
                        if(this.url.startsWith("http://www.youtube.com/watch") || this.url.startsWith("https://www.youtube.com/watch")) {
                           conn = XDMUtil.getTempFile(this.config.tempdir).getAbsolutePath();
                           sock = new FileOutputStream(conn);
                           yt = true;
                        }

                        try {
                           this.copyData(this.sRemoteIn, this.sLocalOut, size1, ts, this.sockRemote, sock);
                           if(sock != null) {
                              try {
                                 sock.close();
                              } catch (Exception var48) {
                                 ;
                              }
                           }

                           if(yt) {
                              Logger.log("Parsing youtube page");
                              String exx = (String)this.responseHeaders.get("content-encoding");
                              this.parseYTPage(conn, exx != null && exx.toLowerCase().equals("gzip"), this.url, (String)this.responseHeaders.get("user-agent"), this.cookies.size() > 0?this.cookies:null);
                           }
                        } catch (Exception var55) {
                           throw var55;
                        } finally {
                           try {
                              sock.close();
                           } catch (Exception var47) {
                              ;
                           }

                           try {
                              (new File(conn)).delete();
                           } catch (Exception var46) {
                              ;
                           }

                        }

                        if(this.stop) {
                           return;
                        }
                     }

                     this.sLocalOut.flush();
                     if(ts != null && ts.toLowerCase().equals("chunked")) {
                        this.sRemoteIn.close();
                     }

                     if(close1) {
                        Logger.log("Setting keep-alive false");
                        this.closeAll();
                        this.stop = true;
                        return;
                     } else {
                        conn = (String)this.responseHeaders.get("connection");
                        if(conn != null && conn.toLowerCase().equals("close")) {
                           this.closeAll();
                           this.stop = true;
                           return;
                        } else {
                           conn = (String)this.requestHeaders.get("connection");
                           if(conn != null && conn.toLowerCase().equals("close")) {
                              this.closeAll();
                              this.stop = true;
                              return;
                           } else {
                              Socket sock1;
                              if(code1 != 401 && code1 != 407) {
                                 sock1 = this.sockRemote;
                                 if(useProxy) {
                                    SocketPool.putSocket(proxy.host, proxy.port, sock1);
                                 } else {
                                    SocketPool.putSocket(this.host, this.port, sock1);
                                 }

                                 this.sockRemote = null;
                              } else if(!sessionBasedAuth1) {
                                 sock1 = this.sockRemote;
                                 if(useProxy) {
                                    SocketPool.putSocket(proxy.host, proxy.port, sock1);
                                 } else {
                                    SocketPool.putSocket(this.host, this.port, sock1);
                                 }

                                 this.sockRemote = null;
                              } else {
                                 Logger.log("**********************SESSIOM_AUTHHHHHHHHHHHHHHHHH");
                              }

                              return;
                           }
                        }
                     }
                  }
               }

               size = size.trim();
               int index = size.indexOf(58);
               ts = null;
               String close = null;
               ts = size.substring(0, index).trim().toLowerCase();
               close = size.substring(index + 1).trim();
               if(ts.equals("set-cookie")) {
                  this.setCookies.add(close);
               }

               if(!ts.equals("set-cookie") && !this.responseHeaders.containsKey(ts)) {
                  this.responseHeaders.put(ts, close);
               }

               if(!ts.equals("transfer-encoding") && !ts.equals("content-length") && !ts.equals("connection") && !ts.equals("proxy-connection")) {
                  if(ts.equals("www-authenticate") || ts.equals("proxy-authenticate")) {
                     cn = close.toLowerCase();
                     if(cn.indexOf("ntlm") != -1 || cn.indexOf("negotiate") != -1 || cn.indexOf("kerberos") != -1) {
                        sessionBasedAuth1 = true;
                     }
                  }

                  resBuf1.append(size + "\r\n");
               }
            }
         }

         int tmpOut = tmpFile.indexOf(58);
         responseStatus = tmpFile.substring(0, tmpOut).trim().toLowerCase();
         String tmpIn = tmpFile.substring(tmpOut + 1).trim();
         if(responseStatus.equals("cookie")) {
            this.cookies.add(tmpIn);
         }

         if(!responseStatus.equals("cookie") && !responseStatus.equals("host") && !this.requestHeaders.containsKey(responseStatus)) {
            this.requestHeaders.put(responseStatus, tmpIn);
         }

         if(responseStatus.equals("accept-encoding") && tmpIn.indexOf("gzip") != -1) {
            compress = true;
         }

         if(!responseStatus.equals("accept-encoding") && !responseStatus.equals("proxy-connection") && !responseStatus.equals("expect")) {
            reqBuf.append(tmpFile + "\r\n");
         }
      }
   }

   boolean interceptDownload(int code) {
      if(code != 206 && code != 200) {
         return false;
      } else {
         try {
            System.out.println("intercept download ============================");
            boolean e = false;
            if(this.config.siteList != null) {
               for(int contentType = 0; contentType < this.config.siteList.length; ++contentType) {
                  System.out.println(this.host + " " + this.config.siteList[contentType]);
                  if(this.host.equals(this.config.siteList[contentType])) {
                     e = true;
                     break;
                  }
               }
            }

            if(this.requestHeaders.get("xdm-skip") != null) {
               e = true;
            }

            if(e) {
               return false;
            }

            String var12 = (String)this.responseHeaders.get("content-type");
            if(var12 != null) {
               var12 = var12.toLowerCase();
            } else {
               var12 = "application/octet-stream";
            }

            boolean isStreamingVideo = false;

            String referer;
            try {
               referer = (String)this.requestHeaders.get("referer");
               if(!XDMUtil.isNullOrEmpty(referer)) {
                  String size = XDMUtil.getFileName(referer).toLowerCase();
                  if(size.endsWith(".swf")) {
                     isStreamingVideo = true;
                  }
               }
            } catch (Exception var10) {
               ;
            }

            if(!var12.toLowerCase().startsWith("text/") && !var12.startsWith("application/x-shockwave-flash")) {
               if((this.matchedFileType(this.url) || this.matchedFileType(getFileNameFromDisposition((String)this.responseHeaders.get("content-disposition")))) && (code == 206 || code == 200) && this.requestHeaders.get("range") == null && !isStreamingVideo) {
                  DownloadIntercepterInfo var13 = new DownloadIntercepterInfo();
                  var13.url = this.url;
                  var13.cookies = this.cookies;
                  var13.referer = (String)this.requestHeaders.get("referer");
                  var13.ua = (String)this.requestHeaders.get("user-agent");
                  if(this.mgr != null) {
                     this.mgr.interceptDownload(var13);
                  }

                  return true;
               }

               referer = (String)this.requestHeaders.get("referer");
               long var14 = -1L;

               try {
                  var14 = Long.parseLong((String)this.responseHeaders.get("content-length"));
               } catch (Exception var9) {
                  ;
               }

               var12 = var12.split(";")[0];
               if((var12.startsWith("video/") || var12.startsWith("audio/")) && this.media_grabber != null) {
                  String ct = MIMEUtil.getFileExt(var12);
                  this.media_grabber.mediaCaptured(XDMUtil.getFileName(this.url), this.url, ct, var14 > 0L?XDMUtil.getFormattedLength((double)var14):null, referer, (String)this.requestHeaders.get("user-agent"), this.cookies);
               }
            }
         } catch (Exception var11) {
            var11.printStackTrace();
         }

         return false;
      }
   }

   private void handlePARAMRequest(InputStream s) {
      try {
         HashMap e = new HashMap();

         while(true) {
            String url2 = HTTPUtil.readLine(s);
            String cks;
            String min;
            if(XDMUtil.isNullOrEmpty(url2)) {
               url2 = (String)e.get("url");
               String rfr1 = (String)e.get("referer");
               cks = (String)e.get("cookies");
               min = (String)e.get("min");
               if(!XDMUtil.isNullOrEmpty(url2)) {
                  DownloadIntercepterInfo info = new DownloadIntercepterInfo();
                  info.url = url2;
                  info.referer = rfr1;
                  ArrayList cl = null;
                  if(!XDMUtil.isNullOrEmpty(cks)) {
                     cl = new ArrayList();
                     cl.add(cks);
                  }

                  info.cookies = cl;
                  this.mgr.interceptDownload(info);
               }

               if(min == null && this.mgr != null) {
                  this.mgr.restoreWindow();
               }
               break;
            }

            int rfr = url2.indexOf(":");
            if(rfr > 0) {
               cks = url2.substring(0, rfr);
               min = url2.substring(rfr + 1).trim();
               e.put(cks, min);
            }
         }
      } catch (Exception var9) {
         var9.printStackTrace();
      }

   }

   void parseYTPage(String fn, boolean compressed, String referer, String ua, ArrayList cookies) {
      try {
         Object e = new FileInputStream(fn);
         if(compressed) {
            e = new GZIPInputStream((InputStream)e);
         }

         BufferedReader r = new BufferedReader(new InputStreamReader((InputStream)e));
         String title = HTMLTitleParser.GetTitleFromPage(r);
         title = HTMLEscapeUtil.escapeHTMLLine(title);
         title = XDMUtil.createSafeFileName(title);
         ((InputStream)e).close();
         e = new FileInputStream(fn);
         if(compressed) {
            e = new GZIPInputStream((InputStream)e);
         }

         this.grabVideo((InputStream)e, title, ua, referer, cookies);
      } catch (Exception var9) {
         var9.printStackTrace();
      }

   }

   private InputStream createChunkedStreamsIfAvailable(HashMap headers, InputStream s) throws IOException {
      String tenc = (String)headers.get("transfer-encoding");
      Object in2 = s;
      if(tenc != null) {
         if(!tenc.toLowerCase().equals("chunked")) {
            throw new IOException("Transfer Encoding not supported: " + tenc);
         }

         in2 = new ChunkedInputStream(s);
      }

      return (InputStream)in2;
   }

   private void establishConnection(WebProxy proxy) throws IOException {
      if(proxy == null) {
         this.sockRemote = this.connectSocket(this.host, this.port);
      } else {
         String proxyHost = proxy.host;
         int proxyPort = proxy.port;
         this.sockRemote = this.connectSocket(proxyHost, proxyPort);
      }

      if(this.sockRemote == null) {
         throw new IOException("Socket connection failed");
      }
   }

   private void writeString(OutputStream s, String str) throws IOException {
      byte[] buf = HTTPUtil.getBytes(str);
      s.write(buf, 0, buf.length);
   }

   private void copyData(InputStream inStream, OutputStream outStream, long size, String ts, Socket inputSock, FileOutputStream fileStream) throws IOException {
      long rem = size;

      while(true) {
         int l;
         if(size >= 0L) {
            if(rem < 1L) {
               outStream.flush();
               break;
            }

            l = (int)(rem > 8192L?8192L:rem);
            int chunk = inStream.read(this.b, 0, l);
            if(chunk == -1) {
               outStream.flush();
               break;
            }

            rem -= (long)chunk;
            outStream.write(this.b, 0, chunk);
            if(fileStream != null) {
               fileStream.write(this.b, 0, chunk);
            }

            outStream.flush();
         } else {
            byte[] buf;
            if(ts != null) {
               if(!ts.toLowerCase().equals("chunked")) {
                  throw new IOException("Unsupported transfer-encoding: " + ts);
               }

               l = inStream.read(this.b, 0, this.b.length);
               if(l == -1) {
                  buf = HTTPUtil.getBytes("0\r\n\r\n");
                  outStream.write(buf, 0, buf.length);
                  outStream.flush();
                  if(inStream instanceof ChunkedInputStream) {
                     inStream.close();
                  }
                  break;
               }

               String chunk2 = Integer.toHexString(l) + "\r\n";
               buf = HTTPUtil.getBytes(chunk2);
               outStream.write(buf, 0, buf.length);
               outStream.write(this.b, 0, l);
               buf = HTTPUtil.getBytes("\r\n");
               outStream.write(buf, 0, buf.length);
               outStream.flush();
               if(fileStream != null) {
                  fileStream.write(this.b, 0, l);
               }
            } else {
               if(inStream.available() < 1) {
                  buf = HTTPUtil.getBytes("0\r\n\r\n");
                  outStream.write(buf, 0, buf.length);
                  outStream.flush();
                  this.closeAll();
                  this.stop = true;
                  return;
               }

               long l1 = (long)(inStream.available() <= 8192?inStream.available():8192);
               int x = inStream.read(this.b, 0, (int)l1);
               if(x == -1) {
                  buf = HTTPUtil.getBytes("0\r\n\r\n");
                  outStream.write(buf, 0, buf.length);
                  outStream.flush();
                  this.closeAll();
                  this.stop = true;
                  return;
               }

               String chunk1 = Integer.toHexString(x) + "\r\n";
               buf = HTTPUtil.getBytes(chunk1);
               outStream.write(buf, 0, buf.length);
               outStream.write(this.b, 0, x);
               buf = HTTPUtil.getBytes("\r\n");
               outStream.write(buf, 0, buf.length);
               outStream.flush();
               if(fileStream != null) {
                  fileStream.write(this.b, 0, x);
               }
            }
         }

         try {
            Thread.sleep(1L);
         } catch (Exception var15) {
            ;
         }
      }

   }

   private boolean crackURL(String url) {
      try {
         URL e = new URL(url);
         this.host = e.getHost();
         this.port = e.getPort();
         if(this.port < 0) {
            if(url.startsWith("https://")) {
               this.port = 443;
            }

            if(url.startsWith("http://")) {
               this.port = 80;
            }
         }

         this.path = e.getPath();
         if(e.getQuery() != null) {
            this.path = this.path + "?" + e.getQuery();
         }

         if(XDMUtil.isNullOrEmpty(this.path)) {
            this.path = "/";
         }

         return true;
      } catch (Exception var3) {
         return false;
      }
   }

   private void closeAll() {
      this.stop = true;
      if(this.responseHeaders != null) {
         this.responseHeaders.clear();
         this.responseHeaders = null;
      }

      if(this.setCookies != null) {
         this.setCookies.clear();
         this.setCookies = null;
      }

      if(this.sockRemote != null) {
         try {
            this.sockRemote.close();
         } catch (Exception var3) {
            ;
         }
      }

      this.sockRemote = null;

      try {
         this.sockLocal.close();
         this.sockLocal = null;
      } catch (Exception var2) {
         ;
      }

   }

   private Socket connectSocket(String host, int port) throws IOException {
      System.out.println("Connecting to: " + host + ":" + port);
      Socket sock = new Socket();
      sock.setTcpNoDelay(true);
      sock.setSoTimeout(this.config.timeout * 1000);
      sock.setReceiveBufferSize(this.config.tcpBuf * 1000);
      sock.connect(new InetSocketAddress(host, port));
      return sock;
   }

   private boolean grabVideo(InputStream tmpStreamIn, String title, String ua, String referer, ArrayList cookies) {
      ArrayList list = null;

      try {
         JSONParser e = new JSONParser();
         list = e.list(tmpStreamIn);
         if(list != null && list.size() >= 1) {
            for(int i = 0; i < list.size(); ++i) {
               YTVideoInfo info = (YTVideoInfo)list.get(i);
               if(info != null && this.media_grabber != null) {
                  this.media_grabber.mediaCaptured(title, info.url, info.type, info.itag, referer, ua, cookies);
               }
            }

            return true;
         } else {
            return false;
         }
      } catch (Exception var10) {
         var10.printStackTrace();
         return false;
      }
   }

   boolean matchedFileType(String url) {
      if(url == null) {
         return false;
      } else {
         String file = null;

         try {
            file = XDMUtil.getFileName(url);
         } catch (Exception var5) {
            file = url.replace("\"", "");
         }

         for(int i = 0; i < this.config.fileTypes.length; ++i) {
            String type = this.config.fileTypes[i].trim();
            if(type.length() >= 1 && file.toUpperCase().endsWith("." + type)) {
               return true;
            }
         }

         return false;
      }
   }

   public static String getFileNameFromDisposition(String content_disposition) {
      if(content_disposition == null) {
         return "";
      } else {
         try {
            String[] e = content_disposition.split(";");

            for(int i = 0; i < e.length; ++i) {
               String param = e[i].trim().toLowerCase();
               if(param.startsWith("filename")) {
                  int index = param.indexOf(61);
                  String file = param.substring(index + 1);
                  file = file.replace("\"", "");
                  return file;
               }
            }

            return "";
         } catch (Exception var6) {
            return "";
         }
      }
   }

   private void handleLocalRequest(String url, InputStream in, OutputStream out) {
      try {
         long e = 0L;

         while(true) {
            String sb = HTTPUtil.readLine(in);
            if(XDMUtil.isNullOrEmpty(sb)) {
               if(url.contains("/proxy.pac")) {
                  byte[] var11 = this.proxy_pac.getBytes();
                  out.write(("HTTP/1.1 200 OK\r\nContent-Type: application/x-ns-proxy-autoconfig\r\nConnection: Close\r\nContent-Length: " + var11.length + "\r\n\r\n").getBytes());
                  out.write(var11);
                  out.flush();
               } else if(url.contains("/xdmff.xpi")) {
                  InputStream var10 = this.getClass().getResourceAsStream("/ext/xdmff.xpi");
                  out.write("HTTP/1.1 200 OK\r\nContent-Type: application/x-xpinstall\r\nTransfer-Encoding: chunked\r\n\r\n".getBytes());
                  byte[] itag = new byte[8192];

                  while(true) {
                     int fmt = var10.read(itag);
                     if(fmt == -1) {
                        out.write("0\r\n\r\n".getBytes());
                        return;
                     }

                     out.write((fmt + "\r\n").getBytes());
                     out.write(itag, 0, fmt);
                     out.write("\r\n".getBytes());
                  }
               } else if(url.contains("/chrome")) {
                  sb = this.config.tabletMode?"tablet":"desktop";
                  out.write(("HTTP/1.1 200 OK\r\nContent-Length: " + sb.length() + "\r\n" + "Connection: close\r\n\r\n" + sb).getBytes());
                  out.flush();
               } else if(url.contains("/yt_dash_request") && e > 0L) {
                  StringBuilder var12 = new StringBuilder();

                  for(int var13 = 0; (long)var13 < e; ++var13) {
                     var12.append((char)in.read());
                  }

                  if(var12.indexOf("range=") < 0) {
                     String var14 = XDMUtil.getType(var12.toString());
                     String var15 = YouTubeFmtMap.getFMTInfo(var14);
                     if(this.media_grabber != null) {
                        this.media_grabber.mediaCaptured(XDMUtil.getFileName(var12.toString()), var12.toString(), var15, (String)null, (String)null, (String)null, (ArrayList)null);
                     }
                  }
               }
               break;
            }

            if(sb.toLowerCase().startsWith("content-length")) {
               e = (long)Integer.parseInt(sb.split(":")[1].trim());
            }
         }
      } catch (Exception var9) {
         var9.printStackTrace();
      }

   }
}
