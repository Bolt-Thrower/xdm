package org.sdg.xdman.core.common.http;

import java.io.IOException;
import java.net.InetAddress;
import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;
import jcifs.util.Base64;

class NTLMAutheticator {
   private String ntDomain;
   private String ntUser;
   private String ntPass;

   public NTLMAutheticator(String domain, String user, String pass) {
      this.ntDomain = domain;
      this.ntUser = user;
      this.ntPass = pass;
   }

   public String getNTLMString(String challangeString) throws IOException {
      if(challangeString == null) {
         Type1Message challange1 = new Type1Message();
         challange1.setSuppliedDomain(this.ntDomain);
         return Base64.encode(challange1.toByteArray());
      } else {
         byte[] challange = Base64.decode(challangeString);
         Type2Message m2 = new Type2Message(challange);
         Type3Message m3 = new Type3Message(m2, this.ntPass, this.ntDomain, this.ntUser, InetAddress.getLocalHost().getHostName());
         return Base64.encode(m3.toByteArray());
      }
   }
}
