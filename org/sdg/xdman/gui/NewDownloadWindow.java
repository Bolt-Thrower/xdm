package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.gui.DownloadList;
import org.sdg.xdman.gui.StaticResource;
import org.sdg.xdman.gui.StringResource;
import org.sdg.xdman.gui.TitlePanel;
import org.sdg.xdman.gui.XDMButton;
import org.sdg.xdman.gui.XDMFileChooser;
import org.sdg.xdman.util.XDMUtil;

public class NewDownloadWindow extends JFrame implements ActionListener, DocumentListener {
   private static final long serialVersionUID = 6595621766449726115L;
   JPopupMenu pop;
   String user;
   String pass;
   int diffx;
   int diffy;
   Color bgColor;
   DownloadStateListner dwnListener;
   String dir = "";
   Object interceptor;
   XDMConfig config;
   boolean cancelled = true;
   String referer;
   String userAgent;
   public ArrayList cookies;
   JTextField url;
   JTextField file;
   DownloadList list;
   JButton dl;
   JButton dn;
   JButton cn;
   JButton br;

   public void actionPerformed(ActionEvent e) {
      if(e.getSource() instanceof AbstractButton) {
         AbstractButton b = (AbstractButton)e.getSource();
         String id = b.getName();
         if(id == null) {
            return;
         }

         if(id.equals("CANCEL")) {
            this.setVisible(false);
            this.dispose();
         } else {
            String dl;
            if(id.equals("DOWNLOAD_LATER")) {
               if(this.getURL().length() < 1) {
                  JOptionPane.showMessageDialog(this, this.getString("URL_EMPTY"));
                  return;
               }

               if(!XDMUtil.validateURL(this.getURL())) {
                  dl = XDMUtil.createURL(this.getURL());
                  if(dl != null) {
                     this.setURL(dl);
                  } else {
                     JOptionPane.showMessageDialog(this, this.getString("INVALID_URL"));
                  }

                  return;
               }

               this.setVisible(false);
               this.dispose();
               if(this.dwnListener != null) {
                  this.dwnListener.add2Queue(this.getURL(), this.getFile(), this.getDir(), this.getUser(), this.getPass(), this.referer, this.cookies, this.userAgent, true);
               }
            } else if(id.equals("DOWNLOAD_NOW")) {
               if(!XDMUtil.validateURL(this.getURL())) {
                  if(this.getURL().length() < 1) {
                     JOptionPane.showMessageDialog(this, this.getString("URL_EMPTY"));
                     return;
                  }

                  dl = XDMUtil.createURL(this.getURL());
                  if(dl != null) {
                     this.setURL(dl);
                  } else {
                     JOptionPane.showMessageDialog(this, this.getString("INVALID_URL"));
                  }

                  return;
               }

               this.setVisible(false);
               this.dispose();
               if(this.dwnListener != null) {
                  this.dwnListener.downloadNow(this.getURL(), this.getFile(), this.getDir(), this.getUser(), this.getPass(), this.referer, this.cookies, this.userAgent);
               }
            } else if(id.equals(" ... ")) {
               JFileChooser dl1 = XDMFileChooser.getFileChooser(1, new File(this.config.destdir));
               if(dl1.showOpenDialog(this) == 0) {
                  File ig = dl1.getSelectedFile();
                  this.dir = ig.getAbsolutePath();
                  this.config.destdir = this.dir;
               }
            } else if(id.equals("IGNORE_CHK_TXT")) {
               try {
                  URL dl3 = new URL(this.getURL());
                  String ig1 = dl3.getHost();
                  if(this.config.siteList == null) {
                     this.config.siteList = new String[]{ig1};
                  }

                  String[] newArray = new String[this.config.siteList.length + 1];
                  System.arraycopy(this.config.siteList, 0, newArray, 0, this.config.siteList.length);
                  newArray[this.config.siteList.length] = ig1;
                  this.config.siteList = newArray;
                  this.setVisible(false);
                  this.dispose();
               } catch (Exception var7) {
                  var7.printStackTrace();
               }
            } else if(id.equals("BTN_MORE")) {
               if(this.pop == null) {
                  this.pop = new JPopupMenu();
                  JMenuItem dl2 = new JMenuItem("Download Later");
                  dl2.setName("DOWNLOAD_LATER");
                  dl2.addActionListener(this);
                  this.pop.add(dl2);
                  JMenuItem ig2 = new JMenuItem("Don\'t capture downloads from this address");
                  ig2.setName("IGNORE_CHK_TXT");
                  ig2.addActionListener(this);
                  this.pop.add(ig2);
               }

               this.pop.setInvoker(b);
               this.pop.show(b, 0, b.getHeight());
            }
         }
      }

   }

   String getUser() {
      return null;
   }

   String getPass() {
      return null;
   }

   void setURL(String uri) {
      this.url.setText(uri);
   }

   String getURL() {
      return this.url.getText();
   }

   String getFile() {
      return this.file.getText();
   }

   String getDir() {
      return this.dir;
   }

   void setDir(String f) {
      this.dir = f;
   }

   public NewDownloadWindow() {
      this.init();
   }

   public NewDownloadWindow(DownloadStateListner dwnListner, XDMConfig config) {
      this.dwnListener = dwnListner;
      this.config = config;
      this.setAlwaysOnTop(true);
      this.init();
      this.url.requestFocus();
   }

   String getFileName(String url) {
      String file = null;

      try {
         file = XDMUtil.getFileName(url);
      } catch (Exception var4) {
         ;
      }

      if(file == null || file.length() < 1) {
         file = "FILE";
      }

      return file;
   }

