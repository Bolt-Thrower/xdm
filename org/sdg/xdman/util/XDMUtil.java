package org.sdg.xdman.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import org.sdg.xdman.core.common.IXDMConstants;
import org.sdg.xdman.util.Java6Util;
import org.sdg.xdman.util.LinuxUtil;

public class XDMUtil implements IXDMConstants {
   public static final int OS_X = 10;
   public static final int LINUX = 20;
   public static final int WINDOWS = 30;
   public static final int OS_UNKNOWN = 50;
   private static String jarPath = null;
   static final int MB = 1048576;
   static final int KB = 1024;
   static String[] doc = new String[]{".doc", ".docx", ".txt", ".pdf", ".rtf", ".xml", ".c", ".cpp", ".java", ".cs", ".vb", ".html", ".htm", ".chm", ".xls", ".xlsx", ".ppt", ".pptx"};
   static String[] cmp = new String[]{".7z", ".zip", ".rar", ".gz", ".tgz", ".tbz2", ".bz2", ".lzh", ".sit", ".z"};
   static String[] music = new String[]{".mp3", ".wma", ".ogg", ".aiff", ".au", ".mid", ".midi", ".mp2", ".mpa", ".wav", ".aac"};
   static String[] vid = new String[]{".mpg", ".mpeg", ".avi", ".flv", ".asf", ".mov", ".mpe", ".wmv", ".mkv", ".mp4", ".3gp", ".divx", ".vob", ".webm"};
   static String[] prog = new String[]{".exe", ".msi", ".bin", ".sh", ".deb", ".cab", ".cpio", ".dll", ".jar"};
   static char[] invalid_chars = new char[]{'/', '\\', '\"', '?', '*', '<', '>', ':'};
   static long counter = 1L;

