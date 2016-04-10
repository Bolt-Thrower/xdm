package org.sdg.xdman.plugin.youtube;

import java.util.HashMap;

public class YouTubeFmtMap {
   static HashMap itags = new HashMap();

   static {
      itags.put(Integer.valueOf(5), "320 x 240");
      itags.put(Integer.valueOf(6), "450 x 270");
      itags.put(Integer.valueOf(13), "Small");
      itags.put(Integer.valueOf(17), "176 x 144");
      itags.put(Integer.valueOf(18), "480 x 360");
      itags.put(Integer.valueOf(22), "1280 x 720");
      itags.put(Integer.valueOf(34), "480 x 360");
      itags.put(Integer.valueOf(35), "640 x 480 ");
      itags.put(Integer.valueOf(36), "320 x 240");
      itags.put(Integer.valueOf(37), "1920 x 1080");
      itags.put(Integer.valueOf(38), "2048 x 1080");
      itags.put(Integer.valueOf(43), "480 x 360");
      itags.put(Integer.valueOf(44), "640 x 480");
      itags.put(Integer.valueOf(45), "1280 x 720");
      itags.put(Integer.valueOf(46), "1920 x 1080");
      itags.put(Integer.valueOf(59), "854 x 480");
      itags.put(Integer.valueOf(78), "854 x 480");
      itags.put(Integer.valueOf(82), "480 x 360   3D");
      itags.put(Integer.valueOf(83), "640 x 480   3D");
      itags.put(Integer.valueOf(84), "1280 x 720   3D");
      itags.put(Integer.valueOf(85), "1920 x 1080  3D");
      itags.put(Integer.valueOf(100), "480 x 360   3D");
      itags.put(Integer.valueOf(101), "640 x 480   3D");
      itags.put(Integer.valueOf(102), "1280 x 720   3D");
      itags.put(Integer.valueOf(133), "320 x 240");
      itags.put(Integer.valueOf(134), "480 x 360");
      itags.put(Integer.valueOf(135), "640 x 480");
      itags.put(Integer.valueOf(136), "1280 x 720");
      itags.put(Integer.valueOf(137), "1920 x 1080");
      itags.put(Integer.valueOf(139), "Low bitrate");
      itags.put(Integer.valueOf(140), "Med bitrate");
      itags.put(Integer.valueOf(141), "Hi  bitrate");
      itags.put(Integer.valueOf(160), "256 x 144");
      itags.put(Integer.valueOf(167), "360p");
      itags.put(Integer.valueOf(168), "480p");
      itags.put(Integer.valueOf(169), "720p");
      itags.put(Integer.valueOf(170), "1080p");
      itags.put(Integer.valueOf(171), "Med bitrate");
      itags.put(Integer.valueOf(172), "Hi  bitrate");
      itags.put(Integer.valueOf(218), "480");
      itags.put(Integer.valueOf(219), "480");
      itags.put(Integer.valueOf(242), "320 x 240");
      itags.put(Integer.valueOf(243), "480 x 360");
      itags.put(Integer.valueOf(244), "640 x 480");
      itags.put(Integer.valueOf(245), "640 x 480");
      itags.put(Integer.valueOf(246), "640 x 480");
      itags.put(Integer.valueOf(247), "1280 x 720");
      itags.put(Integer.valueOf(248), "1920 x 1080");
      itags.put(Integer.valueOf(264), "1440p");
      itags.put(Integer.valueOf(266), "2160p");
      itags.put(Integer.valueOf(271), "1440p");
      itags.put(Integer.valueOf(272), "2160p");
      itags.put(Integer.valueOf(278), "144p");
      itags.put(Integer.valueOf(298), "720p");
      itags.put(Integer.valueOf(302), "720p");
      itags.put(Integer.valueOf(303), "1080p");
      itags.put(Integer.valueOf(308), "1440p");
      itags.put(Integer.valueOf(313), "2160p");
      itags.put(Integer.valueOf(315), "2160p");
      itags.put(Integer.valueOf(299), "2160p");
   }

   public static String getFMTInfo(String itag) {
      int i = 0;

      try {
         i = Integer.parseInt(itag);
      } catch (Exception var3) {
         ;
      }

      return (String)itags.get(Integer.valueOf(i));
   }
}
