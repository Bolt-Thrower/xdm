package org.sdg.xdman.core.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Observable;

public class XDMConfig extends Observable implements Serializable {
   private static final long serialVersionUID = -459274444783377361L;
   public static boolean hasTray = false;
   public String lafClass;
   public String lafName;
   File file;
   public String jarPath;
   public boolean useHttpProxy;
   public boolean useFtpProxy;
   public boolean useHttpsProxy;
   public String httpProxyHost;
   public String httpsProxyHost;
   public String ftpProxyHost;
   public int httpProxyPort;
   public int httpsProxyPort;
   public int ftpProxyPort;
   public String httpUser;
   public String httpPass;
   public String httpsUser;
   public String httpsPass;
   public String ftpUser;
   public String ftpPass;
   public String proxyUser;
   public String proxyPass;
   public boolean showDownloadPrgDlg = true;
   public boolean showDownloadCompleteDlg = true;
   public boolean showDownloadBox = false;
   public static final int PROMPT = 3;
   public static final int AUTO_RENAME = 0;
   public static final int RESUME = 2;
   public static final int OVERWRITE = 1;
   public int duplicateLinkAction = 3;
   public int maxConn = 8;
   public int timeout = 60;
   public String destdir;
   public String tempdir;
   public boolean executeCmd = false;
   public boolean hungUp = false;
   public boolean halt = false;
   public boolean antivir = false;
   public String cmdTxt;
   public String hungUpTxt;
   public String haltTxt;
   public String antivirTxt;
   public int antidrop = 0;
   public int hungdrop = 0;
   public int haltdrop = 0;
   public String ntDomain = "";
   public final String[] defaultFileTypes = new String[]{"3GP", "7Z", "AAC", "ACE", "AIF", "ARJ", "ASF", "AVI", "BIN", "BZ2", "EXE", "DEB", "DMG", "GZ", "GZIP", "ISO", "LZH", "M4A", "M4V", "MOV", "MP3", "MP4", "MPA", "MPE", "MPEG", "MPG", "MSI", "MSU", "OGG", "PDF", "PLJ", "PPS", "PPT", "QT", "RA", "RAR", "RM", "RPM", "SEA", "SIT", "SITX", "TAR", "TIF", "TIFF", "WAV", "WMA", "WMV", "Z", "ZIP", "JAR", "TLZ", "TBZ2", "TXZ", "XZ", "CBZ", "PKG", "RUN"};
   public String[] fileTypes;
   public boolean schedule;
   public Date startDate;
   public Date endDate;
   public boolean allowbrowser;
   public transient int port;
   public int tcpBuf;
   public boolean compress;
   public boolean attachProxy;
   public boolean autostart;
   public int version;
   public static boolean dubugMode = false;
   public String[] siteList;
   public int mwX;
   public int mwY;
   public int mwW;
   public int mwH;
   public int mgX;
   public int mgY;
   public int mgW;
   public int mgH;
   public int dbX;
   public int dbY;
   public boolean tabletMode;
   public static transient boolean sortAsc = false;
   public static transient int sortField = 0;
   public int maxBPS;

   public String getDefaultShutdownCommand() {
      return "dbus-send --system --print-reply --dest=\"org.freedesktop.ConsoleKit\" /org/freedesktop/ConsoleKit/Manager org.freedesktop.ConsoleKit.Manager.Stop";
   }

   public String getDefaultDisconnectCommand() {
      return "";
   }

   public XDMConfig(File f) {
      this.fileTypes = this.defaultFileTypes;
      this.allowbrowser = false;
      this.port = 9614;
      this.tcpBuf = 64;
      this.compress = false;
      this.attachProxy = false;
      this.autostart = false;
      this.version = 0;
      this.siteList = new String[]{"www.facebook.com"};
      this.tabletMode = false;
      this.maxBPS = 0;
      this.file = f;
      this.haltTxt = this.getDefaultShutdownCommand();
      this.hungUpTxt = this.getDefaultDisconnectCommand();
      this.mwX = this.mwY = this.mwW = this.mwH = this.mgX = this.mgY = this.mgW = this.mgH = this.dbX = this.dbY = -1;
   }

   public void save() {
      ObjectOutputStream out = null;

      try {
         out = new ObjectOutputStream(new FileOutputStream(this.file));
         out.writeObject(this);
         out.close();
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      this.setChanged();
      this.notifyObservers();
   }

   public static XDMConfig load(File file) {
      ObjectInputStream in = null;

      try {
         in = new ObjectInputStream(new FileInputStream(file));
         XDMConfig e = (XDMConfig)in.readObject();
         e.port = 9614;
         e.version = 4;
         return e;
      } catch (Exception var5) {
         var5.printStackTrace();

         try {
            in.close();
         } catch (Exception var4) {
            ;
         }

         return new XDMConfig(file);
      }
   }
}
