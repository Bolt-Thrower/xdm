package jcifs.ntlmssp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import jcifs.Config;
import jcifs.ntlmssp.NtlmMessage;

public class Type1Message extends NtlmMessage {
   private static final int DEFAULT_FLAGS = 512 | (Config.getBoolean("jcifs.smb.client.useUnicode", true)?1:2);
   private static final String DEFAULT_DOMAIN = Config.getProperty("jcifs.smb.client.domain", (String)null);
   private static final String DEFAULT_WORKSTATION;
   private String suppliedDomain;
   private String suppliedWorkstation;

   static {
      String defaultWorkstation = null;

      try {
         defaultWorkstation = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException var2) {
         ;
      }

      DEFAULT_WORKSTATION = defaultWorkstation;
   }

   public Type1Message() {
      this(getDefaultFlags(), getDefaultDomain(), getDefaultWorkstation());
   }

   public Type1Message(int flags, String suppliedDomain, String suppliedWorkstation) {
      this.setFlags(flags);
      this.setSuppliedDomain(suppliedDomain);
      this.setSuppliedWorkstation(suppliedWorkstation);
   }

   public Type1Message(byte[] material) throws IOException {
      this.parse(material);
   }

   public String getSuppliedDomain() {
      return this.suppliedDomain;
   }

   public void setSuppliedDomain(String suppliedDomain) {
      this.suppliedDomain = suppliedDomain;
   }

   public String getSuppliedWorkstation() {
      return this.suppliedWorkstation;
   }

   public void setSuppliedWorkstation(String suppliedWorkstation) {
      this.suppliedWorkstation = suppliedWorkstation;
   }

   public byte[] toByteArray() {
      try {
         String ex = this.getSuppliedDomain();
         String suppliedWorkstation = this.getSuppliedWorkstation();
         int flags = this.getFlags();
         boolean hostInfo = false;
         byte[] domain = new byte[0];
         if(ex != null && ex.length() != 0) {
            hostInfo = true;
            flags |= 4096;
            domain = ex.toUpperCase().getBytes(getOEMEncoding());
         } else {
            flags &= -4097;
         }

         byte[] workstation = new byte[0];
         if(suppliedWorkstation != null && suppliedWorkstation.length() != 0) {
            hostInfo = true;
            flags |= 8192;
            workstation = suppliedWorkstation.toUpperCase().getBytes(getOEMEncoding());
         } else {
            flags &= -8193;
         }

         byte[] type1 = new byte[hostInfo?32 + domain.length + workstation.length:16];
         System.arraycopy(NTLMSSP_SIGNATURE, 0, type1, 0, 8);
         writeULong(type1, 8, 1);
         writeULong(type1, 12, flags);
         if(hostInfo) {
            writeSecurityBuffer(type1, 16, 32, domain);
            writeSecurityBuffer(type1, 24, 32 + domain.length, workstation);
         }

         return type1;
      } catch (IOException var8) {
         throw new IllegalStateException(var8.getMessage());
      }
   }

   public String toString() {
      String suppliedDomain = this.getSuppliedDomain();
      String suppliedWorkstation = this.getSuppliedWorkstation();
      int flags = this.getFlags();
      StringBuffer buffer = new StringBuffer();
      if(suppliedDomain != null) {
         buffer.append("suppliedDomain: ").append(suppliedDomain);
      }

      if(suppliedWorkstation != null) {
         if(buffer.length() > 0) {
            buffer.append("; ");
         }

         buffer.append("suppliedWorkstation: ").append(suppliedWorkstation);
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

   public static String getDefaultDomain() {
      return DEFAULT_DOMAIN;
   }

   public static String getDefaultWorkstation() {
      return DEFAULT_WORKSTATION;
   }

   private void parse(byte[] material) throws IOException {
      int flags;
      for(flags = 0; flags < 8; ++flags) {
         if(material[flags] != NTLMSSP_SIGNATURE[flags]) {
            throw new IOException("Not an NTLMSSP message.");
         }
      }

      if(readULong(material, 8) != 1) {
         throw new IOException("Not a Type 1 message.");
      } else {
         flags = readULong(material, 12);
         String suppliedDomain = null;
         if((flags & 4096) != 0) {
            byte[] suppliedWorkstation = readSecurityBuffer(material, 16);
            suppliedDomain = new String(suppliedWorkstation, getOEMEncoding());
         }

         String var6 = null;
         if((flags & 8192) != 0) {
            byte[] workstation = readSecurityBuffer(material, 24);
            var6 = new String(workstation, getOEMEncoding());
         }

         this.setFlags(flags);
         this.setSuppliedDomain(suppliedDomain);
         this.setSuppliedWorkstation(var6);
      }
   }
}
