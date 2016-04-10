package jcifs.ntlmssp;

import jcifs.Config;
import jcifs.ntlmssp.NtlmFlags;

public abstract class NtlmMessage implements NtlmFlags {
   protected static final byte[] NTLMSSP_SIGNATURE = new byte[]{(byte)78, (byte)84, (byte)76, (byte)77, (byte)83, (byte)83, (byte)80, (byte)0};
   private static final String OEM_ENCODING = Config.getProperty("jcifs.encoding", System.getProperty("file.encoding"));
   private int flags;

   public int getFlags() {
      return this.flags;
   }

   public void setFlags(int flags) {
      this.flags = flags;
   }

   public boolean getFlag(int flag) {
      return (this.getFlags() & flag) != 0;
   }

   public void setFlag(int flag, boolean value) {
      this.setFlags(value?this.getFlags() | flag:this.getFlags() & ~flag);
   }

   static int readULong(byte[] src, int index) {
      return src[index] & 255 | (src[index + 1] & 255) << 8 | (src[index + 2] & 255) << 16 | (src[index + 3] & 255) << 24;
   }

   static int readUShort(byte[] src, int index) {
      return src[index] & 255 | (src[index + 1] & 255) << 8;
   }

   static byte[] readSecurityBuffer(byte[] src, int index) {
      int length = readUShort(src, index);
      int offset = readULong(src, index + 4);
      byte[] buffer = new byte[length];
      System.arraycopy(src, offset, buffer, 0, length);
      return buffer;
   }

   static void writeULong(byte[] dest, int offset, int ulong) {
      dest[offset] = (byte)(ulong & 255);
      dest[offset + 1] = (byte)(ulong >> 8 & 255);
      dest[offset + 2] = (byte)(ulong >> 16 & 255);
      dest[offset + 3] = (byte)(ulong >> 24 & 255);
   }

   static void writeUShort(byte[] dest, int offset, int ushort) {
      dest[offset] = (byte)(ushort & 255);
      dest[offset + 1] = (byte)(ushort >> 8 & 255);
   }

   static void writeSecurityBuffer(byte[] dest, int offset, int bodyOffset, byte[] src) {
      int length = src != null?src.length:0;
      if(length != 0) {
         writeUShort(dest, offset, length);
         writeUShort(dest, offset + 2, length);
         writeULong(dest, offset + 4, bodyOffset);
         System.arraycopy(src, 0, dest, bodyOffset, length);
      }
   }

   static String getOEMEncoding() {
      return OEM_ENCODING;
   }

   public abstract byte[] toByteArray();
}
