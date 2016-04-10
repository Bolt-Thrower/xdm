package org.sdg.xdman.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.JSpinner.DateEditor;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import org.sdg.xdman.core.common.Authenticator;
import org.sdg.xdman.core.common.Credential;
import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.gui.CredentialTableModel;
import org.sdg.xdman.gui.StaticResource;
import org.sdg.xdman.gui.StringResource;
import org.sdg.xdman.gui.TitlePanel;
import org.sdg.xdman.gui.XDMIconMap;
import org.sdg.xdman.gui.XDMTableHeaderRenderer;
import org.sdg.xdman.util.XDMUtil;

public class ConfigDialog extends JDialog implements ActionListener {
   private static final long serialVersionUID = -4157977457853022678L;
   JButton save;
   JButton cancel;
   JButton br1;
   JButton br2;
   JButton defaults;
   JButton addAuth;
   JButton removeAuth;
   JButton editAuth;
   JLabel title;
   JLabel[] arrLbl;
   CardLayout cardLayout;
   JPanel general;
   JPanel saveto;
   JPanel advanced;
   JPanel connection;
   JPanel proxyPanel;
   JPanel fileTypesPanel;
   JPanel authPanel;
   JPanel schedulePanel;
   JPanel centerPanel;
   JPanel rightPanel;
   JPanel cardPanel;
   JPanel pane;
   Font plainFont;
   Font boldFont;
   JCheckBox chkDwnldPrg;
   JCheckBox chkFinishedDlg;
   JCheckBox chkAllowBrowser;
   JCheckBox schedule;
   JCheckBox chkCustCmd;
   JCheckBox chkHltCmd;
   JCheckBox chkHngCmd;
   JCheckBox chkAvCmd;
   JComboBox cmbConn;
   JComboBox cmbTimeout;
   JComboBox cmbTcpW;
   JComboBox cmbDupAction;
   JScrollPane jsp;
   JTextField txtTmpDir;
   JTextField txtDstDir;
   JTextField txtCustCmd;
   JTextField txtHltCmd;
   JTextField txtMdmCmd;
   JTextField txtScnCmd;
   JTextArea txtArea;
   JTextArea txtException;
   SpinnerDateModel start;
   SpinnerDateModel end;
   JSpinner startDate;
   JSpinner endDate;
   CredentialTableModel model;
   JTable table;
   XDMConfig config;
   JFileChooser folderBrowser;
   DownloadStateListner mgr;
   Frame parent;
   ConfigDialog.ConfigMouseAdapter clickHandler;

   public ConfigDialog(Frame parent, XDMConfig config, DownloadStateListner mgr) {
      super(parent, true);
      this.parent = parent;
      this.config = config;
      this.mgr = mgr;
      this.init();
      this.setConfig();
   }

   public void showDialog() {
      this.setConfig();
      this.setLocationRelativeTo(this.parent);
      this.setVisible(true);
   }

   private void createFolderBrowser() {
      this.folderBrowser = new JFileChooser();
      this.folderBrowser.setFileSelectionMode(1);
   }

   public void actionPerformed(ActionEvent e) {
      if(e.getSource() instanceof JButton) {
         String cmd = ((JButton)e.getSource()).getName();
         if(cmd == null) {
            return;
         }

         if(cmd.equals("BR_TMP_DIR")) {
            if(this.folderBrowser == null) {
               this.createFolderBrowser();
            }

            this.folderBrowser.setCurrentDirectory(new File(this.config.tempdir));
            if(this.folderBrowser.showSaveDialog(this) == 0) {
               this.txtTmpDir.setText(this.folderBrowser.getSelectedFile().getAbsolutePath());
            }
         } else if(cmd.equals("BR_DST_DIR")) {
            if(this.folderBrowser == null) {
               this.createFolderBrowser();
            }

            this.folderBrowser.setCurrentDirectory(new File(this.config.destdir));
            if(this.folderBrowser.showSaveDialog(this) == 0) {
               this.txtDstDir.setText(this.folderBrowser.getSelectedFile().getAbsolutePath());
            }
         } else if(cmd.equals("HTTP_PROXY")) {
            this.showProxyDialog(0);
         } else if(cmd.equals("HTTPS_PROXY")) {
            this.showProxyDialog(1);
         } else if(cmd.equals("FTP_PROXY")) {
            this.showProxyDialog(2);
         } else if(cmd.equals("LBL_DEFAULTS")) {
            String c = "";

            for(int host = 0; host < this.config.defaultFileTypes.length; ++host) {
               c = c + this.config.defaultFileTypes[host] + " ";
            }

            this.txtArea.setText(c);
         } else {
            int var5;
            if(cmd.equals("LBL_EDT_AUTH")) {
               var5 = this.table.getSelectedRow();
               if(var5 < 0) {
                  JOptionPane.showMessageDialog(this, "No item selected");
                  return;
               }

               Credential var7 = this.getCredential("" + this.model.getValueAt(var5, 0), "" + this.model.getValueAt(var5, 1), "");
               if(var7 != null) {
                  Authenticator.getInstance().addCreditential(var7);
               }
            } else if(cmd.equals("LBL_DEL_AUTH")) {
               var5 = this.table.getSelectedRow();
               if(var5 < 0) {
                  JOptionPane.showMessageDialog(this, this.getString("NONE_SELECTED"));
                  return;
               }

               String var8 = "" + this.model.getValueAt(var5, 0);
               Authenticator.getInstance().removeCreditential(var8);
            } else if(cmd.equals("LBL_ADD_AUTH")) {
               Credential var6 = this.getCredential("", "", "");
               if(var6 != null) {
                  Authenticator.getInstance().addCreditential(var6);
               }
            } else if(cmd.equals("SAVE")) {
               this.saveConfig();
               this.setVisible(false);
            } else if(cmd.equals("CANCEL")) {
               this.setVisible(false);
            }
         }
      }

   }

