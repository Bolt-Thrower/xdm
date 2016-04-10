package org.apache.commons.net.io;

import java.util.EventListener;
import org.apache.commons.net.io.CopyStreamEvent;

public interface CopyStreamListener extends EventListener {
   void bytesTransferred(CopyStreamEvent var1);

   void bytesTransferred(long var1, int var3, long var4);
}
