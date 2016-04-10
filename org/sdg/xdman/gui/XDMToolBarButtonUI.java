package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

public class XDMToolBarButtonUI extends BasicButtonUI {
   Color pressedColor = new Color(170, 170, 170);
   Color rolloverColor = new Color(180, 180, 180);

   protected void paintButtonNormal(Graphics g, AbstractButton b) {
   }

   protected void paintButtonPressed(Graphics g, AbstractButton b) {
      Graphics2D g2 = (Graphics2D)g;
      g2.setColor(this.pressedColor);
      g2.fillRect(0, 0, b.getWidth() - 1, b.getHeight() - 1);
   }

   protected void paintButtonRollOver(Graphics g, AbstractButton b) {
      Graphics2D g2 = (Graphics2D)g;
      g2.setPaint(this.rolloverColor);
      g2.fillRect(0, 0, b.getWidth() - 1, b.getHeight() - 1);
   }

   public void paint(Graphics g, JComponent c) {
      AbstractButton b = (AbstractButton)c;
      ButtonModel bm = b.getModel();
      if(bm.isRollover()) {
         this.paintButtonRollOver(g, b);
      } else {
         this.paintButtonNormal(g, b);
      }

      super.paint(g, c);
   }
}