   Credential getCredential(String shost, String suser, String spass) {
      JTextField host = new JTextField(shost);
      JTextField user = new JTextField(suser);
      JPasswordField pass = new JPasswordField(spass);
      Object[] obj = new Object[]{this.getString("HOST"), host, this.getString("USER_NAME"), user, this.getString("PASSWORD"), pass};

      while(true) {
         while(JOptionPane.showOptionDialog((Component)null, obj, this.getString("LBL_CR"), 2, 3, (Icon)null, (Object[])null, (Object)null) == 0) {
            if(host.getText() != null && host.getText().length() >= 1) {
               if(user.getText() != null && user.getText().length() >= 1) {
                  Credential c = new Credential();
                  c.host = host.getText();
                  c.user = user.getText();
                  c.pass = pass.getPassword().length > 0?new String(pass.getPassword()):null;
                  return c;
               }

               JOptionPane.showMessageDialog((Component)null, this.getString("LBL_USER"));
            } else {
               JOptionPane.showMessageDialog((Component)null, this.getString("LBL_HOST"));
            }
         }

         return null;
      }
   }

   private void showProxyDialog(int proxy_type) {
      JTextField address = new JTextField();
      JTextField user = new JTextField();
      JPasswordField pass = new JPasswordField();
      JCheckBox useProxy = new JCheckBox();
      String type = this.getString("LBL_HTTP_PROXY");
      if(proxy_type == 1) {
         type = this.getString("LBL_HTTPS_PROXY");
         useProxy.setText(type);
         useProxy.setSelected(this.config.useHttpsProxy);
         if(!XDMUtil.isNullOrEmpty(this.config.httpsProxyHost)) {
            address.setText(this.config.httpsProxyHost + ":" + this.config.httpsProxyPort);
            user.setText(this.config.httpsUser);
            pass.setText(this.config.httpsPass);
         }
      } else if(proxy_type == 2) {
         type = this.getString("LBL_FTP_PROXY");
         useProxy.setText(type);
         useProxy.setSelected(this.config.useFtpProxy);
         if(!XDMUtil.isNullOrEmpty(this.config.ftpProxyHost)) {
            address.setText(this.config.ftpProxyHost + ":" + this.config.ftpProxyPort);
            user.setText(this.config.ftpUser);
            pass.setText(this.config.ftpPass);
         }
      } else {
         useProxy.setText(type);
         useProxy.setSelected(this.config.useHttpProxy);
         if(!XDMUtil.isNullOrEmpty(this.config.httpProxyHost)) {
            address.setText(this.config.httpProxyHost + ":" + this.config.httpProxyPort);
            user.setText(this.config.httpUser);
            pass.setText(this.config.httpPass);
         }
      }

      for(Object[] objs = new Object[]{useProxy, this.getString("LBL_PROXY_ADDR"), address, this.getString("LBL_PROXY_USER"), user, this.getString("LBL_PROXY_PASS"), pass}; JOptionPane.showOptionDialog(this, objs, type, 2, 3, (Icon)null, (Object[])null, (Object)null) == 0; JOptionPane.showMessageDialog(this, this.getString("LBL_PROXY_INVALID"))) {
         String proxy_addr = address.getText();
         String[] arr;
         int port;
         if(proxy_type == 0) {
            if(proxy_addr.length() < 1) {
               this.config.useHttpProxy = false;
               return;
            }

            try {
               if(!useProxy.isSelected()) {
                  this.config.useHttpProxy = false;
                  return;
               }

               arr = proxy_addr.split(":");
               if(arr[0].length() > 0) {
                  port = Integer.parseInt(arr[1]);
                  if(port > 0 && port < '\ufffe') {
                     this.config.httpProxyHost = arr[0];
                     this.config.httpProxyPort = port;
                     if(user.getText().length() > 0) {
                        this.config.httpUser = user.getText();
                        if(pass.getPassword().length > 0) {
                           this.config.httpPass = new String(pass.getPassword());
                        }
                     }

                     this.config.useHttpProxy = true;
                     return;
                  }
               }
            } catch (Exception var13) {
               ;
            }
         } else if(proxy_type == 1) {
            if(proxy_addr.length() < 1) {
               this.config.useHttpsProxy = false;
               return;
            }

            try {
               if(!useProxy.isSelected()) {
                  this.config.useHttpProxy = false;
                  return;
               }

               arr = proxy_addr.split(":");
               if(arr[0].length() > 0) {
                  port = Integer.parseInt(arr[1]);
                  if(port > 0 && port < '\ufffe') {
                     this.config.httpsProxyHost = arr[0];
                     this.config.httpsProxyPort = port;
                     if(user.getText().length() > 0) {
                        this.config.httpsUser = user.getText();
                        if(pass.getPassword().length > 0) {
                           this.config.httpsPass = new String(pass.getPassword());
                        }
                     }

                     this.config.useHttpsProxy = true;
                     return;
                  }
               }
            } catch (Exception var12) {
               ;
            }
         } else if(proxy_type == 2) {
            if(proxy_addr.length() < 1) {
               this.config.useFtpProxy = false;
               return;
            }

            try {
               if(!useProxy.isSelected()) {
                  this.config.useHttpProxy = false;
                  return;
               }

               arr = proxy_addr.split(":");
               if(arr[0].length() > 0) {
                  port = Integer.parseInt(arr[1]);
                  if(port > 0 && port < '\ufffe') {
                     this.config.ftpProxyHost = arr[0];
                     this.config.ftpProxyPort = port;
                     if(user.getText().length() > 0) {
                        this.config.ftpUser = user.getText();
                        if(pass.getPassword().length > 0) {
                           this.config.ftpPass = new String(pass.getPassword());
                        }
                     }

                     this.config.useFtpProxy = true;
                     return;
                  }
               }
            } catch (Exception var11) {
               ;
            }
         }
      }

   }

