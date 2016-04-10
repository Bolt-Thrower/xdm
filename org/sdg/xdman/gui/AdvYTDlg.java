package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.gui.StaticResource;
import org.sdg.xdman.gui.TitlePanel;
import org.sdg.xdman.gui.XDMButton;

public class AdvYTDlg extends JDialog {
   private static final long serialVersionUID = 662954598636682751L;
   Color bgColor;
   XDMConfig config;
   JButton btn;

   public AdvYTDlg(XDMConfig config) {
      this.config = config;
      this.init();
   }

   void init() {
      this.setSize(450, 300);
      this.bgColor = Color.WHITE;
      this.setModal(true);
      JPanel p = new JPanel((LayoutManager)null);
      p.setBorder(new LineBorder(Color.GRAY));
      p.setBackground(Color.WHITE);
      this.add(p);
      this.setUndecorated(true);
      this.setResizable(false);
      TitlePanel titlePanel = new TitlePanel((LayoutManager2)null, this);
      titlePanel.setOpaque(false);
      titlePanel.setBounds(1, 1, 448, 50);
      p.add(titlePanel);
      XDMButton closeBtn = new XDMButton();
      closeBtn.setBounds(420, 5, 24, 24);
      closeBtn.setContentAreaFilled(false);
      closeBtn.setBorderPainted(false);
      closeBtn.setFocusPainted(false);
      closeBtn.setIcon(StaticResource.getIcon("close_btn.png"));
      closeBtn.setRolloverIcon(StaticResource.getIcon("close_btn_r.png"));
      closeBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            AdvYTDlg.this.setVisible(false);
            AdvYTDlg.this.dispose();
         }
      });
      titlePanel.add(closeBtn);
      JLabel titleLbl = new JLabel("ADVANCED YOUTUBE DOWNLOADER");
      titleLbl.setFont(StaticResource.plainFontBig2);
      titleLbl.setForeground(StaticResource.selectedColor);
      titleLbl.setBounds(25, 15, 350, 30);
      titlePanel.add(titleLbl);
      JLabel lineLbl = new JLabel();
      lineLbl.setBackground(StaticResource.selectedColor);
      lineLbl.setBounds(0, 55, 450, 1);
      lineLbl.setOpaque(true);
      p.add(lineLbl);
      JTextArea txtDesc = new JTextArea();
      txtDesc.setEditable(false);
      txtDesc.setLineWrap(true);
      txtDesc.setWrapStyleWord(true);
      txtDesc.setText("XDM will try to download YouTube videos by impersonating Chrome or Firefox as tablet browser.\n\nTo download videos click \"Enable\" and play the video in browser, and disable this when done.");
      txtDesc.setBounds(20, 70, 420, 170);
      p.add(txtDesc);
      this.btn = new JButton("Enable");
      this.updateBtnText();
      this.btn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            AdvYTDlg.this.config.tabletMode = !AdvYTDlg.this.config.tabletMode;
            AdvYTDlg.this.updateBtnText();
         }
      });
      this.btn.setFont(StaticResource.plainFontBig2);
      this.btn.setBounds(20, 240, 410, 40);
      p.add(this.btn);
   }

   void updateBtnText() {
      this.btn.setText(this.config.tabletMode?"Disable":"Enable");
   }
}
