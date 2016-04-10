package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import org.sdg.xdman.gui.StaticResource;

public class SegmentPanel extends JComponent {
   private static final long serialVersionUID = -6537879808121349569L;
   long[] start;
   long[] length;
   long[] dwnld;
   long len;

   public void setValues(long[] start, long[] length, long[] dwnld, long len) {
      this.length = length;
      this.start = start;
      this.dwnld = dwnld;
      this.len = len;
      this.repaint();
   }

   public void paintComponent(Graphics g) {
      if(g != null) {
         Graphics2D g2 = (Graphics2D)g;
         g2.setPaint(Color.GRAY);
         g2.fillRect(0, 0, this.getWidth(), this.getHeight());
         if(this.len != 0L) {
            float r = (float)this.getWidth() / (float)this.len;

            for(int i = 0; i < this.start.length; ++i) {
               int _start = (int)((float)this.start[i] * r);
               int _length = (int)((float)this.length[i] * r);
               int _dwnld = (int)((float)this.dwnld[i] * r);
               if(_dwnld > _length) {
                  _dwnld = _length;
               }

               g2.setPaint(StaticResource.selectedColor);
               g2.fillRect(_start, 0, _dwnld + 1, this.getHeight());
            }

         }
      }
   }
}
