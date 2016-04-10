package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import org.sdg.xdman.gui.XDMButton;
import org.sdg.xdman.gui.XDMIconMap;

public class XDMScrollBarUI extends BasicScrollBarUI {
   Color borderColor = new Color(185, 185, 185);
   Color roColor = new Color(170, 170, 170);
   Color barColor = new Color(190, 190, 190);

   public static ComponentUI createUI(JComponent c) {
      return new XDMScrollBarUI();
   }

   public void installUI(JComponent c) {
      super.installUI(c);
      if(this.scrollbar.getOrientation() == 0) {
         c.setPreferredSize(new Dimension(15, c.getPreferredSize().height));
      } else {
         c.setPreferredSize(new Dimension(c.getPreferredSize().width, 15));
      }

   }

   protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
      if(!thumbBounds.isEmpty() && this.scrollbar.isEnabled()) {
         int w = thumbBounds.width;
         int h = thumbBounds.height;
         g.translate(thumbBounds.x, thumbBounds.y);
         Graphics2D g2 = (Graphics2D)g;
         if(this.isThumbRollover()) {
            g2.setColor(this.roColor);
         } else {
            g2.setColor(this.barColor);
         }

         g.fillRect(1, 1, w - 3, h - 3);
         g2.setColor(this.borderColor);
         g.drawRect(1, 1, w - 3, h - 3);
         g.translate(-thumbBounds.x, -thumbBounds.y);
      }
   }

   protected JButton createDecreaseButton(int orientation) {
      XDMButton btn = new XDMButton();
      btn.setHorizontalAlignment(0);
      btn.setPreferredSize(new Dimension(15, 15));
      btn.setContentAreaFilled(false);
      btn.setBorderPainted(false);
      btn.setOpaque(false);
      if(orientation == 1) {
         btn.setIcon(XDMIconMap.getIcon("UP_ARROW"));
      }

      if(orientation == 5) {
         btn.setIcon(XDMIconMap.getIcon("DOWN_ARROW"));
      }

      if(orientation == 3) {
         btn.setIcon(XDMIconMap.getIcon("LEFT_ARROW"));
      }

      if(orientation == 7) {
         btn.setIcon(XDMIconMap.getIcon("RIGHT_ARROW"));
      }

      return btn;
   }

   protected JButton createIncreaseButton(int orientation) {
      XDMButton btn = new XDMButton();
      btn.setHorizontalAlignment(0);
      btn.setPreferredSize(new Dimension(15, 15));
      btn.setContentAreaFilled(false);
      btn.setBorderPainted(false);
      if(orientation == 1) {
         btn.setIcon(XDMIconMap.getIcon("UP_ARROW"));
      }

      if(orientation == 5) {
         btn.setIcon(XDMIconMap.getIcon("DOWN_ARROW"));
      }

      if(orientation == 3) {
         btn.setIcon(XDMIconMap.getIcon("LEFT_ARROW"));
      }

      if(orientation == 7) {
         btn.setIcon(XDMIconMap.getIcon("RIGHT_ARROW"));
      }

      return btn;
   }

   protected void paintTrack22(Graphics g, JComponent c, Rectangle r) {
   }
}
