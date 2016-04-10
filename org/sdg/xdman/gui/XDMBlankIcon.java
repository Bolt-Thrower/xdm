package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

public class XDMBlankIcon implements Icon {
   int width;
   int height;
   Color lightColor;
   Color darkColor;

   public XDMBlankIcon(int width, int height) {
      this.width = width;
      this.height = height;
      this.lightColor = Color.WHITE;
      this.darkColor = new Color(230, 230, 230);
   }

   public int getIconHeight() {
      return this.height;
   }

   public int getIconWidth() {
      return this.width;
   }

   public void paintIcon(Component c, Graphics g, int x, int y) {
   }
}
