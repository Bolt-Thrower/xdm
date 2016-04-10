package org.sdg.xdman.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.gui.HelpDialog;
import org.sdg.xdman.gui.StaticResource;
import org.sdg.xdman.gui.StringResource;
import org.sdg.xdman.gui.TitlePanel;
import org.sdg.xdman.gui.XDMButton;
import org.sdg.xdman.gui.XDMIconMap;
import org.sdg.xdman.util.LinuxUtil;
import org.sdg.xdman.util.OSXUtil;
import org.sdg.xdman.util.XDMUtil;

public class BrowserIntDlg extends JDialog implements ActionListener {
   private static final long serialVersionUID = -6629147016436649030L;
   JTabbedPane pane;
   JPanel p0;
   JPanel p1;
   JPanel p2;
   JPanel p3;
   JTextArea text1;
   JTextArea text2;
   JTextArea text3;
   JTextArea text4;
   JLabel ff;
   JButton helpff;
   JButton auto;
   JButton man;
   JButton openfolder1;
   JButton chromeHelp;
   JButton openfolder2;
   JButton operaHelp;
   JCheckBox autoStart;
   JCheckBox adv;
   boolean unix = false;
   JButton btn1;
   JButton btn2;
   XDMConfig config;
   Color bgColor;
   JPanel centerPanel;
   JPanel chromePanel;
   JPanel ffPanel;
   JPanel othersPanel;
   CardLayout card;
   JButton cb;
   JButton fb;
   JButton ob;
   JPanel cardPanel;

