package org.sdg.xdman.util;

public final class Base64 {
   private static final int BASELENGTH = 255;
   private static final int LOOKUPLENGTH = 64;
   private static final int TWENTYFOURBITGROUP = 24;
   private static final int EIGHTBIT = 8;
   private static final int SIXTEENBIT = 16;
   private static final int FOURBYTE = 4;
   private static final int SIGN = -128;
   private static final char PAD = '=';
   private static final boolean fDebug = false;
   private static final byte[] base64Alphabet = new byte[255];
   private static final char[] lookUpBase64Alphabet = new char[64];

   static {
      int i;
      for(i = 0; i < 255; ++i) {
         base64Alphabet[i] = -1;
      }

      for(i = 90; i >= 65; --i) {
         base64Alphabet[i] = (byte)(i - 65);
      }

      for(i = 122; i >= 97; --i) {
         base64Alphabet[i] = (byte)(i - 97 + 26);
      }

      for(i = 57; i >= 48; --i) {
         base64Alphabet[i] = (byte)(i - 48 + 52);
      }

      base64Alphabet[43] = 62;
      base64Alphabet[47] = 63;

      for(i = 0; i <= 25; ++i) {
         lookUpBase64Alphabet[i] = (char)(65 + i);
      }

      i = 26;

      int j;
      for(j = 0; i <= 51; ++j) {
         lookUpBase64Alphabet[i] = (char)(97 + j);
         ++i;
      }

      i = 52;

      for(j = 0; i <= 61; ++j) {
         lookUpBase64Alphabet[i] = (char)(48 + j);
         ++i;
      }

      lookUpBase64Alphabet[62] = 43;
      lookUpBase64Alphabet[63] = 47;
   }

   protected static boolean isWhiteSpace(char octect) {
      return octect == 32 || octect == 13 || octect == 10 || octect == 9;
   }

   protected static boolean isPad(char octect) {
      return octect == 61;
   }

   protected static boolean isData(char octect) {
      return base64Alphabet[octect] != -1;
   }

   protected static boolean isBase64(char octect) {
      return isWhiteSpace(octect) || isPad(octect) || isData(octect);
   }

   public static String encode(byte[] binaryData) {
      if(binaryData == null) {
         return null;
      } else {
         int lengthDataBits = binaryData.length * 8;
         if(lengthDataBits == 0) {
            return "";
         } else {
            int fewerThan24bits = lengthDataBits % 24;
            int numberTriplets = lengthDataBits / 24;
            int numberQuartet = fewerThan24bits != 0?numberTriplets + 1:numberTriplets;
            int numberLines = (numberQuartet - 1) / 19 + 1;
            char[] encodedData = (char[])null;
            encodedData = new char[numberQuartet * 4 + numberLines];
            boolean k = false;
            boolean l = false;
            boolean b1 = false;
            boolean b2 = false;
            boolean b3 = false;
            int encodedIndex = 0;
            int dataIndex = 0;
            int i = 0;

            byte val3;
            byte var21;
            byte var20;
            byte var23;
            byte var22;
            byte var24;
            for(int val1 = 0; val1 < numberLines - 1; ++val1) {
               for(int val2 = 0; val2 < 19; ++val2) {
                  var22 = binaryData[dataIndex++];
                  var24 = binaryData[dataIndex++];
                  var23 = binaryData[dataIndex++];
                  var21 = (byte)(var24 & 15);
                  var20 = (byte)(var22 & 3);
                  val3 = (var22 & -128) == 0?(byte)(var22 >> 2):(byte)(var22 >> 2 ^ 192);
                  byte val21 = (var24 & -128) == 0?(byte)(var24 >> 4):(byte)(var24 >> 4 ^ 240);
                  byte val31 = (var23 & -128) == 0?(byte)(var23 >> 6):(byte)(var23 >> 6 ^ 252);
                  encodedData[encodedIndex++] = lookUpBase64Alphabet[val3];
                  encodedData[encodedIndex++] = lookUpBase64Alphabet[val21 | var20 << 4];
                  encodedData[encodedIndex++] = lookUpBase64Alphabet[var21 << 2 | val31];
                  encodedData[encodedIndex++] = lookUpBase64Alphabet[var23 & 63];
                  ++i;
               }

               encodedData[encodedIndex++] = 10;
            }

            byte var25;
            byte var26;
            while(i < numberTriplets) {
               var22 = binaryData[dataIndex++];
               var24 = binaryData[dataIndex++];
               var23 = binaryData[dataIndex++];
               var21 = (byte)(var24 & 15);
               var20 = (byte)(var22 & 3);
               var26 = (var22 & -128) == 0?(byte)(var22 >> 2):(byte)(var22 >> 2 ^ 192);
               var25 = (var24 & -128) == 0?(byte)(var24 >> 4):(byte)(var24 >> 4 ^ 240);
               val3 = (var23 & -128) == 0?(byte)(var23 >> 6):(byte)(var23 >> 6 ^ 252);
               encodedData[encodedIndex++] = lookUpBase64Alphabet[var26];
               encodedData[encodedIndex++] = lookUpBase64Alphabet[var25 | var20 << 4];
               encodedData[encodedIndex++] = lookUpBase64Alphabet[var21 << 2 | val3];
               encodedData[encodedIndex++] = lookUpBase64Alphabet[var23 & 63];
               ++i;
            }

            if(fewerThan24bits == 8) {
               var22 = binaryData[dataIndex];
               var20 = (byte)(var22 & 3);
               var26 = (var22 & -128) == 0?(byte)(var22 >> 2):(byte)(var22 >> 2 ^ 192);
               encodedData[encodedIndex++] = lookUpBase64Alphabet[var26];
               encodedData[encodedIndex++] = lookUpBase64Alphabet[var20 << 4];
               encodedData[encodedIndex++] = 61;
               encodedData[encodedIndex++] = 61;
            } else if(fewerThan24bits == 16) {
               var22 = binaryData[dataIndex];
               var24 = binaryData[dataIndex + 1];
               var21 = (byte)(var24 & 15);
               var20 = (byte)(var22 & 3);
               var26 = (var22 & -128) == 0?(byte)(var22 >> 2):(byte)(var22 >> 2 ^ 192);
               var25 = (var24 & -128) == 0?(byte)(var24 >> 4):(byte)(var24 >> 4 ^ 240);
               encodedData[encodedIndex++] = lookUpBase64Alphabet[var26];
               encodedData[encodedIndex++] = lookUpBase64Alphabet[var25 | var20 << 4];
               encodedData[encodedIndex++] = lookUpBase64Alphabet[var21 << 2];
               encodedData[encodedIndex++] = 61;
            }

            encodedData[encodedIndex] = 10;
            return new String(encodedData);
         }
      }
   }

