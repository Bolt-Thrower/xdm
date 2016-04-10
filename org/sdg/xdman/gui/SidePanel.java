package org.sdg.xdman.gui;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;
import org.sdg.xdman.gui.StaticResource;

public class SidePanel extends JPanel {
   private static final long serialVersionUID = 3821650643051584496L;
   Image imgBar = StaticResource.getIcon("bg_nav.png").getImage();

   public SidePanel() {
      this.setOpaque(false);
   }

   protected void paintComponent(Graphics g) {
      g.drawImage(this.imgBar, 0, 0, this.getWidth(), this.getHeight(), this);
      super.paintComponent(g);
   }
}
