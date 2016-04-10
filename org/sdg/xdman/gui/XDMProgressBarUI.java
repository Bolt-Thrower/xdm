package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class XDMProgressBarUI extends BasicProgressBarUI {
   GradientPaint high;
   GradientPaint low;
   GradientPaint back;

   public static ComponentUI createUI(JComponent c) {
      return new XDMProgressBarUI();
   }

   public void paint(Graphics g, JComponent c) {
      if(g instanceof Graphics2D) {
         if(this.high == null) {
            this.high = new GradientPaint(0.0F, 0.0F, new Color(117, 225, 248), 0.0F, (float)(c.getHeight() / 2), new Color(88, 207, 229), false);
         }

         if(this.low == null) {
            this.low = new GradientPaint(0.0F, 0.0F, new Color(3, 157, 177), 0.0F, (float)(c.getHeight() / 2), new Color(10, 160, 182), false);
         }

         if(this.back == null) {
            this.back = new GradientPaint(0.0F, 0.0F, Color.WHITE, 0.0F, (float)(c.getHeight() / 2), Color.LIGHT_GRAY, false);
         }

         Graphics2D g2 = (Graphics2D)g;
         g2.setPaint(this.back);
         g2.fillRect(0, 0, c.getWidth(), c.getHeight());
         g2.setColor(Color.GRAY);
         g2.drawRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);
         if(this.progressBar.isIndeterminate()) {
            this.paintIndeterminate(g, c);
         } else {
            this.paintDeterminate(g, c);
         }

      }
   }

   protected void paintIndeterminate(Graphics g, JComponent c) {
      Insets b = this.progressBar.getInsets();
      int barRectWidth = this.progressBar.getWidth() - (b.right + b.left);
      int barRectHeight = this.progressBar.getHeight() - (b.top + b.bottom);
      if(barRectWidth > 0 && barRectHeight > 0) {
         Graphics2D g2 = (Graphics2D)g;
         this.boxRect = this.getBox(this.boxRect);
         if(this.boxRect != null) {
            g2.setPaint(this.high);
            g2.fillRect(this.boxRect.x, this.boxRect.y, this.boxRect.width, this.boxRect.height / 2);
            g2.setPaint(this.low);
            g2.fillRect(this.boxRect.x, this.boxRect.height / 2, this.boxRect.width, this.boxRect.height);
         }

      }
   }

   protected void paintDeterminate(Graphics g, JComponent c) {
      Insets b = this.progressBar.getInsets();
      int barRectWidth = this.progressBar.getWidth() - (b.right + b.left);
      int barRectHeight = this.progressBar.getHeight() - (b.top + b.bottom);
      if(barRectWidth > 0 && barRectHeight > 0) {
         int amountFull = this.getAmountFull(b, barRectWidth, barRectHeight);
         Graphics2D g2 = (Graphics2D)g;
         g2.setColor(this.progressBar.getForeground());
         if(this.progressBar.getOrientation() == 0) {
            g2.setPaint(this.high);
            g2.fillRect(0, 0, amountFull, c.getHeight() / 2);
            g2.setPaint(this.low);
            g2.fillRect(0, c.getHeight() / 2, amountFull, c.getHeight());
         }

         if(this.progressBar.isStringPainted()) {
            this.paintString(g, b.left, b.top, barRectWidth, barRectHeight, amountFull, b);
         }

      }
   }
}