   private void saveConfig() {
      this.config.showDownloadPrgDlg = this.chkDwnldPrg.isSelected();
      this.config.showDownloadPrgDlg = this.chkDwnldPrg.isSelected();
      this.config.showDownloadCompleteDlg = this.chkFinishedDlg.isSelected();
      this.config.allowbrowser = this.chkAllowBrowser.isSelected();
      this.config.duplicateLinkAction = this.cmbDupAction.getSelectedIndex();
      this.config.maxConn = Integer.parseInt("" + this.cmbConn.getSelectedItem());
      this.config.timeout = Integer.parseInt("" + this.cmbTimeout.getSelectedItem());
      this.config.tcpBuf = Integer.parseInt("" + this.cmbTcpW.getSelectedItem());
      this.config.tempdir = this.txtTmpDir.getText();
      this.config.destdir = this.txtDstDir.getText();
      this.config.executeCmd = this.chkCustCmd.isSelected();
      this.config.cmdTxt = this.txtCustCmd.getText();
      this.config.halt = this.chkHltCmd.isSelected();
      this.config.haltTxt = this.txtHltCmd.getText();
      this.config.hungUpTxt = this.txtMdmCmd.getText();
      this.config.hungUp = this.chkHngCmd.isSelected();
      this.config.antivirTxt = this.txtScnCmd.getText();
      ArrayList lst = new ArrayList();
      String[] arr = this.txtArea.getText().replaceAll("\n", " ").split(" ");

      int i;
      for(i = 0; i < arr.length; ++i) {
         String t = arr[i].trim();
         if(t.length() > 0) {
            lst.add(t);
         }
      }

      this.config.fileTypes = new String[lst.size()];

      for(i = 0; i < lst.size(); ++i) {
         this.config.fileTypes[i] = (String)lst.get(i);
      }

      arr = this.txtException.getText().split("\n");
      this.config.siteList = arr;
      this.config.schedule = this.schedule.isSelected();
      this.config.startDate = this.start.getDate();
      this.config.endDate = this.end.getDate();
      this.config.save();
      if(this.mgr != null) {
         this.mgr.configChanged();
      }

   }

