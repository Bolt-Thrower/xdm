package org.sdg.xdman.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import org.sdg.xdman.gui.OSXAppTransferHandler;
import org.sdg.xdman.gui.StaticResource;
import org.sdg.xdman.gui.TitlePanel;
import org.sdg.xdman.gui.XDMMainWindow;
import org.sdg.xdman.util.OSXUtil;

public class OSXInstallWindow extends JDialog {
   private static final long serialVersionUID = -9048168403718918075L;

   public OSXInstallWindow() {
      this.setSize(300, 300);
      this.setUndecorated(true);
      JPanel panel = new JPanel(new BorderLayout());
      panel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
      panel.setBackground(Color.WHITE);
      this.add(panel);
      TitlePanel tp = new TitlePanel(new BorderLayout(), this);
      tp.setBackground(Color.WHITE);
      JLabel title = new JLabel("XDM setup", 0);
      title.setForeground(StaticResource.selectedColor);
      title.setBorder(new EmptyBorder(20, 20, 20, 20));
      title.setFont(StaticResource.plainFontBig2);
      tp.add(title, "Center");
      panel.add(tp, "North");
      JLabel lblDrag = new JLabel("", 0);
      lblDrag.setIcon(XDMMainWindow.getIcon("icon.png"));
      OSXAppTransferHandler osxh = new OSXAppTransferHandler();
      lblDrag.setTransferHandler(osxh);
      lblDrag.addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent e) {
            JComponent c = (JComponent)e.getSource();
            TransferHandler th = c.getTransferHandler();
            th.exportAsDrag(c, e, 1);
         }
      });
      panel.add(lblDrag);
      JLabel info = new JLabel("", 0);
      info.setFont(StaticResource.plainFont);
      JPanel p = new JPanel(new BorderLayout());
      p.setBackground(Color.WHITE);
      info.setText("<html><center>Drag this icon to Application folder to install XDM as app<br>OR<br>Simply copy this Jar file to any folder and double click to start</center></html>");
      info.setBorder(new EmptyBorder(20, 20, 30, 20));
      info.setForeground(StaticResource.selectedColor);
      p.add(info);
      Box b = Box.createHorizontalBox();
      JButton btnClose = new JButton("CLOSE");
      btnClose.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            OSXInstallWindow.this.setVisible(false);
         }
      });
      b.add(Box.createHorizontalGlue());
      b.add(btnClose, "South");
      b.add(Box.createHorizontalGlue());
      b.setBorder(new EmptyBorder(10, 10, 10, 10));
      p.add(b, "South");
      panel.add(p, "South");
      File folder = new File(System.getProperty("user.home"), ".xdm");
      folder.mkdir();
      OSXUtil.createFixedAppBundle(folder);
      osxh.setAppFolderLocation(new File(folder, "xdm.app"));
   }

   public static void main(String[] args) {
      (new OSXInstallWindow()).setVisible(true);
   }
}