   public BrowserIntDlg(JFrame f, XDMConfig config) {
      super(f);
      this.setUndecorated(true);
      this.centerPanel = new JPanel(new BorderLayout());
      this.centerPanel.setBorder(new LineBorder(Color.BLACK));
      this.add(this.centerPanel);
      this.config = config;
      this.setTitle(StringResource.getString("BI_LBL_11"));
      this.setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
      TitlePanel tp = new TitlePanel(new BorderLayout(), this);
      tp.setBackground(StaticResource.titleColor);
      JLabel title = new JLabel(StringResource.getString("BI_LBL_11"));
      title.setForeground(Color.WHITE);
      title.setBorder(new EmptyBorder(20, 20, 20, 20));
      title.setFont(title.getFont().deriveFont(1, (float)title.getFont().getSize() * 1.2F));
      tp.add(title, "Center");
      XDMButton closeBtn = new XDMButton();
      closeBtn.setBounds(320, 5, 24, 24);
      closeBtn.setContentAreaFilled(false);
      closeBtn.setBorderPainted(false);
      closeBtn.setFocusPainted(false);
      closeBtn.setMargin(new Insets(0, 0, 0, 0));
      closeBtn.setIcon(StaticResource.getIcon("close_btn.png"));
      closeBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            BrowserIntDlg.this.setVisible(false);
         }
      });
      Box b = Box.createVerticalBox();
      b.setBorder(new EmptyBorder(5, 5, 5, 5));
      b.add(closeBtn);
      b.add(Box.createVerticalGlue());
      tp.add(b, "East");
      this.add(tp, "North");
      this.ff = new JLabel(XDMIconMap.getIcon("APP_ICON"));
      this.bgColor = new Color(UIManager.getColor("Label.background").getRGB());
      this.ff.setText("http://127.0.0.1:9614/xdmff.xpi");
      this.ff.setName("http://127.0.0.1:9614/xdmff.xpi");
      this.ff.setHorizontalAlignment(0);
      this.ff.setVerticalAlignment(0);
      this.ff.setHorizontalTextPosition(0);
      this.ff.setVerticalTextPosition(3);
      this.ff.setBackground(Color.WHITE);
      this.ff.setOpaque(true);
      this.ff.setBorder(new LineBorder(Color.GRAY));
      this.ff.setCursor(new Cursor(13));
      this.ff.setTransferHandler(new TransferHandler() {
         private static final long serialVersionUID = 1L;

         protected Transferable createTransferable(JComponent c) {
            return new StringSelection(c.getName());
         }

         public int getSourceActions(JComponent c) {
            return 1;
         }
      });
      this.ff.addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent e) {
            JComponent c = (JComponent)e.getSource();
            TransferHandler th = c.getTransferHandler();
            th.exportAsDrag(c, e, 1);
         }
      });
      this.btn1 = new JButton(StringResource.getString("BI_LBL_7"));
      this.btn1.addActionListener(this);
      this.btn2 = new JButton(StringResource.getString("BI_LBL_12"));
      this.btn2.addActionListener(this);
      this.autoStart = new JCheckBox(StringResource.getString("BI_LBL_8"));
      this.autoStart.addActionListener(this);
      this.autoStart.setSelected(config.autostart);
      this.autoStart.setBorder(new EmptyBorder(20, 20, 20, 20));
      this.autoStart.setBackground(Color.WHITE);
      this.createChromePanel();
      this.createFFPanel();
      this.createOthersPanel();
      this.card = new CardLayout();
      this.cardPanel = new JPanel(this.card);
      this.cardPanel.add(this.chromePanel, "CP");
      this.cardPanel.add(this.ffPanel, "FP");
      this.cardPanel.add(this.othersPanel, "OP");
      this.card.first(this.cardPanel);
      this.centerPanel.add(this.cardPanel);
      this.setSize(450, 450);
      this.cb = new XDMButton("Google Chrome");
      this.fb = new XDMButton("Mozilla Firefox");
      this.ob = new XDMButton("Other Browsers");
      this.cb.setIcon(StaticResource.getIcon("chrome.png"));
      this.fb.setIcon(StaticResource.getIcon("firefox.png"));
      this.ob.setIcon(StaticResource.getIcon("other.png"));
      this.cb.setBackground(StaticResource.selectedColor);
      this.cb.addActionListener(this);
      this.fb.addActionListener(this);
      this.ob.addActionListener(this);
      this.prepareButton(this.cb);
      this.prepareButton(this.fb);
      this.prepareButton(this.ob);
      JPanel bp = new JPanel(new GridLayout());
      bp.add(this.cb);
      bp.add(this.fb);
      bp.add(this.ob);
      this.centerPanel.add(bp, "North");
      this.createExt();
   }

   private void createExt() {
      try {
         File e = new File(System.getProperty("user.home"), "xdm-helper");
         e.mkdirs();
         XDMUtil.copyStream(this.getClass().getResourceAsStream("/ext/xdm-helper/background.js"), new FileOutputStream(new File(e, "background.js")));
         XDMUtil.copyStream(this.getClass().getResourceAsStream("/ext/xdm-helper/manifest.json"), new FileOutputStream(new File(e, "manifest.json")));
         XDMUtil.copyStream(this.getClass().getResourceAsStream("/ext/xdmff.xpi"), new FileOutputStream(new File(e, "xdmff.xpi")));
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   void prepareButton(JButton btn) {
      btn.setBorderPainted(false);
      btn.setFocusPainted(false);
      btn.setHorizontalAlignment(0);
      btn.setHorizontalTextPosition(0);
      btn.setVerticalTextPosition(3);
   }

   void createChromePanel() {
      this.chromePanel = new JPanel(new BorderLayout());
      this.chromePanel.setBackground(Color.WHITE);
      JLabel lbl = new JLabel(XDMIconMap.getIcon("CI_ICON"), 2);
      lbl.setBorder(new EmptyBorder(20, 20, 20, 20));
      this.chromePanel.add(lbl, "North");
      JTextArea text3 = new JTextArea();
      text3.setBackground(this.bgColor);
      text3.setOpaque(false);
      text3.setWrapStyleWord(true);
      text3.setEditable(false);
      text3.setLineWrap(true);
      text3.setBorder(new EmptyBorder(0, 20, 20, 20));
      String txt = (new File(System.getProperty("user.home"), "xdm-helper")).getAbsolutePath();
      text3.setText(StringResource.getString("BI_LBL_17").replace("<FOLDER>", txt));
      this.chromePanel.add(text3);
   }

   void createFFPanel() {
      this.ffPanel = new JPanel(new BorderLayout(20, 20));
      this.ffPanel.setBackground(Color.WHITE);
      this.ffPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
      this.text2 = new JTextArea();
      Cursor c = this.text2.getCursor();
      this.text2.setBackground(this.bgColor);
      this.text2.setOpaque(false);
      this.text2.setWrapStyleWord(true);
      this.text2.setEditable(false);
      this.text2.setLineWrap(true);
      this.text2.setText(StringResource.getString("BI_LBL_2"));
      this.text2.setCursor(c);
      this.ffPanel.add(this.text2, "North");
      this.ffPanel.add(this.ff);
      this.helpff = new JButton(StringResource.getString("BI_LBL_3"));
      this.helpff.addActionListener(this);
      JPanel pp = new JPanel(new BorderLayout(10, 10));
      pp.setBackground(Color.white);
      JTextArea txt2 = new JTextArea();
      txt2.setOpaque(false);
      txt2.setWrapStyleWord(true);
      txt2.setEditable(false);
      txt2.setLineWrap(true);
      String txt = (new File(System.getProperty("user.home"), "xdm-helper/xdmff.xpi")).getAbsolutePath();
      txt2.setText(StringResource.getString("BI_LBL_FF").replace("<FILE>", txt));
      pp.add(txt2);
      pp.add(this.helpff, "South");
      this.ffPanel.add(pp, "South");
   }

   void createOthersPanel() {
      this.othersPanel = new JPanel(new BorderLayout(10, 10));
      this.othersPanel.setBackground(Color.WHITE);
      this.othersPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
      JTextArea txt1 = new JTextArea(StringResource.getString("BI_LBL_6"));
      txt1.setBorder(new EmptyBorder(20, 20, 20, 20));
      txt1.setBackground(this.bgColor);
      txt1.setOpaque(false);
      txt1.setWrapStyleWord(true);
      txt1.setEditable(false);
      txt1.setLineWrap(true);
      this.othersPanel.add(txt1, "North");
      JPanel biPanel = new JPanel(new GridLayout(2, 1, 20, 20));
      biPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
      biPanel.add(this.btn1);
      biPanel.add(this.btn2);
      biPanel.setBackground(Color.WHITE);
      this.othersPanel.add(biPanel);
      this.othersPanel.add(this.autoStart, "South");
   }

   void setProxy() {
      if(JOptionPane.showConfirmDialog(this, StringResource.getString("BI_LBL_13"), StringResource.getString("DEFAULT_TITLE"), 0, 3) == 0) {
         String osName = System.getProperty("os.name");
         if(osName.contains("OS X")) {
            if(!OSXUtil.attachProxy()) {
               JOptionPane.showMessageDialog((Component)null, StringResource.getString("BI_LBL_14"), StringResource.getString("DEFAULT_TITLE"), 0);
            } else {
               JOptionPane.showMessageDialog((Component)null, StringResource.getString("BI_LBL_16"));
            }
         } else if(!LinuxUtil.attachProxy()) {
            JOptionPane.showMessageDialog((Component)null, StringResource.getString("BI_LBL_14"), StringResource.getString("DEFAULT_TITLE"), 0);
         } else {
            JOptionPane.showMessageDialog((Component)null, StringResource.getString("BI_LBL_16"));
         }
      }

   }

   public void actionPerformed(ActionEvent e) {
      if(e.getSource() == this.btn1) {
         this.setProxy();
      } else if(e.getSource() == this.cb) {
         this.card.show(this.cardPanel, "CP");
         this.cb.setBackground(StaticResource.selectedColor);
         this.ob.setBackground(this.bgColor);
         this.fb.setBackground(this.bgColor);
      } else if(e.getSource() == this.fb) {
         this.card.show(this.cardPanel, "FP");
         this.fb.setBackground(StaticResource.selectedColor);
         this.ob.setBackground(this.bgColor);
         this.cb.setBackground(this.bgColor);
      } else if(e.getSource() == this.ob) {
         this.card.show(this.cardPanel, "OP");
         this.ob.setBackground(StaticResource.selectedColor);
         this.fb.setBackground(this.bgColor);
         this.cb.setBackground(this.bgColor);
      } else if(e.getSource() == this.autoStart) {
         if(this.autoStart.isSelected()) {
            if(System.getProperty("os.name").contains("OS X")) {
               OSXUtil.enableAutoStart();
            } else {
               LinuxUtil.enableAutoStartLinux();
            }

            this.config.autostart = true;
         } else {
            if(System.getProperty("os.name").contains("OS X")) {
               OSXUtil.disableAutoStart();
            } else {
               LinuxUtil.disableAutoStartLinux();
            }

            this.config.autostart = false;
         }
      } else if(e.getSource() == this.btn2) {
         this.showHelp("BROWSER_INTEGRATION");
      } else if(e.getSource() == this.helpff) {
         this.showHelp("CAPTURE_VIDEO");
      }

   }

   void showHelp(String topic) {
      try {
         HelpDialog err = HelpDialog.getHelpDialog();
         err.setDocument(topic);
         err.setLocationRelativeTo((Component)null);
         err.setVisible(true);
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }
}
