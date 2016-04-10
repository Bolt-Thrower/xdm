package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;
import org.sdg.xdman.gui.StaticResource;

public class XDMMenuUI extends BasicMenuUI {
   Color colorSelect;
   Color colorBg;

   public static ComponentUI createUI(JComponent c) {
      return new XDMMenuUI();
   }

   public void installUI(JComponent c) {
      super.installUI(c);
      this.colorBg = Color.WHITE;
      this.colorSelect = StaticResource.selectedColor;
   }

   protected void paintButtonPressed(Graphics g, AbstractButton b) {
      Color c = g.getColor();
      Graphics2D g2 = (Graphics2D)g;
      g2.setPaint(this.colorSelect);
      g2.fillRect(0, 0, b.getWidth(), b.getHeight());
      g.setColor(c);
   }

   protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
      ButtonModel model = menuItem.getModel();
      Color oldColor = g.getColor();
      if(!model.isArmed() && (!(menuItem instanceof JMenu) || !model.isSelected())) {
         g.setColor(this.colorBg);
      } else {
         this.paintButtonPressed(g, menuItem);
      }

      g.setColor(oldColor);
   }
}