   private void setConfig() {
      this.chkDwnldPrg.setSelected(this.config.showDownloadPrgDlg);
      this.chkFinishedDlg.setSelected(this.config.showDownloadCompleteDlg);
      this.chkAllowBrowser.setSelected(this.config.allowbrowser);
      this.cmbDupAction.setSelectedIndex(this.config.duplicateLinkAction);
      this.cmbConn.setSelectedItem(String.valueOf(this.config.maxConn));
      this.cmbTimeout.setSelectedItem(String.valueOf(this.config.timeout));
      this.cmbTcpW.setSelectedItem(String.valueOf(this.config.tcpBuf));
      this.txtTmpDir.setText(this.config.tempdir);
      this.txtDstDir.setText(this.config.destdir);
      this.chkCustCmd.setSelected(this.config.executeCmd);
      this.txtCustCmd.setText(this.config.cmdTxt);
      this.chkHltCmd.setSelected(this.config.halt);
      this.txtHltCmd.setText(this.config.haltTxt);
      this.txtMdmCmd.setText(this.config.hungUpTxt);
      this.chkHngCmd.setSelected(this.config.hungUp);
      this.txtScnCmd.setText(this.config.antivirTxt);
      String[] arr = this.config.fileTypes;
      String types = "";

      for(int sites = 0; sites < arr.length; ++sites) {
         types = types + arr[sites] + " ";
      }

      this.txtArea.setText(types);
      arr = this.config.siteList;
      String var5 = "";

      for(int i = 0; i < arr.length; ++i) {
         var5 = var5 + arr[i] + "\n";
      }

      this.txtException.setText(var5);
      this.schedule.setSelected(this.config.schedule);
      if(this.config.startDate != null) {
         this.start.setValue(this.config.startDate);
         this.end.setValue(this.config.endDate);
      }

      this.model.load();
   }

   private void init() {
      this.setTitle(this.getString("CONFIG_TITLE"));
      this.setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
      this.setUndecorated(true);
      this.setSize(500, 400);
      this.plainFont = new Font("Dialog", 0, 12);
      this.boldFont = new Font("Dialog", 3, 12);
      this.clickHandler = new ConfigDialog.ConfigMouseAdapter();
      this.centerPanel = new JPanel(new BorderLayout());
      this.centerPanel.setBorder(new LineBorder(Color.BLACK));
      this.add(this.centerPanel);
      this.createLeftPanel();
      this.createRightPanel();
      this.cardLayout = new CardLayout();
      this.cardPanel = new JPanel(this.cardLayout);
      this.cardPanel.setBackground(Color.white);
      this.pane.add(this.cardPanel);
      this.cardPanel.add(this.createGeneralPanel(), this.getString("CONFIG_LBL1"));
      this.cardPanel.add(this.createConnectionPanel(), this.getString("CONFIG_LBL2"));
      this.cardPanel.add(this.createSaveToPanel(), this.getString("CONFIG_LBL3"));
      this.cardPanel.add(this.createAdvancedPanel(), this.getString("CONFIG_LBL4"));
      this.cardPanel.add(this.createProxyPanel(), this.getString("CONFIG_LBL5"));
      this.cardPanel.add(this.createTypesPanel(), this.getString("CONFIG_LBL6"));
      this.cardPanel.add(this.createCredentialPanel(), this.getString("CONFIG_LBL7"));
      this.cardPanel.add(this.createSchedulerPanel(), this.getString("CONFIG_LBL8"));
      this.cardPanel.add(this.createExceptionsPanel(), this.getString("CONFIG_LBL9"));
      this.showPanel(this.getString("CONFIG_LBL1"));
   }

   void showPanel(String name) {
      this.title.setText(name);
      this.cardLayout.show(this.cardPanel, name);
   }

   JPanel createCredentialPanel() {
      JPanel box = new JPanel(new BorderLayout(5, 5));
      box.setOpaque(false);
      box.setBorder(new EmptyBorder(10, 0, 0, 10));
      this.model = new CredentialTableModel();
      Authenticator.getInstance().addObserver(this.model);
      this.table = new JTable(this.model);
      if(System.getProperty("xdm.defaulttheme") != null) {
         this.table.getTableHeader().setDefaultRenderer(new XDMTableHeaderRenderer());
      }

      this.table.setFillsViewportHeight(true);
      JScrollPane jsp = new JScrollPane(this.table);
      jsp.setPreferredSize(new Dimension(10, 10));
      box.add(jsp);
      Box b = Box.createHorizontalBox();
      b.add(Box.createHorizontalGlue());
      this.addAuth = new JButton(this.getString("LBL_ADD_AUTH"));
      this.addAuth.setName("LBL_ADD_AUTH");
      this.addAuth.addActionListener(this);
      this.removeAuth = new JButton(this.getString("LBL_DEL_AUTH"));
      this.removeAuth.setName("LBL_DEL_AUTH");
      this.removeAuth.addActionListener(this);
      this.editAuth = new JButton(this.getString("LBL_EDT_AUTH"));
      this.editAuth.setName("LBL_EDT_AUTH");
      this.editAuth.addActionListener(this);
      this.addAuth.setPreferredSize(this.removeAuth.getPreferredSize());
      this.editAuth.setPreferredSize(this.removeAuth.getPreferredSize());
      b.add(this.addAuth);
      b.add(Box.createHorizontalStrut(10));
      b.add(this.removeAuth);
      b.add(Box.createHorizontalStrut(10));
      b.add(this.editAuth);
      box.add(b, "South");
      return box;
   }

