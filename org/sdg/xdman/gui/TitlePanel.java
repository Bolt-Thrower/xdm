package org.sdg.xdman.gui;

import java.awt.LayoutManager2;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JPanel;

public class TitlePanel extends JPanel {
   private static final long serialVersionUID = -6469773600360331175L;
   Window parentWindow;
   int diffx;
   int diffy;

   public TitlePanel(Window w) {
      this.parentWindow = w;
      this.registerMouseListener();
   }

   public TitlePanel(LayoutManager2 lm, Window w) {
      super(lm);
      this.parentWindow = w;
      this.registerMouseListener();
   }

   public void registerMouseListener() {
      this.addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent me) {
            TitlePanel.this.diffx = me.getXOnScreen() - TitlePanel.this.parentWindow.getLocationOnScreen().x;
            TitlePanel.this.diffy = me.getYOnScreen() - TitlePanel.this.parentWindow.getLocationOnScreen().y;
         }
      });
      this.addMouseMotionListener(new MouseMotionAdapter() {
         public void mouseDragged(MouseEvent me) {
            TitlePanel.this.parentWindow.setLocation(me.getXOnScreen() - TitlePanel.this.diffx, me.getYOnScreen() - TitlePanel.this.diffy);
         }
      });
   }
}
