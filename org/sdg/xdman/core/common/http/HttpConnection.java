package org.sdg.xdman.core.common.http;

import java.io.IOException;
import java.net.UnknownHostException;
import org.sdg.xdman.core.common.AuthenticationException;
import org.sdg.xdman.core.common.Authenticator;
import org.sdg.xdman.core.common.Connection;
import org.sdg.xdman.core.common.ConnectionManager;
import org.sdg.xdman.core.common.Credential;
import org.sdg.xdman.core.common.InvalidContentException;
import org.sdg.xdman.core.common.InvalidReplyException;
import org.sdg.xdman.core.common.ResumeNotSupportedException;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.core.common.http.XDMHttpClient2;
import org.sdg.xdman.gui.StringResource;

public class HttpConnection extends Connection {
   public XDMHttpClient2 client;
   boolean clientSet = false;
   int count = 0;
   XDMConfig config;
   public int errorCode;

   public HttpConnection(String url, String fileName, long startOff, long length, long contentLength, ConnectionManager mgr, Object lock, Credential c, XDMConfig config) {
      super(url, fileName, startOff, length, contentLength, mgr, lock);
      this.credential = c;
      this.config = config;
      this.stop = false;
   }

   public HttpConnection(Connection.State state, int timeout, ConnectionManager mgr, Object lock, Credential c, XDMConfig config) {
      super(state, timeout, mgr, lock);
      this.credential = c;
      this.config = config;
      this.stop = false;
   }

