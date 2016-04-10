package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.sdg.xdman.core.common.ConnectionManager;
import org.sdg.xdman.core.common.DownloadInfo;
import org.sdg.xdman.core.common.DownloadProgressListener;
import org.sdg.xdman.gui.CircleProgressBar;
import org.sdg.xdman.gui.SegmentPanel;
import org.sdg.xdman.gui.StaticResource;
import org.sdg.xdman.gui.TitlePanel;
import org.sdg.xdman.gui.XDMButton;
import org.sdg.xdman.util.XDMUtil;

public class XDMDownloadWindow extends JFrame implements ActionListener, DownloadProgressListener {
   private static final long serialVersionUID = 5894247973832127732L;
   CircleProgressBar cPrg;
   SegmentPanel sp;
   private JLabel url;
   private JLabel status;
   private JLabel downloaded;
   private JLabel rate;
   private JLabel time;
   ConnectionManager mgr;
   File file;
   boolean valid = true;

   public XDMDownloadWindow(ConnectionManager mgr) {
      this.init();
      this.mgr = mgr;
   }

   public void showWindow() {
      this.setVisible(true);
   }

   public boolean isValidWindow() {
      return this.valid;
   }

   public void update(DownloadInfo info) {
      if(info.state != 50 && info.state != 40 && info.state != 30) {
         this.url.setText(info.file);
         this.status.setText(info.status);
         this.downloaded.setText("Downloaded " + info.downloaded + " " + info.length);
         this.rate.setText(info.speed);
         this.time.setText("ETA " + info.eta);
         this.sp.setValues(info.startoff, info.len, info.dwn, info.rlen);
         this.cPrg.setValue(info.prg);
         this.file = info.path;
      } else {
         this.valid = false;
         if(info.state == 30 && this.isVisible()) {
            JOptionPane.showMessageDialog(this, info.msg);
         }

         if(this.mgr != null) {
            this.mgr.setProgressListener((DownloadProgressListener)null);
         }

         this.mgr = null;
         this.setVisible(false);
         this.dispose();
      }
   }

   public void actionPerformed(ActionEvent e) {
      JButton b = (JButton)e.getSource();
      if(b.getName().equals("PAUSE")) {
         if(this.mgr != null) {
            this.mgr.stop();
         }

         this.setVisible(false);
         this.dispose();
      } else if(b.getName().equals("PREVIEW") && this.file != null) {
         XDMUtil.open(this.file);
      }

      if(b.getName().equals("BACKGROUND")) {
         this.setVisible(false);
      }

   }

