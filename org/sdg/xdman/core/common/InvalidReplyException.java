package org.sdg.xdman.core.common;

public class InvalidReplyException extends Exception {
   private static final long serialVersionUID = -7536341657327508929L;
   int replyCode;
   String replyMessage;

   public InvalidReplyException(int rc, String rep) {
      super("Invalid response from server: (" + rc + ") " + rep);
      this.replyCode = rc;
      this.replyMessage = rep;
   }

   public String getMessage() {
      return "Invalid response from server: (" + this.replyCode + ") " + this.replyMessage;
   }
}
