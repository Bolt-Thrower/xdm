package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;
import org.sdg.xdman.gui.StaticResource;

public class XDMMenuItemUI extends BasicMenuItemUI {
   Color colorSelect;
   Color colorBg;

   public static ComponentUI createUI(JComponent c) {
      return new XDMMenuItemUI();
   }

   public XDMMenuItemUI() {
      this.colorSelect = StaticResource.selectedColor;
      this.colorBg = Color.WHITE;
   }

   protected Dimension getPreferredMenuItemSize(JComponent c, Icon checkIcon, Icon arrowIcon, int defaultTextIconGap) {
      Dimension d = super.getPreferredMenuItemSize(c, checkIcon, arrowIcon, defaultTextIconGap);
      return new Dimension(d.width + 10, d.height);
   }

   public void installUI(JComponent c) {
      super.installUI(c);
      c.setBorder((Border)null);
      if(c instanceof AbstractButton) {
         AbstractButton btn = (AbstractButton)c;
         btn.setBorder(new EmptyBorder(5, 10, 5, 10));
         btn.setBorderPainted(false);
      }

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
      int menuWidth = menuItem.getWidth();
      int menuHeight = menuItem.getHeight();
      g.setColor(this.colorBg);
      g.fillRect(0, 0, menuWidth, menuHeight);
      if(model.isArmed() || menuItem instanceof JMenu && model.isSelected()) {
         this.paintButtonPressed(g, menuItem);
      }

      if(menuItem instanceof JCheckBoxMenuItem) {
         ((JCheckBoxMenuItem)menuItem).isSelected();
      }

      g.setColor(oldColor);
   }
}