   Box createExceptionsPanel() {
      Box box = Box.createVerticalBox();
      box.setOpaque(false);
      box.setBorder(new EmptyBorder(10, 0, 0, 10));
      Box b0 = Box.createHorizontalBox();
      b0.add(new JLabel(this.getString("LBL_EXCEPT")));
      b0.setBorder(new EmptyBorder(0, 0, 10, 0));
      b0.add(Box.createHorizontalGlue());
      box.add(b0);
      this.txtException = new JTextArea();
      this.txtException.setLineWrap(false);
      this.txtException.setWrapStyleWord(true);
      JScrollPane jsp = new JScrollPane(this.txtException);
      jsp.setPreferredSize(new Dimension(10, 10));
      box.add(jsp);
      Box b = Box.createHorizontalBox();
      b.add(new JLabel(this.getString("LBL_EXCEPT_LN")));
      b.add(Box.createHorizontalGlue());
      b.setBorder(new EmptyBorder(5, 0, 5, 0));
      box.add(b);
      return box;
   }

   Box createSchedulerPanel() {
      Box box = Box.createVerticalBox();
      box.setOpaque(false);
      box.setBorder(new EmptyBorder(10, 0, 0, 10));
      Box b0 = Box.createHorizontalBox();
      this.schedule = new JCheckBox(this.getString("LBL_Q"));
      this.schedule.setContentAreaFilled(false);
      this.schedule.setFocusPainted(false);
      b0.add(this.schedule);
      b0.setBorder(new EmptyBorder(0, 0, 10, 0));
      b0.add(Box.createHorizontalGlue());
      box.add(b0);
      this.start = new SpinnerDateModel();
      this.end = new SpinnerDateModel();
      this.startDate = new JSpinner(this.start);
      this.startDate.setEditor(new DateEditor(this.startDate, "dd-MMM-yy hh:mm a"));
      this.startDate.setMaximumSize(this.startDate.getPreferredSize());
      this.endDate = new JSpinner(this.end);
      this.endDate.setEditor(new DateEditor(this.endDate, "dd-MMM-yy hh:mm a"));
      this.endDate.setMaximumSize(this.endDate.getPreferredSize());
      Box b1 = Box.createHorizontalBox();
      b1.add(new JLabel(this.getString("LBL_START_Q")));
      b1.add(Box.createHorizontalGlue());
      b1.add(this.startDate);
      box.add(b1);
      box.add(Box.createRigidArea(new Dimension(10, 10)));
      Box b2 = Box.createHorizontalBox();
      b2.add(new JLabel(this.getString("LBL_STOP_Q")));
      b2.add(Box.createHorizontalGlue());
      b2.add(this.endDate);
      box.add(b2);
      return box;
   }

   Box createTypesPanel() {
      Box box = Box.createVerticalBox();
      box.setOpaque(false);
      box.setBorder(new EmptyBorder(10, 0, 0, 10));
      Box b0 = Box.createHorizontalBox();
      b0.add(new JLabel(this.getString("LBL_FILE_TYPES")));
      b0.setBorder(new EmptyBorder(0, 0, 10, 0));
      b0.add(Box.createHorizontalGlue());
      box.add(b0);
      this.txtArea = new JTextArea();
      this.txtArea.setWrapStyleWord(true);
      this.txtArea.setLineWrap(true);
      JScrollPane jsp = new JScrollPane(this.txtArea);
      jsp.setPreferredSize(new Dimension(10, 10));
      box.add(jsp);
      Box b = Box.createHorizontalBox();
      b.add(Box.createHorizontalGlue());
      this.defaults = new JButton(this.getString("LBL_DEFAULTS"));
      this.defaults.addActionListener(this);
      this.defaults.setName("LBL_DEFAULTS");
      b.add(this.defaults);
      b.setBorder(new EmptyBorder(5, 0, 5, 0));
      box.add(b);
      return box;
   }

