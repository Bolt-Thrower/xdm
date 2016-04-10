package org.sdg.xdman.interceptor;

import java.util.ArrayList;
import org.sdg.xdman.util.XDMUtil;

public class DownloadIntercepterInfo {
   public String url;
   public String ua;
   public ArrayList cookies;
   public String referer;

   public void copyTo(DownloadIntercepterInfo info) {
      info.url = new String(this.url.toCharArray());
      if(!XDMUtil.isNullOrEmpty(this.ua)) {
         info.ua = new String(this.ua.toCharArray());
      }

      if(!XDMUtil.isNullOrEmpty(this.referer)) {
         info.referer = new String(this.referer.toCharArray());
      }

      if(this.cookies != null) {
         info.cookies = new ArrayList();
         info.cookies.addAll(this.cookies);
      }

   }
}
