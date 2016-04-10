package org.sdg.xdman.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.JTableHeader;
import org.sdg.xdman.core.common.Authenticator;
import org.sdg.xdman.core.common.ConnectionManager;
import org.sdg.xdman.core.common.DownloadInfo;
import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.gui.AboutDialog;
import org.sdg.xdman.gui.AdvYTDlg;
import org.sdg.xdman.gui.AssembleDialog;
import org.sdg.xdman.gui.BarPanel;
import org.sdg.xdman.gui.BatchDlg;
import org.sdg.xdman.gui.BatchDownloadDlg;
import org.sdg.xdman.gui.BatchDownloadListener;
import org.sdg.xdman.gui.BatchItem;
import org.sdg.xdman.gui.BrowserIntDlg;
import org.sdg.xdman.gui.ConfigDialog;
import org.sdg.xdman.gui.DownloadCompleteDialog;
import org.sdg.xdman.gui.DownloadList;
import org.sdg.xdman.gui.DownloadListItem;
import org.sdg.xdman.gui.DropBox;
import org.sdg.xdman.gui.FileTransferHandler;
import org.sdg.xdman.gui.HelpDialog;
import org.sdg.xdman.gui.IconUtil;
import org.sdg.xdman.gui.MainTableModel;
import org.sdg.xdman.gui.MediaInfo;
import org.sdg.xdman.gui.MediaTableModel;
import org.sdg.xdman.gui.NewDownloadWindow;
import org.sdg.xdman.gui.OSXInstallWindow;
import org.sdg.xdman.gui.PropertiesDialog;
import org.sdg.xdman.gui.RefreshLinkDlg;
import org.sdg.xdman.gui.SidePanel;
import org.sdg.xdman.gui.StaticResource;
import org.sdg.xdman.gui.StringResource;
import org.sdg.xdman.gui.XDMButton;
import org.sdg.xdman.gui.XDMButtonUI;
import org.sdg.xdman.gui.XDMDownloadWindow;
import org.sdg.xdman.gui.XDMFileChooser;
import org.sdg.xdman.gui.XDMFrame;
import org.sdg.xdman.gui.XDMIconMap;
import org.sdg.xdman.gui.XDMListItemRenderer;
import org.sdg.xdman.gui.XDMLookAndFeel;
import org.sdg.xdman.gui.XDMThrottleDlg;
import org.sdg.xdman.gui.XDMToolBarButtonUI;
import org.sdg.xdman.gui.XDMTreeNode;
import org.sdg.xdman.gui.YoutubeGrabberDlg;
import org.sdg.xdman.interceptor.DownloadIntercepterInfo;
import org.sdg.xdman.interceptor.IMediaGrabber;
import org.sdg.xdman.interceptor.XDMServer;
import org.sdg.xdman.util.LinuxUtil;
import org.sdg.xdman.util.Logger;
import org.sdg.xdman.util.OSXUtil;
import org.sdg.xdman.util.XDMUtil;

public class XDMMainWindow extends XDMFrame implements TreeSelectionListener, ActionListener, DownloadStateListner, BatchDownloadListener, IMediaGrabber {
   private static final long serialVersionUID = -5562142092210683635L;
   JComboBox cmb;
   String state;
   String type;
   int windowState;
   JPanel toolbar;
   JButton addurl;
   JButton resume;
   JButton pause;
   JButton delete;
   JButton option;
   JButton youtube;
   JButton media;
   JButton exit;
   JLabel closetree;
   JSplitPane split;
   JTree tree;
   JTable table;
   MainTableModel model = new MainTableModel();
   static DownloadList list = null;
   static String tempdir = null;
   static String destdir = null;
   static String appdir = null;
   static XDMConfig config;
   Toolkit t;
   JPopupMenu pop;
   JSplitPane content;
   XDMToolBarButtonUI toolBtnUI;
   JMenuBar bar;
   XDMButtonUI btnUI;
   ConfigDialog configDlg;
   BatchDlg batchDlg;
   BatchDownloadDlg listDlg;
   MediaTableModel mediaModel = new MediaTableModel();
   YoutubeGrabberDlg ytDlg;
   JPopupMenu ctxPopup;
   BrowserIntDlg biDlg;
   JLabel lbl;
   AboutDialog abtDlg;
   boolean processQueue;
   DownloadListItem qi;
   boolean schedulerActive;
   AssembleDialog asmDlg;
   Clipboard clipboard;
   DropBox w;
   JLabel[] lblCatArr;
   SidePanel sp;
   JButton btnSort;
   JTextField txtSearch;
   JMenuItem[] sortItems;
   JButton[] btnTabArr;
   Thread st;
   int hotcount = 0;
   boolean hot = false;
   static HashMap arg = new HashMap();

