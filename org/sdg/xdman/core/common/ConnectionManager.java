package org.sdg.xdman.core.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.sdg.xdman.core.common.ChunkFileInfo;
import org.sdg.xdman.core.common.Connection;
import org.sdg.xdman.core.common.Credential;
import org.sdg.xdman.core.common.DownloadInfo;
import org.sdg.xdman.core.common.DownloadProgressListener;
import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.core.common.HelpListener;
import org.sdg.xdman.core.common.HelperConnection;
import org.sdg.xdman.core.common.IXDMConstants;
import org.sdg.xdman.core.common.UnsupportedProtocolException;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.core.common.ftp.FTPConnection;
import org.sdg.xdman.core.common.http.HttpConnection;
import org.sdg.xdman.gui.AuthDialog;
import org.sdg.xdman.gui.StringResource;
import org.sdg.xdman.util.MIMEUtil;
import org.sdg.xdman.util.XDMUtil;

public class ConnectionManager implements IXDMConstants, HelpListener {
   UUID id;
   public ArrayList cookieList;
   public String referer;
   public String userAgent;
   boolean init = false;
   long length = -1L;
   long downloaded = 0L;
   String fileName = null;
   String finalFileName;
   private String url;
   List list = new ArrayList();
   int MAX_CHUNK = 8;
   int timeout = '\uea60';
   List fileList = new ArrayList();
   private String tempdir;
   private String destdir;
   int fileCounter = 0;
   Object lock = new Object();
   String statefileName = ".state";
   File statefile;
   public boolean stop = false;
   long startTime;
   long prevdownload;
   String resume_support = "---";
   String status = StringResource.getString("CONNECTING");
   int cnc = 0;
   public int state = 40;
   String category;
   File prevtempdir;
   boolean assembling = false;
   long assemble_len = 0L;
   XDMConfig config;
   int MIN_CHUNK_SZ = 8192;
   HelperConnection helper;
   public DownloadProgressListener prgListener;
   public DownloadStateListner dwnListener;
   public boolean overwrite = false;
   long pdwnld = 0L;
   long ptime = 0L;
   float prate = 0.0F;
   long lastTime = 0L;
   long lastSavedTime = 0L;
   boolean done = false;
   long endTime;
   MIMEUtil mimeutil;
   private Credential creditential;

   public UUID getUUID() {
      return this.id;
   }

   public void setProgressListener(DownloadProgressListener pl) {
      this.prgListener = pl;
   }

   public void setTempdir(String tempdir) {
      this.tempdir = tempdir;
      this.prevtempdir = new File(tempdir);
   }

   public String getTempdir() {
      return this.tempdir;
   }

   public void setFileName(String file) {
      this.finalFileName = file;
   }

   public String getFileName() {
      return this.finalFileName;
   }

   public void setDestdir(String destdir) {
      this.destdir = destdir;
   }

