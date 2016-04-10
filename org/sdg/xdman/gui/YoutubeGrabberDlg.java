package org.sdg.xdman.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.core.common.http.XDMHttpClient2;
import org.sdg.xdman.gui.StaticResource;
import org.sdg.xdman.gui.StringResource;
import org.sdg.xdman.gui.XDMFrame;
import org.sdg.xdman.gui.XDMIconMap;
import org.sdg.xdman.gui.YTListRenderer;
import org.sdg.xdman.interceptor.DownloadIntercepterInfo;
import org.sdg.xdman.interceptor.HTMLEscapeUtil;
import org.sdg.xdman.interceptor.HTMLTitleParser;
import org.sdg.xdman.plugin.youtube.JSONParser;
import org.sdg.xdman.plugin.youtube.ParserProgressListner;
import org.sdg.xdman.plugin.youtube.YTVideoInfo;
import org.sdg.xdman.util.XDMUtil;

public class YoutubeGrabberDlg extends XDMFrame implements ActionListener, ParserProgressListner, Runnable {
   private static final long serialVersionUID = -1072376334080340930L;
   Dimension btnDim;
   CardLayout card;
   JPanel p1;
   JPanel p2;
   JPanel p3;
   JTextField ytaddr;
   JButton get_video;
   JButton cancel;
   JButton abort;
   JPanel p;
   String info1 = StringResource.getString("YT_LBL1");
   String info2 = StringResource.getString("YT_LBL2");
   XDMConfig config;
   Thread ytd;
   JProgressBar prg;
   JTextField lbl;
   JList list;
   DefaultListModel model;
   JButton btnDwnld;
   JButton btnCancel;
   DownloadStateListner dl;
   byte[] b = new byte[8192];
   boolean network_err = true;
   boolean available = true;
   private XDMHttpClient2 client;

   public YoutubeGrabberDlg(DownloadStateListner mg) {
      this.dl = mg;
      this.setSize(420, 350);
      JLabel titleLbl = new JLabel(StringResource.getString("YT_LBL3"));
      titleLbl.setForeground(Color.WHITE);
      titleLbl.setFont(StaticResource.plainFontBig2);
      titleLbl.setBorder(new EmptyBorder(10, 10, 10, 10));
      this.getTitlePanel().add(titleLbl);
      this.createP1();
      this.createP2();
      this.createP3();
      this.card = new CardLayout();
      this.p = new JPanel(this.card);
      this.p.add(this.p1, "1");
      this.p.add(this.p2, "2");
      this.p.add(this.p3, "3");
      this.add(this.p);

      try {
         this.setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
      } catch (Exception var4) {
         ;
      }

      this.card.show(this.p, "1");
   }

   void showDialog(JFrame f, XDMConfig config, String txt2) {
      if(this.available) {
         this.card.show(this.p, "1");
         this.ytaddr.setText(txt2);
         this.setLocationRelativeTo(f);
         String txt = this.info2 + " 0 KB";
         this.lbl.setText(txt);
         this.config = config;
         this.setSize(420, 350);
         this.setVisible(true);
      }

   }

   void createP1() {
      Box p11 = Box.createVerticalBox();
      p11.setBackground(Color.white);
      p11.setOpaque(true);
      this.p1 = new JPanel(new BorderLayout());
      Box b1 = Box.createHorizontalBox();
      b1.setBackground(Color.white);
      b1.setOpaque(true);
      b1.setBorder(new EmptyBorder(25, 15, 10, 15));
      JLabel lbl = new JLabel();
      lbl.setText(StringResource.getString("YT_LBL1"));
      lbl.setOpaque(false);
      JLabel icon = new JLabel(XDMIconMap.getIcon("YT_ICON"));
      icon.setMaximumSize(new Dimension(75, 75));
      b1.add(icon);
      b1.add(Box.createRigidArea(new Dimension(10, 10)));
      b1.add(lbl);
      this.ytaddr = new JTextField();
      this.ytaddr.setMaximumSize(new Dimension(this.ytaddr.getMaximumSize().width, this.ytaddr.getPreferredSize().height));
      p11.add(b1);
      Box b2 = Box.createHorizontalBox();
      b2.add(this.ytaddr);
      b2.setBackground(Color.white);
      b2.setOpaque(true);
      b2.setBorder(new EmptyBorder(10, 15, 15, 15));
      p11.add(b2);
      p11.add(Box.createVerticalStrut(20));
      this.p1.add(p11);
      Box b3 = Box.createHorizontalBox();
      this.get_video = new JButton(StringResource.getString("YT_LBL4"));
      this.btnDim = this.get_video.getPreferredSize();
      this.get_video.setName("YT_LBL4");
      this.get_video.addActionListener(this);
      this.cancel = new JButton(StringResource.getString("CANCEL"));
      this.cancel.setName("CANCEL");
      this.cancel.addActionListener(this);
      this.cancel.setPreferredSize(this.btnDim);
      b3.add(Box.createHorizontalGlue());
      b3.add(this.get_video);
      b3.add(Box.createRigidArea(new Dimension(10, 10)));
      b3.add(this.cancel);
      b3.setBorder(new EmptyBorder(10, 15, 10, 15));
      b3.setOpaque(true);
      b3.setBackground(StaticResource.titleColor);
      this.p1.add(b3, "South");
   }