   public boolean connect() {
      this.status = 10;
      int code = 0;

      while(true) {
         this.read = 0L;
         this.clen = 0L;
         if(this.stop) {
            this.close();
            break;
         }

         this.chkPause();

         try {
            this.message = StringResource.getString("CONNECTING");
            this.mgr.updated();
            this.msg("Connecting...");
            if(this.length > 0L && this.startOff + this.downloaded - (this.startOff + this.length - 1L) > 0L) {
               this.mgr.donwloadComplete(this);
               return true;
            }

            this.client = new XDMHttpClient2(this.config);
            this.msg("Connecting to..." + this.url);
            this.client.connect(this.url);
            this.count = 0;
            this.client.addCookies(this.mgr.cookieList);
            this.client.addRequestHeaders("referer", this.mgr.referer);
            this.client.addRequestHeaders("user-agent", this.mgr.userAgent);
            if(this.length > 0L) {
               this.client.addRequestHeaders("Range", "bytes=" + (this.startOff + this.downloaded) + "-");
            } else {
               this.client.addRequestHeaders("Range", "bytes=0-");
            }

            if(this.credential == null) {
               this.credential = Authenticator.getInstance().getCredential(this.client.host);
            }

            if(this.credential != null) {
               this.client.user = this.credential.user;
               this.client.pass = this.credential.pass;
            }

            this.msg("SEND GET...");
            this.message = StringResource.getString("SEND_GET");
            this.client.sendRequest();
            this.count = 0;
            this.msg("SEND GET...Done");
            if(this.stop) {
               this.close();
            } else {
               this.chkPause();
               this.message = StringResource.getString("PARSING_RESPONSE");
               this.mgr.updated();
               code = this.client.getResponseCode();
               this.msg("Response code: " + code);
               if(code >= 300 && code < 400) {
                  this.client.close();
                  if(this.length < 0L) {
                     this.url = this.client.getResponseHeader("location");
                     if(!this.url.startsWith("http")) {
                        this.url = "http://" + this.client.getHostString() + "/" + this.url;
                     }

                     this.url = this.url.replace(" ", "%20");
                     throw new IllegalAccessException("Redirecting to: " + this.url);
                  }

                  throw new InvalidReplyException(code, "Invalid redirect");
               }

               if(code != 200 && code != 206 && code != 416 && code != 413 && code != 401 && code != 408 && code != 407 && code != 503) {
                  throw new InvalidReplyException(code, "Invalid response from server");
               }

               if(code == 503) {
                  throw new Exception();
               }

               if(code == 401) {
                  this.credential = this.mgr.getCreditential();
                  if(this.credential == null) {
                     throw new AuthenticationException(this.client.statusLine);
                  }

                  throw new IllegalArgumentException("Unauthorized");
               }

               if(code == 407) {
                  throw new AuthenticationException(this.client.statusLine);
               }

               if(this.startOff + this.downloaded > 0L && code != 206) {
                  throw new ResumeNotSupportedException("Server does not support partial content(Resume feature)");
               }

               long e = this.client.getContentLength();
               this.clen = e;
               if(this.length < 0L) {
                  try {
                     this.length = e;
                     this.contentLength = e;
                  } catch (Exception var9) {
                     ;
                  }
               }

               this.msg("Expected contentlength: " + this.contentLength + " found " + e + " " + this.length);
               if(this.contentLength != -1L && this.length != -1L && this.contentLength != e && this.contentLength - this.downloaded != e) {
                  throw new InvalidContentException("Invalid Content Length: Expected: " + this.contentLength + " but got: " + e);
               }

               if(!this.stop) {
                  this.chkPause();
                  this.in = this.client.in;
                  this.status = 20;
                  this.message = StringResource.getString("DOWNLOADING");
                  this.buf = new byte[this.config.tcpBuf];
                  this.mgr.updated();
                  this.msg("Notify...");
                  this.msg("Going to call connected()...");
                  this.content_type = this.client.getResponseHeader("content-type");

                  try {
                     if(this.content_type.indexOf(";") >= 0) {
                        this.content_type = this.content_type.split(";")[0].trim();
                     }
                  } catch (Exception var5) {
                     ;
                  }

                  System.out.println("Final content-type: " + this.content_type);
                  this.content_disposition = this.client.getContentName();
                  this.mgr.connected(this);
                  this.msg("Returned from connected()");
                  System.out.println(this.client.sock.isConnected() + " " + this.client.sock.isClosed());
                  return true;
               }

               this.close();
            }
            break;
         } catch (UnknownHostException var10) {
            this.message = StringResource.getString("DISCONNECT");
            this.mgr.updated();
            this.msg(var10);
            var10.printStackTrace();
            this.close();
            if(this.count > 5) {
               this.status = 30;
               this.lastError = "Host not found";
               this.errorCode = 0;
               break;
            }

            if(this.stop) {
               this.close();
               break;
            }

            this.chkPause();
            this.msg("Sleeping 5 sec");
            this.message = StringResource.getString("DISCONNECT");

            try {
               Thread.sleep(5000L);
               this.chkPause();
            } catch (Exception var8) {
               ;
            }

            this.message = StringResource.getString("CONNECTING");
            this.mgr.updated();
            ++this.count;
         } catch (IllegalAccessException var11) {
            this.message = "Redirecting...";
            this.mgr.updated();
            this.msg(var11);
            var11.printStackTrace();
            this.close();
            if(this.stop) {
               this.close();
               break;
            }

            this.chkPause();
         } catch (IllegalArgumentException var12) {
            this.message = "Authenticating...";
            this.mgr.updated();
            this.msg(var12);
            var12.printStackTrace();
            this.close();
            if(this.stop) {
               this.close();
               break;
            }

            this.chkPause();
         } catch (AuthenticationException var13) {
            this.message = "Authenticating...";
            this.mgr.updated();
            this.msg(var13);
            var13.printStackTrace();
            this.close();
            if(this.stop) {
               this.close();
               break;
            }

            this.chkPause();
            if(this.client != null) {
               code = this.client.getResponseCode();
            }

            if(code == 401) {
               this.credential = this.mgr.getCreditential();
            }

            if(this.credential == null) {
               this.message = var13.getMessage();
               this.errorCode = 0;
               this.mgr.updated();
               this.msg(var13);
               var13.printStackTrace();
               this.close();
               this.status = 30;
               this.lastError = "Content size invalid";
               break;
            }
         } catch (InvalidContentException var14) {
            this.message = var14.getMessage();
            this.errorCode = 1;
            this.mgr.updated();
            this.msg(var14);
            var14.printStackTrace();
            this.close();
            this.status = 30;
            this.lastError = "Content size invalid";
            break;
         } catch (InvalidReplyException var15) {
            this.message = var15.getMessage();
            this.errorCode = 3;
            this.mgr.updated();
            this.msg(var15);
            var15.printStackTrace();
            this.close();
            this.status = 30;
            this.lastError = this.client.statusLine;
            break;
         } catch (ResumeNotSupportedException var16) {
            this.message = var16.getMessage();
            this.errorCode = 4;
            this.mgr.updated();
            this.msg(var16);
            var16.printStackTrace();
            this.close();
            this.status = 30;
            this.lastError = "Resume not supported";
            break;
         } catch (Exception var17) {
            this.message = StringResource.getString("CONNECTING");
            this.mgr.updated();
            this.msg(var17);
            var17.printStackTrace();
            this.close();
            if(this.stop) {
               this.close();
               break;
            }

            this.chkPause();
            this.msg("Sleeping 2 sec");

            try {
               Thread.sleep(2000L);
               this.chkPause();
            } catch (Exception var7) {
               ;
            }
         } catch (Error var18) {
            this.message = "Not a valid response";
            this.errorCode = 5;
            this.mgr.updated();
            this.msg(var18);
            var18.printStackTrace();
            this.close();
            this.status = 30;
            this.lastError = "Not a valid response";
            break;
         }

         this.msg("Remaining " + (this.length - this.downloaded));
         if(this.stop) {
            this.close();
            break;
         }

         this.chkPause();
         this.client = null;
         this.clientSet = false;

         try {
            this.client.close();
         } catch (Exception var6) {
            ;
         }
      }

      this.msg("Exiting connect");
      if(!this.stop) {
         this.status = 30;
         this.message = StringResource.getString("DISCONNECT");
         this.mgr.updated();
         this.mgr.failed(this.lastError + " ", this.errorCode);
      }

      return false;
   }

   long getContentLengthFromRange(String r) {
      try {
         String e = r.split("/")[0].split("-")[1];
         return Long.parseLong(e) + 1L;
      } catch (Exception var3) {
         return -1L;
      }
   }

   public void close() {
      System.out.println("*************************STOP********************");
      this.msg(Boolean.valueOf(this.stop));
      this.msg(this.stop?"STOP ":"Releasing all resource...");

      try {
         this.out.close();
      } catch (Exception var3) {
         ;
      }

      try {
         this.client.close();
      } catch (Exception var2) {
         ;
      }

      this.msg("Releasing all resource...done");
      this.message = StringResource.getString("DISCONNECT");
   }

   public boolean isEOF() {
      try {
         System.out.println("IS EOF: " + this.in.read());
      } catch (IOException var2) {
         var2.printStackTrace();
      }

      return this.read == this.clen && this.read > 0L;
   }
}
