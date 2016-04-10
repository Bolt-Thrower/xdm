package org.sdg.xdman.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.sdg.xdman.gui.StaticResource;
import org.sdg.xdman.gui.TitlePanel;

public class XDMFrame extends JFrame {
   private static final long serialVersionUID = -8094995420106046965L;
   private boolean maximizeBox = true;
   private boolean minimizeBox = true;
   JPanel panTitle;
   JPanel panClient;
   private JLabel lblRightGrip;
   private JLabel lblLeftGrip;
   private JLabel lblTopGrip;
   private JLabel lblBottomGrip;
   int diffx;
   int diffy;
   Box vBox;
   ActionListener actClose = new ActionListener() {
      public void actionPerformed(ActionEvent action) {
         XDMFrame.this.dispatchEvent(new WindowEvent(XDMFrame.this, 201));
      }
   };
   ActionListener actMax = new ActionListener() {
      public void actionPerformed(ActionEvent action) {
         XDMFrame.this.setMaximizedBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
         XDMFrame.this.setExtendedState((XDMFrame.this.getExtendedState() & 6) == 6?0:6);
      }
   };
   ActionListener actMin = new ActionListener() {
      public void actionPerformed(ActionEvent action) {
         XDMFrame.this.setExtendedState(XDMFrame.this.getExtendedState() | 1);
      }
   };
   Cursor curDefault;
   Cursor curNResize;
   Cursor curEResize;
   Cursor curWResize;
   Cursor curSResize;
   Cursor curSEResize;
   Cursor curSWResize;

   public XDMFrame() {
      this.setUndecorated(true);
      this.createCursors();
      this.createResizeGrip();
      this.panTitle = new TitlePanel(new BorderLayout(), this);
      this.panTitle.setBackground(StaticResource.titleColor);
      this.panTitle.setBorder(new EmptyBorder(8, 8, 0, 8));
      this.panTitle.setOpaque(true);
      this.registerTitlePanel(this.panTitle);
      this.panClient = new JPanel(new BorderLayout());
      this.panClient.setBackground(Color.WHITE);
      JPanel panContent = new JPanel(new BorderLayout());
      panContent.add(this.panTitle, "North");
      panContent.add(this.panClient);
      super.add(panContent);
   }

   public JPanel getTitlePanel() {
      return this.panTitle;
   }

   public void setMaximizeBox(boolean maximizeBox) {
      this.maximizeBox = maximizeBox;
   }

   public boolean isMaximizeBox() {
      return this.maximizeBox;
   }

   public void setMinimizeBox(boolean minimizeBox) {
      this.minimizeBox = minimizeBox;
   }

   public boolean isMinimizeBox() {
      return this.minimizeBox;
   }

   public Component add(Component c) {
      return this.panClient.add(c);
   }