   void createP2() {
      this.p2 = new JPanel(new BorderLayout());
      Box p22 = Box.createVerticalBox();
      Box b0 = Box.createHorizontalBox();
      b0.setOpaque(true);
      b0.setBackground(Color.white);
      Box b1 = Box.createVerticalBox();
      b1.setOpaque(true);
      b1.setBackground(Color.white);
      b0.setBorder(new EmptyBorder(25, 15, 10, 15));
      this.lbl = new JTextField();
      this.lbl.setFont(new Font("Dialog", 1, 14));
      this.lbl.setOpaque(false);
      this.lbl.setEditable(false);
      this.lbl.setBorder((Border)null);
      this.lbl.setText(StringResource.getString("YT_LBL2"));
      JLabel icon = new JLabel(XDMIconMap.getIcon("YT_ICON"));
      icon.setMaximumSize(new Dimension(75, 75));
      Box bb = Box.createVerticalBox();
      bb.add(icon);
      bb.add(Box.createVerticalGlue());
      b0.add(bb);
      b0.add(Box.createRigidArea(new Dimension(10, 10)));
      this.prg = new JProgressBar();
      this.prg.setIndeterminate(true);
      this.prg.setPreferredSize(new Dimension(15, 15));
      this.prg.setMinimumSize(new Dimension(0, 15));
      this.prg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
      JTextArea label = new JTextArea(StringResource.getString("YT_LBL9"));
      label.setEditable(false);
      label.setWrapStyleWord(true);
      label.setOpaque(false);
      label.setBorder((Border)null);
      label.setLineWrap(true);
      this.lbl.setPreferredSize(new Dimension(label.getPreferredSize().width, 50));
      b1.add(this.lbl);
      b1.add(label);
      this.lbl.setText(StringResource.getString("YT_LBL2"));
      b1.add(Box.createVerticalStrut(20));
      b1.add(this.prg);
      b1.add(Box.createVerticalStrut(15));
      b0.add(b1);
      Box b3 = Box.createHorizontalBox();
      this.abort = new JButton(StringResource.getString("CANCEL"));
      if(this.btnDim != null) {
         this.abort.setPreferredSize(this.btnDim);
      }

      this.abort.setName("ABORT");
      this.abort.addActionListener(this);
      b3.add(Box.createHorizontalGlue());
      b3.add(this.abort);
      b3.setBorder(new EmptyBorder(10, 15, 10, 15));
      b3.setOpaque(true);
      b3.setBackground(StaticResource.titleColor);
      p22.add(b0);
      this.p2.add(p22);
      this.p2.add(b3, "South");
   }

   JPanel createP3() {
      this.p3 = new JPanel(new BorderLayout());
      this.model = new DefaultListModel();
      this.list = new JList(this.model);
      this.list.setCellRenderer(new YTListRenderer());
      this.p3.add(new JScrollPane(this.list));
      Box box = Box.createHorizontalBox();
      box.add(Box.createHorizontalGlue());
      this.btnDwnld = new JButton("Download");
      this.btnDwnld.addActionListener(this);
      this.btnCancel = new JButton("Close");
      this.btnCancel.addActionListener(this);
      box.add(this.btnDwnld);
      box.add(Box.createHorizontalStrut(10));
      box.add(this.btnCancel);
      this.btnCancel.setPreferredSize(this.btnDwnld.getPreferredSize());
      box.add(Box.createHorizontalStrut(10));
      box.add(Box.createRigidArea(new Dimension(0, 40)));
      this.p3.add(box, "South");
      box.setOpaque(true);
      box.setBackground(StaticResource.titleColor);
      return this.p3;
   }

   public void actionPerformed(ActionEvent e) {
      if(e.getSource() == this.get_video) {
         String index = this.ytaddr.getText();
         if(index.length() < 1) {
            JOptionPane.showMessageDialog(this, StringResource.getString("URL_EMPTY"));
            return;
         }

         if(!XDMUtil.validateURL(index)) {
            JOptionPane.showMessageDialog(this, StringResource.getString("YT_LBL5"));
            return;
         }

         if(!index.startsWith("http://www.youtube.com/watch") && !index.startsWith("https://www.youtube.com/watch")) {
            JOptionPane.showMessageDialog(this, StringResource.getString("YT_LBL5"));
            return;
         }

         this.card.show(this.p, "2");
         this.ytd = new Thread(this);
         this.ytd.start();
      }

      if(e.getSource() == this.abort) {
         if(this.ytd != null) {
            this.ytd.interrupt();
            this.setVisible(false);
         }

         this.setVisible(false);
      }

      if(e.getSource() == this.cancel) {
         this.setVisible(false);
      }

      if(e.getSource() == this.btnDwnld) {
         int index1 = this.list.getSelectedIndex();
         if(index1 < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to download");
            return;
         }

         YTVideoInfo info = (YTVideoInfo)this.model.get(index1);
         DownloadIntercepterInfo di = new DownloadIntercepterInfo();
         di.url = info.url;
         di.referer = info.ua;
         di.referer = info.referer;
         this.dl.interceptDownload(di);
      }

      if(e.getSource() == this.btnCancel) {
         this.setVisible(false);
      }

   }

