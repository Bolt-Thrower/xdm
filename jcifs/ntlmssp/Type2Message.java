package jcifs.ntlmssp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import jcifs.Config;
import jcifs.ntlmssp.NtlmMessage;
import jcifs.ntlmssp.Type1Message;

public class Type2Message extends NtlmMessage {
   private static final int DEFAULT_FLAGS = 512 | (Config.getBoolean("jcifs.smb.client.useUnicode", true)?1:2);
   private static final String DEFAULT_DOMAIN = Config.getProperty("jcifs.smb.client.domain", (String)null);
   private static final byte[] DEFAULT_TARGET_INFORMATION;
   private byte[] challenge;
   private String target;
   private byte[] context;
   private byte[] targetInformation;

   static {
      byte[] domain = new byte[0];
      if(DEFAULT_DOMAIN != null) {
         try {
            domain = DEFAULT_DOMAIN.getBytes("UnicodeLittleUnmarked");
         } catch (IOException var8) {
            ;
         }
      }

      int domainLength = domain.length;
      byte[] server = new byte[0];

      try {
         String serverLength = InetAddress.getLocalHost().getHostName();
         if(serverLength != null) {
            try {
               server = serverLength.getBytes("UnicodeLittleUnmarked");
            } catch (IOException var6) {
               ;
            }
         }
      } catch (UnknownHostException var7) {
         ;
      }

      int serverLength1 = server.length;
      byte[] targetInfo = new byte[(domainLength > 0?domainLength + 4:0) + (serverLength1 > 0?serverLength1 + 4:0) + 4];
      int offset = 0;
      if(domainLength > 0) {
         writeUShort(targetInfo, offset, 2);
         offset += 2;
         writeUShort(targetInfo, offset, domainLength);
         offset += 2;
         System.arraycopy(domain, 0, targetInfo, offset, domainLength);
         offset += domainLength;
      }

      if(serverLength1 > 0) {
         writeUShort(targetInfo, offset, 1);
         offset += 2;
         writeUShort(targetInfo, offset, serverLength1);
         offset += 2;
         System.arraycopy(server, 0, targetInfo, offset, serverLength1);
      }

      DEFAULT_TARGET_INFORMATION = targetInfo;
   }

   public Type2Message() {
      this(getDefaultFlags(), (byte[])null, (String)null);
   }

   public Type2Message(Type1Message type1) {
      this(type1, (byte[])null, (String)null);
   }

   public Type2Message(Type1Message type1, byte[] challenge, String target) {
      this(getDefaultFlags(type1), challenge, type1 != null && target == null && type1.getFlag(4)?getDefaultDomain():target);
   }

   public Type2Message(int flags, byte[] challenge, String target) {
      this.setFlags(flags);
      this.setChallenge(challenge);
      this.setTarget(target);
      if(target != null) {
         this.setTargetInformation(getDefaultTargetInformation());
      }

   }

   public Type2Message(byte[] material) throws IOException {
      this.parse(material);
   }

   public byte[] getChallenge() {
      return this.challenge;
   }

   public void setChallenge(byte[] challenge) {
      this.challenge = challenge;
   }

   public String getTarget() {
      return this.target;
   }

   public void setTarget(String target) {
      this.target = target;
   }

   public byte[] getTargetInformation() {
      return this.targetInformation;
   }

   public void setTargetInformation(byte[] targetInformation) {
      this.targetInformation = targetInformation;
   }

   public byte[] getContext() {
      return this.context;
   }

   public void setContext(byte[] context) {
      this.context = context;
   }

   public byte[] toByteArray() {
      try {
         String ex = this.getTarget();
         byte[] challenge = this.getChallenge();
         byte[] context = this.getContext();
         byte[] targetInformation = this.getTargetInformation();
         int flags = this.getFlags();
         byte[] target = new byte[0];
         if((flags & 458752) != 0) {
            if(ex != null && ex.length() != 0) {
               target = (flags & 1) != 0?ex.getBytes("UnicodeLittleUnmarked"):ex.toUpperCase().getBytes(getOEMEncoding());
            } else {
               flags &= -458753;
            }
         }

         if(targetInformation != null) {
            flags ^= 8388608;
            if(context == null) {
               context = new byte[8];
            }
         }

         int data = 32;
         if(context != null) {
            data += 8;
         }

         if(targetInformation != null) {
            data += 8;
         }

         byte[] type2 = new byte[data + target.length + (targetInformation != null?targetInformation.length:0)];
         System.arraycopy(NTLMSSP_SIGNATURE, 0, type2, 0, 8);
         writeULong(type2, 8, 2);
         writeSecurityBuffer(type2, 12, data, target);
         writeULong(type2, 20, flags);
         System.arraycopy(challenge != null?challenge:new byte[8], 0, type2, 24, 8);
         if(context != null) {
            System.arraycopy(context, 0, type2, 32, 8);
         }

         if(targetInformation != null) {
            writeSecurityBuffer(type2, 40, data + target.length, targetInformation);
         }

         return type2;
      } catch (IOException var9) {
         throw new IllegalStateException(var9.getMessage());
      }
   }

