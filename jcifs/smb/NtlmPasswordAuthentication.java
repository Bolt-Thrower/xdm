package jcifs.smb;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Random;
import jcifs.Config;
import jcifs.util.DES;
import jcifs.util.HMACT64;
import jcifs.util.MD4;

public final class NtlmPasswordAuthentication implements Principal, Serializable {
   private static final long serialVersionUID = -7421110415238844612L;
   private static final int LM_COMPATIBILITY = Config.getInt("jcifs.smb.lmCompatibility", 0);
   static final String DEFAULT_DOMAIN = Config.getProperty("jcifs.smb.client.domain", "?");
   private static final String DEFAULT_USERNAME = Config.getProperty("jcifs.smb.client.username", "GUEST");
   static final String BLANK = "";
   static final String DEFAULT_PASSWORD = Config.getProperty("jcifs.smb.client.password", "");
   private static final Random RANDOM = new Random();
   private static final byte[] S8 = new byte[]{(byte)75, (byte)71, (byte)83, (byte)33, (byte)64, (byte)35, (byte)36, (byte)37};
   static final NtlmPasswordAuthentication NULL = new NtlmPasswordAuthentication("", "", "");
   static final NtlmPasswordAuthentication GUEST = new NtlmPasswordAuthentication("?", "GUEST", "");
   static final NtlmPasswordAuthentication DEFAULT = new NtlmPasswordAuthentication((String)null);
   String domain;
   String username;
   String password;
   byte[] ansiHash;
   byte[] unicodeHash;
   boolean hashesExternal = false;
   byte[] clientChallenge = null;
   byte[] challenge = null;

   private static void E(byte[] key, byte[] data, byte[] e) {
      byte[] key7 = new byte[7];
      byte[] e8 = new byte[8];

      for(int i = 0; i < key.length / 7; ++i) {
         System.arraycopy(key, i * 7, key7, 0, 7);
         DES des = new DES(key7);
         des.encrypt(data, e8);
         System.arraycopy(e8, 0, e, i * 8, 8);
      }

   }

   public static byte[] getPreNTLMResponse(String password, byte[] challenge) {
      byte[] p14 = new byte[14];
      byte[] p21 = new byte[21];
      byte[] p24 = new byte[24];

      byte[] passwordBytes;
      try {
         passwordBytes = password.toUpperCase().getBytes(Config.getProperty("jcifs.encoding", System.getProperty("file.encoding")));
      } catch (UnsupportedEncodingException var7) {
         return null;
      }

      int passwordLength = passwordBytes.length;
      if(passwordLength > 14) {
         passwordLength = 14;
      }

      System.arraycopy(passwordBytes, 0, p14, 0, passwordLength);
      E(p14, S8, p21);
      E(p21, challenge, p24);
      return p24;
   }

   public static byte[] getNTLMResponse(String password, byte[] challenge) {
      byte[] uni = (byte[])null;
      byte[] p21 = new byte[21];
      byte[] p24 = new byte[24];

      try {
         uni = password.getBytes("UnicodeLittleUnmarked");
      } catch (UnsupportedEncodingException var8) {
         ;
      }

      MD4 md4 = new MD4();
      md4.update(uni);

      try {
         md4.digest(p21, 0, 16);
      } catch (Exception var7) {
         ;
      }

      E(p21, challenge, p24);
      return p24;
   }

   public static byte[] getLMv2Response(String domain, String user, String password, byte[] challenge, byte[] clientChallenge) {
      try {
         byte[] ex = new byte[24];
         MD4 md4 = new MD4();
         md4.update(password.getBytes("UnicodeLittleUnmarked"));
         HMACT64 hmac = new HMACT64(md4.digest());
         hmac.update(user.toUpperCase().getBytes("UnicodeLittleUnmarked"));
         hmac.update(domain.toUpperCase().getBytes("UnicodeLittleUnmarked"));
         hmac = new HMACT64(hmac.digest());
         hmac.update(challenge);
         hmac.update(clientChallenge);
         hmac.digest(ex, 0, 16);
         System.arraycopy(clientChallenge, 0, ex, 16, 8);
         return ex;
      } catch (Exception var8) {
         return null;
      }
   }

   public NtlmPasswordAuthentication(String userInfo) {
      this.domain = this.username = this.password = null;
      if(userInfo != null) {
         int end = userInfo.length();
         int i = 0;

         int u;
         for(u = 0; i < end; ++i) {
            char c = userInfo.charAt(i);
            if(c == 59) {
               this.domain = userInfo.substring(0, i);
               u = i + 1;
            } else if(c == 58) {
               this.password = userInfo.substring(i + 1);
               break;
            }
         }

         this.username = userInfo.substring(u, i);
      }

      if(this.domain == null) {
         this.domain = DEFAULT_DOMAIN;
      }

      if(this.username == null) {
         this.username = DEFAULT_USERNAME;
      }

      if(this.password == null) {
         this.password = DEFAULT_PASSWORD;
      }

   }

