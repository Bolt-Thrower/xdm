package org.sdg.xdman.core.common;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import org.sdg.xdman.core.common.ConnectionManager;
import org.sdg.xdman.core.common.Credential;
import org.sdg.xdman.core.common.IXDMConstants;
import org.sdg.xdman.gui.StringResource;

public abstract class Connection implements Runnable, IXDMConstants {
   protected Credential credential;
   public long read;
   public long clen;
   public String content_type;
   public String content_disposition;
   public String url;
   public String fileName;
   protected volatile long length = -1L;
   protected volatile long contentLength;
   protected volatile long downloaded = 0L;
   protected volatile long startOff;
   public int status;
   protected boolean stop = false;
   protected ConnectionManager mgr;
   protected Object lock;
   protected boolean pause = false;
   protected String lastError = null;
   protected InputStream in;
   public RandomAccessFile out;
   protected byte[] buf = new byte[8192];
   protected Thread t;
   protected String message;
   public float rate;
   public long time;
   long oldRead;
   long _startTime = System.currentTimeMillis();
   long _bytesCount = 0L;

   public Connection.State getState() {
      return new Connection.State(this.status, this.fileName, this.length, this.contentLength, this.downloaded, this.startOff, this.url);
   }

   public void setState(Connection.State t) {
      this.status = t.stat;
      this.fileName = t.fileName;
      this.length = t.length;
      this.contentLength = t.contentLength;
      this.downloaded = t.downloaded;
      this.startOff = t.startOff;
      this.url = t.url;
   }

   public long getLength() {
      return this.length;
   }

   public void setLength(long length) {
      this.length = length;
   }

   public long getContentLength() {
      return this.contentLength;
   }

   public void setContentLength(long contentLength) {
      this.contentLength = contentLength;
   }

   public long getDownloaded() {
      return this.downloaded;
   }

   public void setDownloaded(long downloaded) {
      this.downloaded = downloaded;
   }

   public long getStartOff() {
      return this.startOff;
   }

   public void setStartOff(long startOff) {
      this.startOff = startOff;
   }

   protected Connection(String url, String fileName, long startOff, long length, long contentLength, ConnectionManager mgr, Object lock) {
      this.url = url;
      this.fileName = fileName;
      this.length = length;
      this.contentLength = contentLength;
      this.mgr = mgr;
      this.startOff = startOff;
      this.lock = lock;
   }

   protected Connection(Connection.State state, int timeout, ConnectionManager mgr, Object lock) {
      this.setState(state);
      this.mgr = mgr;
      this.lock = lock;
      this.stop = false;
      this.pause = false;
   }

   public abstract boolean connect();

   public void run() {
      while(true) {
         if(!this.download()) {
            if(this.stop) {
               this.close();
            } else {
               this.chkPause();
               if(!this.connect()) {
                  this.close();
                  return;
               }

               if(!this.stop) {
                  this.chkPause();
                  continue;
               }

               this.close();
            }
         }

         return;
      }
   }

   public abstract void close();

   public void start() {
      this.t = new Thread(this);
      this.t.start();
   }

   public void stop() {
      this.stop = true;
      this.t.interrupt();
      this.message = StringResource.getString("DISCONNECT");
   }

   public void pause() {
      this.pause = true;
   }

   protected void chkPause() {
      if(this.pause) {
         try {
            this.msg("Pausing...");
            Object e = this.lock;
            synchronized(this.lock) {
               this.lock.wait();
            }

            this.msg("Resuming...");
         } catch (Exception var3) {
            this.msg(var3);
            var3.printStackTrace();
         }

      }
   }

   public void resume() {
      this.pause = false;
      Object var1 = this.lock;
      synchronized(this.lock) {
         this.lock.notify();
      }
   }

