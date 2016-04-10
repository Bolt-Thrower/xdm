package org.apache.commons.net;

import java.io.IOException;

public class MalformedServerReplyException extends IOException {
   private static final long serialVersionUID = 6006765264250543945L;

   public MalformedServerReplyException() {
   }

   public MalformedServerReplyException(String message) {
      super(message);
   }
}