   public String getDestdir() {
      return this.destdir;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getUrl() {
      return this.url;
   }

   public ConnectionManager(UUID id, String url, String file, String destdir, String tempdir, String userAgent, String referer, ArrayList cookies, XDMConfig config) {
      this.id = id;
      this.setUrl(url);
      this.setDestdir(destdir);
      this.setTempdir(tempdir);
      this.statefile = new File(tempdir, this.statefileName);
      this.fileName = file;
      this.finalFileName = file;
      this.category = XDMUtil.findCategory(this.fileName);
      this.config = config;
      this.userAgent = userAgent;
      this.referer = referer;
      if(cookies != null) {
         this.cookieList = new ArrayList();
         this.cookieList.addAll(cookies);
      }

      this.timeout = config.timeout;
      this.MAX_CHUNK = config.maxConn;
   }

   private String getUniqueFileName(String dir, String f) {
      return XDMUtil.getUniqueFileName(dir, f);
   }

   private File getUniqueFolderName() {
      return new File(this.tempdir, UUID.randomUUID().toString());
   }

   public synchronized void connected(Connection c) throws UnsupportedProtocolException {
      if(++this.cnc > 1) {
         this.resume_support = StringResource.getString("YES");
      }

      if(!this.init) {
         boolean redirected = !this.url.equals(c.url);
         this.setUrl(c.url);
         this.length = c.getLength();
         if(c.content_disposition != null) {
            this.fileName = this.getFileName(c.content_disposition);
            this.finalFileName = this.fileName;
         } else {
            if(redirected) {
               this.fileName = this.getFileName(this.getUrl());
               this.fileName = this.getUniqueFileName(this.destdir, this.fileName);
            }

            System.out.println("Checking auto extension...");
            this.checkExt(c.content_type);
            if(!this.overwrite) {
               this.fileName = this.getUniqueFileName(this.destdir, this.fileName);
            }

            this.finalFileName = this.fileName;
         }

         if(!this.overwrite) {
            this.fileName = this.getUniqueFileName(this.destdir, this.fileName);
         }

         File f = this.getUniqueFolderName();
         f.mkdirs();
         this.tempdir = f.getAbsolutePath();
         this.statefile = new File(this.tempdir, ".state");
         c.fileName = (new File(this.tempdir, 0 + this.fileName)).toString();
         this.category = XDMUtil.findCategory(this.fileName);
         this.init = true;
         int state2 = this.state;
         this.state = 70;
         this.updated();
         this.state = state2;
         this.updated();
      }

      this.saveState();
      this.createChunk();
      this.status = StringResource.getString("DOWNLOADING");
   }

   public synchronized void failed(String error, int errorCode) {
      if(this.state != 30) {
         if(this.cnc < 2) {
            this.resume_support = StringResource.getString("NO");
         }

         for(int dwn = 0; dwn < this.list.size(); ++dwn) {
            if(((Connection)this.list.get(dwn)).status != 30) {
               return;
            }
         }

         if(errorCode == 3) {
            long var6 = 0L;

            for(int i = 0; i < this.list.size(); ++i) {
               var6 += ((Connection)this.list.get(i)).downloaded;
            }

            if(var6 > 0L) {
               errorCode = 2;
            }
         }

         if(errorCode == 5) {
            this.status = StringResource.getString(errMsg[errorCode]);
         } else {
            this.status = StringResource.getString(errMsg[errorCode]);
         }

         this.state = 30;
         this.updated();
         if(this.dwnListener != null) {
            this.dwnListener.downloadFailed(this.getUUID());
         }

      }
   }

   public void updated() {
      long time = System.currentTimeMillis();
      if(this.state == 50 || this.state == 40 || this.state == 30 || this.state == 70) {
         this.lastTime = 0L;
         this.lastSavedTime = 0L;
      }

      if(time - this.lastTime > 1000L) {
         this.lastTime = time;

         try {
            DownloadInfo e = new DownloadInfo();
            e.id = this.id;
            e.path = new File(this.tempdir);
            e.url = this.getUrl();
            e.file = this.finalFileName;
            int sz = this.list.size() + this.fileList.size();
            long[] len = new long[sz];
            long[] start = new long[sz];
            long[] dwn = new long[sz];
            String stat = this.status;
            int k = 0;
            long dwnld = 0L;
            e.rlen = this.length;

            int diff;
            for(diff = 0; diff < this.fileList.size(); ++diff) {
               ChunkFileInfo c = (ChunkFileInfo)this.fileList.get(diff);
               len[k] = c.len;
               start[k] = c.start;
               dwn[k] = c.len;
               dwnld += dwn[k];
               ++k;
            }

            e.stat = new String[this.list.size()];
            e.dwnld = new String[this.list.size()];

            for(diff = 0; diff < this.list.size(); ++diff) {
               Connection var20 = (Connection)this.list.get(diff);
               len[k] = var20.getLength();
               start[k] = var20.getStartOff();
               dwn[k] = var20.getDownloaded();
               dwnld += dwn[k];
               e.stat[diff] = var20.message;
               e.dwnld[diff] = XDMUtil.getFormattedLength((double)var20.getDownloaded());
               ++k;
            }

            if(dwnld > this.length) {
               dwnld = this.length;
            }

            e.rdwn = dwnld;
            e.len = len;
            e.startoff = start;
            e.dwn = dwn;
            e.length = XDMUtil.getFormattedLength((double)this.length);
            e.downloaded = XDMUtil.getFormattedLength((double)dwnld);
            long var21 = dwnld - this.prevdownload;
            long dt = time - this.startTime;
            float rte;
            if(dt != 0L) {
               rte = (float)var21 / (float)dt * 1000.0F;
               e.eta = XDMUtil.getETA((double)(this.length - dwnld), rte);
            }

            rte = 0.0F;

            for(int prg = 0; prg < this.list.size(); ++prg) {
               rte += ((Connection)this.list.get(prg)).rate;
            }

            e.speed = XDMUtil.getFormattedLength((double)rte) + "/S";
            if(this.assembling) {
               e.eta = "";
               e.speed = "";
            }

            e.resume = this.resume_support;
            e.status = stat;
            float var22 = 0.0F;
            if(!this.assembling) {
               if(this.length <= 0L) {
                  var22 = 0.0F;
               } else {
                  var22 = (float)dwnld / (float)this.length * 100.0F;
               }
            } else if(this.length > 0L) {
               var22 = (float)this.assemble_len / (float)this.length * 100.0F;
            }

            e.prg = (int)var22;
            e.progress = String.format("%.1f", new Object[]{Float.valueOf(var22)});
            e.state = this.state;
            e.msg = this.status;
            e.category = this.category;
            e.tempdir = this.tempdir;
            if(this.prgListener != null && this.prgListener.isValidWindow()) {
               this.prgListener.update(e);
            }

            if(this.dwnListener != null) {
               if(this.state == 70) {
                  this.dwnListener.downloadConfirmed(this.id, e);
               } else {
                  this.dwnListener.updateManager(this.id, e);
               }
            }
         } catch (Exception var19) {
            var19.printStackTrace();
         }
      }

      if(time - this.lastSavedTime > 5000L) {
         try {
            if(this.state != 50) {
               this.saveState();
            }
         } catch (Exception var18) {
            ;
         }

         this.lastSavedTime = time;
      }

   }

   void connect(String url, String fileName, long startOff, long length, long contentLength, int timeout) throws UnsupportedProtocolException {
      if(url.startsWith("ftp://")) {
         FTPConnection c = new FTPConnection(url, fileName, startOff, length, contentLength, timeout, this, this.lock, this.creditential, this.config);
         this.list.add(c);
         c.start();
      } else {
         if(!url.startsWith("http")) {
            throw new UnsupportedProtocolException();
         }

         HttpConnection c1 = new HttpConnection(url, fileName, startOff, length, contentLength, this, this.lock, this.creditential, this.config);
         this.list.add(c1);
         c1.start();
      }

   }

   String getFileName(String url) {
      String file = null;

      try {
         file = XDMUtil.getFileName(url);
      } catch (Exception var4) {
         ;
      }

      if(file == null || file.length() < 1) {
         file = "FILE";
      }

      return file;
   }

   synchronized void createChunk() throws UnsupportedProtocolException {
      if(!this.stop) {
         if(this.downloaded < this.length) {
            try {
               int lc;
               for(lc = 0; lc < this.list.size(); ++lc) {
                  Connection lc_len = (Connection)this.list.get(lc);
                  if(lc_len.status == 30) {
                     try {
                        lc_len.start();
                     } catch (Exception var20) {
                        var20.printStackTrace();
                     }
                  }
               }

               if(this.list.size() >= this.MAX_CHUNK) {
                  return;
               }

               for(lc = 0; lc < this.list.size(); ++lc) {
                  ((Connection)this.list.get(lc)).pause();
               }

               Connection var23 = this.findChunk();
               if(var23 == null) {
                  return;
               }

               long var22 = var23.getLength();
               long lc_dwn = var23.getDownloaded();
               long lc_off = var23.getStartOff();
               long rem = var22 - lc_dwn;
               if(rem >= (long)this.MIN_CHUNK_SZ) {
                  long startOff = lc_off + var22 - rem / 2L;
                  long len = rem / 2L;
                  long clen = this.length - startOff;
                  if(var22 > lc_dwn) {
                     var23.setLength(var22 - rem / 2L);
                     var23.msg("Changing length to " + var23.getLength() + " from " + var22);
                     this.connect(this.getUrl(), (new File(this.getTempdir(), startOff + this.fileName)).toString(), startOff, len, clen, this.config.timeout);
                  }

                  return;
               }
            } finally {
               for(int i = 0; i < this.list.size(); ++i) {
                  ((Connection)this.list.get(i)).resume();
               }

            }

         }
      }
   }

   Connection findChunk() {
      long len = -9999L;
      Connection c = null;

      for(int i = 0; i < this.list.size(); ++i) {
         Connection cl = (Connection)this.list.get(i);
         long diff = cl.getLength() - cl.getDownloaded();
         if(diff > len && diff > 0L) {
            len = diff;
            c = cl;
         }
      }

      if(len < (long)this.MIN_CHUNK_SZ) {
         return null;
      } else {
         return c;
      }
   }

   Connection findNextChunk(long end) {
      for(int i = 0; i < this.list.size(); ++i) {
         Connection cl = (Connection)this.list.get(i);
         if(cl.getStartOff() == end && cl.status != 20 && cl.getDownloaded() == 0L) {
            return cl;
         }
      }

      return null;
   }

   public synchronized boolean donwloadComplete(Connection c) throws UnsupportedProtocolException {
      if(this.downloaded >= this.length) {
         return true;
      } else {
         this.saveState();
         if(this.done) {
            c.close();
            return true;
         } else if(this.length < 0L) {
            c.close();
            this.list.remove(c);
            this.fileList.add(new ChunkFileInfo(c.fileName, c.getStartOff(), c.getDownloaded()));
            this.downloaded += c.getDownloaded();
            this.length = this.downloaded;
            this.checkFinished();
            return true;
         } else {
            Connection nc = this.findNextChunk(c.getStartOff() + c.getLength());
            if(nc == null) {
               try {
                  c.out.close();
               } catch (Exception var4) {
                  ;
               }

               this.fileList.add(new ChunkFileInfo(c.fileName, c.getStartOff(), c.getLength()));
               this.downloaded += c.getLength();
               this.list.remove(c);
               boolean finish = this.checkFinished();
               if(finish) {
                  System.out.println("FINISHED");
               } else {
                  System.out.println("NOT FINISHED");
                  this.createChunk();
                  this.needsHelp();
               }

               return true;
            } else {
               this.list.remove(nc);
               nc.stop();
               if(this.checkFinished()) {
                  return true;
               } else {
                  c.setLength(c.getLength() + nc.getLength());
                  this.createChunk();
                  return false;
               }
            }
         }
      }
   }

   boolean checkFinished() {
      System.out.println("***IN CHECK FINISHED***");
      if(this.list.size() == 0) {
         if(this.length > -1L) {
            if(this.downloaded >= this.length) {
               this.assemble();
               this.done = true;
               System.out.println("*************************check finished returned true");
               return true;
            }

            System.out.println("DOWNLOADED:= " + this.downloaded + " LENGTH:= " + this.length);
         } else {
            System.out.println("CHECK_FINISHED_LENGTH<0");
         }
      } else {
         System.out.println("checkfinished: " + this.list.size());
      }

      return false;
   }

   void assemble() {
      this.status = StringResource.getString("ASSEMBLE_MSG");
      if(this.helper != null) {
         this.helper.stop();
      }

      this.assembling = true;
      this.updated();
      Collections.sort(this.fileList, new ChunkFileInfo());
      if(!this.stop) {
         try {
            File e = new File(this.destdir);
            if(!e.exists()) {
               e.mkdirs();
            }

            if(!this.overwrite) {
               this.finalFileName = this.getUniqueFileName(this.destdir, this.finalFileName);
            }

            File outFile = new File(this.getDestdir(), this.finalFileName);
            FileOutputStream out = new FileOutputStream(outFile);
            int state2 = this.state;
            this.state = 70;
            this.updated();
            this.state = state2;
            this.updated();
            FileInputStream in = null;
            int count = 0;
            long MB = 1048576L;

            int t;
            ChunkFileInfo f;
            for(t = 0; t < this.fileList.size(); ++t) {
               f = (ChunkFileInfo)this.fileList.get(t);
               System.out.println("Reading..." + f.file);
               in = new FileInputStream(f.file);
               long i = f.len;
               byte[] buf = new byte[65536];

               do {
                  int x = (int)(i > (long)buf.length?(long)buf.length:i);
                  int r = in.read(buf, 0, x);
                  if(r == -1) {
                     throw new IllegalArgumentException("Assemble EOF");
                  }

                  out.write(buf, 0, r);
                  i -= (long)r;
                  this.assemble_len += (long)r;
                  count += r;
                  if((long)count > MB) {
                     this.updated();
                     count = 0;
                  }
               } while(i != 0L);

               in.close();
            }

            out.close();
            t = 0;

            while(true) {
               if(t >= this.fileList.size()) {
                  File var17 = new File(this.tempdir);
                  File[] var18 = var17.listFiles();

                  for(int var19 = 0; var19 < var18.length; ++var19) {
                     var18[var19].delete();
                  }

                  var17.delete();
                  break;
               }

               f = (ChunkFileInfo)this.fileList.get(t);
               System.out.println("Deleting: " + f.file + " " + (new File(f.file)).delete());
               ++t;
            }
         } catch (Exception var16) {
            var16.printStackTrace();
            this.status = StringResource.getString("ASSEMBLE_ERR");
            this.state = 30;
            this.updated();
            if(this.dwnListener != null) {
               this.dwnListener.downloadFailed(this.id);
            }

            return;
         }

         this.status = StringResource.getString("DOWNLOAD_COMPLETE");
         this.state = 50;
         this.updated();
         if(this.dwnListener != null) {
            this.dwnListener.downloadComplete(this.id);
         }

      }
   }

   public void start() throws UnsupportedProtocolException {
      this.state = 20;
      this.startTime = System.currentTimeMillis();
      this.connect(this.getUrl(), "FILE", 0L, -1L, -1L, this.config.timeout);
   }

   public synchronized void saveDownload() {
      long time = System.currentTimeMillis();
      if(time - this.endTime > 5000L) {
         this.saveState();
         this.endTime = time;
      }

   }

   public synchronized void saveState() {
      try {
         if(!this.init) {
            return;
         }

         ObjectOutputStream e = new ObjectOutputStream(new FileOutputStream(this.statefile));
         e.writeUTF(this.getUrl());
         e.writeObject(this.creditential);
         e.writeUTF(this.fileName);
         e.writeUTF(this.finalFileName);
         e.writeUTF(this.getDestdir());
         e.writeLong(this.length);
         e.writeLong(this.downloaded);
         e.writeInt(this.fileList.size());

         int dwn;
         for(dwn = 0; dwn < this.fileList.size(); ++dwn) {
            e.writeObject(this.fileList.get(dwn));
         }

         e.writeInt(this.list.size());

         for(dwn = 0; dwn < this.list.size(); ++dwn) {
            Connection c = (Connection)this.list.get(dwn);
            e.writeObject(c.getState());
         }

         long var11 = 0L;

         int i;
         for(i = 0; i < this.fileList.size(); ++i) {
            ChunkFileInfo c1 = (ChunkFileInfo)this.fileList.get(i);
            var11 += c1.len;
         }

         for(i = 0; i < this.list.size(); ++i) {
            Connection var12 = (Connection)this.list.get(i);
            long len = var12.getLength();
            long dwnl = var12.getDownloaded();
            if(len < dwnl) {
               var11 += len;
            } else {
               var11 += dwnl;
            }
         }

         e.writeLong(var11);
         e.writeBoolean(Boolean.valueOf(this.overwrite).booleanValue());
         e.close();
      } catch (Exception var10) {
         var10.printStackTrace();
      }

   }

   public synchronized boolean restoreState() {
      try {
         ObjectInputStream e = new ObjectInputStream(new FileInputStream(this.statefile));
         String u = e.readUTF();
         if(this.url != null && this.url.length() >= 1) {
            u = this.url;
         }

         this.setUrl(u);
         this.creditential = (Credential)e.readObject();
         this.fileName = e.readUTF();
         String finalFileName2 = e.readUTF();
         if(this.finalFileName == null) {
            this.finalFileName = finalFileName2;
         }

         String destdir2 = e.readUTF();
         if(this.destdir == null) {
            this.setDestdir(destdir2);
         }

         this.length = e.readLong();
         this.downloaded = e.readLong();
         int sz = e.readInt();
         this.fileList = new ArrayList();

         int i;
         for(i = 0; i < sz; ++i) {
            this.fileList.add((ChunkFileInfo)e.readObject());
         }

         sz = e.readInt();
         this.list = new ArrayList();

         for(i = 0; i < sz; ++i) {
            Connection.State state = (Connection.State)e.readObject();
            state.url = this.url;
            if(this.url.startsWith("http")) {
               this.list.add(new HttpConnection(state, this.timeout, this, this.lock, this.creditential, this.config));
            } else {
               this.list.add(new FTPConnection(state, this.timeout, this, this.lock, this.creditential, this.config));
            }
         }

         this.prevdownload = e.readLong();
         this.overwrite = e.readBoolean();
         e.close();
         return true;
      } catch (Exception var8) {
         var8.printStackTrace();
         System.out.println(var8.getMessage());
         return false;
      }
   }

   public void stop() {
      this.stop = true;
      this.status = StringResource.getString("STOPPED");

      for(int i = 0; i < this.list.size(); ++i) {
         Connection c = (Connection)this.list.get(i);
         c.stop();

         try {
            c.out.close();
         } catch (Exception var4) {
            ;
         }
      }

      this.saveState();
      this.state = 40;
      this.lastTime = 0L;
      this.updated();
      if(this.dwnListener != null) {
         this.dwnListener.downloadPaused(this.id);
      }

   }

   public void resume() {
      if(this.state != 20) {
         this.state = 20;
         this.startTime = System.currentTimeMillis();
         this.init = this.restoreState();
         if(this.init) {
            if(this.list.size() < 1) {
               long i = 0L;

               for(int i1 = 0; i1 < this.fileList.size(); ++i1) {
                  i += ((ChunkFileInfo)this.fileList.get(i1)).len;
               }

               if(i == this.length) {
                  (new Thread() {
                     public void run() {
                        ConnectionManager.this.assemble();
                     }
                  }).start();
               }
            } else {
               for(int var4 = 0; var4 < this.list.size(); ++var4) {
                  Connection c = (Connection)this.list.get(var4);
                  c.start();
               }
            }
         } else {
            this.state = 30;
            this.status = StringResource.getString("PART_ERR");
            this.updated();
            if(this.dwnListener != null) {
               this.dwnListener.downloadFailed(this.id);
            }
         }

      }
   }

   void checkExt(String mime) {
      System.out.println("MIME-TYPE: " + mime);
      if(mime != null) {
         if(mime.equals("text/html") && !this.fileName.endsWith("html") && !this.fileName.endsWith(".htm")) {
            this.fileName = this.fileName + ".html";
         } else {
            if(this.fileName.indexOf(".") < 0) {
               String ext = MIMEUtil.getFileExt(mime);
               System.out.println("EXTENSION: " + ext);
               if(ext != null) {
                  this.fileName = this.fileName + "." + ext;
               }
            }

         }
      }
   }

   public void setTimeOut(int tout) {
      this.timeout = tout * 1000;
   }

   public void setMaxConn(int c) {
      this.MAX_CHUNK = c;
   }

   public void setCredential(String user, String pass) {
      this.creditential = new Credential();
      this.creditential.user = user;
      this.creditential.pass = pass;
   }

   public synchronized Credential getCreditential() {
      System.out.println("Ask for credentials");
      if(this.creditential != null) {
         return this.creditential;
      } else {
         String[] a = AuthDialog.getAuth();
         if(a == null) {
            this.creditential = null;
            return null;
         } else {
            this.creditential = new Credential();
            this.creditential.host = this.url;
            this.creditential.pass = a[1];
            this.creditential.user = a[0];
            return this.creditential;
         }
      }
   }

   public void helpComplete(Object invoker, Object data) {
      if(invoker instanceof HelperConnection) {
         System.out.println("Helper Connection Complete...");
         HelperConnection hc = (HelperConnection)invoker;
         if(this.state == 50) {
            System.out.println("Helper returing because MGR state is complete.");
            return;
         }

         if(this.list.size() != 1) {
            System.out.println("Helper returing because list size!=1: " + this.list.size());
            return;
         }

         Connection c = (Connection)this.list.get(0);
         if(c == hc.c) {
            if(hc.stop) {
               System.out.println("Helper return as stopped");
               return;
            }

            if(c.status == 50) {
               System.out.println("Helper returning because thread state COMPLETE");
               return;
            }

            c.stop();

            try {
               System.out.println("Replace stream");
               c.out.close();
               RandomAccessFile e = new RandomAccessFile(hc.fileName, "rw");
               e.write(hc.out.toByteArray());
               e.close();
               this.fileList.add(new ChunkFileInfo(hc.fileName, hc.start, hc.length));
               System.out.println("Finalized download: HELPER");
               this.downloaded += hc.length;
               System.out.println("HELPER CALLING DWNCOMPLETE");
               System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&HELPER USED AND DONE");
               c.length = hc.start - c.startOff;
               c.downloaded = c.length;
               c.status = 50;
               System.out.println("DWN: " + this.downloaded + " LEN: " + this.length);
               if(this.donwloadComplete(c)) {
                  System.out.println("Helper Thread complete.");
               } else {
                  System.out.println("Mgr return false ERROR!!!");
               }
            } catch (Exception var6) {
               System.out.println(var6);
               var6.printStackTrace();
            }
         } else {
            System.out.println("Helper is returning because reference is not same");
         }
      }

   }

   boolean needsHelp() {
      if(this.list.size() == 1) {
         Connection c = (Connection)this.list.get(0);
         if(c instanceof HttpConnection) {
            HttpConnection hc = (HttpConnection)c;
            if(hc.length + this.downloaded == this.length) {
               long start = hc.startOff + hc.downloaded;
               long dwn = hc.downloaded;
               long len = hc.length - dwn;
               if(len - dwn < (long)this.MIN_CHUNK_SZ) {
                  if(this.helper != null) {
                     System.out.println("HELPER!=NULL");
                     return false;
                  } else if(start == hc.startOff) {
                     System.out.println("Helper return as startoff same");
                     return false;
                  } else {
                     this.helper = new HelperConnection(this.config, start, len, this.url, this, hc, (new File(this.getTempdir(), start + this.fileName)).toString(), hc.credential, this);
                     this.helper.start();
                     System.out.println("Helper Connection Started");
                     return true;
                  }
               } else {
                  System.out.println("HELPER Chunk SZ>32K");
                  return false;
               }
            } else {
               System.out.println("HELPER SIZE DOES NOT MATCH");
               return false;
            }
         } else {
            System.out.println("Helper reference ERROR");
            return false;
         }
      } else {
         System.out.println("Helper return: List size!=1 : " + this.list.size());
         return false;
      }
   }

   public static void main(String[] a) throws Exception {
      String url = "http://localhost:8080/x.zip";
      URI uri = new URI("http://localhost:8080/x.zip");
      System.out.println(uri.getRawPath() + "?" + uri.getQuery() + " " + uri.getScheme());
      System.out.println(uri.getRawSchemeSpecificPart());
      XDMConfig config = XDMConfig.load((File)null);
      ConnectionManager mgr = new ConnectionManager((UUID)null, url, "x.zip", "g:/tst", "g:/tst", (String)null, (String)null, (ArrayList)null, config);
      mgr.start();
   }
}