   public boolean download() {
      this.read = 0L;
      this.msg("To download: " + this.length);
      if(this.stop) {
         this.close();
         this.msg("Returning because STOP");
         return true;
      } else {
         this.chkPause();
         if(this.status != 20) {
            this.msg("Returning because NOT DOWNLOADING");
            return false;
         } else if(this.in == null) {
            this.msg("Returning because IN IS NULL");
            return false;
         } else {
            try {
               this.msg("Init download...");
               this.msg("Opening file: " + this.fileName);
               this.out = new RandomAccessFile(this.fileName, "rw");
               this.out.seek(this.downloaded);

               while(!this.stop) {
                  this.message = StringResource.getString("DOWNLOADING");
                  this.mgr.updated();
                  this.chkPause();
                  if(this.length > -1L && this.downloaded >= this.length) {
                     if(this.downloaded > this.length) {
                        this.read = 0L;
                     }

                     this.msg("Download complete before: " + this.downloaded + " / " + this.length);
                     this.msg("Going to call downloadComplete()...");
                     this.status = 50;
                     if(this.mgr == null) {
                        return true;
                     }

                     if(this.mgr.donwloadComplete(this)) {
                        this.msg("called downloadComplete()");
                        this.message = StringResource.getString("DOWNLOAD_COMPLETE");
                        this.close();
                        return true;
                     }

                     this.status = 20;
                     this.msg("called downloadComplete()...");
                     this.msg("Download complete after: " + this.downloaded + " / " + this.length);
                  }

                  int len = (int)(this.length - this.downloaded);
                  int e;
                  if(len < this.buf.length && len > 0) {
                     e = this.in.read(this.buf, 0, len);
                  } else {
                     e = this.in.read(this.buf, 0, this.buf.length);
                  }

                  if(e != -1) {
                     this.read += (long)e;
                  }

                  if(this.stop) {
                     this.close();
                     return true;
                  }

                  this.chkPause();
                  if(e == -1) {
                     if(this.length > -1L) {
                        if(this.downloaded >= this.length) {
                           this.status = 50;
                           this.mgr.donwloadComplete(this);
                           this.close();
                           return true;
                        }

                        throw new Exception("Unexpected End Of Stream: " + this.downloaded + " / " + this.length);
                     }

                     this.status = 50;
                     this.mgr.donwloadComplete(this);
                     return true;
                  }

                  this.chkPause();
                  this.out.write(this.buf, 0, e);
                  this.downloaded += (long)e;
                  this._bytesCount += (long)e;

                  long currentTime;
                  long tdiff;
                  long diff;
                  try {
                     if(e > 0 && this.mgr != null && this.mgr.config.maxBPS > 0) {
                        currentTime = (long)(this.mgr.config.maxBPS / this.mgr.config.maxConn);
                        if(currentTime > 0L) {
                           tdiff = System.currentTimeMillis() - this._startTime;
                           if(tdiff > 0L) {
                              diff = this._bytesCount * 1000L / tdiff;
                              if(diff > currentTime) {
                                 long _waitTime = this._bytesCount * 1000L / currentTime;
                                 long _toWait = _waitTime - tdiff;
                                 if(_toWait > 1L) {
                                    try {
                                       Thread.sleep((long)((int)_toWait));
                                    } catch (Exception var15) {
                                       ;
                                    }

                                    long _diff = System.currentTimeMillis() - this._startTime;
                                    if(_diff > 1000L) {
                                       this._bytesCount = 0L;
                                       this._startTime = System.currentTimeMillis();
                                    }
                                 }
                              }
                           }
                        }
                     }
                  } catch (Exception var16) {
                     var16.printStackTrace();
                  }

                  currentTime = System.currentTimeMillis();
                  tdiff = currentTime - this.time;
                  diff = this.read - this.oldRead;
                  if((int)(tdiff / 1000L) > 0) {
                     this.rate = (float)diff / (float)tdiff * 1000.0F;
                     this.oldRead = this.read;
                     this.time = currentTime;
                  }

                  if(this.stop) {
                     this.close();
                     return true;
                  }

                  this.message = StringResource.getString("DOWNLOADING");
                  this.chkPause();
                  this.mgr.updated();
                  this.mgr.saveDownload();
               }

               this.close();
               return true;
            } catch (Exception var17) {
               if(this.length < 0L) {
                  this.close();
                  return true;
               } else {
                  this.msg(var17);
                  var17.printStackTrace();
                  if(this.stop) {
                     this.close();
                     return true;
                  } else {
                     this.chkPause();
                     this.close();
                     System.out.println("Returing false@@@@Error in : " + var17);
                     return false;
                  }
               }
            }
         }
      }
   }

   public abstract boolean isEOF();

   public void msg(Object o) {
      System.out.println(this.startOff + " :  " + o.toString());
   }

   public static class State implements Serializable {
      private static final long serialVersionUID = 4156081526363598564L;
      int stat;
      String fileName;
      String url;
      long length;
      long contentLength;
      long downloaded;
      long startOff;

      public State(int stat, String fileName, long length, long contentLength, long downloaded, long startOff, String url) {
         this.stat = stat;
         this.fileName = fileName;
         this.length = length;
         this.contentLength = contentLength;
         this.downloaded = downloaded;
         this.startOff = startOff;
         this.url = url;
      }
   }
}
