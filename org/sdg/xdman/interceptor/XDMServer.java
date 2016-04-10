package org.sdg.xdman.interceptor;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.interceptor.IMediaGrabber;
import org.sdg.xdman.interceptor.Intercepter;

public class XDMServer implements Runnable {
   ServerSocket server;
   XDMConfig config;
   DownloadStateListner mgr;
   IMediaGrabber gbr;

   public XDMServer(XDMConfig config, DownloadStateListner mgr, IMediaGrabber gbr) {
      this.config = config;
      this.mgr = mgr;
      this.gbr = gbr;
   }

   public void sendParams(HashMap args) {
      try {
         Socket e = new Socket("127.0.0.1", 9614);
         OutputStream out = e.getOutputStream();
         out.write("PARAM\r\n".getBytes());
         Set keys = args.keySet();
         Iterator it = keys.iterator();

         while(it.hasNext()) {
            String key = (String)it.next();
            out.write((key + ": " + (String)args.get(key) + "\r\n").getBytes());
            System.out.println(key + ": " + (String)args.get(key) + "\r\n");
         }

         out.write("\r\n".getBytes());
         out.flush();
         out.close();
         e.close();
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }

   public boolean start() {
      try {
         this.server = new ServerSocket(9614, Integer.MAX_VALUE, InetAddress.getByName("127.0.0.1"));
      } catch (Exception var2) {
         var2.printStackTrace();
         return false;
      }

      (new Thread(this)).start();
      return true;
   }

   public void run() {
      try {
         while(true) {
            Socket e = this.server.accept();
            Intercepter interceptor = new Intercepter(e, this.config, this.mgr, this.gbr);
            (new Thread(interceptor)).start();
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }
   }
}
