package org.sdg.xdman.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import javax.swing.JComponent;
import org.sdg.xdman.gui.StaticResource;

public class CircleProgressBar extends JComponent {
   private static final long serialVersionUID = 3778513245025142955L;
   Stroke stroke = new BasicStroke(4.0F);
   private int value;
   Color foreColor;
   Color backColor;

   public CircleProgressBar() {
      this.foreColor = StaticResource.selectedColor;
      this.backColor = new Color(73, 73, 73);
   }

   public void paint(Graphics g) {
      Graphics2D g2 = (Graphics2D)g;
      if(g2 != null) {
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         int sweep_angle = this.value * 360 / 100;
         g2.setColor(Color.GRAY);
         g2.setStroke(this.stroke);
         g2.drawArc(2, 2, this.getWidth() - 4 - 8, this.getHeight() - 4 - 8, 90, -360);
         g2.setColor(this.foreColor);
         g2.drawArc(2, 2, this.getWidth() - 4 - 8, this.getHeight() - 4 - 8, 90, -sweep_angle);
         g2.setFont(StaticResource.plainFontBig2);
         FontMetrics fm = g2.getFontMetrics();
         String str = this.value + "%";
         int w = fm.stringWidth(str);
         g2.drawString(str, this.getWidth() / 2 - w / 2, this.getHeight() / 2 + 5);
      }
   }

   public void setValue(int value) {
      this.value = value;
      this.repaint();
   }

   public int getValue() {
      return this.value;
   }
}
