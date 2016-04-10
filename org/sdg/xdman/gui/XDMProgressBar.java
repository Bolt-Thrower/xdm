package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

public class XDMProgressBar extends JComponent {
   private static final long serialVersionUID = 8688628575007423155L;
   int value = 0;
   GradientPaint high;
   GradientPaint low;
   GradientPaint back;

   protected void paintComponent(Graphics g) {
      if(g != null) {
         if(this.high == null) {
            this.high = new GradientPaint(0.0F, 0.0F, new Color(117, 225, 248), 0.0F, (float)(this.getHeight() / 2), new Color(88, 207, 229), false);
         }

         if(this.low == null) {
            this.low = new GradientPaint(0.0F, 0.0F, new Color(3, 157, 177), 0.0F, (float)(this.getHeight() / 2), new Color(10, 160, 182), false);
         }

         if(this.back == null) {
            this.back = new GradientPaint(0.0F, 0.0F, Color.WHITE, 0.0F, (float)(this.getHeight() / 2), Color.LIGHT_GRAY, false);
         }

         int pos = (int)((float)this.getWidth() / 100.0F * (float)this.value);
         Graphics2D g2 = (Graphics2D)g;
         g2.setPaint(this.back);
         g2.fillRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
         g2.setPaint(this.high);
         g2.fillRect(0, 0, pos, this.getHeight() / 2);
         g2.setPaint(this.low);
         g2.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
         g2.fillRect(0, this.getHeight() / 2, pos, this.getHeight() - 1);
      }
   }

   public void setValue(int value) {
      this.value = value;
      this.repaint();
   }
}