   Box createProxyPanel() {
      Box box = Box.createVerticalBox();
      box.setOpaque(false);
      box.setBorder(new EmptyBorder(10, 0, 0, 10));
      JPanel p = new JPanel(new GridLayout(3, 1, 5, 5));
      p.setOpaque(false);
      JButton http = new JButton(this.getString("HTTP_PROXY"));
      http.setName("HTTP_PROXY");
      http.addActionListener(this);
      p.add(http);
      JButton https = new JButton(this.getString("HTTPS_PROXY"));
      https.setName("HTTPS_PROXY");
      https.addActionListener(this);
      p.add(https);
      JButton ftp = new JButton(this.getString("FTP_PROXY"));
      ftp.setName("FTP_PROXY");
      ftp.addActionListener(this);
      p.add(ftp);
      box.add(p);
      box.add(Box.createVerticalGlue());
      return box;
   }

   Box createAdvancedPanel() {
      Box box = Box.createVerticalBox();
      box.setOpaque(false);
      box.setBorder(new EmptyBorder(10, 0, 0, 10));
      Box b0 = Box.createHorizontalBox();
      b0.add(new JLabel(this.getString("DWN_CMD")));
      b0.setBorder(new EmptyBorder(0, 0, 10, 0));
      b0.add(Box.createHorizontalGlue());
      box.add(b0);
      Box b = Box.createHorizontalBox();
      this.chkCustCmd = new JCheckBox(this.getString("CST_CMD"));
      this.chkCustCmd.setFocusPainted(false);
      this.chkCustCmd.setContentAreaFilled(false);
      b.add(this.chkCustCmd);
      b.add(Box.createHorizontalGlue());
      box.add(b);
      Box box1 = Box.createHorizontalBox();
      box1.setBorder(new EmptyBorder(5, 0, 5, 0));
      this.txtCustCmd = new JTextField(15);
      this.txtCustCmd.setMaximumSize(new Dimension(this.txtCustCmd.getMaximumSize().width, this.txtCustCmd.getPreferredSize().height));
      box1.add(this.txtCustCmd);
      box.add(box1);
      Box b2 = Box.createHorizontalBox();
      this.chkHltCmd = new JCheckBox(this.getString("CMD_HLT"));
      this.chkHltCmd.setFocusPainted(false);
      this.chkHltCmd.setContentAreaFilled(false);
      b2.add(this.chkHltCmd);
      b2.add(Box.createHorizontalGlue());
      box.add(b2);
      Box box2 = Box.createHorizontalBox();
      box2.setBorder(new EmptyBorder(5, 0, 5, 0));
      this.txtHltCmd = new JTextField(15);
      this.txtHltCmd.setMaximumSize(new Dimension(this.txtHltCmd.getMaximumSize().width, this.txtHltCmd.getPreferredSize().height));
      box2.add(this.txtHltCmd);
      box.add(box2);
      Box b3 = Box.createHorizontalBox();
      this.chkHngCmd = new JCheckBox(this.getString("CMD_HNG"));
      this.chkHngCmd.setFocusPainted(false);
      this.chkHngCmd.setContentAreaFilled(false);
      b3.add(this.chkHngCmd);
      b3.add(Box.createHorizontalGlue());
      box.add(b3);
      Box box3 = Box.createHorizontalBox();
      box3.setBorder(new EmptyBorder(5, 0, 5, 0));
      this.txtMdmCmd = new JTextField(15);
      this.txtMdmCmd.setMaximumSize(new Dimension(this.txtMdmCmd.getMaximumSize().width, this.txtMdmCmd.getPreferredSize().height));
      box3.add(this.txtMdmCmd);
      box.add(box3);
      Box b4 = Box.createHorizontalBox();
      this.chkAvCmd = new JCheckBox(this.getString("CMD_SCN"));
      this.chkAvCmd.setFocusPainted(false);
      this.chkAvCmd.setContentAreaFilled(false);
      b4.add(this.chkAvCmd);
      b4.add(Box.createHorizontalGlue());
      box.add(b4);
      Box box4 = Box.createHorizontalBox();
      box4.setBorder(new EmptyBorder(5, 0, 5, 0));
      this.txtScnCmd = new JTextField(15);
      this.txtScnCmd.setMaximumSize(new Dimension(this.txtScnCmd.getMaximumSize().width, this.txtScnCmd.getPreferredSize().height));
      box4.add(this.txtScnCmd);
      box.add(box4);
      box.add(Box.createVerticalGlue());
      return box;
   }