   public static void browse(String url) {
      try {
         if(System.getProperty("os.name").toLowerCase().contains("linux")) {
            LinuxUtil.browse(new URI(url));
         } else {
            Desktop.getDesktop().browse(new URI(url));
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   public static String getJarPath() {
      if(jarPath == null) {
         try {
            String e = XDMUtil.class.getResource("/res/icon.png").toString();
            e = e.replace("jar:", "");
            int exx = e.lastIndexOf("!");
            if(exx > 0) {
               e = e.substring(0, exx);
            }

            String path = (new URI(e)).getPath();
            jarPath = (new File(path)).getParent();
         } catch (Exception var4) {
            var4.printStackTrace();

            try {
               jarPath = (new File(XDMUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getParent();
            } catch (Exception var3) {
               var4.printStackTrace();
            }
         }
      }

      return jarPath;
   }

   public static int getOS() {
      String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
      return !os.contains("OS X") && !os.contains("Mac") && !os.contains("Darwin") && !os.contains("os X") && !os.contains("os x")?(!os.contains("Linux") && !os.contains("LINUX") && !os.contains("linux")?(!os.contains("WINDOWS") && !os.contains("Windows") && !os.contains("windows")?50:30):20):10;
   }

   public static boolean isNullOrEmpty(String str) {
      return str == null || str.length() < 1;
   }

   public static String getFormattedLength(double length) {
      return length < 0.0D?"---":(length > 1048576.0D?String.format("%.1f MB", new Object[]{Float.valueOf((float)length / 1048576.0F)}):(length > 1024.0D?String.format("%.1f KB", new Object[]{Float.valueOf((float)length / 1024.0F)}):String.format("%d B", new Object[]{Integer.valueOf((int)length)})));
   }

   public static String getETA(double length, float rate) {
      if(length == 0.0D) {
         return "0 second.";
      } else if(length >= 1.0D && rate > 0.0F) {
         int sec = (int)(length / (double)rate);
         return hms(sec);
      } else {
         return "---";
      }
   }

   static String hms(int sec) {
      boolean hrs = false;
      boolean min = false;
      int hrs1 = sec / 3600;
      int min1 = sec % 3600 / 60;
      sec %= 60;
      String str = "";
      if(hrs1 > 0) {
         str = str + hrs1 + " hour ";
      }

      str = str + min1 + " min ";
      str = str + sec + " seconds ";
      return str;
   }

   public static boolean validateURL(String url) {
      try {
         new URL(url);
         return url.startsWith("http") || url.startsWith("ftp://");
      } catch (Exception var2) {
         return false;
      }
   }

   public static String findCategory(String filename) {
      String file = filename.toLowerCase();

      int i;
      for(i = 0; i < doc.length; ++i) {
         if(file.endsWith(doc[i])) {
            return "Documents";
         }
      }

      for(i = 0; i < cmp.length; ++i) {
         if(file.endsWith(cmp[i])) {
            return "Compressed";
         }
      }

      for(i = 0; i < music.length; ++i) {
         if(file.endsWith(music[i])) {
            return "Music";
         }
      }

      for(i = 0; i < prog.length; ++i) {
         if(file.endsWith(prog[i])) {
            return "Programs";
         }
      }

      for(i = 0; i < vid.length; ++i) {
         if(file.endsWith(vid[i])) {
            return "Video";
         }
      }

      return "Other";
   }

   public static String getFileName2(String url) {
      String file = null;

      try {
         file = (new File((new URI(url)).getPath())).getName();
         System.out.println("File name: " + file);
      } catch (Exception var3) {
         ;
      }

      if(file == null || file.length() < 1) {
         file = "FILE";
      }

      return file;
   }

   public static void open(File f) {
      char ch = File.separatorChar;
      if(ch == 92) {
         openWindows(f);
      } else if(System.getProperty("os.name").toLowerCase().contains("linux")) {
         LinuxUtil.open(f);
      } else {
         Java6Util.desktop$open(f);
      }

   }

   private static void openWindows(File f) {
      try {
         ProcessBuilder e = new ProcessBuilder(new String[0]);
         ArrayList lst = new ArrayList();
         lst.add("rundll32");
         lst.add("url.dll,FileProtocolHandler");
         lst.add(f.getAbsolutePath());
         e.command(lst);
         e.start();
      } catch (IOException var3) {
         var3.printStackTrace();
      }

   }

   public static String getFileName(String uri) {
      try {
         if(uri == null) {
            return "FILE";
         } else if(!uri.equals("/") && uri.length() >= 1) {
            int e = uri.lastIndexOf("/");
            String path = uri;
            if(e > -1) {
               path = uri.substring(e);
            }

            int qindex = path.indexOf("?");
            if(qindex > -1) {
               path = path.substring(0, qindex);
            }

            path = decode(path);
            return path.length() < 1?"FILE":(path.equals("/")?"FILE":path);
         } else {
            return "FILE";
         }
      } catch (Exception var4) {
         return "FILE";
      }
   }

   public static String createSafeFileName(String str) {
      String safe_name = str;

      for(int i = 0; i < invalid_chars.length; ++i) {
         if(safe_name.indexOf(invalid_chars[i]) != -1) {
            safe_name = safe_name.replace(invalid_chars[i], '_');
         }
      }

      return safe_name;
   }

   static String getWithoutExt(String file) {
      int index = file.lastIndexOf(".");
      return index < 0?file:file.substring(0, index);
   }

   static String getExt(String file) {
      try {
         int e = file.lastIndexOf(".");
         return e < 0?"":file.substring(e);
      } catch (Exception var2) {
         return "";
      }
   }

   public static String getUniqueFileName(String dir, String f) {
      for(File target = new File(dir, f); target.exists(); target = new File(dir, f)) {
         String name = getWithoutExt(target.getName());
         int index = name.lastIndexOf(95);
         String prefix = name;
         int count = 0;
         if(index > 0 && index < name.length() - 1) {
            try {
               count = Integer.parseInt(name.substring(index + 1));
               prefix = name.substring(0, index);
            } catch (Exception var8) {
               ;
            }
         }

         ++count;
         String ext = getExt(target.getName());
         f = prefix + "_" + count + ext;
      }

      return f;
   }

   public static String decode(String str) {
      char[] ch = str.toCharArray();
      StringBuffer buf = new StringBuffer();

      for(int i = 0; i < ch.length; ++i) {
         if(ch[i] != 47 && ch[i] != 92 && ch[i] != 34 && ch[i] != 63 && ch[i] != 42 && ch[i] != 60 && ch[i] != 62 && ch[i] != 58) {
            if(ch[i] == 37 && i + 2 < ch.length) {
               int c = Integer.parseInt(String.valueOf(ch[i + 1]) + ch[i + 2], 16);
               buf.append((char)c);
               i += 2;
            } else {
               buf.append(ch[i]);
            }
         }
      }

      return buf.toString();
   }

   public static String getType(String url) {
      try {
         int e = url.indexOf("?");
         if(e > 0) {
            String substr = url.substring(e + 1);
            String[] arr = substr.split("&");

            for(int i = 0; i < arr.length; ++i) {
               if(arr[i].toLowerCase().startsWith("itag=")) {
                  return arr[i].split("=")[1];
               }
            }
         }

         return null;
      } catch (Exception var5) {
         return null;
      }
   }

   public static String nvl(Object o) {
      return o == null?"":o.toString();
   }

   public static String createURL(String str) {
      try {
         new URL(str);
         return null;
      } catch (MalformedURLException var2) {
         return "http://" + str;
      }
   }

   public static void main(String[] args) throws InterruptedException {
      System.out.println(hms(13547));
      String str = "http://sound27.mp3pk.com/indian/race2/[Songs.PK]%20Race%202%20-%2003%20-%20Lat%20Lag%20Gayee.mp3";
      System.out.println(str);
      System.out.println(decode(str) + "\n" + getFileName(str));
      System.out.println(getFileName(str));
   }

   public static synchronized File getTempFile(String tmpdir) {
      String name = String.valueOf(System.currentTimeMillis() + ++counter);
      return new File(tmpdir, name);
   }

   public static void copyStream(InputStream in, OutputStream out) throws Exception {
      byte[] b = new byte[512];

      while(true) {
         int x = in.read(b);
         if(x == -1) {
            in.close();
            out.close();
            return;
         }

         out.write(b, 0, x);
      }
   }
}