   public NtlmPasswordAuthentication(String domain, String username, String password) {
      this.domain = domain;
      this.username = username;
      this.password = password;
      if(domain == null) {
         this.domain = DEFAULT_DOMAIN;
      }

      if(username == null) {
         this.username = DEFAULT_USERNAME;
      }

      if(password == null) {
         this.password = DEFAULT_PASSWORD;
      }

   }

   public NtlmPasswordAuthentication(String domain, String username, byte[] challenge, byte[] ansiHash, byte[] unicodeHash) {
      if(domain != null && username != null && ansiHash != null && unicodeHash != null) {
         this.domain = domain;
         this.username = username;
         this.password = null;
         this.challenge = challenge;
         this.ansiHash = ansiHash;
         this.unicodeHash = unicodeHash;
         this.hashesExternal = true;
      } else {
         throw new IllegalArgumentException("External credentials cannot be null");
      }
   }

   public String getDomain() {
      return this.domain;
   }

   public String getUsername() {
      return this.username;
   }

   public String getPassword() {
      return this.password;
   }

   public String getName() {
      boolean d = this.domain.length() > 0 && !this.domain.equals("?");
      return d?this.domain + "\\" + this.username:this.username;
   }

   public byte[] getAnsiHash(byte[] challenge) {
      if(this.hashesExternal) {
         return this.ansiHash;
      } else {
         switch(LM_COMPATIBILITY) {
         case 0:
         case 1:
            return getPreNTLMResponse(this.password, challenge);
         case 2:
            return getNTLMResponse(this.password, challenge);
         case 3:
         case 4:
         case 5:
            if(this.clientChallenge == null) {
               this.clientChallenge = new byte[8];
               RANDOM.nextBytes(this.clientChallenge);
            }

            return getLMv2Response(this.domain, this.username, this.password, challenge, this.clientChallenge);
         default:
            return getPreNTLMResponse(this.password, challenge);
         }
      }
   }

   public byte[] getUnicodeHash(byte[] challenge) {
      if(this.hashesExternal) {
         return this.unicodeHash;
      } else {
         switch(LM_COMPATIBILITY) {
         case 0:
         case 1:
         case 2:
            return getNTLMResponse(this.password, challenge);
         case 3:
         case 4:
         case 5:
            return new byte[0];
         default:
            return getNTLMResponse(this.password, challenge);
         }
      }
   }

   public byte[] getUserSessionKey(byte[] challenge) {
      if(this.hashesExternal) {
         return null;
      } else {
         byte[] key = new byte[16];

         try {
            this.getUserSessionKey(challenge, key, 0);
         } catch (Exception var4) {
            ;
         }

         return key;
      }
   }

   void getUserSessionKey(byte[] challenge, byte[] dest, int offset) throws Exception {
      if(!this.hashesExternal) {
         MD4 md4 = new MD4();
         md4.update(this.password.getBytes("UnicodeLittleUnmarked"));
         switch(LM_COMPATIBILITY) {
         case 0:
         case 1:
         case 2:
            md4.update(md4.digest());
            md4.digest(dest, offset, 16);
            break;
         case 3:
         case 4:
         case 5:
            if(this.clientChallenge == null) {
               this.clientChallenge = new byte[8];
               RANDOM.nextBytes(this.clientChallenge);
            }

            HMACT64 hmac = new HMACT64(md4.digest());
            hmac.update(this.username.toUpperCase().getBytes("UnicodeLittleUnmarked"));
            hmac.update(this.domain.toUpperCase().getBytes("UnicodeLittleUnmarked"));
            byte[] ntlmv2Hash = hmac.digest();
            hmac = new HMACT64(ntlmv2Hash);
            hmac.update(challenge);
            hmac.update(this.clientChallenge);
            HMACT64 userKey = new HMACT64(ntlmv2Hash);
            userKey.update(hmac.digest());
            userKey.digest(dest, offset, 16);
            break;
         default:
            md4.update(md4.digest());
            md4.digest(dest, offset, 16);
         }

      }
   }

   public boolean equals(Object obj) {
      if(obj instanceof NtlmPasswordAuthentication) {
         NtlmPasswordAuthentication ntlm = (NtlmPasswordAuthentication)obj;
         if(ntlm.domain.toUpperCase().equals(this.domain.toUpperCase()) && ntlm.username.toUpperCase().equals(this.username.toUpperCase())) {
            if(this.hashesExternal && ntlm.hashesExternal) {
               if(Arrays.equals(this.ansiHash, ntlm.ansiHash) && Arrays.equals(this.unicodeHash, ntlm.unicodeHash)) {
                  return true;
               }

               return false;
            }

            if(!this.hashesExternal && this.password.equals(ntlm.password)) {
               return true;
            }
         }
      }

      return false;
   }

   public int hashCode() {
      return this.getName().toUpperCase().hashCode();
   }

   public String toString() {
      return this.getName();
   }
}