   private void createResizeGrip() {
      XDMFrame.GripMouseAdapter gma = new XDMFrame.GripMouseAdapter();
      this.lblRightGrip = new JLabel();
      this.lblRightGrip.setMaximumSize(new Dimension(2, this.lblRightGrip.getMaximumSize().height));
      this.lblRightGrip.setPreferredSize(new Dimension(2, this.lblRightGrip.getPreferredSize().height));
      this.lblRightGrip.setBackground(Color.BLACK);
      this.lblRightGrip.setOpaque(true);
      super.add(this.lblRightGrip, "East");
      this.lblBottomGrip = new JLabel();
      this.lblBottomGrip.setMaximumSize(new Dimension(this.lblBottomGrip.getPreferredSize().width, 2));
      this.lblBottomGrip.setPreferredSize(new Dimension(this.lblBottomGrip.getPreferredSize().width, 2));
      this.lblBottomGrip.setBackground(Color.BLACK);
      this.lblBottomGrip.setOpaque(true);
      super.add(this.lblBottomGrip, "South");
      this.lblLeftGrip = new JLabel();
      this.lblLeftGrip.setMaximumSize(new Dimension(2, this.lblLeftGrip.getPreferredSize().height));
      this.lblLeftGrip.setPreferredSize(new Dimension(2, this.lblLeftGrip.getPreferredSize().height));
      this.lblLeftGrip.setBackground(Color.BLACK);
      this.lblLeftGrip.setOpaque(true);
      super.add(this.lblLeftGrip, "West");
      this.lblTopGrip = new JLabel();
      this.lblTopGrip.setMaximumSize(new Dimension(this.lblTopGrip.getPreferredSize().width, 2));
      this.lblTopGrip.setPreferredSize(new Dimension(this.lblTopGrip.getPreferredSize().width, 2));
      this.lblTopGrip.setBackground(Color.BLACK);
      this.lblTopGrip.setOpaque(true);
      super.add(this.lblTopGrip, "North");
      if(this.isResizable()) {
         this.lblTopGrip.addMouseListener(gma);
         this.lblTopGrip.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
               int y = me.getYOnScreen();
               int diff = XDMFrame.this.getLocationOnScreen().y - y;
               XDMFrame.this.setLocation(XDMFrame.this.getLocation().x, me.getLocationOnScreen().y);
               System.out.println(diff);
               XDMFrame.this.setSize(XDMFrame.this.getWidth(), XDMFrame.this.getHeight() + diff);
            }
         });
         this.lblRightGrip.addMouseListener(gma);
         this.lblRightGrip.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
               int x = me.getXOnScreen();
               int diff = x - XDMFrame.this.getLocationOnScreen().x;
               XDMFrame.this.setSize(diff, XDMFrame.this.getHeight());
            }
         });
         this.lblLeftGrip.addMouseListener(gma);
         this.lblLeftGrip.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
               int x = me.getXOnScreen();
               int diff = XDMFrame.this.getLocationOnScreen().x - x;
               XDMFrame.this.setLocation(me.getLocationOnScreen().x, XDMFrame.this.getLocation().y);
               XDMFrame.this.setSize(diff + XDMFrame.this.getWidth(), XDMFrame.this.getHeight());
            }
         });
         this.lblBottomGrip.addMouseListener(gma);
         this.lblBottomGrip.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
               int y = me.getYOnScreen();
               int diff = y - XDMFrame.this.getLocationOnScreen().y;
               XDMFrame.this.setSize(XDMFrame.this.getWidth(), diff);
            }
         });
      }

   }

   void registerTitlePanel(JPanel panel) {
      this.vBox = Box.createVerticalBox();
      this.vBox.setOpaque(true);
      this.vBox.setBackground(StaticResource.titleColor);
      Box hBox = Box.createHorizontalBox();
      hBox.setBackground(StaticResource.titleColor);
      if(this.minimizeBox) {
         hBox.add(this.createTransparentButton(StaticResource.getIcon("min_btn.png"), StaticResource.getIcon("min_btn_r.png"), new Dimension(24, 24), this.actMin));
      }

      if(this.maximizeBox) {
         hBox.add(this.createTransparentButton(StaticResource.getIcon("max_btn.png"), StaticResource.getIcon("max_btn_r.png"), new Dimension(24, 24), this.actMax));
      }

      hBox.add(this.createTransparentButton(StaticResource.getIcon("close_btn.png"), StaticResource.getIcon("close_btn_r.png"), new Dimension(24, 24), this.actClose));
      this.vBox.add(hBox);
      this.vBox.add(Box.createVerticalGlue());
      panel.add(this.vBox, "East");
   }

   private void createCursors() {
      this.curDefault = new Cursor(0);
      this.curNResize = new Cursor(8);
      this.curWResize = new Cursor(10);
      this.curEResize = new Cursor(11);
      this.curSResize = new Cursor(9);
   }

   JButton createTransparentButton(ImageIcon icon, ImageIcon rIcon, Dimension d, ActionListener actionListener) {
      JButton btn = new JButton(icon);
      btn.setBackground(StaticResource.titleColor);
      btn.setRolloverIcon(rIcon);
      btn.setBorderPainted(false);
      btn.setContentAreaFilled(false);
      btn.setFocusPainted(false);
      btn.setPreferredSize(d);
      btn.addActionListener(actionListener);
      return btn;
   }

   class GripMouseAdapter extends MouseAdapter {
      public void mouseEntered(MouseEvent me) {
         if(me.getSource() == XDMFrame.this.lblBottomGrip) {
            XDMFrame.this.lblBottomGrip.setCursor(XDMFrame.this.curSResize);
         } else if(me.getSource() == XDMFrame.this.lblRightGrip) {
            XDMFrame.this.lblRightGrip.setCursor(XDMFrame.this.curEResize);
         } else if(me.getSource() == XDMFrame.this.lblLeftGrip) {
            XDMFrame.this.lblLeftGrip.setCursor(XDMFrame.this.curWResize);
         } else if(me.getSource() == XDMFrame.this.lblTopGrip) {
            XDMFrame.this.lblTopGrip.setCursor(XDMFrame.this.curNResize);
         }

      }

      public void mouseExited(MouseEvent me) {
         ((JLabel)me.getSource()).setCursor(XDMFrame.this.curDefault);
      }
   }
}
