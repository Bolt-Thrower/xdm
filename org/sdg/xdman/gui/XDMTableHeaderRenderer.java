package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class XDMTableHeaderRenderer extends JLabel implements TableCellRenderer {
   private static final long serialVersionUID = 1182486808273962013L;
   Color bgColor;

   public XDMTableHeaderRenderer() {
      super.setOpaque(false);
      this.setBorder(new EmptyBorder(2, 2, 2, 2));
      this.setFont(new Font("Dialog", 0, 12));
      this.bgColor = new Color(230, 230, 230);
   }

   public Component getTableCellRendererComponent(JTable table, Object data, boolean isSelected, boolean hasFocus, int row, int column) {
      this.setText("" + data);
      return this;
   }

   protected void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D)g;
      g2.setColor(this.bgColor);
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
      g.setColor(Color.LIGHT_GRAY);
      g.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, this.getHeight());
      super.paintComponent(g);
   }
}
