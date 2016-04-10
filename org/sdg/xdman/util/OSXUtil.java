package org.sdg.xdman.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import org.sdg.xdman.gui.StaticResource;
import org.sdg.xdman.util.XDMUtil;

public class OSXUtil {
   public static void createAppBundle(File folder) {
      try {
         File e = new File(folder, "xdm.app/Contents/MacOS");
         e.mkdirs();
         File fExe = new File(e, "xdm");
         FileWriter fw = new FileWriter(fExe);
         fw.write("#!/bin/sh\n\"" + (new File(System.getProperty("java.home"), "bin/java")).getAbsolutePath() + "\"" + " -Xdock:name=XDM -jar \"" + (new File(XDMUtil.getJarPath(), "xdm.jar")).getAbsolutePath() + "\"");
         fw.close();
         fExe.setExecutable(true);
         File fRes = new File(folder, "xdm.app/Contents/Resources");
         fRes.mkdirs();
         XDMUtil.copyStream(OSXUtil.class.getResourceAsStream("/Resources/OSX/icon.icns"), new FileOutputStream(new File(fRes, "icon.icns")));
         XDMUtil.copyStream(OSXUtil.class.getResourceAsStream("/Resources/OSX/Info.plist"), new FileOutputStream(new File(new File(folder, "xdm.app/Contents"), "Info.plist")));
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public static void createFixedAppBundle(File folder) {
      try {
         File e = new File(folder, "xdm.app/Contents/MacOS");
         e.mkdirs();
         File fExe = new File(e, "xdm");
         FileWriter fw = new FileWriter(fExe);
         fw.write("#!/bin/sh\n\"" + (new File(System.getProperty("java.home"), "bin/java")).getAbsolutePath() + "\"" + " -Xdock:name=XDM -jar \"" + (new File("/Applications/xdm.app/Contents/Resources", "xdm.jar")).getAbsolutePath() + "\"");
         fw.close();
         fExe.setExecutable(true);
         System.out.println("chmod 755 \"" + fExe + "\"");
         System.out.println("chmod " + Runtime.getRuntime().exec("chmod 755 \"" + fExe + "\"").waitFor());
         File fRes = new File(folder, "xdm.app/Contents/Resources");
         fRes.mkdirs();
         XDMUtil.copyStream(OSXUtil.class.getResourceAsStream("/Resources/OSX/icon.icns"), new FileOutputStream(new File(fRes, "icon.icns")));
         XDMUtil.copyStream(new FileInputStream(new File(XDMUtil.getJarPath(), "xdm.jar")), new FileOutputStream(new File(fRes, "xdm.jar")));
         XDMUtil.copyStream(OSXUtil.class.getResourceAsStream("/Resources/OSX/Info.plist"), new FileOutputStream(new File(new File(folder, "xdm.app/Contents"), "Info.plist")));
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public static void disableAutoStart() {
      try {
         (new File((new File(System.getProperty("user.home"), "Library/LaunchAgents/org.sdg.xdman.plist")).getAbsolutePath())).delete();
      } catch (Exception var1) {
         var1.printStackTrace();
      }

   }

   public static void enableAutoStart() {
      try {
         BufferedReader e = new BufferedReader(new InputStreamReader(StaticResource.class.getResourceAsStream("/Resources/OSX/plist")));
         StringBuilder sb = new StringBuilder();

         while(true) {
            String startupDir = e.readLine();
            if(startupDir == null) {
               File startupDir1 = new File(System.getProperty("user.home"), "Library/LaunchAgents");
               if(!startupDir1.exists()) {
                  startupDir1.mkdirs();
               }

               File f = new File(startupDir1, "org.sdg.xdman.plist");
               FileWriter fw = new FileWriter(f);
               fw.write(sb.toString());
               fw.close();
               f.setExecutable(true);
               return;
            }

            if(startupDir.contains("$JAVA")) {
               startupDir = startupDir.replace("$JAVA", (new File(System.getProperty("java.home"), "bin/java")).getAbsolutePath());
            }

            if(startupDir.contains("$JAR")) {
               startupDir = startupDir.replace("$JAR", (new File(XDMUtil.getJarPath(), "xdm.jar")).getAbsolutePath());
            }

            sb.append(startupDir + "\n");
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }
   }

   public static boolean attachProxy() {
      File scriptFile = null;

      try {
         ProcessBuilder e = new ProcessBuilder(new String[]{"osascript"});
         scriptFile = new File(System.getProperty("user.home"), ".attach");
         e.command(new String[]{"osascript", "-e", "do shell script \"sh \\\"" + scriptFile.getAbsolutePath() + "\\\"\" with administrator privileges"});
         System.out.println("do shell script \"sh \\\"" + scriptFile.getAbsolutePath() + "\\\"\" with administrator privileges");
         ArrayList list = new ArrayList();
         String script = "#!/bin/bash\n";
         Process proc = Runtime.getRuntime().exec("networksetup -listallnetworkservices");
         BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));

         while(true) {
            String fw = br.readLine();
            if(fw == null) {
               for(Iterator var7 = list.iterator(); var7.hasNext(); script = script + "networksetup -setautoproxyurl \"" + fw + "\"" + " http://127.0.0.1:9614/proxy.pac\n") {
                  fw = (String)var7.next();
               }

               FileWriter fw1 = new FileWriter(scriptFile);
               fw1.write(script);
               fw1.close();
               proc = e.start();
               boolean var9 = proc.waitFor() == 0;
               return var9;
            }

            if(!fw.contains("*")) {
               list.add(fw);
            }
         }
      } catch (Exception var17) {
         var17.printStackTrace();
      } finally {
         try {
            scriptFile.delete();
         } catch (Exception var16) {
            ;
         }

      }

      return false;
   }
}
