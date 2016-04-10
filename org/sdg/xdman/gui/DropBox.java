package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JWindow;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.gui.MediaInfo;
import org.sdg.xdman.gui.MediaTableModel;
import org.sdg.xdman.gui.StaticResource;
import org.sdg.xdman.gui.XDMIconMap;
import org.sdg.xdman.gui.XDMTransferHandler;
import org.sdg.xdman.util.XDMUtil;

public class DropBox extends JWindow implements ActionListener {
   private static final long serialVersionUID = -6560446385567170000L;
   JLabel label;
   int relx;
   int rely;
   DownloadStateListner mgr;
   Icon lblIcon;
   JButton closeBtn;
   MediaTableModel model;
   JMenu menu;
   JMenuBar bar;
   boolean isVideoMode = false;
   Cursor hc = new Cursor(12);
   public static final int VIDEO_WIDTH = 200;
   DownloadStateListner dl;
   XDMConfig config;
   JMenuItem[] items;

   public DropBox(JLabel label, DownloadStateListner mgr, MediaTableModel m, DownloadStateListner dl, XDMConfig config) {
      this.setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
      this.setAlwaysOnTop(true);
      this.add(label);
      this.mgr = mgr;
      this.label = label;
      this.model = m;
      this.dl = dl;
      this.config = config;
      this.label.setTransferHandler(new XDMTransferHandler(mgr));
      this.add(label);
      label.addMouseMotionListener(new MouseMotionAdapter() {
         public void mouseDragged(MouseEvent me) {
            int x = DropBox.this.getX();
            int y = DropBox.this.getY();
            DropBox.this.setLocation(x + me.getX() - DropBox.this.relx, y + me.getY() - DropBox.this.rely);
         }
      });
      label.addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent me) {
            DropBox.this.relx = me.getX();
            DropBox.this.rely = me.getY();
         }
      });
      this.setSize(22, 22);
      this.closeBtn = new JButton();
      this.closeBtn.setContentAreaFilled(false);
      this.closeBtn.setBorderPainted(false);
      this.closeBtn.setFocusPainted(false);
      this.closeBtn.setMargin(new Insets(0, 0, 0, 0));
      this.closeBtn.setIcon(StaticResource.getIcon("close_btn.png"));
      this.closeBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            DropBox.this.setVideoPopup(false);
            DropBox.this.model.clear();
         }
      });
      this.add(this.closeBtn, "East");
      this.closeBtn.setVisible(false);
      this.closeBtn.setBackground(StaticResource.selectedColor);
      this.menu = new JMenu("DOWNLOAD VIDEO");
      this.menu.setFont(new Font("Dialog", 1, 14));
      this.menu.setForeground(Color.WHITE);
      this.menu.setBackground(StaticResource.selectedColor);
      this.menu.setBorderPainted(false);
      this.menu.getPopupMenu().addPopupMenuListener(new PopupMenuListener() {
         public void popupMenuCanceled(PopupMenuEvent e) {
         }

         public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
         }

         public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            DropBox.this.populateMenu();
         }
      });
      this.bar = new JMenuBar();
      this.bar.setOpaque(false);
      this.bar.setCursor(this.hc);
      this.bar.addMouseMotionListener(new MouseMotionAdapter() {
         public void mouseDragged(MouseEvent me) {
            int x = DropBox.this.getX();
            int y = DropBox.this.getY();
            DropBox.this.setLocation(x + me.getX() - DropBox.this.relx, y + me.getY() - DropBox.this.rely);
         }
      });
      this.bar.addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent me) {
            DropBox.this.relx = me.getX();
            DropBox.this.rely = me.getY();
         }
      });
      this.getContentPane().setBackground(StaticResource.selectedColor);
      this.bar.setBackground(StaticResource.selectedColor);
      this.bar.setBorderPainted(false);
      this.bar.add(Box.createHorizontalGlue());
      this.bar.add(this.menu);
      this.bar.add(Box.createHorizontalGlue());
      this.bar.setVisible(false);
   }

   void setVideoPopup(boolean enable) {
      int x;
      if(!enable) {
         if(!this.isVideoMode) {
            return;
         }

         this.setSize(22, 22);
         if(this.isVideoMode) {
            x = this.getLocationOnScreen().x + 200 - 22;
            if(x > 0) {
               x = Math.min(x, Toolkit.getDefaultToolkit().getScreenSize().width - 30);
            }

            this.setLocation(x, this.getLocationOnScreen().y);
         }

         this.closeBtn.setVisible(false);
         this.add(this.label);
         this.remove(this.bar);
      } else {
         if(this.isVideoMode) {
            return;
         }

         x = this.getLocationOnScreen().x - 200 + 22;
         this.setSize(200, 22);
         this.label.setOpaque(true);
         this.label.setBackground(StaticResource.selectedColor);
         this.label.setForeground(Color.WHITE);
         this.setLocation(x, this.getLocationOnScreen().y);
         this.label.setFont(StaticResource.plainFontBig);
         this.closeBtn.setVisible(true);
         this.bar.setVisible(true);
         this.add(this.bar);
         this.remove(this.label);
      }

      this.isVideoMode = enable;
      this.validate();
   }

   void menuClicked(int i) {
      MediaInfo info = (MediaInfo)this.model.list.get(i);
      this.dl.addDownload(info.url, info.name, this.config.destdir, (String)null, (String)null, info.referer, info.cookies, info.userAgent);
   }

   void populateMenu() {
      if(this.isVideoMode) {
         int i;
         if(this.items != null) {
            for(i = 0; i < this.items.length; ++i) {
               this.menu.remove(this.items[i]);
            }
         }

         this.items = new JMenuItem[this.model.list.size()];

         for(i = 0; i < this.model.getRowCount(); ++i) {
            try {
               MediaInfo e = (MediaInfo)this.model.list.get(i);
               String name = e.name;
               String info = (e.type == null?"":e.type) + " " + (e.size == null?"":e.size);
               name = XDMUtil.createSafeFileName(name);
               if(name.length() > 30) {
                  name = name.substring(0, 30) + "...";
               }

               JMenuItem item = new JMenuItem(name + " " + info.toUpperCase());
               item.addActionListener(this);
               this.menu.add(item);
               this.items[i] = item;
            } catch (Exception var6) {
               var6.printStackTrace();
            }
         }

      }
   }

   public void actionPerformed(ActionEvent e) {
      if(this.items != null) {
         for(int i = 0; i < this.items.length; ++i) {
            if(e.getSource() == this.items[i]) {
               this.menuClicked(i);
            }
         }
      }

   }
}
