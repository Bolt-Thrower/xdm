package org.sdg.xdman.core.common.ftp;

import java.net.URI;
import java.net.UnknownHostException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPHTTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.sdg.xdman.core.common.Authenticator;
import org.sdg.xdman.core.common.Connection;
import org.sdg.xdman.core.common.ConnectionManager;
import org.sdg.xdman.core.common.Credential;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.gui.StringResource;

public class FTPConnection extends Connection {
   XDMConfig config;
   String dir;
   String file;
   int port;
   String host;
   String path;
   FTPClient client;
   int count;
   int errorCode;

   public FTPConnection(String url, String fileName, long startOff, long length, long contentLength, int timeout, ConnectionManager mgr, Object lock, Credential c, XDMConfig config) {
      super(url, fileName, startOff, length, contentLength, mgr, lock);
      this.credential = c;
      this.config = config;
   }

   public FTPConnection(Connection.State state, int timeout, ConnectionManager mgr, Object lock, Credential c, XDMConfig config) {
      super(state, timeout, mgr, lock);
      this.credential = c;
      this.config = config;
   }

   public void close() {
      this.msg(Boolean.valueOf(this.stop));
      this.msg(this.stop?"STOP ":"Releasing all resource...");

      try {
         this.out.close();
      } catch (Exception var3) {
         ;
      }

      try {
         this.client.disconnect();
      } catch (Exception var2) {
         ;
      }

      this.msg("Releasing all resource...done");
      this.message = StringResource.getString("DISCONNECT");
   }

