package org.sdg.xdman.interceptor;

import java.util.ArrayList;

public interface IMediaGrabber {
   void mediaCaptured(String var1, String var2, String var3, String var4, String var5, String var6, ArrayList var7);

   void showGrabber();

   void showNotification();

   void showNotificationText(String var1, String var2);
}
