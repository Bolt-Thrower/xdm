package org.apache.commons.net.io;

import java.util.EventObject;

public class CopyStreamEvent extends EventObject {
   private static final long serialVersionUID = -964927635655051867L;
   public static final long UNKNOWN_STREAM_SIZE = -1L;
   private final int bytesTransferred;
   private final long totalBytesTransferred;
   private final long streamSize;

   public CopyStreamEvent(Object source, long totalBytesTransferred, int bytesTransferred, long streamSize) {
      super(source);
      this.bytesTransferred = bytesTransferred;
      this.totalBytesTransferred = totalBytesTransferred;
      this.streamSize = streamSize;
   }

   public int getBytesTransferred() {
      return this.bytesTransferred;
   }

   public long getTotalBytesTransferred() {
      return this.totalBytesTransferred;
   }

   public long getStreamSize() {
      return this.streamSize;
   }

   public String toString() {
      return this.getClass().getName() + "[source=" + this.source + ", total=" + this.totalBytesTransferred + ", bytes=" + this.bytesTransferred + ", size=" + this.streamSize + "]";
   }
}