   Box createSaveToPanel() {
      Box box = Box.createVerticalBox();
      box.setOpaque(false);
      box.setBorder(new EmptyBorder(10, 0, 0, 10));
      Box b = Box.createHorizontalBox();
      b.add(new JLabel(this.getString("TMP_DIR")));
      b.add(Box.createHorizontalGlue());
      box.add(b);
      Box box1 = Box.createHorizontalBox();
      box1.setBorder(new EmptyBorder(5, 0, 5, 0));
      this.txtTmpDir = new JTextField(15);
      this.txtTmpDir.setMaximumSize(new Dimension(this.txtTmpDir.getMaximumSize().width, this.txtTmpDir.getPreferredSize().height));
      this.txtTmpDir.setEditable(false);
      this.txtTmpDir.setBackground(Color.white);
      box1.add(this.txtTmpDir);
      box1.add(Box.createRigidArea(new Dimension(10, 10)));
      this.br1 = new JButton("...");
      this.br1.addActionListener(this);
      this.br1.setName("BR_TMP_DIR");
      box1.add(this.br1);
      box.add(box1);
      Box b2 = Box.createHorizontalBox();
      b2.add(new JLabel(this.getString("DST_DIR")));
      b2.add(Box.createHorizontalGlue());
      box.add(b2);
      Box box2 = Box.createHorizontalBox();
      box2.setBorder(new EmptyBorder(5, 0, 5, 0));
      this.txtDstDir = new JTextField(15);
      this.txtDstDir.setMaximumSize(new Dimension(this.txtDstDir.getMaximumSize().width, this.txtDstDir.getPreferredSize().height));
      this.txtDstDir.setEditable(false);
      box2.add(this.txtDstDir);
      box2.add(Box.createRigidArea(new Dimension(10, 10)));
      this.br2 = new JButton("...");
      this.br2.addActionListener(this);
      this.br2.setName("BR_DST_DIR");
      box2.add(this.br2);
      this.txtDstDir.setBackground(Color.white);
      box.add(box2);
      box.add(Box.createVerticalGlue());
      return box;
   }

