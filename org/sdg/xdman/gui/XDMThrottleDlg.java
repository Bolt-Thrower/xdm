package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.gui.StaticResource;
import org.sdg.xdman.gui.TitlePanel;

public class XDMThrottleDlg extends JDialog {
   private static final long serialVersionUID = -4516761235891212218L;
   XDMConfig config;

   public XDMThrottleDlg(XDMConfig config) {
      this.setModal(true);
      this.config = config;
      this.setUndecorated(true);
      this.setSize(400, 150);
      this.getContentPane().setLayout((LayoutManager)null);
      this.getContentPane().setBackground(Color.black);
      JPanel panel = new JPanel((LayoutManager)null);
      panel.setBounds(1, 1, 398, 148);
      panel.setBackground(Color.WHITE);
      this.add(panel);
      TitlePanel titlePanel = new TitlePanel((LayoutManager2)null, this);
      titlePanel.setBackground(StaticResource.titleColor);
      titlePanel.setBounds(0, 0, 398, 50);
      JLabel titleLbl = new JLabel("XDM SPEED LIMITER", 0);
      titleLbl.setFont(StaticResource.plainFontBig);
      titleLbl.setForeground(Color.WHITE);
      titleLbl.setBounds(0, 0, 398, 50);
      titlePanel.add(titleLbl);
      panel.add(titlePanel);
      JLabel lbl = new JLabel("Maximum download speed [KB/Sec] (0 unlimited)", 4);
      lbl.setBounds(10, 70, 320, 20);
      panel.add(lbl);
      final JTextField txtSpeed = new JTextField(10);
      txtSpeed.setBounds(340, 70, 50, 20);
      panel.add(txtSpeed);
      txtSpeed.setText(String.valueOf(config.maxBPS / 1024));
      JButton okBtn = new JButton("OK");
      okBtn.setBounds(200, 115, 90, 25);
      panel.add(okBtn);
      okBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            try {
               int ex = Integer.parseInt(txtSpeed.getText()) * 1024;
               if(ex < 0) {
                  throw new Exception();
               }

               XDMThrottleDlg.this.config.maxBPS = ex;
            } catch (Exception var3) {
               JOptionPane.showMessageDialog((Component)null, "Please enter whole number only");
               return;
            }

            XDMThrottleDlg.this.dispose();
         }
      });
      JButton closeBtn = new JButton("CANCEL");
      closeBtn.setBounds(300, 115, 90, 25);
      panel.add(closeBtn);
      closeBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            XDMThrottleDlg.this.dispose();
         }
      });
   }
}
