package org.sdg.xdman.util;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URI;
import org.sdg.xdman.util.XDMUtil;

public class LinuxUtil {
   public static void createDesktopFile(String target, boolean min) {
      try {
         StringBuffer e = new StringBuffer();
         e.append("[Desktop Entry]\n");
         e.append("Encoding=UTF-8\n");
         e.append("Version=1.0\n");
         e.append("Type=Application\n");
         e.append("Terminal=false\n");
         String jarPath = XDMUtil.getJarPath();
         e.append("Exec=\"" + (new File(System.getProperty("java.home"), "bin/java")).getAbsolutePath() + "\" -jar \"" + (new File(jarPath, "xdm.jar")).getAbsolutePath() + "\"" + (min?" -m":"") + "\n");
         e.append("Name=Xtreme Download Manager\n");
         e.append("Comment=Powerfull download accelarator and video downloader\n");
         e.append("Categories=Network;\n");
         e.append("Icon=" + (new File(jarPath, "icon.png")).getAbsolutePath() + "\n");
         File desktop = new File(target, "xdm.desktop");
         FileOutputStream out = new FileOutputStream(desktop);
         out.write(e.toString().getBytes());
         out.close();
         desktop.setExecutable(true);
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   public static boolean disableAutoStartLinux() {
      String[] autoStartDirs = new String[]{".config/autostart"};
      File home = new File(System.getProperty("user.home"));
      File autoStartDir = null;

      for(int i = 0; i < autoStartDirs.length; ++i) {
         autoStartDir = new File(home, autoStartDirs[i]);
         if(!autoStartDir.exists()) {
            autoStartDir = null;
         } else {
            File file = new File(autoStartDir, "xdm.desktop");
            if(file.exists() && file.delete()) {
               return true;
            }
         }
      }

      return false;
   }

   public static void browse(URI uri) {
      try {
         ProcessBuilder e = new ProcessBuilder(new String[0]);
         e.command(new String[]{"xdg-open", uri.toString()});
         e.start().waitFor();
      } catch (Exception var4) {
         try {
            if(Desktop.isDesktopSupported()) {
               Desktop.getDesktop().browse(uri);
            }
         } catch (Exception var3) {
            ;
         }
      }

   }

   public static void open(final File f) {
      (new Thread() {
         public void run() {
            try {
               ProcessBuilder e = new ProcessBuilder(new String[0]);
               e.command(new String[]{"xdg-open", f.getAbsolutePath()});
               e.start().waitFor();
            } catch (Exception var4) {
               var4.printStackTrace();

               try {
                  if(Desktop.isDesktopSupported()) {
                     Desktop.getDesktop().open(f);
                  }
               } catch (Exception var3) {
                  var3.printStackTrace();
               }
            }

         }
      }).start();
   }

   public static void enableAutoStartLinux() {
      String autoStartDir = ".config/autostart";
      File home = new File(System.getProperty("user.home"));
      File fAutoStartDir = new File(home, autoStartDir);
      if(!fAutoStartDir.exists()) {
         fAutoStartDir.mkdirs();
      }

      createDesktopFile(fAutoStartDir.getAbsolutePath(), true);
   }

   public static boolean attachProxy() {
      return setGnome3Proxy() || setGnome2Proxy() || setGnomeProxy() || setKDEProxy();
   }

   static boolean setGnome3Proxy() {
      try {
         Process e = Runtime.getRuntime().exec("gsettings set org.gnome.system.proxy autoconfig-url \'http://127.0.0.1:9614/proxy.pac\'");
         e.waitFor();
         if(e.exitValue() != 0) {
            System.out.println("gsettings Exit code: " + e.exitValue());
            return false;
         } else {
            e = Runtime.getRuntime().exec("gsettings set org.gnome.system.proxy mode \'auto\'");
            e.waitFor();
            if(e.exitValue() != 0) {
               System.out.println("gsettings Exit code: " + e.exitValue());
               return false;
            } else {
               return true;
            }
         }
      } catch (Exception var1) {
         var1.printStackTrace();
         return false;
      }
   }

   static boolean setGnome2Proxy() {
      try {
         Process e = Runtime.getRuntime().exec("gconftool-2 -s /system/proxy/autoconfig_url --type string \'http://127.0.0.1:9614/proxy.pac\'");
         e.waitFor();
         if(e.exitValue() != 0) {
            System.out.println("gconftool-2 Exit code: " + e.exitValue());
            return false;
         } else {
            e = Runtime.getRuntime().exec("gconftool-2 -s /system/proxy/mode --type string \'auto\'");
            e.waitFor();
            if(e.exitValue() != 0) {
               System.out.println("gconftool-2 Exit code: " + e.exitValue());
               return false;
            } else {
               return true;
            }
         }
      } catch (Exception var1) {
         var1.printStackTrace();
         return false;
      }
   }

   static boolean setGnomeProxy() {
      try {
         Process e = Runtime.getRuntime().exec("gconftool -s /system/proxy/autoconfig_url --type string \'http://127.0.0.1:9614/proxy.pac\'");
         e.waitFor();
         if(e.exitValue() != 0) {
            System.out.println("gconftool Exit code: " + e.exitValue());
            return false;
         } else {
            e = Runtime.getRuntime().exec("gconftool -s /system/proxy/mode --type string \'auto\'");
            e.waitFor();
            if(e.exitValue() != 0) {
               System.out.println("gconftool Exit code: " + e.exitValue());
               return false;
            } else {
               return true;
            }
         }
      } catch (Exception var1) {
         var1.printStackTrace();
         return false;
      }
   }

   static boolean setKDEProxy() {
      try {
         String e = ".kde/share/config/kioslaverc";
         String kde4 = ".kde4/share/config/kioslaverc";
         String tmp = ".kioslaverc_tmp";
         File tmp_file = new File(System.getProperty("user.home"), tmp);
         File fkde = new File(System.getProperty("user.home"), e);
         if(!fkde.exists()) {
            fkde = new File(System.getProperty("user.home"), kde4);
            if(!fkde.exists()) {
               return false;
            }
         }

         boolean proxySettingsFound = false;
         boolean autoConfigFound = false;
         boolean proxyTypeFound = false;
         BufferedReader br = new BufferedReader(new FileReader(fkde));
         BufferedWriter bw = new BufferedWriter(new FileWriter(tmp_file));

         while(true) {
            String line = br.readLine();
            if(line == null) {
               br.close();
               bw.close();
               if(!proxySettingsFound) {
                  br = new BufferedReader(new FileReader(tmp_file));
                  bw = new BufferedWriter(new FileWriter(fkde));

                  while(true) {
                     line = br.readLine();
                     if(line == null) {
                        bw.write("[Proxy Settings]\nProxy Config Script=http://127.0.0.1:9614/proxy.pac\nProxyType=2\n");
                        br.close();
                        bw.close();
                        break;
                     }

                     bw.write(line + "\n");
                  }
               } else {
                  br = new BufferedReader(new FileReader(tmp_file));
                  bw = new BufferedWriter(new FileWriter(fkde));

                  while(true) {
                     line = br.readLine();
                     if(line == null) {
                        br.close();
                        bw.close();
                        break;
                     }

                     if(line.equals("[Proxy Settings]")) {
                        bw.write(line + "\n");
                        if(!autoConfigFound) {
                           bw.write("Proxy Config Script=http://127.0.0.1:9614/proxy.pac\n");
                        }

                        if(!proxyTypeFound) {
                           bw.write("ProxyType=2\n");
                        }
                     } else {
                        if(line.startsWith("Proxy Config Script")) {
                           bw.write("Proxy Config Script=http://127.0.0.1:9614/proxy.pac\n");
                        }

                        if(line.startsWith("ProxyType")) {
                           bw.write("ProxyType=2\n");
                        } else {
                           bw.write(line + "\n");
                        }
                     }
                  }
               }

               return true;
            }

            bw.write(line + "\n");
            if(line.equals("[Proxy Settings]")) {
               proxySettingsFound = true;
            }

            if(line.startsWith("Proxy Config Script")) {
               autoConfigFound = true;
            }

            if(line.startsWith("ProxyType")) {
               proxyTypeFound = true;
            }
         }
      } catch (Exception var11) {
         System.out.println(var11);
         var11.printStackTrace();
         return false;
      }
   }
}