   Box createConnectionPanel() {
      Box box = Box.createVerticalBox();
      box.setOpaque(false);
      box.setBorder(new EmptyBorder(10, 0, 0, 10));
      Box box2 = Box.createHorizontalBox();
      box2.add(new JLabel(this.getString("DWN_TYM")));
      box2.add(Box.createHorizontalGlue());
      this.cmbTimeout = new JComboBox(new String[]{"10", "15", "20", "30", "45", "60", "120", "180", "240", "300"});
      this.cmbTimeout.setMaximumSize(this.cmbTimeout.getPreferredSize());
      box2.add(this.cmbTimeout);
      box.add(box2);
      box2.setBorder(new EmptyBorder(2, 0, 2, 0));
      Box box1 = Box.createHorizontalBox();
      box1.add(new JLabel(this.getString("DWN_SEG")));
      box1.add(Box.createHorizontalGlue());
      this.cmbConn = new JComboBox(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "20", "24", "26", "28", "30", "32"});
      this.cmbConn.setMaximumSize(this.cmbTimeout.getPreferredSize());
      this.cmbConn.setPreferredSize(this.cmbTimeout.getPreferredSize());
      box1.add(this.cmbConn);
      box.add(box1);
      box1.setBorder(new EmptyBorder(2, 0, 2, 0));
      Box box3 = Box.createHorizontalBox();
      box3.add(new JLabel(this.getString("DWN_TCP")));
      box3.add(Box.createHorizontalGlue());
      this.cmbTcpW = new JComboBox(new String[]{"8", "16", "32", "64"});
      this.cmbTcpW.setMaximumSize(this.cmbTimeout.getPreferredSize());
      this.cmbTcpW.setPreferredSize(this.cmbTimeout.getPreferredSize());
      box3.add(this.cmbTcpW);
      box.add(box3);
      box3.setBorder(new EmptyBorder(2, 0, 2, 0));
      box.add(Box.createVerticalGlue());
      return box;
   }

   Box createGeneralPanel() {
      this.chkDwnldPrg = new JCheckBox(this.getString("SHOW_DWNLD_PRG"));
      this.chkDwnldPrg.setContentAreaFilled(false);
      this.chkDwnldPrg.setFocusPainted(false);
      this.chkFinishedDlg = new JCheckBox(this.getString("SHOW_DWNLD_DLG"));
      this.chkFinishedDlg.setContentAreaFilled(false);
      this.chkFinishedDlg.setFocusPainted(false);
      this.chkAllowBrowser = new JCheckBox(this.getString("ALLOW_BROWSER"));
      this.chkAllowBrowser.setContentAreaFilled(false);
      this.chkAllowBrowser.setFocusPainted(false);
      this.cmbDupAction = new JComboBox(new String[]{StringResource.getString("DUP__OP1"), StringResource.getString("DUP__OP2"), StringResource.getString("DUP__OP3"), StringResource.getString("DUP__OP4")});
      this.cmbDupAction.setBorder((Border)null);
      this.cmbDupAction.setMaximumSize(new Dimension(this.chkDwnldPrg.getPreferredSize().width, this.cmbDupAction.getPreferredSize().height));
      Box box = Box.createVerticalBox();
      box.setOpaque(false);
      box.setBorder(new EmptyBorder(10, 0, 0, 10));
      Box b0 = Box.createHorizontalBox();
      b0.add(this.chkDwnldPrg);
      b0.add(Box.createHorizontalGlue());
      box.add(b0);
      Box b1 = Box.createHorizontalBox();
      b1.add(this.chkFinishedDlg);
      b1.add(Box.createHorizontalGlue());
      box.add(b1);
      Box b2 = Box.createHorizontalBox();
      b2.add(this.chkAllowBrowser);
      b2.add(Box.createHorizontalGlue());
      box.add(b2);
      Box b4 = Box.createHorizontalBox();
      b4.add(new JLabel(this.getString("SHOW_DUP_ACTION")));
      b4.add(Box.createHorizontalGlue());
      b4.add(this.cmbDupAction);
      box.add(Box.createVerticalStrut(10));
      box.add(b4);
      return box;
   }

   void createRightPanel() {
      this.rightPanel = new JPanel(new BorderLayout());
      this.rightPanel.setBackground(Color.white);
      TitlePanel tp = new TitlePanel(new BorderLayout(), this);
      tp.setBackground(Color.WHITE);
      this.title = new JLabel();
      this.title.setBorder(new EmptyBorder(20, 20, 20, 20));
      this.title.setFont(this.title.getFont().deriveFont(1, (float)this.title.getFont().getSize() * 1.2F));
      tp.add(this.title, "Center");
      this.rightPanel.add(tp, "North");
      this.pane = new JPanel(new BorderLayout());
      this.pane.setBackground(Color.white);
      this.jsp = new JScrollPane(this.pane);
      this.jsp.setBackground(Color.white);
      this.jsp.setBorder(new EmptyBorder(10, 10, 10, 0));
      this.rightPanel.add(this.jsp);
      this.save = new JButton(this.getString("SAVE"));
      this.save.setName("SAVE");
      this.save.addActionListener(this);
      this.cancel = new JButton(this.getString("CANCEL"));
      this.cancel.setName("CANCEL");
      this.cancel.addActionListener(this);
      this.save.setPreferredSize(this.cancel.getPreferredSize());
      Box downBox = Box.createHorizontalBox();
      downBox.add(Box.createHorizontalGlue());
      downBox.add(this.save);
      downBox.add(Box.createRigidArea(new Dimension(5, 5)));
      downBox.add(this.cancel);
      downBox.setBorder(new EmptyBorder(10, 10, 10, 10));
      this.rightPanel.add(downBox, "South");
      this.centerPanel.add(this.rightPanel);
   }

   void createLeftPanel() {
      Box leftBox = Box.createVerticalBox();
      leftBox.setOpaque(true);
      leftBox.setBackground(StaticResource.titleColor);
      JLabel title = new JLabel(this.getString("CONFIG_TITLE"));
      title.setForeground(Color.white);
      title.setFont(title.getFont().deriveFont(1, (float)title.getFont().getSize() * 1.2F));
      title.setBorder(new EmptyBorder(20, 20, 20, 40));
      leftBox.add(title);
      this.centerPanel.add(leftBox, "West");
      this.arrLbl = new JLabel[9];

      for(int i = 0; i < 9; ++i) {
         String id = "CONFIG_LBL" + (i + 1);
         this.arrLbl[i] = new JLabel(this.getString(id));
         this.arrLbl[i].setName(id);
         this.arrLbl[i].addMouseListener(this.clickHandler);
         this.arrLbl[i].setForeground(Color.white);
         this.arrLbl[i].setFont(this.plainFont);
         this.arrLbl[i].setBorder(new EmptyBorder(5, 20, 5, 20));
         leftBox.add(this.arrLbl[i]);
      }

   }

   private String getString(String id) {
      return StringResource.getString(id);
   }

   class ConfigMouseAdapter extends MouseAdapter {
      public void mouseClicked(MouseEvent e) {
         if(e.getSource() instanceof JLabel) {
            JLabel src = (JLabel)e.getSource();
            String id = src.getName();
            ConfigDialog.this.showPanel(ConfigDialog.this.getString(id));

            for(int i = 0; i < ConfigDialog.this.arrLbl.length; ++i) {
               if(ConfigDialog.this.arrLbl[i] == src) {
                  src.setFont(ConfigDialog.this.boldFont);
               } else {
                  ConfigDialog.this.arrLbl[i].setFont(ConfigDialog.this.plainFont);
               }
            }
         }

      }
   }
}
