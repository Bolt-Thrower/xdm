package org.sdg.xdman.gui;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;
import org.sdg.xdman.gui.StaticResource;

public class BarPanel extends JPanel {
   private static final long serialVersionUID = -5396480713429517585L;
   Image imgBar = StaticResource.getIcon("bar.png").getImage();

   public BarPanel() {
      this.setOpaque(false);
   }

   protected void paintComponent(Graphics g) {
      g.drawImage(this.imgBar, 0, 0, this.getWidth(), this.getHeight(), this);
      super.paintComponent(g);
   }
}
