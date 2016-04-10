package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class StaticResource {
   public static Color whiteColor = new Color(235, 235, 235);
   public static Color titleColor = new Color(14, 20, 25);
   public static Color selectedColor = new Color(51, 181, 229);
   public static Color btnBgColor = new Color(36, 122, 241);
   public static Font plainFont = new Font("Dialog", 0, 12);
   public static Font boldFont = new Font("Dialog", 1, 12);
   public static Font plainFontBig = new Font("Dialog", 0, 14);
   public static Font plainFontBig2 = new Font("Dialog", 0, 18);
   static MouseAdapter ma = new MouseAdapter() {
      Color bgColor;

      public void mouseEntered(MouseEvent e) {
         this.bgColor = ((JButton)e.getSource()).getBackground();
         ((JButton)e.getSource()).setBackground(StaticResource.selectedColor);
      }

      public void mouseExited(MouseEvent e) {
         ((JButton)e.getSource()).setBackground(this.bgColor);
      }
   };

   public static ImageIcon getIcon(String name) {
      try {
         URL e = StaticResource.class.getResource("/Resources/Icons/" + name);
         if(e == null) {
            throw new Exception();
         } else {
            return new ImageIcon(e);
         }
      } catch (Exception var2) {
         return new ImageIcon("Resources/Icons/" + name);
      }
   }
}
