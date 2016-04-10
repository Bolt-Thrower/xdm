package org.sdg.xdman.plugin.youtube;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import org.sdg.xdman.plugin.youtube.ParserProgressListner;
import org.sdg.xdman.plugin.youtube.YTVideoInfo;
import org.sdg.xdman.plugin.youtube.YouTubeFmtMap;
import org.sdg.xdman.util.MIMEUtil;

public class JSONParser {
   public ParserProgressListner plistener;
   long read;

   public ArrayList list(InputStream in) throws Exception {
      ArrayList list = new ArrayList();

      do {
         String map;
         do {
            String ln = this.readJSLine(in);
            if(ln == null) {
               return list;
            }

            map = this.parseJSONLine(ln);
         } while(map == null);

         String[] arr = map.split(",");

         for(int i = 0; i < arr.length; ++i) {
            YTVideoInfo info = this.getVideoFmt(this.decodeJSONEscape(arr[i]));
            if(info != null) {
               list.add(info);
            }
         }
      } while(list.size() <= 0);

      return list;
   }

   private YTVideoInfo getVideoFmt(String encoded_str) throws Exception {
      String[] enc_arr = encoded_str.split("&");
      boolean isEncrypted = false;
      int itag = 0;
      String url = "";
      String sig = "";
      String quality = "";
      String type = "";

      for(int info = 0; info < enc_arr.length; ++info) {
         String enc_str = enc_arr[info];
         int index = enc_str.indexOf("=");
         if(index > 0) {
            String key = enc_str.substring(0, index);
            String value = enc_str.substring(index + 1);
            value = value.replace("\\", "");
            System.out.println("YT_KEY: " + key + " VALUE: " + value);
            int itg;
            if(key.trim().equals("url")) {
               url = URLDecoder.decode(value, "utf-8");
               itg = url.indexOf(";");
               if(itg > 0) {
                  url = url.substring(0, itg);
               }

               itg = url.indexOf(",");
               if(itg > 0) {
                  url = url.substring(0, itg);
                  url = url.replace("\\", "");
               }
            }

            if(key.trim().equals("s")) {
               isEncrypted = true;
            }

            if(key.trim().equals("sig")) {
               sig = value;
               itg = value.indexOf(";");
               if(itg > 0) {
                  sig = value.substring(0, itg);
               }

               itg = sig.indexOf(",");
               if(itg > 0) {
                  sig = sig.substring(0, itg);
                  sig = sig.replace("\\", "");
               }
            }

            if(key.trim().equals("itag")) {
               try {
                  String var18 = URLDecoder.decode(value, "utf-8");
                  var18 = var18.replace("\\", "");
                  var18 = var18.trim();
                  itag = Integer.parseInt(var18);
               } catch (Exception var16) {
                  ;
               }
            }

            if(key.trim().equals("quality")) {
               quality = URLDecoder.decode(value, "utf-8");
               quality = quality.replace("\\", "");
            }

            if(key.trim().equals("type")) {
               type = URLDecoder.decode(value, "utf-8");
               type = type.replace("\\", "");
            }
         }
      }

      if(url.length() >= 1 && !isEncrypted) {
         YTVideoInfo var17 = new YTVideoInfo();
         var17.url = url + (sig.length() < 1?"":"&signature=" + sig);
         var17.itag = YouTubeFmtMap.getFMTInfo(String.valueOf(itag));
         var17.quality = quality;
         if(type != null) {
            try {
               type = type.split(";")[0].trim();
            } catch (Exception var15) {
               ;
            }
         }

         type = MIMEUtil.getFileExt(type);
         var17.type = type;
         return var17;
      } else {
         return null;
      }
   }

   private String decodeJSONEscape(String json) {
      StringBuffer buf = new StringBuffer();
      int pos = 0;

      while(true) {
         int index = json.indexOf("\\u", pos);
         if(index < 0) {
            if(pos < json.length()) {
               buf.append(json.substring(pos));
            }

            return buf.toString();
         }

         buf.append(json.substring(pos, index));
         String code = json.substring(index + 2, index + 2 + 4);
         int char_code = Integer.parseInt(code, 16);
         buf.append((char)char_code);
         pos = index + 6;
      }
   }

   private String parseJSONLine(String line) {
      String key = "url_encoded_fmt_stream_map";
      int index = line.indexOf(key);
      if(index < 0) {
         return null;
      } else {
         int colonIndex = line.indexOf(58, index + key.length());
         if(colonIndex < 0) {
            return null;
         } else {
            int quoteStartIndex = line.indexOf(34, colonIndex);
            if(quoteStartIndex < 0) {
               return null;
            } else {
               int quoteEndIndex = line.indexOf(34, quoteStartIndex + 1);
               if(quoteEndIndex < 0) {
                  return null;
               } else {
                  String url_encoded_fmt_stream = line.substring(quoteStartIndex + 1, quoteEndIndex);
                  return url_encoded_fmt_stream;
               }
            }
         }
      }
   }

   private String readJSLine(InputStream in) throws Exception {
      StringBuffer buf = null;

      while(true) {
         int x = in.read();
         if(x == -1) {
            break;
         }

         ++this.read;
         if(buf == null) {
            buf = new StringBuffer();
         }

         if(x != 10 && x != 13) {
            if(x == 59) {
               break;
            }

            buf.append((char)x);
         }
      }

      if(this.plistener != null) {
         this.plistener.update(this.read);
      }

      return buf == null?null:buf.toString().trim();
   }
}
