package jcifs.util;

import java.io.PrintStream;

public class Hexdump {
   private static final String NL = System.getProperty("line.separator");
   private static final int NL_LENGTH;
   private static final char[] SPACE_CHARS;
   public static final char[] HEX_DIGITS;

   static {
      NL_LENGTH = NL.length();
      SPACE_CHARS = new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
      HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
   }

   public static void hexdump(PrintStream ps, byte[] src, int srcIndex, int length) {
      if(length != 0) {
         int s = length % 16;
         int r = s == 0?length / 16:length / 16 + 1;
         char[] c = new char[r * (74 + NL_LENGTH)];
         char[] d = new char[16];
         int si = 0;
         int ci = 0;

         do {
            toHexChars(si, c, ci, 5);
            ci += 5;
            c[ci++] = 58;

            do {
               if(si == length) {
                  int n = 16 - s;
                  System.arraycopy(SPACE_CHARS, 0, c, ci, n * 3);
                  ci += n * 3;
                  System.arraycopy(SPACE_CHARS, 0, d, s, n);
                  break;
               }

               c[ci++] = 32;
               int i = src[srcIndex + si] & 255;
               toHexChars(i, c, ci, 2);
               ci += 2;
               if(i >= 0 && !Character.isISOControl((char)i)) {
                  d[si % 16] = (char)i;
               } else {
                  d[si % 16] = 46;
               }

               ++si;
            } while(si % 16 != 0);

            c[ci++] = 32;
            c[ci++] = 32;
            c[ci++] = 124;
            System.arraycopy(d, 0, c, ci, 16);
            ci += 16;
            c[ci++] = 124;
            NL.getChars(0, NL_LENGTH, c, ci);
            ci += NL_LENGTH;
         } while(si < length);

         ps.println(c);
      }
   }

   public static String toHexString(int val, int size) {
      char[] c = new char[size];
      toHexChars(val, c, 0, size);
      return new String(c);
   }

   public static String toHexString(long val, int size) {
      char[] c = new char[size];
      toHexChars(val, c, 0, size);
      return new String(c);
   }

   public static String toHexString(byte[] src, int srcIndex, int size) {
      char[] c = new char[size];
      size = size % 2 == 0?size / 2:size / 2 + 1;
      int i = 0;

      for(int j = 0; i < size; ++i) {
         c[j++] = HEX_DIGITS[src[i] >> 4 & 15];
         if(j == c.length) {
            break;
         }

         c[j++] = HEX_DIGITS[src[i] & 15];
      }

      return new String(c);
   }

   public static void toHexChars(int val, char[] dst, int dstIndex, int size) {
      for(; size > 0; --size) {
         int i = dstIndex + size - 1;
         if(i < dst.length) {
            dst[i] = HEX_DIGITS[val & 15];
         }

         if(val != 0) {
            val >>>= 4;
         }
      }

   }

   public static void toHexChars(long val, char[] dst, int dstIndex, int size) {
      for(; size > 0; --size) {
         dst[dstIndex + size - 1] = HEX_DIGITS[(int)(val & 15L)];
         if(val != 0L) {
            val >>>= 4;
         }
      }

   }
}
