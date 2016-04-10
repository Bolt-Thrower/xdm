package jcifs.ntlmssp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import jcifs.Config;
import jcifs.ntlmssp.NtlmMessage;
import jcifs.ntlmssp.Type2Message;
import jcifs.smb.NtlmPasswordAuthentication;

public class Type3Message extends NtlmMessage {
   private static final int DEFAULT_FLAGS = 512 | (Config.getBoolean("jcifs.smb.client.useUnicode", true)?1:2);
   private static final String DEFAULT_DOMAIN = Config.getProperty("jcifs.smb.client.domain", (String)null);
   private static final String DEFAULT_USER = Config.getProperty("jcifs.smb.client.username", (String)null);
   private static final String DEFAULT_PASSWORD = Config.getProperty("jcifs.smb.client.password", (String)null);
   private static final String DEFAULT_WORKSTATION;
   private static final int LM_COMPATIBILITY;
   private static final SecureRandom RANDOM = new SecureRandom();
   private byte[] lmResponse;
   private byte[] ntResponse;
   private String domain;
   private String user;
   private String workstation;
   private byte[] sessionKey;

   static {
      String defaultWorkstation = null;

      try {
         defaultWorkstation = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException var2) {
         ;
      }

      DEFAULT_WORKSTATION = defaultWorkstation;
      LM_COMPATIBILITY = Config.getInt("jcifs.smb.lmCompatibility", 0);
   }

   public Type3Message() {
      this.setFlags(getDefaultFlags());
      this.setDomain(getDefaultDomain());
      this.setUser(getDefaultUser());
      this.setWorkstation(getDefaultWorkstation());
   }

   public Type3Message(Type2Message type2) {
      this.setFlags(getDefaultFlags(type2));
      this.setWorkstation(getDefaultWorkstation());
      String domain = getDefaultDomain();
      this.setDomain(domain);
      String user = getDefaultUser();
      this.setUser(user);
      String password = getDefaultPassword();
      switch(LM_COMPATIBILITY) {
      case 0:
      case 1:
         this.setLMResponse(getLMResponse(type2, password));
         this.setNTResponse(getNTResponse(type2, password));
         break;
      case 2:
         byte[] nt = getNTResponse(type2, password);
         this.setLMResponse(nt);
         this.setNTResponse(nt);
         break;
      case 3:
      case 4:
      case 5:
         byte[] clientChallenge = new byte[8];
         RANDOM.nextBytes(clientChallenge);
         this.setLMResponse(getLMv2Response(type2, domain, user, password, clientChallenge));
         break;
      default:
         this.setLMResponse(getLMResponse(type2, password));
         this.setNTResponse(getNTResponse(type2, password));
      }

   }

   public Type3Message(Type2Message type2, String password, String domain, String user, String workstation) {
      this.setFlags(getDefaultFlags(type2));
      this.setDomain(domain);
      this.setUser(user);
      this.setWorkstation(workstation);
      switch(LM_COMPATIBILITY) {
      case 0:
      case 1:
         this.setLMResponse(getLMResponse(type2, password));
         this.setNTResponse(getNTResponse(type2, password));
         break;
      case 2:
         byte[] nt = getNTResponse(type2, password);
         this.setLMResponse(nt);
         this.setNTResponse(nt);
         break;
      case 3:
      case 4:
      case 5:
         byte[] clientChallenge = new byte[8];
         RANDOM.nextBytes(clientChallenge);
         this.setLMResponse(getLMv2Response(type2, domain, user, password, clientChallenge));
         break;
      default:
         this.setLMResponse(getLMResponse(type2, password));
         this.setNTResponse(getNTResponse(type2, password));
      }

   }

   public Type3Message(int flags, byte[] lmResponse, byte[] ntResponse, String domain, String user, String workstation) {
      this.setFlags(flags);
      this.setLMResponse(lmResponse);
      this.setNTResponse(ntResponse);
      this.setDomain(domain);
      this.setUser(user);
      this.setWorkstation(workstation);
   }

   public Type3Message(byte[] material) throws IOException {
      this.parse(material);
   }

   public byte[] getLMResponse() {
      return this.lmResponse;
   }

   public void setLMResponse(byte[] lmResponse) {
      this.lmResponse = lmResponse;
   }

   public byte[] getNTResponse() {
      return this.ntResponse;
   }

   public void setNTResponse(byte[] ntResponse) {
      this.ntResponse = ntResponse;
   }

   public String getDomain() {
      return this.domain;
   }