   void init() {
      this.setTitle("Downloading...");
      this.setSize(350, 250);
      this.setLocationRelativeTo((Component)null);
      Color bgColor = new Color(73, 73, 73);
      this.getContentPane().setLayout((LayoutManager)null);
      this.getContentPane().setBackground(bgColor);
      this.setUndecorated(true);
      this.setResizable(false);
      TitlePanel titlePanel = new TitlePanel((LayoutManager2)null, this);
      titlePanel.setOpaque(false);
      titlePanel.setBounds(0, 0, 350, 50);
      XDMButton closeBtn = new XDMButton();
      closeBtn.setBounds(320, 5, 24, 24);
      closeBtn.setContentAreaFilled(false);
      closeBtn.setBorderPainted(false);
      closeBtn.setFocusPainted(false);
      closeBtn.setIcon(StaticResource.getIcon("close_btn.png"));
      closeBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            XDMDownloadWindow.this.valid = false;
            if(XDMDownloadWindow.this.mgr != null) {
               XDMDownloadWindow.this.mgr.stop();
            }

            XDMDownloadWindow.this.mgr = null;
            XDMDownloadWindow.this.setVisible(false);
            XDMDownloadWindow.this.dispose();
         }
      });
      titlePanel.add(closeBtn);
      XDMButton minBtn = new XDMButton();
      minBtn.setBounds(296, 5, 24, 24);
      minBtn.setContentAreaFilled(false);
      minBtn.setBorderPainted(false);
      minBtn.setFocusPainted(false);
      minBtn.setIcon(StaticResource.getIcon("min_btn.png"));
      minBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent action) {
            XDMDownloadWindow.this.setExtendedState(XDMDownloadWindow.this.getExtendedState() | 1);
         }
      });
      titlePanel.add(minBtn);
      JLabel titleLbl = new JLabel("Downloading ...");
      this.url = titleLbl;
      titleLbl.setFont(StaticResource.plainFontBig2);
      titleLbl.setForeground(StaticResource.selectedColor);
      titleLbl.setBounds(25, 15, 200, 30);
      titlePanel.add(titleLbl);
      this.add(titlePanel);
      JLabel lineLbl = new JLabel();
      lineLbl.setBackground(StaticResource.selectedColor);
      lineLbl.setBounds(0, 55, 400, 2);
      lineLbl.setOpaque(true);
      this.add(lineLbl);
      this.cPrg = new CircleProgressBar();
      this.cPrg.setValue(30);
      this.cPrg.setBounds(20, 80, 72, 72);
      this.add(this.cPrg);
      JLabel lblSize = new JLabel("123 MB");
      this.rate = lblSize;
      lblSize.setHorizontalAlignment(0);
      lblSize.setBounds(15, 160, 80, 25);
      lblSize.setForeground(Color.WHITE);
      this.add(lblSize);
      JLabel lblStat = new JLabel("Downloading...");
      this.status = lblStat;
      lblStat.setBounds(120, 85, 200, 25);
      lblStat.setForeground(Color.WHITE);
      this.add(lblStat);
      this.sp = new SegmentPanel();
      this.sp.setBounds(120, 115, 200, 5);
      this.add(this.sp);
      JLabel lblDet = new JLabel("Downloaded 10/123 MB");
      this.downloaded = lblDet;
      lblDet.setBounds(120, 125, 200, 25);
      lblDet.setForeground(Color.WHITE);
      this.add(lblDet);
      JLabel lblETA = new JLabel("ETA 23 Min");
      this.time = lblETA;
      lblETA.setBounds(120, 150, 200, 25);
      lblETA.setForeground(Color.WHITE);
      this.add(lblETA);
      JPanel panel = new JPanel((LayoutManager)null);
      panel.setBounds(0, 200, 350, 50);
      panel.setBackground(Color.GRAY);
      this.add(panel);
      JButton btnMore = new JButton("Hide");
      JButton btnDN = new JButton("Preview");
      JButton btnCN = new JButton("Pause");
      btnMore.setBounds(0, 1, 100, 50);
      btnMore.setName("BACKGROUND");
      btnMore.addActionListener(this);
      btnMore.setBackground(bgColor);
      btnMore.setForeground(Color.WHITE);
      btnMore.setFont(StaticResource.plainFontBig);
      btnMore.setBorderPainted(false);
      btnMore.setMargin(new Insets(0, 0, 0, 0));
      btnMore.setFocusPainted(false);
      btnMore.addMouseListener(StaticResource.ma);
      panel.add(btnMore);
      btnDN.setBounds(101, 1, 144, 50);
      btnDN.setName("PREVIEW");
      btnDN.addActionListener(this);
      btnDN.setBackground(bgColor);
      btnDN.setForeground(Color.WHITE);
      btnDN.setBorderPainted(false);
      btnDN.setFont(StaticResource.plainFontBig);
      btnDN.setBorderPainted(false);
      btnDN.setMargin(new Insets(0, 0, 0, 0));
      btnDN.setFocusPainted(false);
      btnDN.addMouseListener(StaticResource.ma);
      panel.add(btnDN);
      btnCN.setBounds(246, 1, 104, 50);
      btnCN.setName("PAUSE");
      btnCN.setBackground(bgColor);
      btnCN.setForeground(Color.WHITE);
      btnCN.setFont(StaticResource.plainFontBig);
      btnCN.setBorderPainted(false);
      btnCN.setMargin(new Insets(0, 0, 0, 0));
      btnCN.setFocusPainted(false);
      btnCN.addMouseListener(StaticResource.ma);
      btnCN.addActionListener(this);
      panel.add(btnCN);
   }
}
