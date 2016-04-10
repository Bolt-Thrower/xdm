package org.apache.commons.net.io;

import java.util.EventListener;
import java.util.Iterator;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.util.ListenerList;

public class CopyStreamAdapter implements CopyStreamListener {
   private final ListenerList internalListeners = new ListenerList();

   public void bytesTransferred(CopyStreamEvent event) {
      Iterator var3 = this.internalListeners.iterator();

      while(var3.hasNext()) {
         EventListener listener = (EventListener)var3.next();
         ((CopyStreamListener)listener).bytesTransferred(event);
      }

   }

   public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
      Iterator var7 = this.internalListeners.iterator();

      while(var7.hasNext()) {
         EventListener listener = (EventListener)var7.next();
         ((CopyStreamListener)listener).bytesTransferred(totalBytesTransferred, bytesTransferred, streamSize);
      }

   }

   public void addCopyStreamListener(CopyStreamListener listener) {
      this.internalListeners.addListener(listener);
   }

   public void removeCopyStreamListener(CopyStreamListener listener) {
      this.internalListeners.removeListener(listener);
   }
}