   public XDMMainWindow() {
      this.setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
      this.setTitle(this.getString("TITLE"));
      this.t = Toolkit.getDefaultToolkit();
      if(config.mwW <= 0) {
         Dimension lblTitle = this.t.getScreenSize();
         int menuBox = 700;
         int pClient = 400;
         if(lblTitle.width < menuBox) {
            menuBox = lblTitle.width;
         }

         if(lblTitle.height < pClient) {
            pClient = lblTitle.height;
         }

         this.setSize(menuBox, pClient);
         this.setLocationRelativeTo((Component)null);
      } else {
         this.setSize(config.mwW, config.mwH);
         this.setLocation(config.mwX, config.mwY);
      }

      JLabel var19 = new JLabel("XDM 2016");
      var19.setBorder(new EmptyBorder(20, 20, 0, 0));
      var19.setFont(new Font("Dialog", 0, 24));
      var19.setForeground(StaticResource.whiteColor);
      this.getTitlePanel().add(var19, "West");
      this.bar = new JMenuBar();
      this.bar.setBorderPainted(false);
      this.bar.setForeground(StaticResource.whiteColor);
      this.bar.setMaximumSize(new Dimension(this.bar.getMaximumSize().width, 30));
      this.bar.setBackground(StaticResource.titleColor);
      this.createMenu(this.bar);
      Box var20 = Box.createHorizontalBox();
      var20.add(Box.createHorizontalGlue());
      var20.add(this.bar);
      var20.add(Box.createHorizontalStrut(30));
      this.getTitlePanel().add(var20);
      this.createTabs();
      JPanel var21 = new JPanel(new BorderLayout());
      JPanel panCenter = new JPanel(new BorderLayout());
      panCenter.setBackground(Color.WHITE);
      BarPanel bp = new BarPanel();
      bp.setLayout(new BorderLayout());
      bp.add(Box.createRigidArea(new Dimension(0, 30)));
      bp.add(this.createSearchPane(), "East");
      panCenter.add(bp, "North");
      var21.add(panCenter);
      this.sp = new SidePanel();
      this.sp.setLayout((LayoutManager)null);
      this.sp.setPreferredSize(new Dimension(150, 250));
      this.lblCatArr = new JLabel[6];
      JLabel lblAllCat = new JLabel(this.getString("TREE_ALL"));
      lblAllCat.setName("TREE_ALL");
      lblAllCat.setFont(StaticResource.plainFont);
      lblAllCat.setBorder(new EmptyBorder(5, 20, 5, 5));
      this.lblCatArr[0] = lblAllCat;
      JLabel lblDocCat = new JLabel(this.getString("TREE_DOCUMENTS"));
      lblDocCat.setName("TREE_DOCUMENTS");
      lblDocCat.setFont(StaticResource.plainFont);
      lblDocCat.setBorder(new EmptyBorder(5, 20, 5, 5));
      this.lblCatArr[1] = lblDocCat;
      JLabel lblArcCat = new JLabel(this.getString("TREE_COMPRESSED"));
      lblArcCat.setName("TREE_COMPRESSED");
      lblArcCat.setFont(StaticResource.plainFont);
      lblArcCat.setBorder(new EmptyBorder(5, 20, 5, 5));
      this.lblCatArr[2] = lblArcCat;
      JLabel lblMusCat = new JLabel(this.getString("TREE_MUSIC"));
      lblMusCat.setName("TREE_MUSIC");
      lblMusCat.setFont(StaticResource.plainFont);
      lblMusCat.setBorder(new EmptyBorder(5, 20, 5, 5));
      this.lblCatArr[3] = lblMusCat;
      JLabel lblVidCat = new JLabel(this.getString("TREE_VIDEOS"));
      lblVidCat.setName("TREE_VIDEOS");
      lblVidCat.setFont(StaticResource.plainFont);
      lblVidCat.setBorder(new EmptyBorder(5, 20, 5, 5));
      this.lblCatArr[4] = lblVidCat;
      JLabel lblAppCat = new JLabel(this.getString("TREE_PROGRAMS"));
      lblAppCat.setName("TREE_PROGRAMS");
      lblAppCat.setFont(StaticResource.plainFont);
      lblAppCat.setBorder(new EmptyBorder(5, 20, 5, 5));
      this.lblCatArr[5] = lblAppCat;
      this.lblCatArr[0].setBackground(new Color(242, 242, 242));
      this.lblCatArr[0].setOpaque(true);

      for(int bb = 0; bb < 6; ++bb) {
         final int cc = bb;
         this.lblCatArr[cc].setBounds(0, 20 + cc * 35, 149, 27);
         this.lblCatArr[cc].addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
               XDMMainWindow.this.actionPerformed(new ActionEvent(XDMMainWindow.this.lblCatArr[cc], 0, ""));
            }
         });
         this.sp.add(this.lblCatArr[cc]);
      }

      var21.add(this.sp, "West");
      this.add(var21);
      Box var22 = Box.createHorizontalBox();
      var22.add(Box.createRigidArea(new Dimension(25, 60)));
      var22.setBackground(StaticResource.titleColor);
      var22.setOpaque(true);
      XDMButton btnAdd = new XDMButton(StaticResource.getIcon("tool_add.png"));
      btnAdd.putClientProperty("xdmbutton.grayrollover", "true");
      btnAdd.setBorderPainted(false);
      btnAdd.addActionListener(this);
      btnAdd.setName("ADD_URL");
      btnAdd.setBackground(StaticResource.titleColor);
      btnAdd.setMargin(new Insets(0, 0, 0, 0));
      var22.add(btnAdd);
      var22.add(Box.createRigidArea(new Dimension(10, 10)));
      XDMButton btnDel = new XDMButton(StaticResource.getIcon("tool_del.png"));
      btnDel.putClientProperty("xdmbutton.grayrollover", "true");
      btnDel.setBorderPainted(false);
      btnDel.setBackground(StaticResource.titleColor);
      btnDel.setMargin(new Insets(0, 0, 0, 0));
      btnDel.addActionListener(this);
      btnDel.setName("DELETE");
      var22.add(btnDel);
      var22.add(Box.createRigidArea(new Dimension(10, 10)));
      XDMButton btnPause = new XDMButton(StaticResource.getIcon("tool_pause.png"));
      btnPause.putClientProperty("xdmbutton.grayrollover", "true");
      btnPause.setBorderPainted(false);
      btnPause.setBackground(StaticResource.titleColor);
      btnPause.setMargin(new Insets(0, 0, 0, 0));
      btnPause.addActionListener(this);
      btnPause.setName("PAUSE");
      var22.add(btnPause);
      var22.add(Box.createRigidArea(new Dimension(10, 10)));
      XDMButton btnResume = new XDMButton(StaticResource.getIcon("tool_resume1.png"));
      btnResume.putClientProperty("xdmbutton.grayrollover", "true");
      btnResume.setBorderPainted(false);
      btnResume.setBackground(StaticResource.titleColor);
      btnResume.setMargin(new Insets(0, 0, 0, 0));
      btnResume.addActionListener(this);
      btnResume.setName("RESUME");
      var22.add(btnResume);
      var22.add(Box.createRigidArea(new Dimension(10, 10)));
      XDMButton btnSettings = new XDMButton(StaticResource.getIcon("tool_settings2.png"));
      btnSettings.putClientProperty("xdmbutton.grayrollover", "true");
      btnSettings.setBorderPainted(false);
      btnSettings.setBackground(StaticResource.titleColor);
      btnSettings.setMargin(new Insets(0, 0, 0, 0));
      btnSettings.addActionListener(this);
      btnSettings.setName("OPTIONS");
      var22.add(btnSettings);
      var22.add(Box.createRigidArea(new Dimension(10, 10)));
      var21.add(var22, "South");
      list = new DownloadList(appdir);
      this.model = new MainTableModel();
      this.model.setList(list);
      this.table = new JTable(this.model);
      this.table.setTableHeader((JTableHeader)null);
      this.table.setDefaultRenderer(DownloadListItem.class, new XDMListItemRenderer());
      this.table.setRowHeight(70);
      this.table.setShowGrid(false);
      this.table.setFillsViewportHeight(true);
      this.table.setBorder(new EmptyBorder(0, 0, 0, 0));
      this.table.setTransferHandler(new FileTransferHandler(list, this));
      this.table.setDragEnabled(true);
      JScrollPane jsp = new JScrollPane(this.table);
      jsp.setBorder(new EmptyBorder(0, 0, 0, 0));
      panCenter.add(jsp);
      if(config.destdir == null) {
         config.destdir = destdir;
      }

      if(config.tempdir == null) {
         config.tempdir = tempdir;
      }

      this.createTray();
      this.table.addMouseListener(new MouseAdapter() {
         public void mouseReleased(MouseEvent me) {
            if(me.getButton() == 3) {
               if(XDMMainWindow.this.ctxPopup == null) {
                  XDMMainWindow.this.createContextMenu();
               }

               XDMMainWindow.this.ctxPopup.show(XDMMainWindow.this.table, me.getX(), me.getY());
            }

         }
      });
   }

   Component createSearchPane() {
      this.btnSort = new XDMButton("Newest on top");
      this.btnSort.setBorderPainted(false);
      this.btnSort.setFocusPainted(false);
      this.btnSort.setContentAreaFilled(false);
      this.txtSearch = new JTextField();
      this.txtSearch.setBorder((Border)null);
      final JButton btnSearch = new JButton();
      btnSearch.setName("BTN_SEARCH");
      btnSearch.addActionListener(this);
      btnSearch.setPreferredSize(new Dimension(20, 20));
      btnSearch.setBackground(Color.WHITE);
      btnSearch.setIcon(StaticResource.getIcon("search16.png"));
      btnSearch.setBorderPainted(false);
      btnSearch.setContentAreaFilled(false);
      this.txtSearch.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent e) {
            if(e.getKeyCode() == 10) {
               btnSearch.doClick();
            }

         }
      });
      Box b = Box.createHorizontalBox();
      b.setOpaque(true);
      b.setBackground(Color.WHITE);
      b.setPreferredSize(new Dimension(130, 20));
      b.setMaximumSize(new Dimension(130, 20));
      this.txtSearch.setPreferredSize(new Dimension(70, 20));
      this.txtSearch.setMaximumSize(new Dimension(this.txtSearch.getMaximumSize().width, 20));
      b.add(this.txtSearch);
      b.add(btnSearch);
      b.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
      Box bp = Box.createHorizontalBox();
      bp.setOpaque(false);
      bp.setBorder(new EmptyBorder(3, 3, 3, 18));
      bp.add(Box.createHorizontalStrut(10));
      bp.add(this.btnSort);
      bp.add(Box.createHorizontalStrut(20));
      bp.add(b);
      this.sortItems = new JMenuItem[]{new JMenuItem("Date"), new JMenuItem("Size"), new JMenuItem("Name"), new JMenuItem("Type"), new JMenuItem("Ascending"), new JMenuItem("Descending")};
      final JPopupMenu popSort = new JPopupMenu();

      for(int i = 0; i < this.sortItems.length; ++i) {
         popSort.add(this.sortItems[i]);
         if(i >= 0 && i <= 3) {
            this.sortItems[i].setName("COL:" + i);
            if(i == XDMConfig.sortField) {
               this.sortItems[i].setFont(StaticResource.boldFont);
            } else {
               this.sortItems[i].setFont(StaticResource.plainFont);
            }
         }

         if(i == 3) {
            popSort.addSeparator();
         }

         if(i == 4) {
            this.sortItems[4].setName("CTX_ASC");
            this.sortItems[4].setFont(XDMConfig.sortAsc?StaticResource.boldFont:StaticResource.plainFont);
         }

         if(i == 5) {
            this.sortItems[5].setName("CTX_DESC");
            this.sortItems[5].setFont(!XDMConfig.sortAsc?StaticResource.boldFont:StaticResource.plainFont);
         }

         this.sortItems[i].addActionListener(this);
      }

      popSort.setInvoker(this.btnSort);
      this.btnSort.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            popSort.show(XDMMainWindow.this.btnSort, 0, XDMMainWindow.this.btnSort.getHeight());
         }
      });
      return bp;
   }

   private void createTabs() {
      XDMButton btnAllTab = new XDMButton(this.getString("ALL_DOWNLOADS"));
      XDMButton btnIncompleteTab = new XDMButton(this.getString("ALL_UNFINISHED"));
      XDMButton btnCompletedTab = new XDMButton(this.getString("ALL_FINISHED"));
      this.btnTabArr = new JButton[3];
      this.btnTabArr[0] = btnAllTab;
      this.btnTabArr[0].setName("ALL_DOWNLOADS");
      this.btnTabArr[1] = btnIncompleteTab;
      this.btnTabArr[1].setName("ALL_UNFINISHED");
      this.btnTabArr[2] = btnCompletedTab;
      this.btnTabArr[2].setName("ALL_FINISHED");

      for(int pp = 0; pp < 3; ++pp) {
         this.btnTabArr[pp].setFont(StaticResource.plainFontBig);
         this.btnTabArr[pp].setBorderPainted(false);
         this.btnTabArr[pp].addActionListener(this);
      }

      btnAllTab.setBackground(new Color(242, 242, 242));
      btnIncompleteTab.setBackground(StaticResource.titleColor);
      btnIncompleteTab.setForeground(StaticResource.whiteColor);
      btnCompletedTab.setBackground(StaticResource.titleColor);
      btnCompletedTab.setForeground(StaticResource.whiteColor);
      JPanel var7 = new JPanel(new BorderLayout());
      var7.setOpaque(false);
      JPanel p = new JPanel(new GridLayout(1, 3, 5, 0));
      p.setOpaque(false);
      Dimension d = new Dimension(350, 30);
      p.setPreferredSize(d);
      p.setMaximumSize(d);
      p.setMinimumSize(d);
      p.setBackground(Color.WHITE);
      p.add(btnAllTab);
      p.add(btnIncompleteTab);
      p.add(btnCompletedTab);
      var7.add(p, "East");
      this.getTitlePanel().add(var7, "South");
   }

   int getDupAction(String url) {
      JTextField txt = new JTextField(url, 30);
      String lbl = StringResource.getString("DUP_TXT");
      JComboBox choice = new JComboBox(new String[]{StringResource.getString("DUP_OP1"), StringResource.getString("DUP_OP2"), StringResource.getString("DUP_OP3")});
      JCheckBox chk = new JCheckBox(StringResource.getString("DUP_CHK"));
      int ret = JOptionPane.showOptionDialog((Component)null, new Object[]{txt, lbl, choice, chk}, StringResource.getString("DUP_TITLE"), 2, 3, (Icon)null, (Object[])null, (Object)null);
      if(ret == 0) {
         int index = choice.getSelectedIndex();
         if(chk.isSelected()) {
            config.duplicateLinkAction = index;
         }

         return index;
      } else {
         return -1;
      }
   }

   void showMessageBox(String msg, String title, int msgType) {
      JOptionPane.showMessageDialog(this, msg, title, msgType);
   }

   void removeDownloads() {
      int[] indexes = this.table.getSelectedRows();
      if(indexes.length < 1) {
         this.showMessageBox(this.getString("NONE_SELECTED"), this.getString("DEFAULT_TITLE"), 0);
      } else if(JOptionPane.showConfirmDialog(this, "Are you sure you want to delete selected download" + (indexes.length > 1?"s":"") + "?", "Confirm delete", 0) == 0) {
         DownloadListItem[] items = new DownloadListItem[indexes.length];

         int i;
         DownloadListItem item;
         for(i = 0; i < indexes.length; ++i) {
            item = list.get(indexes[i]);
            if(item.mgr != null) {
               this.showMessageBox(this.getString("DWN_ACTIVE"), this.getString("DEFAULT_TITLE"), 0);
               return;
            }

            items[i] = item;
         }

         for(i = 0; i < indexes.length; ++i) {
            item = items[i];
            String tmpdir = item.tempdir;
            list.remove(item);
            if(!XDMUtil.isNullOrEmpty(tmpdir)) {
               this.delDirRec(tmpdir);
            }
         }

         this.model.fireTableDataChanged();
         list.downloadStateChanged();
      }
   }

   void delDirRec(String dir) {
      File fdir = new File(dir);
      File[] files = fdir.listFiles();
      if(files == null) {
         fdir.delete();
      } else {
         for(int i = 0; i < files.length; ++i) {
            files[i].delete();
         }

         fdir.delete();
      }

   }

   private void resumeDownload() {
      int index = this.table.getSelectedRow();
      if(index < 0) {
         this.showMessageBox(this.getString("NONE_SELECTED"), this.getString("DEFAULT_TITLE"), 0);
      } else {
         DownloadListItem item = list.get(index);
         if(item != null) {
            this.resumeDownload(item);
         }
      }
   }

   void resumeDownload(DownloadListItem item) {
      if(item.mgr != null) {
         this.showMessageBox(this.getString("DWN_ACTIVE"), this.getString("DEFAULT_TITLE"), 0);
      } else if(item.state == 50) {
         this.showMessageBox(this.getString("DWN_FINISHED"), this.getString("DEFAULT_TITLE"), 0);
      } else {
         if(item.tempdir != null && item.tempdir.length() >= 1) {
            ConnectionManager mgr = new ConnectionManager(item.id, item.url, item.filename, item.saveto, item.tempdir, item.userAgent, item.referer, item.cookies, config);
            if(!XDMUtil.isNullOrEmpty(item.user) && !XDMUtil.isNullOrEmpty(item.pass)) {
               mgr.setCredential(item.user, item.pass);
            }

            item.mgr = mgr;
            this.model.fireTableDataChanged();
            list.downloadStateChanged();
            XDMDownloadWindow dw = new XDMDownloadWindow(mgr);
            item.window = dw;
            if(config.showDownloadPrgDlg && this.qi != item) {
               dw.showWindow();
            }

            mgr.setProgressListener(dw);
            mgr.dwnListener = this;

            try {
               mgr.resume();
            } catch (Exception var5) {
               var5.printStackTrace();
            }
         } else {
            this.startDownload(item.url, item.filename, item.saveto, (String)null, (String)null, item.userAgent, item.referer, item.cookies, item, true);
         }

      }
   }

   private void restartDownload() {
      int index = this.table.getSelectedRow();
      if(index < 0) {
         this.showMessageBox(this.getString("NONE_SELECTED"), this.getString("DEFAULT_TITLE"), 0);
      } else {
         DownloadListItem item = list.get(index);
         if(item != null) {
            this.restartDownload(item);
         }
      }
   }

   void restartDownload(DownloadListItem item) {
      if(item.mgr != null) {
         this.showMessageBox(this.getString("DWN_ACTIVE"), this.getString("DEFAULT_TITLE"), 0);
      } else {
         this.startDownload(item.url, item.filename, item.saveto, (String)null, (String)null, item.userAgent, item.referer, item.cookies, item, true);
      }
   }

   private void pauseDownload() {
      int[] indexes = this.table.getSelectedRows();

      for(int i = 0; i < indexes.length; ++i) {
         DownloadListItem item = list.get(indexes[i]);
         if(item.mgr != null) {
            item.mgr.stop();
         }
      }

   }

   void addDownload() {
      NewDownloadWindow fdlg = new NewDownloadWindow(this, config);
      fdlg.setDir(config.destdir);
      fdlg.showDlg();
   }

   synchronized void startDownload(String url, String name, String folder, String user, String pass, String userAgent, String referer, ArrayList cookies, DownloadListItem item, boolean overriteExisting) {
      UUID id = UUID.randomUUID();
      ConnectionManager mgr = new ConnectionManager(id, url, name, folder, config.tempdir, userAgent, referer, cookies, config);
      mgr.overwrite = overriteExisting;
      if(item == null) {
         item = new DownloadListItem();
         list.add(item);
      }

      if(!XDMUtil.isNullOrEmpty(user) && !XDMUtil.isNullOrEmpty(pass)) {
         mgr.setCredential(user, pass);
         item.user = user;
         item.pass = pass;
      }

      item.mgr = mgr;
      item.isDASH = false;
      item.filename = name;
      item.url = url;
      item.q = false;
      item.dateadded = item.lasttry = (new SimpleDateFormat("MMM dd")).format(new Date());
      item.id = id;
      item.saveto = folder;
      item.icon = IconUtil.getIcon(XDMUtil.findCategory(item.filename));
      item.userAgent = userAgent;
      item.referer = referer;
      if(cookies != null) {
         item.cookies = new ArrayList();
         item.cookies.addAll(cookies);
      }

      item.state = 10;
      item.type = XDMUtil.findCategory(name);
      list.sort();
      this.model.fireTableDataChanged();
      list.downloadStateChanged();
      XDMDownloadWindow dw = new XDMDownloadWindow(mgr);
      item.window = dw;
      if(config.showDownloadPrgDlg && this.qi != item) {
         dw.showWindow();
      }

      mgr.setProgressListener(dw);
      mgr.dwnListener = this;

      try {
         mgr.start();
      } catch (Exception var15) {
         var15.printStackTrace();
      }

   }

   public void add2Queue(String url, String name, String folder, String user, String pass, String referer, ArrayList cookies, String userAgent, boolean q) {
      UUID id = UUID.randomUUID();
      DownloadListItem item = new DownloadListItem();
      item.isDASH = false;
      list.add(item);
      item.user = user;
      item.pass = pass;
      item.filename = name;
      item.url = url;
      item.q = q;
      item.dateadded = item.lasttry = (new SimpleDateFormat("MMM dd")).format(new Date());
      item.id = id;
      item.saveto = folder;
      item.icon = IconUtil.getIcon(XDMUtil.findCategory(item.filename));
      item.userAgent = userAgent;
      item.referer = referer;
      if(cookies != null) {
         item.cookies.addAll(cookies);
      }

      item.state = 40;
      item.status = "Stopped";
      item.type = XDMUtil.findCategory(name);
      this.model.fireTableDataChanged();
      list.downloadStateChanged();
   }

   public void configChanged() {
      if(config.schedule) {
         if(this.schedulerActive) {
            Logger.log("Scheduler is active");
            return;
         }

         this.st = new Thread(new Runnable() {
            public void run() {
               XDMMainWindow.this.schedulerRun();
            }
         });
         this.st.start();
      }

   }

   public void downloadComplete(UUID id) {
      DownloadListItem item = list.getByID(id);
      if(item != null) {
         item.window = null;
         item.status = "Download Complete " + item.size;
         item.timeleft = "";
         item.state = 50;
         this.model.fireTableDataChanged();
         list.downloadStateChanged();
         if(this.qi == item) {
            this.qi = null;
            if(this.processQueue && this.processNextQueuedDownload()) {
               return;
            }
         }

         if(config.halt) {
            this.executeCommands();
         } else if(config.showDownloadCompleteDlg) {
            DownloadCompleteDialog cdlg = new DownloadCompleteDialog(config);
            cdlg.setData(item.filename, item.saveto);
            cdlg.setLocationRelativeTo((Component)null);
            cdlg.setVisible(true);
         }

      }
   }

   public void downloadConfirmed(UUID id, Object data) {
      DownloadListItem item = list.getByID(id);
      ConnectionManager mgr = item.mgr;
      item.tempdir = mgr.getTempdir();
      item.url = mgr.getUrl();
      item.filename = mgr.getFileName();
      item.icon = IconUtil.getIcon(XDMUtil.findCategory(item.filename));
      item.updateData((DownloadInfo)data);
      this.model.fireTableDataChanged();
      list.downloadStateChanged();
   }

   public void downloadFailed(UUID id) {
      System.out.println("Download failed");
      list.downloadStateChanged();
      DownloadListItem item = list.getByID(id);
      if(item != null) {
         item.window = null;
         if(this.qi == item) {
            this.qi = null;
            if(this.processQueue) {
               this.processNextQueuedDownload();
            } else {
               System.out.println("Queue stopped");
            }
         } else {
            System.out.println("Not queued");
         }

      }
   }

   public void downloadNow(String url, String name, String folder, String user, String pass, String referer, ArrayList cookies, String userAgent) {
      boolean overwrite = false;

      for(int i = 0; i < list.list.size(); ++i) {
         DownloadListItem item = (DownloadListItem)list.list.get(i);
         if(url.equals(item.url)) {
            int action = config.duplicateLinkAction;
            if(action == 3) {
               action = this.getDupAction(url);
            }

            if(action == -1) {
               return;
            }

            if(action == 2) {
               if(item.state == 50) {
                  File file = new File(item.saveto, item.filename);
                  if(file.exists()) {
                     XDMUtil.open(file.getParentFile());
                  } else if(item.mgr == null) {
                     this.restartDownload(item);
                  } else {
                     this.showMessageBox(this.getString("DWN_ACTIVE"), "Message", 0);
                  }
               } else if(item.mgr == null) {
                  this.resumeDownload(item);
               } else {
                  this.showMessageBox(this.getString("DWN_ACTIVE"), "Message", 0);
               }

               return;
            }

            overwrite = action == 1;
            break;
         }
      }

      this.startDownload(url, name, folder, user, pass, userAgent, referer, cookies, (DownloadListItem)null, overwrite);
   }

   public void downloadPaused(UUID id) {
      synchronized(this) {
         DownloadListItem item = list.getByID(id);
         if(item != null) {
            item.window = null;
            item.mgr = null;
            item.status = "Stopped " + item.sprg + "% of " + item.size;
            item.state = 40;
            int index = list.getIndex(item);
            if(index >= 0) {
               this.model.fireTableRowsUpdated(index, index);
               list.downloadStateChanged();
               if(this.qi == item) {
                  this.qi = null;
                  if(this.processQueue) {
                     if(JOptionPane.showConfirmDialog(this, this.getString("CONTINUE_Q"), this.getString("DEFAULT_TITLE"), 0, 3) == 0) {
                        this.processNextQueuedDownload();
                     } else {
                        this.processQueue = false;
                        this.qi = null;
                     }
                  } else {
                     this.qi = null;
                  }
               }

            }
         }
      }
   }

   public void exit() {
   }

   public void getCredentials(ConnectionManager mgr, String host) {
   }

   public void interceptDownload(DownloadIntercepterInfo info) {
      this.addDownload(info.url, XDMUtil.getFileName(info.url), config.destdir, (String)null, (String)null, info.referer, info.cookies, info.ua);
   }

   public void restoreWindow() {
      this.setVisible(true);
   }

   public void startQueue() {
      if(this.processQueue) {
         this.showMessageBox(this.getString("Q_STARTED"), this.getString("DEFAULT_TITLE"), 0);
      } else if(this.qi == null) {
         this.processQueue = true;
         this.processNextQueuedDownload();
      }
   }

   public void stopQueue() {
      this.processQueue = false;
      if(this.qi != null) {
         if(this.qi.mgr != null) {
            this.qi.mgr.stop();
            this.qi = null;
         } else {
            this.qi = null;
         }

      }
   }

   public void updateManager(UUID id, Object data) {
      DownloadListItem item = list.getByID(id);
      if(item != null) {
         DownloadInfo info = (DownloadInfo)data;
         item.updateData(info);
         int index = list.getIndex(item);
         if(index >= 0) {
            this.model.fireTableRowsUpdated(index, index);
         }

      }
   }

   public void ytCallback(String yturl) {
   }

   public void valueChanged(TreeSelectionEvent e) {
      String status = null;
      byte state = 0;
      Object[] o = e.getPath().getPath();

      for(int i = 0; i < o.length; ++i) {
         XDMTreeNode node = (XDMTreeNode)o[i];
         if(node.id.equals("TREE_UNFINISHED")) {
            state = 1;
         }

         if(node.id.equals("TREE_FINISHED")) {
            state = 50;
         }

         if(node.id.equals("TREE_DOCUMENTS")) {
            status = "Documents";
         }

         if(node.id.equals("TREE_COMPRESSED")) {
            status = "Compressed";
         }

         if(node.id.equals("TREE_MUSIC")) {
            status = "Music";
         }

         if(node.id.equals("TREE_PROGRAMS")) {
            status = "Programs";
         }

         if(node.id.equals("TREE_VIDEOS")) {
            status = "Video";
         }

         list.setState(state);
         list.setType(status);
         this.model.fireTableDataChanged();
      }

   }

   JMenu createMenu(String title) {
      JMenu menu = new JMenu(title);
      menu.setForeground(StaticResource.whiteColor);
      menu.setFont(new Font("Dialog", 0, 12));
      menu.setBorderPainted(false);
      return menu;
   }

   void createMenu(JMenuBar bar) {
      JMenu file = this.createMenu(this.getString("FILE"));
      this.addMenuItem("ADD_URL", file);
      this.addMenuItem("BATCH_DOWNLOAD", file);
      this.addMenuItem("YOUTUBE_DOWNLOADER", file);
      this.addMenuItem("ADV_YT", file);
      this.addMenuItem("CLIP_ADD", file);
      this.addMenuItem("DELETE_DWN", file);
      this.addMenuItem("DELETE_COMPLETED", file);
      this.addMenuItem("MAKE_SHORTCUT", file);
      if(System.getProperty("os.name").contains("OS X")) {
         JMenuItem dwn = new JMenuItem("Install as Application");
         dwn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               OSXInstallWindow osx = new OSXInstallWindow();
               osx.setLocationRelativeTo((Component)null);
               osx.setModal(true);
               osx.setVisible(true);
            }
         });
         file.add(dwn);
      }

      this.addMenuItem("EXIT", file);
      JMenu dwn1 = this.createMenu(this.getString("DOWNLOAD"));
      this.addMenuItem("PAUSE", dwn1);
      this.addMenuItem("RESUME", dwn1);
      this.addMenuItem("RESTART", dwn1);
      this.addMenuItem("START_Q", dwn1);
      this.addMenuItem("STOP_Q", dwn1);
      JMenu tools = this.createMenu(this.getString("TOOLS"));
      this.addMenuItem("OPTIONS", tools);
      this.addMenuItem("REFRESH_LINK", tools);
      this.addMenuItem("BROWSER_INT", tools);
      this.addMenuItem("CTX_ASM", tools);
      this.addMenuItem("THROTTLE_DLG", tools);
      JMenu view = this.createMenu(this.getString("VIEW"));
      this.addMenuItem("CTX_OPEN", view);
      this.addMenuItem("CTX_OPEN_FOLDER", view);
      this.addMenuItem("CTX_SAVE_AS", view);
      this.addMenuItem("CTX_COPY_URL", view);
      this.addMenuItem("CTX_COPY_FILE", view);
      JMenu help = this.createMenu(this.getString("HELP"));
      this.addMenuItem("CONTENT", help);
      this.addMenuItem("HOME_PAGE", help);
      this.addMenuItem("UPDATE", help);
      this.addMenuItem("ABOUT", help);
      bar.add(file);
      bar.add(dwn1);
      bar.add(view);
      bar.add(tools);
      bar.add(help);
   }

   void addMenuItem(String id, JComponent menu) {
      JMenuItem mitem = new JMenuItem(this.getString(id));
      mitem.setName(id);
      mitem.addActionListener(this);
      menu.add(mitem);
   }

   String getString(String id) {
      String str = StringResource.getString(id);
      return str == null?"":str;
   }

   static ImageIcon getIcon(String name) {
      try {
         return new ImageIcon(XDMMainWindow.class.getResource("/res/" + name));
      } catch (Exception var2) {
         return new ImageIcon("res/" + name);
      }
   }

   void tabClicked(ActionEvent e) {
      for(int i = 0; i < 3; ++i) {
         if(this.btnTabArr[i] == e.getSource()) {
            this.btnTabArr[i].setBackground(new Color(242, 242, 242));
            this.btnTabArr[i].setForeground(Color.BLACK);
         } else {
            this.btnTabArr[i].setBackground(StaticResource.titleColor);
            this.btnTabArr[i].setForeground(StaticResource.whiteColor);
         }
      }

   }

   void updateSortText() {
      String text = "";
      switch(XDMConfig.sortField) {
      case 0:
         text = XDMConfig.sortAsc?"Oldest on top":"Newest on top";
         break;
      case 1:
         text = XDMConfig.sortAsc?"Smallest on top":"Largest on top";
         break;
      case 2:
         text = XDMConfig.sortAsc?"Name [A-Z]":"Name [Z-A]";
         break;
      case 3:
         text = XDMConfig.sortAsc?"Type [A-Z]":"Type [Z-A]";
      }

      this.btnSort.setText(text);
   }

   public void actionPerformed(ActionEvent e) {
      String name;
      if(e.getSource() instanceof JLabel) {
         name = null;
         JLabel var16 = (JLabel)e.getSource();
         if(var16.getName().equals("TREE_DOCUMENTS")) {
            name = "Documents";
         }

         if(var16.getName().equals("TREE_COMPRESSED")) {
            name = "Compressed";
         }

         if(var16.getName().equals("TREE_MUSIC")) {
            name = "Music";
         }

         if(var16.getName().equals("TREE_PROGRAMS")) {
            name = "Programs";
         }

         if(var16.getName().equals("TREE_VIDEOS")) {
            name = "Video";
         }

         for(int var13 = 0; var13 < this.lblCatArr.length; ++var13) {
            if(var16 == this.lblCatArr[var13]) {
               this.lblCatArr[var13].setBackground(new Color(242, 242, 242));
               this.lblCatArr[var13].setOpaque(true);
            } else {
               this.lblCatArr[var13].setOpaque(false);
            }
         }

         this.sp.repaint();
         list.setType(name);
         this.model.fireTableDataChanged();
      } else {
         if(e.getSource() instanceof AbstractButton) {
            name = ((AbstractButton)e.getSource()).getName();
            if(name == null) {
               return;
            }

            if("ADD_URL".equals(name)) {
               this.addDownload();
            } else if("ADV_YT".equals(name)) {
               AdvYTDlg var17 = new AdvYTDlg(config);
               var17.setLocationRelativeTo((Component)null);
               var17.setVisible(true);
            } else if("THROTTLE_DLG".equals(name)) {
               XDMThrottleDlg var15 = new XDMThrottleDlg(config);
               var15.setLocationRelativeTo((Component)null);
               var15.setVisible(true);
            } else {
               byte var14;
               if("ALL_DOWNLOADS".equals(name)) {
                  this.tabClicked(e);
                  var14 = 0;
                  list.setState(var14);
                  this.model.fireTableDataChanged();
               } else if("ALL_UNFINISHED".equals(name)) {
                  this.tabClicked(e);
                  var14 = 1;
                  list.setState(var14);
                  this.model.fireTableDataChanged();
               } else if("ALL_FINISHED".equals(name)) {
                  this.tabClicked(e);
                  var14 = 50;
                  list.setState(var14);
                  this.model.fireTableDataChanged();
               } else if("PAUSE".equals(name)) {
                  this.pauseDownload();
               } else if("RESUME".equals(name)) {
                  this.resumeDownload();
               } else if(!"DELETE".equals(name) && !"DELETE_DWN".equals(name)) {
                  if("RESTART".equals(name)) {
                     this.restartDownload();
                  } else if("OPTIONS".equals(name)) {
                     if(this.configDlg == null) {
                        this.configDlg = new ConfigDialog(this, config, this);
                     }

                     this.configDlg.showDialog();
                  } else if("EXIT".equals(name)) {
                     this.exitXDM();
                  } else if("BATCH_DOWNLOAD".equals(name)) {
                     if(this.batchDlg == null) {
                        this.batchDlg = new BatchDlg(this);
                     }

                     this.batchDlg.setLocationRelativeTo(this);
                     this.batchDlg.setVisible(true);
                  } else if("CLIP_ADD".equals(name)) {
                     if(this.listDlg == null) {
                        this.listDlg = new BatchDownloadDlg();
                     }

                     this.listDlg.setLocationRelativeTo(this);
                     this.listDlg.showDialog(config.destdir, this);
                  } else if(!"MEDIA_GRABBER".equals(name) && !"GRABBER".equals(name)) {
                     if(!"YOUTUBE_DOWNLOADER".equals(name) && !"YOUTUBE".equals(name)) {
                        if("DELETE_COMPLETED".equals(name)) {
                           this.deleteFinished();
                        } else if("REFRESH_LINK".equals(name)) {
                           this.refreshLink();
                        } else if("CTX_OPEN".equals(name)) {
                           this.openFile();
                        } else if("CTX_OPEN_FOLDER".equals(name)) {
                           this.openFolder();
                        } else if("CTX_SAVE_AS".equals(name)) {
                           this.renameFile();
                        } else if("BROWSER_INT".equals(name)) {
                           this.showBrowserIntegrationDlg();
                        } else if("RESTORE".equals(name)) {
                           this.restoreWindow();
                        } else if("ABOUT".equals(name)) {
                           this.abtDlg = new AboutDialog();
                           this.abtDlg.setLocationRelativeTo((Component)null);
                           this.abtDlg.setVisible(true);
                        } else {
                           int index;
                           DownloadListItem item;
                           if("PROPERTIES".equals(name)) {
                              index = this.table.getSelectedRow();
                              if(index < 0) {
                                 this.showMessageBox(this.getString("NONE_SELECTED"), this.getString("DEFAULT_TITLE"), 0);
                                 return;
                              }

                              item = list.get(index);
                              PropertiesDialog.showDetails(item);
                           } else if("HOME_PAGE".equals(name)) {
                              XDMUtil.browse("http://www.sourceforge.net/projects/xdman");
                           } else if("UPDATE".equals(name)) {
                              XDMUtil.browse("http://xdman.sourceforge.net/update/update.php?v=5047");
                           } else if("CONTENT".equals(name)) {
                              try {
                                 HelpDialog var12 = HelpDialog.getHelpDialog();
                                 var12.setDocument("BROWSER_INTEGRATION");
                                 var12.setLocationRelativeTo((Component)null);
                                 var12.setVisible(true);
                              } catch (Exception var7) {
                                 var7.printStackTrace();
                              }
                           } else if("CTX_ASC".equals(name)) {
                              XDMConfig.sortAsc = true;
                              this.sortItems[4].setFont(StaticResource.boldFont);
                              this.sortItems[5].setFont(StaticResource.plainFont);
                              this.sort();
                           } else if("CTX_DESC".equals(name)) {
                              this.sortItems[5].setFont(StaticResource.boldFont);
                              this.sortItems[4].setFont(StaticResource.plainFont);
                              XDMConfig.sortAsc = false;
                              this.sort();
                           } else if(name.startsWith("COL:")) {
                              for(index = 0; index < 4; ++index) {
                                 if(e.getSource() == this.sortItems[index]) {
                                    this.sortItems[index].setFont(StaticResource.boldFont);
                                 } else {
                                    this.sortItems[index].setFont(StaticResource.plainFont);
                                 }
                              }

                              String var11 = name.split(":")[1];
                              XDMConfig.sortField = Integer.parseInt(var11);
                              this.sort();
                           } else if("BTN_SEARCH".equals(name)) {
                              this.sort();
                           } else if("START_Q".equals(name)) {
                              this.startQueue();
                           } else if("STOP_Q".equals(name)) {
                              this.stopQueue();
                           } else if("CTX_ADD_Q".equals(name)) {
                              index = this.table.getSelectedRow();
                              if(index < 0) {
                                 this.showMessageBox(this.getString("NONE_SELECTED"), this.getString("DEFAULT_TITLE"), 0);
                                 return;
                              }

                              item = list.get(index);
                              if(item == null) {
                                 return;
                              }

                              this.add2Q(item, true);
                           } else if("CTX_DEL_Q".equals(name)) {
                              index = this.table.getSelectedRow();
                              if(index < 0) {
                                 this.showMessageBox(this.getString("NONE_SELECTED"), this.getString("DEFAULT_TITLE"), 0);
                                 return;
                              }

                              item = list.get(index);
                              if(item == null) {
                                 return;
                              }

                              this.add2Q(item, false);
                           } else if("CTX_SHOW_PRG".equals(name)) {
                              index = this.table.getSelectedRow();
                              if(index < 0) {
                                 this.showMessageBox(this.getString("NONE_SELECTED"), this.getString("DEFAULT_TITLE"), 0);
                                 return;
                              }

                              item = list.get(index);
                              if(item == null) {
                                 return;
                              }

                              if(item.window != null) {
                                 item.window.setVisible(true);
                              }
                           } else if("MAKE_SHORTCUT".equals(name)) {
                              JFileChooser var9 = XDMFileChooser.getFileChooser(1, new File(System.getProperty("user.home")));
                              if(var9.showOpenDialog(this) == 0) {
                                 this.createShortcut(var9.getSelectedFile());
                              }
                           } else if("CTX_ASM".equals(name)) {
                              index = this.table.getSelectedRow();
                              if(index < 0) {
                                 this.showMessageBox(this.getString("NONE_SELECTED"), this.getString("DEFAULT_TITLE"), 0);
                                 return;
                              }

                              item = list.get(index);
                              if(item == null) {
                                 return;
                              }

                              if(item.mgr != null || item.state == 50) {
                                 this.showMessageBox(this.getString("DWN_ACTIVE_OR_FINISHED"), this.getString("DEFAULT_TITLE"), 0);
                                 return;
                              }

                              if(JOptionPane.showConfirmDialog(this, this.getString("ASM_WRN"), this.getString("DEFAULT_TITLE"), 0, 3) == 0) {
                                 if(this.asmDlg == null) {
                                    this.asmDlg = new AssembleDialog(this);
                                 }

                                 this.asmDlg.startAssemble(item.filename, item.saveto, item.tempdir);
                              }
                           } else if("CTX_COPY_FILE".equals(name)) {
                              int[] var8 = this.table.getSelectedRows();
                              if(var8 == null || var8.length < 1) {
                                 this.showMessageBox(this.getString("NONE_SELECTED"), this.getString("DEFAULT_TITLE"), 0);
                                 return;
                              }

                              DownloadListItem[] var10 = new DownloadListItem[var8.length];

                              for(int exx = 0; exx < var8.length; ++exx) {
                                 var10[exx] = list.get(var8[exx]);
                              }

                              try {
                                 this.copyFiles(var10);
                              } catch (Exception var6) {
                                 var6.printStackTrace();
                              }
                           } else if("CTX_COPY_URL".equals(name)) {
                              index = this.table.getSelectedRow();
                              if(index < 0) {
                                 this.showMessageBox(this.getString("NONE_SELECTED"), this.getString("DEFAULT_TITLE"), 0);
                                 return;
                              }

                              item = list.get(index);
                              if(item == null) {
                                 return;
                              }

                              this.copyURL(item.url);
                           }
                        }
                     } else {
                        if(this.ytDlg == null) {
                           this.ytDlg = new YoutubeGrabberDlg(this);
                        }

                        this.ytDlg.showDialog(this, config, "");
                     }
                  } else {
                     this.showGrabber();
                  }
               } else {
                  this.removeDownloads();
               }
            }
         }

      }
   }

   public void renameFile(DownloadListItem item, int row) {
      if(item.state == 50) {
         this.showMessageBox(this.getString("DWN_FINISHED"), this.getString("DEFAULT_TITLE"), 0);
      } else {
         JFileChooser jfc = XDMFileChooser.getFileChooser(0, new File(item.saveto, item.filename));
         if(jfc.showSaveDialog(this) != 0) {
            return;
         }

         String file = jfc.getSelectedFile().getName();
         String folder = jfc.getSelectedFile().getParent();
         if(item.mgr != null) {
            if(item.mgr.state == 60) {
               this.showMessageBox(this.getString("DWN_ASSEMBLING"), this.getString("DEFAULT_TITLE"), 0);
            } else {
               item.mgr.setDestdir(folder);
               item.mgr.setFileName(file);
            }
         }

         item.filename = file;
         item.saveto = folder;
         list.downloadStateChanged();
         this.model.fireTableDataChanged();
      }

   }

   private void renameFile() {
      int row = this.table.getSelectedRow();
      if(row >= 0) {
         DownloadListItem item = list.get(row);
         if(item != null) {
            this.renameFile(item, row);
         }
      }
   }

   private void openFolder() {
      int row = this.table.getSelectedRow();
      if(row >= 0) {
         DownloadListItem item = list.get(row);
         if(item != null) {
            File folder = new File(item.saveto);
            if(!folder.exists()) {
               this.showMessageBox(this.getString("FOLDER_NOT_FOUND"), this.getString("DEFAULT_TITLE"), 0);
            } else {
               XDMUtil.open(folder);
            }

         }
      }
   }

   private void openFile() {
      int row = this.table.getSelectedRow();
      if(row >= 0) {
         DownloadListItem item = list.get(row);
         if(item != null) {
            if(item.state == 50) {
               File file = new File(item.saveto, item.filename);
               if(file.exists()) {
                  XDMUtil.open(file);
               } else {
                  this.showMessageBox(this.getString("FILE_NOT_FOUND"), this.getString("DEFAULT_TITLE"), 0);
               }
            } else {
               this.showMessageBox(this.getString("DWN_INCOMPLETE"), this.getString("DEFAULT_TITLE"), 0);
            }

         }
      }
   }

   void createContextMenu() {
      this.ctxPopup = new JPopupMenu();
      this.addMenuItem("CTX_OPEN", this.ctxPopup);
      this.addMenuItem("CTX_OPEN_FOLDER", this.ctxPopup);
      this.addMenuItem("CTX_SAVE_AS", this.ctxPopup);
      this.addMenuItem("CTX_SHOW_PRG", this.ctxPopup);
      this.addMenuItem("PAUSE", this.ctxPopup);
      this.addMenuItem("RESUME", this.ctxPopup);
      this.addMenuItem("RESTART", this.ctxPopup);
      this.addMenuItem("DELETE", this.ctxPopup);
      this.addMenuItem("REFRESH_LINK", this.ctxPopup);
      this.addMenuItem("CTX_ASM", this.ctxPopup);
      this.addMenuItem("CTX_ADD_Q", this.ctxPopup);
      this.addMenuItem("CTX_DEL_Q", this.ctxPopup);
      this.addMenuItem("CTX_COPY_URL", this.ctxPopup);
      this.addMenuItem("CTX_COPY_FILE", this.ctxPopup);
      this.addMenuItem("PROPERTIES", this.ctxPopup);
      this.ctxPopup.setInvoker(this.table);
   }

   private void refreshLink() {
      int index = this.table.getSelectedRow();
      if(index < 0) {
         this.showMessageBox(this.getString("NONE_SELECTED"), this.getString("DEFAULT_TITLE"), 0);
      } else {
         DownloadListItem item = list.get(index);
         if(item != null) {
            if(item.state != 50 && item.mgr == null) {
               String url = XDMUtil.isNullOrEmpty(item.referer)?item.url:item.referer;
               url = RefreshLinkDlg.showDialog(this, url);
               if(url != null) {
                  item.url = url;
                  this.model.fireTableRowsUpdated(index, index);
                  list.downloadStateChanged();
               }
            } else {
               this.showMessageBox(this.getString("DWN_ACTIVE_OR_FINISHED"), this.getString("DEFAULT_TITLE"), 0);
            }
         }

      }
   }

   private void exitXDM() {
      config.mwX = this.getX();
      config.mwY = this.getY();
      config.mwW = this.getWidth();
      config.mwH = this.getHeight();
      if(this.w != null) {
         if(!this.w.isVideoMode) {
            config.dbX = this.w.getX();
         } else {
            int x = this.w.getLocationOnScreen().x + 200 - 22;
            if(x > 0) {
               x = Math.min(x, Toolkit.getDefaultToolkit().getScreenSize().width - 30);
            }

            config.dbX = x;
         }

         config.dbY = this.w.getY();
      }

      list.downloadStateChanged();
      config.save();
      Authenticator.getInstance().save();
      System.exit(0);
   }

   public void initBatchDownload(List list, String user, String pass) {
      if(list != null && list.size() >= 1) {
         if(this.listDlg == null) {
            this.listDlg = new BatchDownloadDlg();
         }

         this.listDlg.setLocationRelativeTo((Component)null);
         ArrayList blist = new ArrayList();

         for(int i = 0; i < list.size(); ++i) {
            BatchItem item = new BatchItem();
            item.url = (String)list.get(i);
            item.fileName = XDMUtil.getFileName(item.url);
            item.user = user;
            item.pass = pass;
            item.dir = config.destdir;
            blist.add(item);
         }

         this.listDlg.showDialog(blist, config.destdir, this);
      }
   }

   public void mediaCaptured(String name, String url, String type, String size, String referer, String ua, ArrayList cookies) {
      String filename = XDMUtil.isNullOrEmpty(name)?XDMUtil.getFileName(url):name;
      MediaInfo info = new MediaInfo();
      info.name = filename;
      info.url = url;
      info.referer = referer;
      info.userAgent = ua;
      info.type = type;
      info.size = size;
      info.cookies = cookies;
      this.mediaModel.add(info);
      this.showNotification();
   }

   public void showGrabber() {
   }

   public void showNotification() {
      this.w.setVideoPopup(true);
   }

   public void showNotificationText(String text, String title) {
      this.showNotification();
   }

   public void addDownload(String url, String name, String folder, String user, String pass, String referer, ArrayList cookies, String userAgent) {
      NewDownloadWindow fdlg = new NewDownloadWindow(this, config);
      fdlg.setURL(url);
      fdlg.file.setText(name);
      fdlg.setDir(config.destdir);
      fdlg.referer = referer;
      fdlg.cookies = cookies;
      fdlg.userAgent = userAgent;
      fdlg.showDlg();
   }

   void deleteFinished() {
      if(JOptionPane.showConfirmDialog(this, "Are you sure you want to delete all completed downloads", "Confirm delete", 0) == 0) {
         ArrayList lists = new ArrayList();

         int i;
         for(i = 0; i < list.list.size(); ++i) {
            DownloadListItem item = (DownloadListItem)list.list.get(i);
            if(item.state == 50) {
               lists.add(item);
            }
         }

         for(i = 0; i < lists.size(); ++i) {
            list.list.remove(lists.get(i));
         }

         this.model.fireTableDataChanged();
         list.downloadStateChanged();
      }
   }

   void createTray() {
      JMenuItem newDownload = new JMenuItem(StringResource.getString("ADD_URL"));
      newDownload.setName("ADD_URL");
      newDownload.addActionListener(this);
      JMenuItem aboutXDM = new JMenuItem(StringResource.getString("ABOUT"));
      aboutXDM.setName("ABOUT");
      aboutXDM.addActionListener(this);
      JMenuItem restore = new JMenuItem(StringResource.getString("RESTORE"));
      restore.setName("RESTORE");
      restore.addActionListener(this);
      JMenuItem exit2 = new JMenuItem(StringResource.getString("EXIT"));
      exit2.setName("EXIT");
      exit2.addActionListener(this);
      final JPopupMenu trayPop = new JPopupMenu();
      trayPop.add(newDownload);
      trayPop.add(aboutXDM);
      trayPop.add(restore);
      trayPop.add(exit2);
      this.lbl = new JLabel();
      this.lbl.setToolTipText("Xtreme Download Manager");
      this.lbl.setOpaque(true);
      this.lbl.setIcon(getIcon("xdm22.png"));
      this.lbl.setHorizontalAlignment(0);
      this.lbl.setVerticalAlignment(0);
      trayPop.setInvoker(this.lbl);
      MouseAdapter m = new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            if(e.getButton() == 1 && e.getClickCount() == 2) {
               XDMMainWindow.this.restoreWindow();
            }

            if(e.getButton() == 3) {
               trayPop.show(XDMMainWindow.this.lbl, e.getX(), e.getY());
            }

         }
      };
      this.lbl.addMouseListener(m);
      this.w = new DropBox(this.lbl, this, this.mediaModel, this, config);
      Dimension scrRect = Toolkit.getDefaultToolkit().getScreenSize();
      if(config.dbX <= 0) {
         this.w.setLocation(scrRect.width - 30, scrRect.height - 200);
      } else {
         int x = Math.min(scrRect.width - 30, config.dbX);
         this.w.setLocation(x, config.dbY);
      }

      this.w.setVisible(true);
   }

   HelpDialog getHTMLViwer() {
      HashMap map = new HashMap();
      map.put("Browser Integration", this.getClass().getResource("/help/browser_integration.html"));
      map.put("Capturing Videos", this.getClass().getResource("/help/video_download.html"));
      map.put("Refresh Broken Downloads", this.getClass().getResource("/help/refresh_link.html"));
      HelpDialog hlp = new HelpDialog();
      hlp.addPages(map);
      return hlp;
   }

   static void parseArgs(String[] a) {
      String key = "url";
      boolean min = false;

      for(int i = 0; i < a.length; ++i) {
         if(a[i].equals("-c")) {
            key = "cookie";
         } else if(a[i].equals("-r")) {
            key = "referer";
         } else if(a[i].equals("-m")) {
            min = true;
         } else if(a[i].equals("-u")) {
            key = "url";
         } else {
            arg.put(key, a[i]);
         }
      }

      if(min) {
         arg.put("min", "true");
      }

   }

   void showBrowserIntegrationDlg() {
      if(this.biDlg == null) {
         this.biDlg = new BrowserIntDlg(this, config);
      }

      this.biDlg.setLocationRelativeTo(this);
      this.biDlg.setVisible(true);
   }

   void sort() {
      list.searchStr = this.txtSearch.getText();
      this.updateSortText();
      list.sort();
      list.downloadStateChanged();
      this.model.fireTableDataChanged();
   }

   boolean processNextQueuedDownload() {
      System.out.println("ProcessNextQueue");
      if(!this.processQueue) {
         return false;
      } else if(this.qi != null) {
         return false;
      } else {
         for(int i = 0; i < list.list.size(); ++i) {
            DownloadListItem di = (DownloadListItem)list.list.get(i);
            if(di.mgr == null && di.state != 50 && di.q) {
               this.qi = di;
               this.resumeDownload(di);
               return true;
            }
         }

         this.qi = null;
         this.processQueue = false;
         config.schedule = false;
         this.schedulerActive = false;
         return false;
      }
   }

   void schedulerRun() {
      this.schedulerActive = true;
      Logger.log("SchedulerRun");

      try {
         if(System.currentTimeMillis() <= config.endDate.getTime()) {
            Logger.log("Starting scheduler");

            while(config.schedule) {
               if(System.currentTimeMillis() > config.startDate.getTime()) {
                  if(!this.processQueue) {
                     Logger.log("Starting queue scheduler");
                     this.processQueue = true;
                     this.processNextQueuedDownload();
                  } else {
                     Logger.log("Queue already started");
                  }

                  while(this.processQueue) {
                     if(!config.schedule) {
                        Logger.log("Scheduler killed");
                        if(this.processQueue) {
                           this.stopQueue();
                           this.processQueue = false;
                        }

                        return;
                     }

                     if(System.currentTimeMillis() > config.endDate.getTime()) {
                        if(this.processQueue) {
                           this.stopQueue();
                           this.processQueue = false;
                        }

                        Logger.log("Time ended processQ: " + this.processQueue);
                        config.schedule = false;
                        return;
                     }

                     Thread.sleep(1000L);
                  }

                  return;
               }

               Thread.sleep(1000L);
            }

            Logger.log("Scheduler killed");
            return;
         }

         Logger.log("Scheduler outdated");
         return;
      } catch (Exception var5) {
         Logger.log(var5);
      } finally {
         this.schedulerActive = false;
      }

   }

   void add2Q(DownloadListItem item, boolean add) {
      if(item.mgr == null && item.state != 50) {
         item.q = add;
         list.downloadStateChanged();
         this.model.fireTableDataChanged();
      } else {
         this.showMessageBox(this.getString("DWN_ACTIVE_OR_FINISHED"), this.getString("DEFAULT_TITLE"), 0);
      }
   }

   void executeCommands() {
      if(config.executeCmd) {
         this.exec(config.cmdTxt);
      }

      if(config.antivir) {
         this.exec(config.antivirTxt);
      }

      if(config.hungUp) {
         this.exec(config.hungUpTxt);
      }

      if(config.halt) {
         this.exec(config.haltTxt);
      }

   }

   void exec(String cmd) {
      try {
         Runtime.getRuntime().exec(cmd);
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   void createShortcut(File f) {
      if(System.getProperty("os.name").contains("OS X")) {
         OSXUtil.createAppBundle(f);
      } else {
         LinuxUtil.createDesktopFile(f.getAbsolutePath(), false);
      }

   }

   void copyFiles(DownloadListItem[] items) {
      Object[] values = (Object[])null;
      if(items != null) {
         values = new Object[items.length];

         for(int plainBuf = 0; plainBuf < items.length; ++plainBuf) {
            DownloadListItem htmlBuf = items[plainBuf];
            File i = new File(htmlBuf.saveto, htmlBuf.filename);
            values[plainBuf] = i;
         }
      }

      StringBuffer var8 = new StringBuffer();
      StringBuffer var9 = new StringBuffer();
      var9.append("<html>\n<body>\n<ul>\n");

      for(int var10 = 0; var10 < values.length; ++var10) {
         Object obj = values[var10];
         String val = obj == null?"":obj.toString();
         var8.append(val + "\n");
         var9.append("  <li>" + val + "\n");
      }

      if(this.clipboard == null) {
         this.clipboard = this.t.getSystemClipboard();
      }

      this.clipboard.setContents(new FileTransferHandler.FileTransferable(var8.toString(), var9.toString(), values), (ClipboardOwner)null);
   }

   void copyURL(String url) {
      System.out.println("Copying url " + url);
      if(this.clipboard == null) {
         this.clipboard = this.t.getSystemClipboard();
      }

      this.clipboard.setContents(new StringSelection(url), (ClipboardOwner)null);
   }

   public static void main(String[] args) {
      System.setProperty("apple.awt.UIElement", "true");
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      System.setProperty("com.apple.mrj.application.menu.about.name", "XDM");

      try {
         UIManager.setLookAndFeel(new XDMLookAndFeel());
      } catch (Exception var11) {
         ;
      }

      String jarPath = XDMUtil.getJarPath();
      System.out.println(jarPath);
      if(System.getProperty("os.name").contains("OS X") && jarPath != null && jarPath.startsWith("/Volumes/xdm5setup")) {
         OSXInstallWindow firstRun = new OSXInstallWindow();
         firstRun.setLocationRelativeTo((Component)null);
         firstRun.setModal(true);
         firstRun.setVisible(true);
         System.exit(0);
      }

      boolean firstRun1 = false;
      File fAppDir = new File(System.getProperty("user.home"), ".xdm");
      File fTmpDir = new File(fAppDir, "temp");
      File configFile = new File(fAppDir, ".xdmconf");
      firstRun1 = !fAppDir.exists();
      config = XDMConfig.load(configFile);
      parseArgs(args);
      if(firstRun1) {
         fAppDir.mkdirs();
         fTmpDir.mkdirs();
      }

      tempdir = fTmpDir.getAbsolutePath();
      destdir = System.getProperty("user.home");
      if(firstRun1) {
         File mw = new File(System.getProperty("user.home"), "Downloads");
         if(mw.exists()) {
            destdir = mw.getAbsolutePath();
            System.out.println(destdir);
         }
      }

      appdir = fAppDir.getAbsolutePath();

      try {
         StringResource.loadResource("en");
         XDMIconMap.setIcon("DOC", getIcon("document.png"));
         XDMIconMap.setIcon("MUSIC", getIcon("music.png"));
         XDMIconMap.setIcon("OTHER", getIcon("other.png"));
         XDMIconMap.setIcon("APP", getIcon("exe.png"));
         XDMIconMap.setIcon("VID", getIcon("video.png"));
         XDMIconMap.setIcon("ZIP", getIcon("arc.png"));
         XDMIconMap.setIcon("FOLDER", getIcon("folder.png"));
         XDMIconMap.setIcon("RIGHT_ARROW", getIcon("left_arrow.png"));
         XDMIconMap.setIcon("LEFT_ARROW", getIcon("right_arrow.png"));
         XDMIconMap.setIcon("UP_ARROW", getIcon("up_arrow.png"));
         XDMIconMap.setIcon("DOWN_ARROW", getIcon("down_arrow.png"));
         XDMIconMap.setIcon("EXPAND_ICON", getIcon("expand.png"));
         XDMIconMap.setIcon("COLLAPSE_ICON", getIcon("collapse.png"));
         XDMIconMap.setIcon("APP_ICON", getIcon("icon.png"));
         XDMIconMap.setIcon("Q_ICON", getIcon("q.png"));
         XDMIconMap.setIcon("YT_ICON", getIcon("youtube72.png"));
         XDMIconMap.setIcon("RF_ICON", getIcon("restart.png"));
         XDMIconMap.setIcon("COLD_ICON", getIcon("xdm22.png"));
         XDMIconMap.setIcon("HOT_ICON", getIcon("xdm22_hot.png"));
         XDMIconMap.setIcon("BACK_ICON", getIcon("back.png"));
         XDMIconMap.setIcon("BACK_R_ICON", getIcon("back_r.png"));
         XDMIconMap.setIcon("NEXT_ICON", getIcon("next.png"));
         XDMIconMap.setIcon("NEXT_R_ICON", getIcon("next_r.png"));
         XDMIconMap.setIcon("FF_ICON", getIcon("firefox.png"));
         XDMIconMap.setIcon("CR_ICON", getIcon("chrome.png"));
         XDMIconMap.setIcon("OP_ICON", getIcon("opera.png"));
         XDMIconMap.setIcon("OT_ICON", getIcon("browser.png"));
         XDMIconMap.setIcon("CI_ICON", getIcon("chrome-inst.png"));
      } catch (Exception var10) {
         var10.printStackTrace();
      }

      XDMMainWindow mw1 = new XDMMainWindow();
      XDMServer server = new XDMServer(config, mw1, mw1);
      if(!server.start()) {
         server.sendParams(arg);
         System.exit(0);
      }

      if(arg.get("min") == null) {
         mw1.setVisible(true);
      }

      Authenticator.getInstance().load(new File(fAppDir, ".xdmauth"));
      mw1.configChanged();
      if(firstRun1) {
         try {
            if(System.getProperty("os.name").contains("OS X")) {
               OSXUtil.createAppBundle(new File(System.getProperty("user.home"), "Desktop"));
               OSXUtil.enableAutoStart();
               config.autostart = true;
            } else {
               mw1.createShortcut(new File(XDMUtil.getJarPath()));
               mw1.createShortcut(new File(System.getProperty("user.home"), "Desktop"));
               LinuxUtil.enableAutoStartLinux();
               config.autostart = true;
            }
         } catch (Exception var9) {
            System.out.println(var9);
         }

         mw1.showBrowserIntegrationDlg();
      }

   }
}
