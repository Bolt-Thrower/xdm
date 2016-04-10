package org.apache.commons.net;

import java.util.EventObject;

public class ProtocolCommandEvent extends EventObject {
   private static final long serialVersionUID = 403743538418947240L;
   private final int __replyCode;
   private final boolean __isCommand;
   private final String __message;
   private final String __command;

   public ProtocolCommandEvent(Object source, String command, String message) {
      super(source);
      this.__replyCode = 0;
      this.__message = message;
      this.__isCommand = true;
      this.__command = command;
   }

   public ProtocolCommandEvent(Object source, int replyCode, String message) {
      super(source);
      this.__replyCode = replyCode;
      this.__message = message;
      this.__isCommand = false;
      this.__command = null;
   }

   public String getCommand() {
      return this.__command;
   }

   public int getReplyCode() {
      return this.__replyCode;
   }

   public boolean isCommand() {
      return this.__isCommand;
   }

   public boolean isReply() {
      return !this.isCommand();
   }

   public String getMessage() {
      return this.__message;
   }
}
