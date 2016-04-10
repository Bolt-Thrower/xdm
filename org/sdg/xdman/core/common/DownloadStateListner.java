package org.sdg.xdman.core.common;

import java.util.ArrayList;
import java.util.UUID;
import org.sdg.xdman.core.common.ConnectionManager;
import org.sdg.xdman.interceptor.DownloadIntercepterInfo;

public interface DownloadStateListner {
   void addDownload(String var1, String var2, String var3, String var4, String var5, String var6, ArrayList var7, String var8);

   void downloadNow(String var1, String var2, String var3, String var4, String var5, String var6, ArrayList var7, String var8);

   void add2Queue(String var1, String var2, String var3, String var4, String var5, String var6, ArrayList var7, String var8, boolean var9);

   void updateManager(UUID var1, Object var2);

   void downloadComplete(UUID var1);

   void downloadFailed(UUID var1);

   void downloadConfirmed(UUID var1, Object var2);

   void downloadPaused(UUID var1);

   void interceptDownload(DownloadIntercepterInfo var1);

   void getCredentials(ConnectionManager var1, String var2);

   void configChanged();

   void ytCallback(String var1);

   void restoreWindow();

   void startQueue();

   void stopQueue();

   void exit();
}
