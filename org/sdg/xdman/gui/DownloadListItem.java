package org.sdg.xdman.gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.Icon;
import org.sdg.xdman.core.common.ConnectionManager;
import org.sdg.xdman.core.common.DownloadInfo;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.gui.StringResource;
import org.sdg.xdman.gui.XDMDownloadWindow;
import org.sdg.xdman.util.XDMUtil;

public class DownloadListItem implements Serializable, Comparable {
   private static final long serialVersionUID = -4925098929484510725L;
   ArrayList cookies;
   String filename;
   boolean q;
   int state;
   UUID id;
   String status;
   String timeleft;
   String transferrate;
   String lasttry;
   String description;
   String dateadded;
   String saveto;
   String type;
   String url;
   String size;
   String tempdir = "";
   String referer;
   String userAgent;
   String user;
   String pass;
   transient Icon icon;
   transient ConnectionManager mgr;
   transient XDMDownloadWindow window;
   long date_created = System.currentTimeMillis();
   public String sdwnld;
   public String sprg;
   public String prime_url;
   public String second_url;
   public String prime_file;
   public String second_file;
   public String prime_dest_dir;
   public String second_dest_dir;
   public boolean secondary_done;
   public boolean isDASH = false;
   public long totalDASHSize;
   public boolean isMerging;
   public long length;
   public long dwnld;

   void updateData(DownloadInfo info) {
      this.status = info.status;
      this.timeleft = info.eta;
      this.transferrate = info.speed;
      this.url = info.url;
      this.size = info.length;
      this.type = info.category;
      this.state = info.state;
      this.sdwnld = info.downloaded;
      this.sprg = info.progress;
      if(info.state != 50 && info.state != 40 && info.state != 30) {
         this.status = this.status + " " + this.sprg + "% of " + this.size;
      } else {
         this.mgr = null;
         this.window = null;
         if(info.state == 50) {
            this.q = false;
            this.status = StringResource.getString("DOWNLOAD_COMPLETE") + " " + this.size;
         } else {
            this.status = StringResource.getString("STOPPED") + " " + this.size;
         }
      }

   }

   public int compareTo(Object it) {
      DownloadListItem item = (DownloadListItem)it;
      int c = XDMConfig.sortField;
      switch(c) {
      case 0:
         return this.date_created > item.date_created?1:-1;
      case 1:
         return XDMUtil.nvl(this.size).compareToIgnoreCase(XDMUtil.nvl(item.size));
      case 2:
         return XDMUtil.nvl(this.filename).compareToIgnoreCase(XDMUtil.nvl(item.filename));
      case 3:
         return this.getExt(this.filename).compareToIgnoreCase(this.getExt(item.filename));
      default:
         return 0;
      }
   }

   String getExt(String name) {
      try {
         String[] e = name.split("\\.");
         return e.length > 1?e[e.length - 1]:"";
      } catch (Exception var3) {
         var3.printStackTrace();
         return "";
      }
   }
}
