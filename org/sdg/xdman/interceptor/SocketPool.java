package org.sdg.xdman.interceptor;

import java.net.Socket;
import java.util.ArrayList;
import org.sdg.xdman.interceptor.SocketInfo;
import org.sdg.xdman.util.Logger;

public class SocketPool {
   static ArrayList list = new ArrayList();

   public static synchronized Socket getSocket(String host, int port) {
      for(int i = 0; i < list.size(); ++i) {
         SocketInfo sock = (SocketInfo)list.get(i);
         if(sock.host.equals(host) && sock.port == port) {
            list.remove(sock);
            Logger.log("Found existing socket for: " + host + " " + sock.socket);
            return sock.socket;
         }
      }

      return null;
   }

   public static synchronized void putSocket(String host, int port, Socket socket) {
      if(socket == null) {
         throw new NullPointerException("Socket being added to pool is null");
      } else {
         SocketInfo info = new SocketInfo();
         info.host = host;
         info.port = port;
         info.socket = socket;
         list.add(info);
      }
   }
}
