package org.apache.commons.net.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class Base64 {
   private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;
   private static final int DEFAULT_BUFFER_SIZE = 8192;
   static final int CHUNK_SIZE = 76;
   private static final byte[] CHUNK_SEPARATOR = new byte[]{(byte)13, (byte)10};
   private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
   private static final byte[] STANDARD_ENCODE_TABLE = new byte[]{(byte)65, (byte)66, (byte)67, (byte)68, (byte)69, (byte)70, (byte)71, (byte)72, (byte)73, (byte)74, (byte)75, (byte)76, (byte)77, (byte)78, (byte)79, (byte)80, (byte)81, (byte)82, (byte)83, (byte)84, (byte)85, (byte)86, (byte)87, (byte)88, (byte)89, (byte)90, (byte)97, (byte)98, (byte)99, (byte)100, (byte)101, (byte)102, (byte)103, (byte)104, (byte)105, (byte)106, (byte)107, (byte)108, (byte)109, (byte)110, (byte)111, (byte)112, (byte)113, (byte)114, (byte)115, (byte)116, (byte)117, (byte)118, (byte)119, (byte)120, (byte)121, (byte)122, (byte)48, (byte)49, (byte)50, (byte)51, (byte)52, (byte)53, (byte)54, (byte)55, (byte)56, (byte)57, (byte)43, (byte)47};
   private static final byte[] URL_SAFE_ENCODE_TABLE = new byte[]{(byte)65, (byte)66, (byte)67, (byte)68, (byte)69, (byte)70, (byte)71, (byte)72, (byte)73, (byte)74, (byte)75, (byte)76, (byte)77, (byte)78, (byte)79, (byte)80, (byte)81, (byte)82, (byte)83, (byte)84, (byte)85, (byte)86, (byte)87, (byte)88, (byte)89, (byte)90, (byte)97, (byte)98, (byte)99, (byte)100, (byte)101, (byte)102, (byte)103, (byte)104, (byte)105, (byte)106, (byte)107, (byte)108, (byte)109, (byte)110, (byte)111, (byte)112, (byte)113, (byte)114, (byte)115, (byte)116, (byte)117, (byte)118, (byte)119, (byte)120, (byte)121, (byte)122, (byte)48, (byte)49, (byte)50, (byte)51, (byte)52, (byte)53, (byte)54, (byte)55, (byte)56, (byte)57, (byte)45, (byte)95};
   private static final byte PAD = 61;
   private static final byte[] DECODE_TABLE = new byte[]{(byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)62, (byte)-1, (byte)62, (byte)-1, (byte)63, (byte)52, (byte)53, (byte)54, (byte)55, (byte)56, (byte)57, (byte)58, (byte)59, (byte)60, (byte)61, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)0, (byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6, (byte)7, (byte)8, (byte)9, (byte)10, (byte)11, (byte)12, (byte)13, (byte)14, (byte)15, (byte)16, (byte)17, (byte)18, (byte)19, (byte)20, (byte)21, (byte)22, (byte)23, (byte)24, (byte)25, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)63, (byte)-1, (byte)26, (byte)27, (byte)28, (byte)29, (byte)30, (byte)31, (byte)32, (byte)33, (byte)34, (byte)35, (byte)36, (byte)37, (byte)38, (byte)39, (byte)40, (byte)41, (byte)42, (byte)43, (byte)44, (byte)45, (byte)46, (byte)47, (byte)48, (byte)49, (byte)50, (byte)51};
   private static final int MASK_6BITS = 63;
   private static final int MASK_8BITS = 255;
   private final byte[] encodeTable;
   private final int lineLength;
   private final byte[] lineSeparator;
   private final int decodeSize;
   private final int encodeSize;
   private byte[] buffer;
   private int pos;
   private int readPos;
   private int currentLinePos;
   private int modulus;
   private boolean eof;
   private int x;

   public Base64() {
      this(false);
   }

   public Base64(boolean urlSafe) {
      this(76, CHUNK_SEPARATOR, urlSafe);
   }

   public Base64(int lineLength) {
      this(lineLength, CHUNK_SEPARATOR);
   }

   public Base64(int lineLength, byte[] lineSeparator) {
      this(lineLength, lineSeparator, false);
   }

   public Base64(int lineLength, byte[] lineSeparator, boolean urlSafe) {
      if(lineSeparator == null) {
         lineLength = 0;
         lineSeparator = EMPTY_BYTE_ARRAY;
      }

      this.lineLength = lineLength > 0?lineLength / 4 * 4:0;
      this.lineSeparator = new byte[lineSeparator.length];
      System.arraycopy(lineSeparator, 0, this.lineSeparator, 0, lineSeparator.length);
      if(lineLength > 0) {
         this.encodeSize = 4 + lineSeparator.length;
      } else {
         this.encodeSize = 4;
      }

      this.decodeSize = this.encodeSize - 1;
      if(containsBase64Byte(lineSeparator)) {
         String sep = newStringUtf8(lineSeparator);
         throw new IllegalArgumentException("lineSeperator must not contain base64 characters: [" + sep + "]");
      } else {
         this.encodeTable = urlSafe?URL_SAFE_ENCODE_TABLE:STANDARD_ENCODE_TABLE;
      }
   }

   public boolean isUrlSafe() {
      return this.encodeTable == URL_SAFE_ENCODE_TABLE;
   }

   boolean hasData() {
      return this.buffer != null;
   }

   int avail() {
      return this.buffer != null?this.pos - this.readPos:0;
   }

   private void resizeBuffer() {
      if(this.buffer == null) {
         this.buffer = new byte[8192];
         this.pos = 0;
         this.readPos = 0;
      } else {
         byte[] b = new byte[this.buffer.length * 2];
         System.arraycopy(this.buffer, 0, b, 0, this.buffer.length);
         this.buffer = b;
      }

   }

   int readResults(byte[] b, int bPos, int bAvail) {
      if(this.buffer != null) {
         int len = Math.min(this.avail(), bAvail);
         if(this.buffer != b) {
            System.arraycopy(this.buffer, this.readPos, b, bPos, len);
            this.readPos += len;
            if(this.readPos >= this.pos) {
               this.buffer = null;
            }
         } else {
            this.buffer = null;
         }

         return len;
      } else {
         return this.eof?-1:0;
      }
   }

   void setInitialBuffer(byte[] out, int outPos, int outAvail) {
      if(out != null && out.length == outAvail) {
         this.buffer = out;
         this.pos = outPos;
         this.readPos = outPos;
      }

   }

   void encode(byte[] in, int inPos, int inAvail) {
      if(!this.eof) {
         if(inAvail < 0) {
            this.eof = true;
            if(this.buffer == null || this.buffer.length - this.pos < this.encodeSize) {
               this.resizeBuffer();
            }

            switch(this.modulus) {
            case 1:
               this.buffer[this.pos++] = this.encodeTable[this.x >> 2 & 63];
               this.buffer[this.pos++] = this.encodeTable[this.x << 4 & 63];
               if(this.encodeTable == STANDARD_ENCODE_TABLE) {
                  this.buffer[this.pos++] = 61;
                  this.buffer[this.pos++] = 61;
               }
               break;
            case 2:
               this.buffer[this.pos++] = this.encodeTable[this.x >> 10 & 63];
               this.buffer[this.pos++] = this.encodeTable[this.x >> 4 & 63];
               this.buffer[this.pos++] = this.encodeTable[this.x << 2 & 63];
               if(this.encodeTable == STANDARD_ENCODE_TABLE) {
                  this.buffer[this.pos++] = 61;
               }
            }

            if(this.lineLength > 0 && this.pos > 0) {
               System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
               this.pos += this.lineSeparator.length;
            }
         } else {
            for(int i = 0; i < inAvail; ++i) {
               if(this.buffer == null || this.buffer.length - this.pos < this.encodeSize) {
                  this.resizeBuffer();
               }

               this.modulus = ++this.modulus % 3;
               int b = in[inPos++];
               if(b < 0) {
                  b += 256;
               }

               this.x = (this.x << 8) + b;
               if(this.modulus == 0) {
                  this.buffer[this.pos++] = this.encodeTable[this.x >> 18 & 63];
                  this.buffer[this.pos++] = this.encodeTable[this.x >> 12 & 63];
                  this.buffer[this.pos++] = this.encodeTable[this.x >> 6 & 63];
                  this.buffer[this.pos++] = this.encodeTable[this.x & 63];
                  this.currentLinePos += 4;
                  if(this.lineLength > 0 && this.lineLength <= this.currentLinePos) {
                     System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
                     this.pos += this.lineSeparator.length;
                     this.currentLinePos = 0;
                  }
               }
            }
         }

      }
   }

   void decode(byte[] in, int inPos, int inAvail) {
      if(!this.eof) {
         if(inAvail < 0) {
            this.eof = true;
         }

         for(int i = 0; i < inAvail; ++i) {
            if(this.buffer == null || this.buffer.length - this.pos < this.decodeSize) {
               this.resizeBuffer();
            }

            byte b = in[inPos++];
            if(b == 61) {
               this.eof = true;
               break;
            }

            if(b >= 0 && b < DECODE_TABLE.length) {
               byte result = DECODE_TABLE[b];
               if(result >= 0) {
                  this.modulus = ++this.modulus % 4;
                  this.x = (this.x << 6) + result;
                  if(this.modulus == 0) {
                     this.buffer[this.pos++] = (byte)(this.x >> 16 & 255);
                     this.buffer[this.pos++] = (byte)(this.x >> 8 & 255);
                     this.buffer[this.pos++] = (byte)(this.x & 255);
                  }
               }
            }
         }

         if(this.eof && this.modulus != 0) {
            this.x <<= 6;
            switch(this.modulus) {
            case 2:
               this.x <<= 6;
               this.buffer[this.pos++] = (byte)(this.x >> 16 & 255);
               break;
            case 3:
               this.buffer[this.pos++] = (byte)(this.x >> 16 & 255);
               this.buffer[this.pos++] = (byte)(this.x >> 8 & 255);
            }
         }

      }
   }

   public static boolean isBase64(byte octet) {
      return octet == 61 || octet >= 0 && octet < DECODE_TABLE.length && DECODE_TABLE[octet] != -1;
   }

   public static boolean isArrayByteBase64(byte[] arrayOctet) {
      for(int i = 0; i < arrayOctet.length; ++i) {
         if(!isBase64(arrayOctet[i]) && !isWhiteSpace(arrayOctet[i])) {
            return false;
         }
      }

      return true;
   }

   private static boolean containsBase64Byte(byte[] arrayOctet) {
      byte[] var4 = arrayOctet;
      int var3 = arrayOctet.length;

      for(int var2 = 0; var2 < var3; ++var2) {
         byte element = var4[var2];
         if(isBase64(element)) {
            return true;
         }
      }

      return false;
   }

   public static byte[] encodeBase64(byte[] binaryData) {
      return encodeBase64(binaryData, false);
   }

   public static String encodeBase64String(byte[] binaryData) {
      return newStringUtf8(encodeBase64(binaryData, true));
   }

   public static String encodeBase64StringUnChunked(byte[] binaryData) {
      return newStringUtf8(encodeBase64(binaryData, false));
   }

   public static String encodeBase64String(byte[] binaryData, boolean useChunking) {
      return newStringUtf8(encodeBase64(binaryData, useChunking));
   }

   public static byte[] encodeBase64URLSafe(byte[] binaryData) {
      return encodeBase64(binaryData, false, true);
   }

   public static String encodeBase64URLSafeString(byte[] binaryData) {
      return newStringUtf8(encodeBase64(binaryData, false, true));
   }

   public static byte[] encodeBase64Chunked(byte[] binaryData) {
      return encodeBase64(binaryData, true);
   }

   public byte[] decode(String pArray) {
      return this.decode(this.getBytesUtf8(pArray));
   }

   private byte[] getBytesUtf8(String pArray) {
      try {
         return pArray.getBytes("UTF8");
      } catch (UnsupportedEncodingException var3) {
         throw new RuntimeException(var3);
      }
   }

   public byte[] decode(byte[] pArray) {
      this.reset();
      if(pArray != null && pArray.length != 0) {
         long len = (long)(pArray.length * 3 / 4);
         byte[] buf = new byte[(int)len];
         this.setInitialBuffer(buf, 0, buf.length);
         this.decode(pArray, 0, pArray.length);
         this.decode(pArray, 0, -1);
         byte[] result = new byte[this.pos];
         this.readResults(result, 0, result.length);
         return result;
      } else {
         return pArray;
      }
   }

   public static byte[] encodeBase64(byte[] binaryData, boolean isChunked) {
      return encodeBase64(binaryData, isChunked, false);
   }

   public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe) {
      return encodeBase64(binaryData, isChunked, urlSafe, Integer.MAX_VALUE);
   }

   public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe, int maxResultSize) {
      if(binaryData != null && binaryData.length != 0) {
         long len = getEncodeLength(binaryData, isChunked?76:0, isChunked?CHUNK_SEPARATOR:EMPTY_BYTE_ARRAY);
         if(len > (long)maxResultSize) {
            throw new IllegalArgumentException("Input array too big, the output array would be bigger (" + len + ") than the specified maxium size of " + maxResultSize);
         } else {
            Base64 b64 = isChunked?new Base64(urlSafe):new Base64(0, CHUNK_SEPARATOR, urlSafe);
            return b64.encode(binaryData);
         }
      } else {
         return binaryData;
      }
   }

   public static byte[] decodeBase64(String base64String) {
      return (new Base64()).decode(base64String);
   }

   public static byte[] decodeBase64(byte[] base64Data) {
      return (new Base64()).decode(base64Data);
   }

   private static boolean isWhiteSpace(byte byteToCheck) {
      switch(byteToCheck) {
      case 9:
      case 10:
      case 13:
      case 32:
         return true;
      default:
         return false;
      }
   }

   public String encodeToString(byte[] pArray) {
      return newStringUtf8(this.encode(pArray));
   }

   private static String newStringUtf8(byte[] encode) {
      String str = null;

      try {
         str = new String(encode, "UTF8");
         return str;
      } catch (UnsupportedEncodingException var3) {
         throw new RuntimeException(var3);
      }
   }

   public byte[] encode(byte[] pArray) {
      this.reset();
      if(pArray != null && pArray.length != 0) {
         long len = getEncodeLength(pArray, this.lineLength, this.lineSeparator);
         byte[] buf = new byte[(int)len];
         this.setInitialBuffer(buf, 0, buf.length);
         this.encode(pArray, 0, pArray.length);
         this.encode(pArray, 0, -1);
         if(this.buffer != buf) {
            this.readResults(buf, 0, buf.length);
         }

         if(this.isUrlSafe() && this.pos < buf.length) {
            byte[] smallerBuf = new byte[this.pos];
            System.arraycopy(buf, 0, smallerBuf, 0, this.pos);
            buf = smallerBuf;
         }

         return buf;
      } else {
         return pArray;
      }
   }

   private static long getEncodeLength(byte[] pArray, int chunkSize, byte[] chunkSeparator) {
      chunkSize = chunkSize / 4 * 4;
      long len = (long)(pArray.length * 4 / 3);
      long mod = len % 4L;
      if(mod != 0L) {
         len += 4L - mod;
      }

      if(chunkSize > 0) {
         boolean lenChunksPerfectly = len % (long)chunkSize == 0L;
         len += len / (long)chunkSize * (long)chunkSeparator.length;
         if(!lenChunksPerfectly) {
            len += (long)chunkSeparator.length;
         }
      }

      return len;
   }

   public static BigInteger decodeInteger(byte[] pArray) {
      return new BigInteger(1, decodeBase64(pArray));
   }

   public static byte[] encodeInteger(BigInteger bigInt) {
      if(bigInt == null) {
         throw new NullPointerException("encodeInteger called with null parameter");
      } else {
         return encodeBase64(toIntegerBytes(bigInt), false);
      }
   }

   static byte[] toIntegerBytes(BigInteger bigInt) {
      int bitlen = bigInt.bitLength();
      bitlen = bitlen + 7 >> 3 << 3;
      byte[] bigBytes = bigInt.toByteArray();
      if(bigInt.bitLength() % 8 != 0 && bigInt.bitLength() / 8 + 1 == bitlen / 8) {
         return bigBytes;
      } else {
         byte startSrc = 0;
         int len = bigBytes.length;
         if(bigInt.bitLength() % 8 == 0) {
            startSrc = 1;
            --len;
         }

         int startDst = bitlen / 8 - len;
         byte[] resizedBytes = new byte[bitlen / 8];
         System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, len);
         return resizedBytes;
      }
   }

   private void reset() {
      this.buffer = null;
      this.pos = 0;
      this.readPos = 0;
      this.currentLinePos = 0;
      this.modulus = 0;
      this.eof = false;
   }

   int getLineLength() {
      return this.lineLength;
   }

   byte[] getLineSeparator() {
      return (byte[])this.lineSeparator.clone();
   }
}