   public void update(long downloaded) {
      String txt = this.info2 + XDMUtil.getFormattedLength((double)downloaded);
      this.lbl.setText(txt);
   }

   public void run() {
      this.grabVideo();
   }

   void grabVideo() {
      this.network_err = true;
      String tmpFile = null;
      FileInputStream tmpStreamIn = null;
      FileOutputStream tmpStreamOut = null;
      ArrayList list = null;
      String title = "";
      int count = 0;
      String ua = "Mozilla/5.0 (Windows NT 6.1; rv:31.0) Gecko/20100101 Firefox/31.0";
      String url = this.ytaddr.getText();

      try {
         int e;
         label319:
         do {
            this.available = false;
            this.client = new XDMHttpClient2(this.config);
            this.client.addRequestHeaders("accept-encoding", "gzip");
            if(count == 2) {
               ua = "Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
            } else if(count == 1) {
               ua = "Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3";
            }

            this.client.addRequestHeaders("user-agent", ua);
            this.client.connect(url);
            this.client.sendRequest();
            if(Thread.interrupted()) {
               throw new InterruptedException();
            }

            e = this.client.getResponseCode();
            if(e != 302 && e != 301) {
               if(e != 200 && e != 206) {
                  throw new Exception("Invalid response: " + e);
               }

               InputStream info = this.client.in;
               tmpFile = XDMUtil.getTempFile(this.config.tempdir).getAbsolutePath();
               tmpStreamOut = new FileOutputStream(tmpFile);
               long read = 0L;

               do {
                  int r = info.read(this.b, 0, this.b.length);
                  if(r == -1) {
                     tmpStreamOut.close();
                     this.client.close();
                     this.network_err = false;
                     tmpStreamIn = new FileInputStream(tmpFile);
                     BufferedReader var40 = new BufferedReader(new InputStreamReader(tmpStreamIn));
                     title = HTMLTitleParser.GetTitleFromPage(var40);
                     title = HTMLEscapeUtil.escapeHTMLLine(title);
                     title = XDMUtil.createSafeFileName(title);
                     tmpStreamIn.close();
                     tmpStreamIn = new FileInputStream(tmpFile);
                     JSONParser p = new JSONParser();
                     list = p.list(tmpStreamIn);
                     if(list != null && list.size() > 0) {
                        break label319;
                     }

                     try {
                        tmpStreamIn.close();
                     } catch (Exception var36) {
                        ;
                     }

                     if(tmpFile != null) {
                        (new File(tmpFile)).delete();
                     }

                     ++count;
                     continue label319;
                  }

                  read += (long)r;
                  tmpStreamOut.write(this.b, 0, r);
                  this.update(read);
               } while(!Thread.interrupted());

               throw new InterruptedException();
            } else {
               url = this.client.getResponseHeader("location");
               this.client.close();
            }
         } while(count < 3);

         if(list == null || list.size() < 1) {
            this.showError();
            (new File(tmpFile)).delete();
            return;
         }

         this.model.clear();

         for(e = 0; e < list.size(); ++e) {
            YTVideoInfo var39 = (YTVideoInfo)list.get(e);
            var39.referer = url;
            var39.ua = ua;
            var39.name = XDMUtil.getFileName(var39.url);
            if(var39 != null) {
               this.model.addElement(var39);
            }
         }

         this.card.show(this.p, "3");
      } catch (Exception var37) {
         var37.printStackTrace();
         if(!(var37 instanceof InterruptedException)) {
            this.showError();
         }

         this.setVisible(false);
      } finally {
         try {
            tmpStreamIn.close();
         } catch (Exception var35) {
            ;
         }

         try {
            tmpStreamOut.close();
         } catch (Exception var34) {
            ;
         }

         try {
            this.client.close();
         } catch (Exception var33) {
            ;
         }

         if(tmpFile != null) {
            (new File(tmpFile)).delete();
         }

         this.available = true;
      }

   }

   void showError() {
      if(this.network_err) {
         JOptionPane.showMessageDialog(this, StringResource.getString("YT_LBL7"));
      } else {
         JOptionPane.showMessageDialog(this, StringResource.getString("YT_LBL8"));
      }

   }

   public static void main(String[] args) {
   }
}
