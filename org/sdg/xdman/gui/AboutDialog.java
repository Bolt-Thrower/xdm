package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.sdg.xdman.gui.StaticResource;
import org.sdg.xdman.gui.StringResource;
import org.sdg.xdman.gui.TitlePanel;
import org.sdg.xdman.gui.XDMIconMap;
import org.sdg.xdman.gui.XDMMainWindow;

public class AboutDialog extends JDialog {
   private static final long serialVersionUID = -214717043594709353L;
   Color bgColor;

   public AboutDialog() {
      this.init();
   }

   void init() {
      this.setUndecorated(true);
      this.setSize(350, 300);
      this.getContentPane().setLayout((LayoutManager)null);
      this.bgColor = new Color(73, 73, 73);
      this.getContentPane().setBackground(this.bgColor);
      TitlePanel titlePanel = new TitlePanel((LayoutManager2)null, this);
      titlePanel.setOpaque(false);
      titlePanel.setBounds(0, 0, 350, 50);
      JLabel titleLbl = new JLabel("XDM 2016", 0);
      titleLbl.setFont(new Font("Dialog", 1, 20));
      titleLbl.setForeground(StaticResource.selectedColor);
      titleLbl.setBounds(0, 0, 320, 74);
      titlePanel.add(titleLbl);
      this.add(titlePanel);
      JLabel lineLbl = new JLabel();
      lineLbl.setBackground(StaticResource.selectedColor);
      lineLbl.setBounds(0, 75, 350, 1);
      lineLbl.setOpaque(true);
      this.add(lineLbl);
      JLabel iconLbl = new JLabel(XDMMainWindow.getIcon("icon.png"));
      iconLbl.setBounds(32, 95, 70, 70);
      this.add(iconLbl);
      JTextArea txtInfo = new JTextArea("6.0.00\nBuilt on Sunday 6 March 2016\nCopyright (C) 2015\nSubhra Das Gupta\nCopyright (C) 2016\nSeedo Eldho Paul");
      txtInfo.setBounds(114, 100, 201, 80);
      txtInfo.setEditable(false);
      txtInfo.setBackground(this.bgColor);
      txtInfo.setForeground(Color.WHITE);
      this.add(txtInfo);
      ImageIcon icon = XDMIconMap.getIcon("APP_ICON");
      this.setTitle(StringResource.getString("ABT_TTL"));
      this.setIconImage(icon.getImage());
      JPanel p = new JPanel((LayoutManager)null);
      p.setBackground(Color.GRAY);
      p.setBounds(0, 210, 350, 70);
      this.add(p);
      JButton okBtn = new JButton("OK");
      okBtn.setForeground(Color.WHITE);
      okBtn.setFont(StaticResource.plainFontBig2);
      okBtn.setBackground(this.bgColor);
      okBtn.setBounds(0, 1, 350, 70);
      p.add(okBtn);
      okBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            AboutDialog.this.setVisible(false);
         }
      });
   }
}
