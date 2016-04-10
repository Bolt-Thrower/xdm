package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import org.sdg.xdman.gui.StaticResource;

public class CategoryBtn extends JButton {
   private static final long serialVersionUID = 7018359807269926427L;
   Color selectBgColor;
   Color rolloverBgColor;
   Image imgBar = (new ImageIcon("Resources/Icons/bg_nav.png")).getImage();

   public CategoryBtn() {
      this.rolloverBgColor = StaticResource.selectedColor;
      this.selectBgColor = Color.WHITE;
   }

   protected void paintComponent(Graphics g) {
      g.setColor(this.selectBgColor);
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
      FontMetrics fm = g.getFontMetrics();
      g.setColor(Color.BLACK);
      g.drawString(this.getText(), 20, this.getHeight() / 2 - fm.getHeight() / 2);
   }
}