   public static byte[] decode(String encoded) {
      if(encoded == null) {
         return null;
      } else {
         char[] base64Data = encoded.toCharArray();
         int len = removeWhiteSpace(base64Data);
         if(len % 4 != 0) {
            return null;
         } else {
            int numberQuadruple = len / 4;
            if(numberQuadruple == 0) {
               return new byte[0];
            } else {
               byte[] decodedData = (byte[])null;
               boolean b1 = false;
               boolean b2 = false;
               boolean b3 = false;
               boolean b4 = false;
               boolean d1 = false;
               boolean d2 = false;
               boolean d3 = false;
               boolean d4 = false;
               int i = 0;
               int encodedIndex = 0;
               int dataIndex = 0;

               byte var17;
               byte var19;
               byte var18;
               char var21;
               byte var20;
               char var23;
               char var22;
               char var24;
               for(decodedData = new byte[numberQuadruple * 3]; i < numberQuadruple - 1; ++i) {
                  if(!isData(var21 = base64Data[dataIndex++]) || !isData(var23 = base64Data[dataIndex++]) || !isData(var22 = base64Data[dataIndex++]) || !isData(var24 = base64Data[dataIndex++])) {
                     return null;
                  }

                  var17 = base64Alphabet[var21];
                  var18 = base64Alphabet[var23];
                  var19 = base64Alphabet[var22];
                  var20 = base64Alphabet[var24];
                  decodedData[encodedIndex++] = (byte)(var17 << 2 | var18 >> 4);
                  decodedData[encodedIndex++] = (byte)((var18 & 15) << 4 | var19 >> 2 & 15);
                  decodedData[encodedIndex++] = (byte)(var19 << 6 | var20);
               }

               if(isData(var21 = base64Data[dataIndex++]) && isData(var23 = base64Data[dataIndex++])) {
                  var17 = base64Alphabet[var21];
                  var18 = base64Alphabet[var23];
                  var22 = base64Data[dataIndex++];
                  var24 = base64Data[dataIndex++];
                  if(isData(var22) && isData(var24)) {
                     var19 = base64Alphabet[var22];
                     var20 = base64Alphabet[var24];
                     decodedData[encodedIndex++] = (byte)(var17 << 2 | var18 >> 4);
                     decodedData[encodedIndex++] = (byte)((var18 & 15) << 4 | var19 >> 2 & 15);
                     decodedData[encodedIndex++] = (byte)(var19 << 6 | var20);
                     return decodedData;
                  } else {
                     byte[] tmp;
                     if(isPad(var22) && isPad(var24)) {
                        if((var18 & 15) != 0) {
                           return null;
                        } else {
                           tmp = new byte[i * 3 + 1];
                           System.arraycopy(decodedData, 0, tmp, 0, i * 3);
                           tmp[encodedIndex] = (byte)(var17 << 2 | var18 >> 4);
                           return tmp;
                        }
                     } else if(!isPad(var22) && isPad(var24)) {
                        var19 = base64Alphabet[var22];
                        if((var19 & 3) != 0) {
                           return null;
                        } else {
                           tmp = new byte[i * 3 + 2];
                           System.arraycopy(decodedData, 0, tmp, 0, i * 3);
                           tmp[encodedIndex++] = (byte)(var17 << 2 | var18 >> 4);
                           tmp[encodedIndex] = (byte)((var18 & 15) << 4 | var19 >> 2 & 15);
                           return tmp;
                        }
                     } else {
                        return null;
                     }
                  }
               } else {
                  return null;
               }
            }
         }
      }
   }

   protected static int removeWhiteSpace(char[] data) {
      if(data == null) {
         return 0;
      } else {
         int newSize = 0;
         int len = data.length;

         for(int i = 0; i < len; ++i) {
            if(!isWhiteSpace(data[i])) {
               data[newSize++] = data[i];
            }
         }

         return newSize;
      }
   }
}
