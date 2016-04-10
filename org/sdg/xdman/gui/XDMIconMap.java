package org.sdg.xdman.gui;

import java.util.HashMap;
import javax.swing.ImageIcon;

public class XDMIconMap {
   static HashMap iconMap = new HashMap();

   public static ImageIcon getIcon(String id) {
      return (ImageIcon)iconMap.get(id);
   }

   public static void setIcon(String id, ImageIcon icon) {
      iconMap.put(id, icon);
   }
}
