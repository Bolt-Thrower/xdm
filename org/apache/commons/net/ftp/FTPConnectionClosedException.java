package org.apache.commons.net.ftp;

import java.io.IOException;

public class FTPConnectionClosedException extends IOException {
   private static final long serialVersionUID = 3500547241659379952L;

   public FTPConnectionClosedException() {
   }

   public FTPConnectionClosedException(String message) {
      super(message);
   }
}
