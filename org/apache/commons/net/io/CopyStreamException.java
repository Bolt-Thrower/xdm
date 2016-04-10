package org.apache.commons.net.io;

import java.io.IOException;

public class CopyStreamException extends IOException {
   private static final long serialVersionUID = -2602899129433221532L;
   private final long totalBytesTransferred;

   public CopyStreamException(String message, long bytesTransferred, IOException exception) {
      super(message);
      this.initCause(exception);
      this.totalBytesTransferred = bytesTransferred;
   }

   public long getTotalBytesTransferred() {
      return this.totalBytesTransferred;
   }

   public IOException getIOException() {
      return (IOException)this.getCause();
   }
}