   public String toString() {
      String target = this.getTarget();
      byte[] challenge = this.getChallenge();
      byte[] context = this.getContext();
      byte[] targetInformation = this.getTargetInformation();
      int flags = this.getFlags();
      StringBuffer buffer = new StringBuffer();
      if(target != null) {
         buffer.append("target: ").append(target);
      }

      int i;
      if(challenge != null) {
         if(buffer.length() > 0) {
            buffer.append("; ");
         }

         buffer.append("challenge: ");
         buffer.append("0x");

         for(i = 0; i < challenge.length; ++i) {
            buffer.append(Integer.toHexString(challenge[i] >> 4 & 15));
            buffer.append(Integer.toHexString(challenge[i] & 15));
         }
      }

      if(context != null) {
         if(buffer.length() > 0) {
            buffer.append("; ");
         }

         buffer.append("context: ");
         buffer.append("0x");

         for(i = 0; i < context.length; ++i) {
            buffer.append(Integer.toHexString(context[i] >> 4 & 15));
            buffer.append(Integer.toHexString(context[i] & 15));
         }
      }

      if(targetInformation != null) {
         if(buffer.length() > 0) {
            buffer.append("; ");
         }

         buffer.append("targetInformation: ");
         buffer.append("0x");

         for(i = 0; i < targetInformation.length; ++i) {
            buffer.append(Integer.toHexString(targetInformation[i] >> 4 & 15));
            buffer.append(Integer.toHexString(targetInformation[i] & 15));
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

   public static int getDefaultFlags(Type1Message type1) {
      if(type1 == null) {
         return DEFAULT_FLAGS;
      } else {
         short flags = 512;
         int type1Flags = type1.getFlags();
         int flags1 = flags | ((type1Flags & 1) != 0?1:2);
         if((type1Flags & 4) != 0) {
            String domain = getDefaultDomain();
            if(domain != null) {
               flags1 |= 65540;
            }
         }

         return flags1;
      }
   }

   public static String getDefaultDomain() {
      return DEFAULT_DOMAIN;
   }

   public static byte[] getDefaultTargetInformation() {
      return DEFAULT_TARGET_INFORMATION;
   }

   private void parse(byte[] material) throws IOException {
      int flags;
      for(flags = 0; flags < 8; ++flags) {
         if(material[flags] != NTLMSSP_SIGNATURE[flags]) {
            throw new IOException("Not an NTLMSSP message.");
         }
      }

      if(readULong(material, 8) != 2) {
         throw new IOException("Not a Type 2 message.");
      } else {
         flags = readULong(material, 20);
         this.setFlags(flags);
         String target = null;
         byte[] bytes = readSecurityBuffer(material, 12);
         if(bytes.length != 0) {
            target = new String(bytes, (flags & 1) != 0?"UnicodeLittleUnmarked":getOEMEncoding());
         }

         this.setTarget(target);

         int offset;
         for(offset = 24; offset < 32; ++offset) {
            if(material[offset] != 0) {
               byte[] i = new byte[8];
               System.arraycopy(material, 24, i, 0, 8);
               this.setChallenge(i);
               break;
            }
         }

         offset = readULong(material, 16);
         if(offset != 32 && material.length != 32) {
            for(int var8 = 32; var8 < 40; ++var8) {
               if(material[var8] != 0) {
                  byte[] context = new byte[8];
                  System.arraycopy(material, 32, context, 0, 8);
                  this.setContext(context);
                  break;
               }
            }

            if(offset != 40 && material.length != 40) {
               bytes = readSecurityBuffer(material, 40);
               if(bytes.length != 0) {
                  this.setTargetInformation(bytes);
               }

            }
         }
      }
   }
}
