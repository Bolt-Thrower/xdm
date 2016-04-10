package org.sdg.xdman.core.common;

import org.sdg.xdman.core.common.DownloadInfo;

public interface DownloadProgressListener {
   void update(DownloadInfo var1);

   boolean isValidWindow();
}
