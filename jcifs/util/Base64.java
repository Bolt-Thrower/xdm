package jcifs.util;

public class Base64 {
   private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

   public static String encode(byte[] bytes) {
      int length = bytes.length;
      if(length == 0) {
         return "";
      } else {
         StringBuffer buffer = new StringBuffer((int)Math.ceil((double)length / 3.0D) * 4);
         int remainder = length % 3;
         length -= remainder;
         int i = 0;

         int block;
         while(i < length) {
            block = (bytes[i++] & 255) << 16 | (bytes[i++] & 255) << 8 | bytes[i++] & 255;
            buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block >>> 18));
            buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block >>> 12 & 63));
            buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block >>> 6 & 63));
            buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block & 63));
         }

         if(remainder == 0) {
            return buffer.toString();
         } else if(remainder == 1) {
            block = (bytes[i] & 255) << 4;
            buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block >>> 6));
            buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block & 63));
            buffer.append("==");
            return buffer.toString();
         } else {
            block = ((bytes[i++] & 255) << 8 | bytes[i] & 255) << 2;
            buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block >>> 12));
            buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block >>> 6 & 63));
            buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block & 63));
            buffer.append("=");
            return buffer.toString();
         }
      }
   }

   public static byte[] decode(String string) {
      int length = string.length();
      if(length == 0) {
         return new byte[0];
      } else {
         int pad = string.charAt(length - 2) == 61?2:(string.charAt(length - 1) == 61?1:0);
         int size = length * 3 / 4 - pad;
         byte[] buffer = new byte[size];
         int i = 0;
         int index = 0;

         while(i < length) {
            int block = ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(string.charAt(i++)) & 255) << 18 | ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(string.charAt(i++)) & 255) << 12 | ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(string.charAt(i++)) & 255) << 6 | "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(string.charAt(i++)) & 255;
            buffer[index++] = (byte)(block >>> 16);
            if(index < size) {
               buffer[index++] = (byte)(block >>> 8 & 255);
            }

            if(index < size) {
               buffer[index++] = (byte)(block & 255);
            }
         }

         return buffer;
      }
   }
}
