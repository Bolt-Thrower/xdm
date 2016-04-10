package org.sdg.xdman.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.gui.DownloadList;
import org.sdg.xdman.gui.StringResource;
import org.sdg.xdman.gui.XDMFileChooser;
import org.sdg.xdman.gui.XDMIconMap;
import org.sdg.xdman.util.XDMUtil;

public class DownloadFileInfoDialog extends JDialog implements ActionListener, DocumentListener {
   DownloadStateListner dwnListener;
   private static final long serialVersionUID = 5445253177209103274L;
   JTextField url = new JTextField();
   JTextField file = new JTextField();
   String dir = "";
   JTextField user = new JTextField();
   JPasswordField pass = new JPasswordField();
   Object interceptor;
   XDMConfig config;
   boolean cancelled = true;
   String referer;
   String userAgent;
   public ArrayList cookies;
   JCheckBox chk;
   DownloadList list;
   JButton dl;
   JButton dn;
   JButton cn;
   JButton br;

   public DownloadFileInfoDialog(DownloadStateListner dwnListner, XDMConfig config) {
      this.dwnListener = dwnListner;
      this.config = config;
      this.setAlwaysOnTop(true);
      this.init();
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

   String getUser() {
      return this.user.getText();
   }

   String getPass() {
      return new String(this.pass.getPassword());
   }

   void setDir(String f) {
      this.dir = f;
   }

   public DownloadFileInfoDialog() {
      this.init();
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
      this.setTitle(this.getString("NEW_DLG_TITLE"));
      this.setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            DownloadFileInfoDialog var2 = DownloadFileInfoDialog.this;
            synchronized(DownloadFileInfoDialog.this) {
               DownloadFileInfoDialog.this.notifyAll();
            }

            DownloadFileInfoDialog.this.setVisible(false);
         }
      });
      this.url.getDocument().addDocumentListener(this);
      JPanel panel = new JPanel(new GridBagLayout());
      GridBagConstraints gc = new GridBagConstraints();
      gc.fill = 2;
      gc.insets = new Insets(5, 5, 5, 5);
      gc.gridx = 0;
      gc.gridy = 0;
      JLabel ul = new JLabel(this.getString("URL"));
      ul.setHorizontalAlignment(4);
      panel.add(ul, gc);
      gc.gridx = 1;
      gc.gridy = 0;
      gc.gridwidth = 3;
      gc.weightx = 1.0D;
      panel.add(this.url, gc);
      gc.weightx = 0.0D;
      gc.gridx = 0;
      gc.gridy = 1;
      gc.gridwidth = 1;
      JLabel sa = new JLabel(this.getString("SAVE_AS"));
      sa.setHorizontalAlignment(4);
      panel.add(sa, gc);
      gc.weightx = 1.0D;
      gc.gridx = 1;
      gc.gridy = 1;
      gc.gridwidth = 2;
      panel.add(this.file, gc);
      gc.gridx = 3;
      gc.gridy = 1;
      gc.gridwidth = 1;
      gc.weightx = 0.0D;
      this.br = new JButton(" ... ");
      this.br.setName(" ... ");
      this.br.addActionListener(this);
      panel.add(this.br, gc);
      gc.weightx = 0.0D;
      gc.gridx = 1;
      gc.gridy = 3;
      gc.gridwidth = 2;
      Box b1 = Box.createHorizontalBox();
      b1.add(Box.createHorizontalGlue());
      JLabel un = new JLabel(this.getString("USER_NAME"));
      un.setHorizontalAlignment(4);
      b1.add(un);
      b1.add(Box.createHorizontalStrut(5));
      b1.add(this.user);
      b1.add(Box.createHorizontalStrut(10));
      JLabel ps = new JLabel(this.getString("PASSWORD"));
      ps.setHorizontalAlignment(4);
      b1.add(ps);
      b1.add(Box.createHorizontalStrut(5));
      b1.add(this.pass);
      b1.add(Box.createHorizontalGlue());
      panel.add(b1, gc);
      this.dl = new JButton(this.getString("DOWNLOAD_LATER"));
      this.dl.setName("DOWNLOAD_LATER");
      this.dl.addActionListener(this);
      this.dn = new JButton(this.getString("DOWNLOAD_NOW"));
      this.dn.setName("DOWNLOAD_NOW");
      this.dn.addActionListener(this);
      this.cn = new JButton(this.getString("CANCEL"));
      this.cn.setName("CANCEL");
      this.cn.addActionListener(this);
      JPanel b2 = new JPanel(new GridLayout(1, 3, 5, 5));
      b2.add(this.dl);
      b2.add(this.dn);
      b2.add(this.cn);
      gc.weightx = 1.0D;
      gc.gridx = 1;
      gc.gridy = 4;
      gc.gridwidth = 2;
      gc.fill = 2;
      panel.add(b2, gc);
      gc.weightx = 0.0D;
      gc.gridx = 1;
      gc.gridy = 5;
      gc.gridwidth = 2;
      this.chk = new JCheckBox(this.getString("IGNORE_CHK_TXT"));
      this.chk.setName("IGNORE_CHK_TXT");
      this.chk.setHorizontalAlignment(2);
      this.chk.setMargin(new Insets(0, 0, 0, 0));
      this.chk.addActionListener(this);
      panel.add(this.chk, gc);
      this.add(panel);
      this.pack();
      this.setResizable(false);
   }

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
            String exx;
            if(id.equals("DOWNLOAD_LATER")) {
               if(this.getURL().length() < 1) {
                  JOptionPane.showMessageDialog(this, this.getString("URL_EMPTY"));
                  return;
               }

               if(!XDMUtil.validateURL(this.getURL())) {
                  exx = XDMUtil.createURL(this.getURL());
                  if(exx != null) {
                     this.setURL(exx);
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

                  exx = XDMUtil.createURL(this.getURL());
                  if(exx != null) {
                     this.setURL(exx);
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
               JFileChooser exx1 = XDMFileChooser.getFileChooser(1, new File(this.config.destdir));
               if(exx1.showOpenDialog(this) == 0) {
                  File host = exx1.getSelectedFile();
                  this.dir = host.getAbsolutePath();
                  this.config.destdir = this.dir;
               }
            } else if(id.equals("IGNORE_CHK_TXT") && this.chk.isSelected()) {
               try {
                  URL exx2 = new URL(this.getURL());
                  String host1 = exx2.getHost();
                  if(this.config.siteList == null) {
                     this.config.siteList = new String[]{host1};
                  }

                  String[] newArray = new String[this.config.siteList.length + 1];
                  System.arraycopy(this.config.siteList, 0, newArray, 0, this.config.siteList.length);
                  newArray[this.config.siteList.length] = host1;
                  this.config.siteList = newArray;
                  this.setVisible(false);
                  this.dispose();
               } catch (Exception var7) {
                  var7.printStackTrace();
               }
            }
         }
      }

   }

   public static void main(String[] args) {
      (new DownloadFileInfoDialog()).setVisible(true);
   }

   protected void finalize() throws Throwable {
      super.finalize();
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
}
