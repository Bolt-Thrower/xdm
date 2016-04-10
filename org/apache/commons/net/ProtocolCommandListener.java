package org.apache.commons.net;

import java.util.EventListener;
import org.apache.commons.net.ProtocolCommandEvent;

public interface ProtocolCommandListener extends EventListener {
   void protocolCommandSent(ProtocolCommandEvent var1);

   void protocolReplyReceived(ProtocolCommandEvent var1);
}
