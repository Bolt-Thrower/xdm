package org.sdg.xdman.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import org.sdg.xdman.gui.StaticResource;
import org.sdg.xdman.gui.XDMIconMap;
import org.sdg.xdman.plugin.youtube.YTVideoInfo;

public class YTListRenderer extends JPanel implements ListCellRenderer {
   private static final long serialVersionUID = -5256235367737001829L;
   JLabel title;
   JLabel status;
   JLabel icon;

   public YTListRenderer() {
      this.setLayout(new BorderLayout(5, 5));
      this.setBorder(new EmptyBorder(5, 5, 5, 5));
      this.icon = new JLabel(XDMIconMap.getIcon("VID"));
      this.add(this.icon, "West");
      this.title = new JLabel();
      this.title.setFont(StaticResource.plainFontBig2);
      this.status = new JLabel();
      JPanel p = new JPanel(new BorderLayout());
      p.setOpaque(false);
      p.add(this.title);
      p.add(this.status, "South");
      this.add(p);
   }

   public Component getListCellRendererComponent(JList list, Object obj, int index, boolean selected, boolean focused) {
      YTVideoInfo info = (YTVideoInfo)obj;
      this.title.setText(info.name);
      this.status.setText(info.itag + " " + info.type);
      if(selected) {
         this.setBackground(StaticResource.selectedColor);
      } else {
         this.setBackground(Color.WHITE);
      }

      return this;
   }
}