   public boolean connect() {
      try {
         URI e = new URI(this.url);
         this.host = e.getHost();
         this.port = e.getPort();
         this.path = e.getPath();
         this.msg("Path: " + this.path);
         this.getPath();
      } catch (Exception var8) {
         var8.printStackTrace();
         return false;
      }

      this.status = 10;

      while(true) {
         this.read = 0L;
         this.clen = 0L;
         if(this.stop) {
            this.close();
            break;
         }

         this.chkPause();

         try {
            if(this.file != null && this.file.length() >= 1) {
               this.message = StringResource.getString("CONNECTING");
               this.mgr.updated();
               this.msg("Connecting...");
               if(this.length > 0L && this.startOff + this.downloaded - (this.startOff + this.length - 1L) > 0L) {
                  this.mgr.donwloadComplete(this);
                  return true;
               }

               if(this.config.useFtpProxy) {
                  String var13 = this.config.ftpProxyHost;
                  int files = this.config.ftpProxyPort;
                  String i = this.config.ftpUser;
                  String f = this.config.ftpPass;
                  boolean proxyAuth = false;
                  if(i != null && i.length() >= 1 && f != null && f.length() >= 1) {
                     proxyAuth = true;
                  }

                  if(proxyAuth) {
                     this.msg("Authenticating with: " + i + ":" + f);
                     this.client = new FTPHTTPClient(var13, files, i, f);
                  } else {
                     this.client = new FTPHTTPClient(var13, files);
                  }
               } else {
                  this.client = new FTPClient();
               }

               this.msg("Connecting to..." + this.url);
               this.message = StringResource.getString("CONNECTING");
               this.mgr.updated();
               this.client.setConnectTimeout(this.config.timeout * 1000);
               this.client.setDataTimeout(this.config.timeout * 1000);
               if(this.port > 0) {
                  this.client.connect(this.host, this.port);
               } else {
                  this.client.connect(this.host);
               }

               if(this.stop) {
                  this.close();
               } else {
                  this.chkPause();
                  if(this.credential == null) {
                     this.credential = Authenticator.getInstance().getCredential(this.host);
                  }

                  this.message = "Logging in...";
                  this.mgr.updated();
                  if(this.credential != null) {
                     this.msg("Loggin in with specifig user/pass");
                     this.client.login(this.credential.user, this.credential.pass);
                  } else {
                     this.msg("Loggin in with anonymous user/pass");
                     this.client.login("anonymous", "anonymous");
                  }

                  if(this.stop) {
                     this.close();
                  } else {
                     this.chkPause();
                     int var14 = this.client.getReplyCode();
                     this.msg("Reply: " + this.client.getReplyString());
                     if(!FTPReply.isPositiveCompletion(var14)) {
                        this.credential = this.mgr.getCreditential();
                        if(this.credential == null) {
                           throw new Error(this.client.getReplyString());
                        }

                        throw new IllegalArgumentException(this.client.getReplyString());
                     }

                     this.msg("Switching to binary mode...");
                     this.client.setFileType(2);
                     var14 = this.client.getReplyCode();
                     this.msg("Reply: " + this.client.getReplyString());
                     if(!FTPReply.isPositiveCompletion(var14)) {
                        throw new Error("Binary transfer not supported by server");
                     }

                     this.msg("Entering passive mode: " + this.dir);
                     this.message = "PASVr...";
                     this.mgr.updated();
                     this.client.enterLocalPassiveMode();
                     if(this.stop) {
                        this.close();
                     } else {
                        this.msg("Changing working dir to: " + this.dir);
                        this.message = "Change dir...";
                        this.mgr.updated();
                        this.client.changeWorkingDirectory(this.dir);
                        if(this.stop) {
                           this.close();
                        } else {
                           this.chkPause();
                           var14 = this.client.getReplyCode();
                           this.msg("Reply: " + this.client.getReplyString());
                           if(!FTPReply.isPositiveCompletion(var14)) {
                              throw new Error("Could not switch to the sprecified directory.");
                           }

                           if(this.length > 0L) {
                              long var16 = this.startOff + this.downloaded;
                              this.msg("Sending range");
                              this.message = "Sending REST...";
                              this.mgr.updated();
                              this.client.setRestartOffset(var16);
                           } else {
                              this.msg("Listing files");
                              FTPFile[] var15 = this.client.listFiles(this.dir);
                              this.chkPause();
                              var14 = this.client.getReplyCode();
                              this.msg("Reply: " + this.client.getReplyString());
                              if(!FTPReply.isPositiveCompletion(var14)) {
                                 throw new Error("File information could not be retrived.");
                              }

                              for(int var17 = 0; var17 < var15.length; ++var17) {
                                 FTPFile var18 = var15[var17];
                                 this.msg(var18.toString() + " Name: " + var18.getName());
                                 if(var18.getName().equals(this.file)) {
                                    this.length = var18.getSize();
                                    this.msg("Length found: " + this.length);
                                    break;
                                 }
                              }

                              this.msg("Listing files...done");
                           }

                           if(this.stop) {
                              this.close();
                           } else {
                              this.chkPause();
                              var14 = this.client.getReplyCode();
                              this.msg("Reply: " + this.client.getReplyString());
                              if(!FTPReply.isPositiveCompletion(var14)) {
                                 throw new Error("Server does not support resume feature.");
                              }

                              this.msg("SEND GET...");
                              this.message = "Send GET...";
                              this.msg("SEND GET...Done");
                              if(this.stop) {
                                 this.close();
                              } else {
                                 this.chkPause();
                                 this.message = "Parsing response...";
                                 this.mgr.updated();
                                 this.chkPause();
                                 this.message = "Opening data connection...";
                                 this.mgr.updated();
                                 this.msg("Opening Stream for: " + this.file);
                                 this.in = this.client.retrieveFileStream(this.file);
                                 this.msg("Data connection mode: " + this.client.getDataConnectionMode());
                                 if(this.in == null) {
                                    throw new Error("Server did not sent any data.");
                                 }

                                 if(!this.stop) {
                                    this.chkPause();
                                    this.status = 20;
                                    this.message = "Downloading...";
                                    this.buf = new byte[8192];
                                    this.mgr.updated();
                                    this.msg("Notify...");
                                    this.msg("Going to call connected()...");
                                    this.mgr.connected(this);
                                    this.msg("Returned from connected()");
                                    return true;
                                 }

                                 this.close();
                              }
                           }
                        }
                     }
                  }
               }
               break;
            }

            throw new Error("No file to download");
         } catch (IllegalArgumentException var9) {
            this.message = "ReConnecting...";
            this.mgr.updated();
            this.msg(var9);
            var9.printStackTrace();
            this.close();
            if(this.stop) {
               this.close();
               break;
            }

            this.chkPause();
         } catch (UnknownHostException var10) {
            this.message = "Connecting...";
            this.mgr.updated();
            this.msg(var10);
            var10.printStackTrace();
            this.close();
            if(this.count > 10) {
               this.status = 30;
               this.errorCode = 0;
               this.lastError = "Host not found";
               break;
            }

            if(this.stop) {
               this.close();
               break;
            }

            this.chkPause();
            this.msg("Sleeping 2 sec");
            this.message = "Disconnect";

            try {
               Thread.sleep(2000L);
               this.chkPause();
            } catch (Exception var7) {
               ;
            }

            this.message = "Connecting...";
            this.mgr.updated();
            ++this.count;
         } catch (Exception var11) {
            this.message = "ReConnecting...";
            this.mgr.updated();
            this.msg(var11);
            var11.printStackTrace();
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
            } catch (Exception var6) {
               ;
            }
         } catch (Error var12) {
            this.message = "Invalid response.";
            this.errorCode = 5;
            this.lastError = var12.getMessage();
            this.mgr.updated();
            this.msg(var12);
            var12.printStackTrace();
            this.close();
            this.status = 30;
            if(this.lastError == null || this.lastError.length() < 1) {
               this.lastError = "Invalid response from server";
            }
            break;
         }

         this.msg("Remaining " + (this.length - this.downloaded));
         if(this.stop) {
            this.close();
            break;
         }

         this.chkPause();
      }

      this.msg("Exiting connect");
      if(!this.stop) {
         this.message = "disconnect.";
         this.mgr.updated();
         this.mgr.failed(this.lastError, this.errorCode);
      }

      return false;
   }

   public boolean isEOF() {
      return false;
   }

   void getPath() {
      int pos = this.path.lastIndexOf("/");
      if(pos >= 0) {
         this.dir = this.path.substring(0, pos);
         if(this.dir.length() < 1) {
            this.dir = "/";
         }

         if(pos != this.path.length() - 1) {
            if(pos < this.path.length() - 1) {
               this.file = this.path.substring(pos + 1);
            }

         }
      }
   }
}
