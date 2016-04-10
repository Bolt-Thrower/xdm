package jcifs.util;

import java.security.MessageDigest;

public class HMACT64 extends MessageDigest implements Cloneable {
   private static final int BLOCK_LENGTH = 64;
   private static final byte IPAD = 54;
   private static final byte OPAD = 92;
   private MessageDigest md5;
   private byte[] ipad = new byte[64];
   private byte[] opad = new byte[64];

   public HMACT64(byte[] key) {
      super("HMACT64");
      int length = Math.min(key.length, 64);

      int ex;
      for(ex = 0; ex < length; ++ex) {
         this.ipad[ex] = (byte)(key[ex] ^ 54);
         this.opad[ex] = (byte)(key[ex] ^ 92);
      }

      for(ex = length; ex < 64; ++ex) {
         this.ipad[ex] = 54;
         this.opad[ex] = 92;
      }

      try {
         this.md5 = MessageDigest.getInstance("MD5");
      } catch (Exception var4) {
         throw new IllegalStateException(var4.getMessage());
      }

      this.engineReset();
   }

   private HMACT64(HMACT64 hmac) throws CloneNotSupportedException {
      super("HMACT64");
      this.ipad = hmac.ipad;
      this.opad = hmac.opad;
      this.md5 = (MessageDigest)hmac.md5.clone();
   }

   public Object clone() {
      try {
         return new HMACT64(this);
      } catch (CloneNotSupportedException var2) {
         throw new IllegalStateException(var2.getMessage());
      }
   }

   protected byte[] engineDigest() {
      byte[] digest = this.md5.digest();
      this.md5.update(this.opad);
      return this.md5.digest(digest);
   }

   protected int engineDigest(byte[] buf, int offset, int len) {
      byte[] digest = this.md5.digest();
      this.md5.update(this.opad);
      this.md5.update(digest);

      try {
         return this.md5.digest(buf, offset, len);
      } catch (Exception var6) {
         throw new IllegalStateException();
      }
   }

   protected int engineGetDigestLength() {
      return this.md5.getDigestLength();
   }

   protected void engineReset() {
      this.md5.reset();
      this.md5.update(this.ipad);
   }

   protected void engineUpdate(byte b) {
      this.md5.update(b);
   }

   protected void engineUpdate(byte[] input, int offset, int len) {
      this.md5.update(input, offset, len);
   }
}