   public void setDomain(String domain) {
      this.domain = domain;
   }

   public String getUser() {
      return this.user;
   }

   public void setUser(String user) {
      this.user = user;
   }

   public String getWorkstation() {
      return this.workstation;
   }

   public void setWorkstation(String workstation) {
      this.workstation = workstation;
   }

   public byte[] getSessionKey() {
      return this.sessionKey;
   }

   public void setSessionKey(byte[] sessionKey) {
      this.sessionKey = sessionKey;
   }

   public byte[] toByteArray() {
      try {
         int ex = this.getFlags();
         boolean unicode = (ex & 1) != 0;
         String oem = unicode?null:getOEMEncoding();
         String domainName = this.getDomain();
         byte[] domain = (byte[])null;
         if(domainName != null && domainName.length() != 0) {
            domain = unicode?domainName.toUpperCase().getBytes("UnicodeLittleUnmarked"):domainName.toUpperCase().getBytes(oem);
         }

         int domainLength = domain != null?domain.length:0;
         String userName = this.getUser();
         byte[] user = (byte[])null;
         if(userName != null && userName.length() != 0) {
            user = unicode?userName.getBytes("UnicodeLittleUnmarked"):userName.toUpperCase().getBytes(oem);
         }

         int userLength = user != null?user.length:0;
         String workstationName = this.getWorkstation();
         byte[] workstation = (byte[])null;
         if(workstationName != null && workstationName.length() != 0) {
            workstation = unicode?workstationName.getBytes("UnicodeLittleUnmarked"):workstationName.toUpperCase().getBytes(oem);
         }

         int workstationLength = workstation != null?workstation.length:0;
         byte[] lmResponse = this.getLMResponse();
         int lmLength = lmResponse != null?lmResponse.length:0;
         byte[] ntResponse = this.getNTResponse();
         int ntLength = ntResponse != null?ntResponse.length:0;
         byte[] sessionKey = this.getSessionKey();
         int keyLength = sessionKey != null?sessionKey.length:0;
         byte[] type3 = new byte[64 + domainLength + userLength + workstationLength + lmLength + ntLength + keyLength];
         System.arraycopy(NTLMSSP_SIGNATURE, 0, type3, 0, 8);
         writeULong(type3, 8, 3);
         byte offset = 64;
         writeSecurityBuffer(type3, 12, offset, lmResponse);
         int offset1 = offset + lmLength;
         writeSecurityBuffer(type3, 20, offset1, ntResponse);
         offset1 += ntLength;
         writeSecurityBuffer(type3, 28, offset1, domain);
         offset1 += domainLength;
         writeSecurityBuffer(type3, 36, offset1, user);
         offset1 += userLength;
         writeSecurityBuffer(type3, 44, offset1, workstation);
         offset1 += workstationLength;
         writeSecurityBuffer(type3, 52, offset1, sessionKey);
         writeULong(type3, 60, ex);
         return type3;
      } catch (IOException var21) {
         throw new IllegalStateException(var21.getMessage());
      }
   }

   public String toString() {
      String user = this.getUser();
      String domain = this.getDomain();
      String workstation = this.getWorkstation();
      byte[] lmResponse = this.getLMResponse();
      byte[] ntResponse = this.getNTResponse();
      byte[] sessionKey = this.getSessionKey();
      int flags = this.getFlags();
      StringBuffer buffer = new StringBuffer();
      if(domain != null) {
         buffer.append("domain: ").append(domain);
      }

      if(user != null) {
         if(buffer.length() > 0) {
            buffer.append("; ");
         }

         buffer.append("user: ").append(user);
      }

      if(workstation != null) {
         if(buffer.length() > 0) {
            buffer.append("; ");
         }

         buffer.append("workstation: ").append(workstation);
      }

      int i;
      if(lmResponse != null) {
         if(buffer.length() > 0) {
            buffer.append("; ");
         }

         buffer.append("lmResponse: ");
         buffer.append("0x");

         for(i = 0; i < lmResponse.length; ++i) {
            buffer.append(Integer.toHexString(lmResponse[i] >> 4 & 15));
            buffer.append(Integer.toHexString(lmResponse[i] & 15));
         }
      }

      if(ntResponse != null) {
         if(buffer.length() > 0) {
            buffer.append("; ");
         }

         buffer.append("ntResponse: ");
         buffer.append("0x");

         for(i = 0; i < ntResponse.length; ++i) {
            buffer.append(Integer.toHexString(ntResponse[i] >> 4 & 15));
            buffer.append(Integer.toHexString(ntResponse[i] & 15));
         }
      }

      if(sessionKey != null) {
         if(buffer.length() > 0) {
            buffer.append("; ");
         }

         buffer.append("sessionKey: ");
         buffer.append("0x");

         for(i = 0; i < sessionKey.length; ++i) {
            buffer.append(Integer.toHexString(sessionKey[i] >> 4 & 15));
            buffer.append(Integer.toHexString(sessionKey[i] & 15));
         }
      }

      if(flags != 0) {
         if(buffer.length() > 0) {
            buffer.append("; ");
         }

         buffer.append("flags: ");
         buffer.append("0x");
         buffer.append(Integer.toHexString(flags >> 28 & 15));
         buffer.append(Integer.toHexString(flags >> 24 & 15));
         buffer.append(Integer.toHexString(flags >> 20 & 15));
         buffer.append(Integer.toHexString(flags >> 16 & 15));
         buffer.append(Integer.toHexString(flags >> 12 & 15));
         buffer.append(Integer.toHexString(flags >> 8 & 15));
         buffer.append(Integer.toHexString(flags >> 4 & 15));
         buffer.append(Integer.toHexString(flags & 15));
      }

      return buffer.toString();
   }

