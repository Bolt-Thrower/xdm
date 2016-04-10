package org.sdg.xdman.core.common;

import java.net.URL;
import org.sdg.xdman.core.common.WebProxy;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.util.XDMUtil;

public class ProxyHelper {
   public static WebProxy getProxyForURL(String url, XDMConfig config) {
      if(url == null) {
         return null;
      } else {
         String host;
         int port;
         WebProxy wp;
         if(url.indexOf("http://") == 0 && config.useHttpProxy) {
            host = config.httpProxyHost;
            port = config.httpProxyPort;
            if(!XDMUtil.isNullOrEmpty(host)) {
               if(port < 0) {
                  port = 80;
               }

               wp = new WebProxy();
               wp.host = host;
               wp.port = port;
               return wp;
            }
         }

         if(url.indexOf("https://") == 0 && config.useHttpsProxy) {
            host = config.httpsProxyHost;
            port = config.httpsProxyPort;
            if(!XDMUtil.isNullOrEmpty(host)) {
               if(port < 0) {
                  port = 80;
               }

               wp = new WebProxy();
               wp.host = host;
               wp.port = port;
               return wp;
            }
         }

         return null;
      }
   }

   public static WebProxy getProxyForURL(URL url, XDMConfig config) {
      return getProxyForURL("" + url, config);
   }
}