   void update(DocumentEvent e) {
      try {
         Document err = e.getDocument();
         int len = err.getLength();
         String text = err.getText(0, len);
         this.file.setText(this.getFileName(text));
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public void changedUpdate(DocumentEvent e) {
      this.update(e);
   }

   public void insertUpdate(DocumentEvent e) {
      this.update(e);
   }

   public void removeUpdate(DocumentEvent e) {
      this.update(e);
   }

   String getString(String id) {
      return StringResource.getString(id);
   }

   void showDlg() {
      if(this.url.getText().length() < 1) {
         try {
            Object d = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            String txt = "";
            if(d != null) {
               txt = d.toString();
            }

            if(txt.length() > 0) {
               int index = txt.indexOf(10);
               if(index != -1) {
                  txt = txt.substring(0, index);
               }

               this.url.setText((new URL(txt)).toString());
            }
         } catch (Exception var4) {
            ;
         }
      }

      Dimension d1 = Toolkit.getDefaultToolkit().getScreenSize();
      this.setLocation(d1.width / 2 - this.getWidth() / 2, d1.height / 2 - this.getHeight() / 2);
      this.setVisible(true);
   }

   void init() {
      this.setSize(400, 210);
      this.bgColor = new Color(73, 73, 73);
      this.getContentPane().setLayout((LayoutManager)null);
      this.getContentPane().setBackground(this.bgColor);
      this.setUndecorated(true);
      this.setResizable(false);
      TitlePanel titlePanel = new TitlePanel((LayoutManager2)null, this);
      titlePanel.setOpaque(false);
      titlePanel.setBounds(0, 0, 400, 50);
      XDMButton closeBtn = new XDMButton();
      closeBtn.setBounds(370, 5, 24, 24);
      closeBtn.setContentAreaFilled(false);
      closeBtn.setBorderPainted(false);
      closeBtn.setFocusPainted(false);
      closeBtn.setIcon(StaticResource.getIcon("close_btn.png"));
      closeBtn.setRolloverIcon(StaticResource.getIcon("close_btn_r.png"));
      closeBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            NewDownloadWindow.this.setVisible(false);
            NewDownloadWindow.this.dispose();
         }
      });
      titlePanel.add(closeBtn);
      JLabel titleLbl = new JLabel("NEW DOWNLOAD");
      titleLbl.setFont(StaticResource.plainFontBig2);
      titleLbl.setForeground(StaticResource.selectedColor);
      titleLbl.setBounds(25, 15, 200, 30);
      titlePanel.add(titleLbl);
      JLabel lineLbl = new JLabel();
      lineLbl.setBackground(StaticResource.selectedColor);
      lineLbl.setBounds(0, 55, 400, 1);
      lineLbl.setOpaque(true);
      this.add(lineLbl);
      JTextField txtURL = new JTextField();
      this.url = txtURL;
      txtURL.getDocument().addDocumentListener(this);
      txtURL.setBorder(new LineBorder(StaticResource.selectedColor, 1));
      txtURL.setBackground(this.bgColor);
      txtURL.setForeground(Color.WHITE);
      txtURL.setBounds(77, 79, 291, 20);
      txtURL.setCaretColor(StaticResource.selectedColor);
      this.add(txtURL);
      JTextField txtFile = new JTextField();
      this.file = txtFile;
      txtFile.setBorder(new LineBorder(StaticResource.selectedColor, 1));
      txtFile.setBackground(this.bgColor);
      txtFile.setForeground(Color.WHITE);
      txtFile.setBounds(77, 111, 241, 20);
      txtFile.setCaretColor(StaticResource.selectedColor);
      this.add(txtFile);
      XDMButton browse = new XDMButton("...");
      browse.setName(" ... ");
      browse.setMargin(new Insets(0, 0, 0, 0));
      browse.setBounds(325, 111, 40, 20);
      browse.addMouseListener(StaticResource.ma);
      browse.setFocusPainted(false);
      browse.setBackground(this.bgColor);
      browse.setBorder(new LineBorder(StaticResource.selectedColor, 1));
      browse.setForeground(Color.WHITE);
      browse.addActionListener(this);
      this.add(browse);
      this.add(titlePanel);
      JLabel lblURL = new JLabel("Address", 4);
      lblURL.setFont(StaticResource.plainFont);
      lblURL.setForeground(Color.WHITE);
      lblURL.setBounds(10, 78, 61, 23);
      this.add(lblURL);
      JLabel lblFile = new JLabel("File", 4);
      lblFile.setFont(StaticResource.plainFont);
      lblFile.setForeground(Color.WHITE);
      lblFile.setBounds(10, 108, 61, 23);
      this.add(lblFile);
      JPanel panel = new JPanel((LayoutManager)null);
      panel.setBounds(0, 155, 400, 55);
      panel.setBackground(Color.GRAY);
      this.add(panel);
      JButton btnMore = new JButton("MORE...");
      JButton btnDN = new JButton("DOWNLOAD NOW");
      JButton btnCN = new JButton("CANCEL");
      btnMore.setBounds(0, 1, 120, 55);
      btnMore.setBackground(this.bgColor);
      btnMore.setForeground(Color.WHITE);
      btnMore.setFont(StaticResource.plainFontBig);
      btnMore.setBorderPainted(false);
      btnMore.setMargin(new Insets(0, 0, 0, 0));
      btnMore.setFocusPainted(false);
      btnMore.addMouseListener(StaticResource.ma);
      btnMore.addActionListener(this);
      btnMore.setName("BTN_MORE");
      panel.add(btnMore);
      btnDN.setBounds(121, 1, 160, 55);
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
      btnCN.setBounds(282, 1, 120, 55);
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

   public static void main(String[] args) {
      (new NewDownloadWindow()).setVisible(true);
   }
}