   public static int getDefaultFlags() {
      return DEFAULT_FLAGS;
   }

   public static int getDefaultFlags(Type2Message type2) {
      if(type2 == null) {
         return DEFAULT_FLAGS;
      } else {
         short flags = 512;
         int flags1 = flags | ((type2.getFlags() & 1) != 0?1:2);
         return flags1;
      }
   }

   public static byte[] getLMResponse(Type2Message type2, String password) {
      return type2 != null && password != null?NtlmPasswordAuthentication.getPreNTLMResponse(password, type2.getChallenge()):null;
   }

   public static byte[] getLMv2Response(Type2Message type2, String domain, String user, String password, byte[] clientChallenge) {
      return type2 != null && domain != null && user != null && password != null && clientChallenge != null?NtlmPasswordAuthentication.getLMv2Response(domain, user, password, type2.getChallenge(), clientChallenge):null;
   }

   public static byte[] getNTResponse(Type2Message type2, String password) {
      return type2 != null && password != null?NtlmPasswordAuthentication.getNTLMResponse(password, type2.getChallenge()):null;
   }

   public static String getDefaultDomain() {
      return DEFAULT_DOMAIN;
   }

   public static String getDefaultUser() {
      return DEFAULT_USER;
   }

   public static String getDefaultPassword() {
      return DEFAULT_PASSWORD;
   }

   public static String getDefaultWorkstation() {
      return DEFAULT_WORKSTATION;
   }

   private void parse(byte[] material) throws IOException {
      for(int lmResponse = 0; lmResponse < 8; ++lmResponse) {
         if(material[lmResponse] != NTLMSSP_SIGNATURE[lmResponse]) {
            throw new IOException("Not an NTLMSSP message.");
         }
      }

      if(readULong(material, 8) != 3) {
         throw new IOException("Not a Type 3 message.");
      } else {
         byte[] var14 = readSecurityBuffer(material, 12);
         int lmResponseOffset = readULong(material, 16);
         byte[] ntResponse = readSecurityBuffer(material, 20);
         int ntResponseOffset = readULong(material, 24);
         byte[] domain = readSecurityBuffer(material, 28);
         int domainOffset = readULong(material, 32);
         byte[] user = readSecurityBuffer(material, 36);
         int userOffset = readULong(material, 40);
         byte[] workstation = readSecurityBuffer(material, 44);
         int workstationOffset = readULong(material, 48);
         int flags;
         String charset;
         if(lmResponseOffset != 52 && ntResponseOffset != 52 && domainOffset != 52 && userOffset != 52 && workstationOffset != 52) {
            this.setSessionKey(readSecurityBuffer(material, 52));
            flags = readULong(material, 60);
            charset = (flags & 1) != 0?"UnicodeLittleUnmarked":getOEMEncoding();
         } else {
            flags = 514;
            charset = getOEMEncoding();
         }

         this.setFlags(flags);
         this.setLMResponse(var14);
         if(LM_COMPATIBILITY < 3) {
            this.setNTResponse(ntResponse);
         }

         this.setDomain(new String(domain, charset));
         this.setUser(new String(user, charset));
         this.setWorkstation(new String(workstation, charset));
      }
   }
}
