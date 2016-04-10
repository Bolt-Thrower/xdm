package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.gui.StaticResource;
import org.sdg.xdman.gui.StringResource;
import org.sdg.xdman.gui.TitlePanel;
import org.sdg.xdman.gui.XDMButton;
import org.sdg.xdman.util.XDMUtil;

public class DownloadCompleteDialog extends JFrame implements ActionListener {
   private static final long serialVersionUID = -6952846084893748315L;
   JTextField file;
   JTextField folder;
   JButton open;
   JButton close;
   JButton open_folder;
   JCheckBox chk;
   XDMConfig config;
   Color bgColor;

   public static void main(String[] args) {
      (new DownloadCompleteDialog((XDMConfig)null)).setVisible(true);
   }

   public DownloadCompleteDialog(XDMConfig config) {
      this.setSize(350, 210);
      this.setAlwaysOnTop(true);
      this.config = config;
      this.bgColor = new Color(73, 73, 73);
      this.getContentPane().setLayout((LayoutManager)null);
      this.getContentPane().setBackground(this.bgColor);
      this.setUndecorated(true);
      this.setResizable(false);
      TitlePanel titlePanel = new TitlePanel((LayoutManager2)null, this);
      titlePanel.setOpaque(false);
      titlePanel.setBounds(0, 0, 400, 50);
      XDMButton closeBtn = new XDMButton();
      closeBtn.setBounds(320, 5, 24, 24);
      closeBtn.setContentAreaFilled(false);
      closeBtn.setBorderPainted(false);
      closeBtn.setFocusPainted(false);
      closeBtn.setIcon(StaticResource.getIcon("close_btn.png"));
      closeBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            DownloadCompleteDialog.this.setVisible(false);
            DownloadCompleteDialog.this.dispose();
         }
      });
      titlePanel.add(closeBtn);
      JLabel titleLbl = new JLabel("DOWNLOAD COMPLETE");
      titleLbl.setFont(StaticResource.plainFontBig2);
      titleLbl.setForeground(StaticResource.selectedColor);
      titleLbl.setBounds(25, 15, 300, 30);
      titlePanel.add(titleLbl);
      this.add(titlePanel);
      JLabel lineLbl = new JLabel();
      lineLbl.setBackground(StaticResource.selectedColor);
      lineLbl.setBounds(0, 55, 400, 1);
      lineLbl.setOpaque(true);
      this.add(lineLbl);
      JLabel lblFile = new JLabel("File Name", 4);
      lblFile.setBounds(0, 75, 70, 20);
      lblFile.setForeground(Color.WHITE);
      this.add(lblFile);
      JLabel lblSave = new JLabel("Save In", 4);
      lblSave.setBounds(0, 100, 70, 20);
      lblSave.setForeground(Color.WHITE);
      this.add(lblSave);
      this.file = new JTextField();
      this.file.setEditable(false);
      this.file.setBorder(new LineBorder(StaticResource.selectedColor, 1));
      this.file.setBackground(this.bgColor);
      this.file.setForeground(Color.WHITE);
      this.file.setBounds(80, 75, 220, 20);
      this.file.setCaretColor(StaticResource.selectedColor);
      this.add(this.file);
      this.folder = new JTextField();
      this.folder.setEditable(false);
      this.folder.setBorder(new LineBorder(StaticResource.selectedColor, 1));
      this.folder.setBackground(this.bgColor);
      this.folder.setForeground(Color.WHITE);
      this.folder.setBounds(80, 100, 220, 20);
      this.folder.setCaretColor(StaticResource.selectedColor);
      this.add(this.folder);
      this.chk = new JCheckBox(StringResource.getString("DONT_SHOW_AGAIN"));
      this.chk.setBackground(this.bgColor);
      this.chk.setForeground(Color.WHITE);
      this.chk.setBounds(80, 130, 200, 20);
      this.add(this.chk);
      JPanel panel = new JPanel((LayoutManager)null);
      panel.setBounds(0, 155, 400, 55);
      panel.setBackground(Color.GRAY);
      this.add(panel);
      JButton btnMore = new JButton("OPEN");
      JButton btnDN = new JButton("OPEN FOLDER");
      JButton btnCN = new JButton("CLOSE");
      this.open = btnMore;
      this.open.addActionListener(this);
      btnMore.setBounds(0, 1, 100, 55);
      btnMore.setBackground(this.bgColor);
      btnMore.setForeground(Color.WHITE);
      btnMore.setFont(StaticResource.plainFontBig);
      btnMore.setBorderPainted(false);
      btnMore.setMargin(new Insets(0, 0, 0, 0));
      btnMore.setFocusPainted(false);
      btnMore.addMouseListener(StaticResource.ma);
      panel.add(btnMore);
      this.open_folder = btnDN;
      this.open_folder.addActionListener(this);
      btnDN.setBounds(101, 1, 148, 55);
      btnDN.setName("DOWNLOAD_NOW");
      btnDN.setBackground(this.bgColor);
      btnDN.setForeground(Color.WHITE);
      btnDN.setBorderPainted(false);
      btnDN.setFont(StaticResource.plainFontBig);
      btnDN.setBorderPainted(false);
      btnDN.setMargin(new Insets(0, 0, 0, 0));
      btnDN.setFocusPainted(false);
      btnDN.addMouseListener(StaticResource.ma);
      btnDN.addActionListener(this);
      panel.add(btnDN);
      this.close = btnCN;
      this.close.addActionListener(this);
      btnCN.setBounds(250, 1, 100, 55);
      btnCN.setName("CANCEL");
      btnCN.setBackground(this.bgColor);
      btnCN.setForeground(Color.WHITE);
      btnCN.setFont(StaticResource.plainFontBig);
      btnCN.setBorderPainted(false);
      btnCN.setMargin(new Insets(0, 0, 0, 0));
      btnCN.setFocusPainted(false);
      btnCN.addMouseListener(StaticResource.ma);
      btnCN.addActionListener(this);
      panel.add(btnCN);
   }

   void setData(String file, String path) {
      this.file.setText(file);
      this.folder.setText(path);
      this.setTitle(file);
   }

   public void actionPerformed(ActionEvent e) {
      if(e.getSource() == this.open) {
         XDMUtil.open(new File(this.folder.getText(), this.file.getText()));
         System.out.println(new File(this.folder.getText(), this.file.getText()));
         this.config.showDownloadCompleteDlg = !this.chk.isSelected();
         this.setVisible(false);
      }

      if(e.getSource() == this.open_folder) {
         XDMUtil.open(new File(this.folder.getText()));
         this.config.showDownloadCompleteDlg = !this.chk.isSelected();
         this.setVisible(false);
      }

      if(e.getSource() == this.close) {
         this.config.showDownloadCompleteDlg = !this.chk.isSelected();
         this.setVisible(false);
         this.dispose();
      }

   }
}
