package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.sdg.xdman.gui.StringResource;
import org.sdg.xdman.gui.XDMIconMap;
import org.sdg.xdman.util.XDMUtil;

public class RefreshLinkDlg {
   static String showDialog(Frame f, String url) {
      JTextField txtLink = new JTextField(30);
      txtLink.setText(url);
      txtLink.setEditable(false);
      txtLink.setBackground(Color.WHITE);
      JButton btnOpen = new JButton(StringResource.getString("RF_LBL5"));
      btnOpen.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if(e.getSource() instanceof JButton) {
               String url = ((JButton)e.getSource()).getName();

               try {
                  if(Desktop.isDesktopSupported()) {
                     Desktop.getDesktop().browse(new URI(url));
                  }
               } catch (Exception var4) {
                  var4.printStackTrace();
               }
            }

         }
      });
      btnOpen.setName(url);
      JTextField txtNewLink = new JTextField();
      Object[] objs = new Object[]{StringResource.getString("RF_LBL1"), txtLink, btnOpen, StringResource.getString("RF_LBL2"), StringResource.getString("RF_LBL3"), txtNewLink};

      while(JOptionPane.showOptionDialog(f, objs, StringResource.getString("RF_LBL4"), 2, 1, XDMIconMap.getIcon("RF_ICON"), (Object[])null, (Object)null) == 0) {
         if(XDMUtil.validateURL(txtNewLink.getText())) {
            return txtNewLink.getText();
         }

         JOptionPane.showMessageDialog((Component)null, StringResource.getString("INVALID_URL"));
      }

      return null;
   }
}
