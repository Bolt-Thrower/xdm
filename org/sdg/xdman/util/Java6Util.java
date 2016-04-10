package org.sdg.xdman.util;

import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import javax.swing.JDialog;
import javax.swing.JTable;

public class Java6Util {
   public static boolean window$setType(Window window, boolean val) {
      try {
         Class e = Class.forName("java.awt.Window.Type", true, Java6Util.class.getClassLoader());
         Enum window$Type = Enum.valueOf(e, "UTILITY");
         Method m = window.getClass().getMethod("setType", new Class[]{e});
         m.invoke(window, new Object[]{window$Type});
         return true;
      } catch (Exception var5) {
         var5.printStackTrace();
         return false;
      }
   }

   public static boolean jtable$setFillsViewportHeight(JTable table, boolean val) {
      try {
         Method e = table.getClass().getMethod("setFillsViewportHeight", new Class[]{Boolean.TYPE});
         e.invoke(table, new Object[]{Boolean.valueOf(val)});
         return true;
      } catch (Exception var3) {
         var3.printStackTrace();
         return false;
      }
   }

   public static int jtable$convertRowIndexToModel(JTable table, int index) {
      try {
         Method e = table.getClass().getMethod("convertRowIndexToModel", new Class[]{Integer.TYPE});
         return ((Integer)e.invoke(table, new Object[]{Integer.valueOf(index)})).intValue();
      } catch (Exception var3) {
         var3.printStackTrace();
         return index;
      }
   }

   public static void jdialog$setIconImage(JDialog dlg, Image image) {
      try {
         Method e = dlg.getClass().getMethod("setIconImage", new Class[]{Image.class});
         e.invoke(dlg, new Object[]{image});
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public static void jtable$setAutoCreateRowSorter(JTable table) {
      try {
         Method e = table.getClass().getMethod("setAutoCreateRowSorter", new Class[]{Boolean.TYPE});
         e.invoke(table, new Object[]{Boolean.valueOf(true)});
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   public static boolean desktop$browse(URI uri) {
      try {
         Class e = Class.forName("java.awt.Desktop");
         Method m = e.getMethod("getDesktop", new Class[0]);
         Object deskObj = m.invoke((Object)null, new Object[0]);
         m = e.getMethod("browse", new Class[]{URI.class});
         m.invoke(deskObj, new Object[]{uri});
         return true;
      } catch (Exception var4) {
         var4.printStackTrace();
         return false;
      }
   }

   public static boolean desktop$open(File file) {
      try {
         Class e = Class.forName("java.awt.Desktop");
         Method m = e.getMethod("getDesktop", new Class[0]);
         Object deskObj = m.invoke((Object)null, new Object[0]);
         m = e.getMethod("open", new Class[]{File.class});
         m.invoke(deskObj, new Object[]{file});
         return true;
      } catch (Exception var4) {
         var4.printStackTrace();
         return false;
      }
   }

   public static boolean file$setExecutable(File file) {
      try {
         Method e = file.getClass().getMethod("setExecutable", new Class[]{Boolean.TYPE});
         e.invoke(file, new Object[]{Boolean.valueOf(true)});
         return true;
      } catch (Exception var2) {
         var2.printStackTrace();
         return false;
      }
   }

   public static boolean trayIcon$isSupported() {
      try {
         Class e = Class.forName("java.awt.SystemTray");
         Method m = e.getMethod("isSupported", new Class[0]);
         return ((Boolean)m.invoke((Object)null, new Object[0])).booleanValue();
      } catch (Exception var2) {
         var2.printStackTrace();
         return false;
      }
   }

   public static Object getSystemTray() {
      try {
         Class e = Class.forName("java.awt.SystemTray");
         Method m = e.getMethod("getSystemTray", new Class[0]);
         return m.invoke((Object)null, new Object[0]);
      } catch (Exception var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static Object createTrayIcon(Image image, String string) {
      try {
         Class e = Class.forName("java.awt.TrayIcon");
         Constructor c = e.getConstructor(new Class[]{Image.class, String.class});
         return c.newInstance(new Object[]{image, string});
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public static void setImageAutoSize(Object trayIconObj) {
      try {
         Method e = trayIconObj.getClass().getMethod("setImageAutoSize", new Class[]{Boolean.TYPE});
         e.invoke(trayIconObj, new Object[]{Boolean.valueOf(true)});
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   public static void setPopupMenu(Object obj, Object arg) {
      try {
         Method e = obj.getClass().getMethod("setPopupMenu", new Class[]{PopupMenu.class});
         e.invoke(obj, new Object[]{arg});
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public static void addActionListener(Object obj, Object arg) {
      try {
         Method e = obj.getClass().getMethod("addActionListener", new Class[]{ActionListener.class});
         e.invoke(obj, new Object[]{arg});
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public static void addMouseListener(Object obj, Object arg) {
      try {
         Method e = obj.getClass().getMethod("addMouseListener", new Class[]{MouseListener.class});
         e.invoke(obj, new Object[]{arg});
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public static void add(Object obj, Object arg) {
      try {
         Class e = Class.forName("java.awt.TrayIcon");
         Method m = obj.getClass().getMethod("add", new Class[]{e});
         m.invoke(obj, new Object[]{arg});
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public static void showMessage(Object obj, Object arg1, Object arg2, Object arg3) {
      try {
         Class e = Class.forName("java.awt.TrayIcon.MessageType.INFO");
         Method m = obj.getClass().getMethod("displayMessage", new Class[]{String.class, String.class, e});
         m.invoke(obj, new Object[]{arg1, arg2, arg3});
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }
}
