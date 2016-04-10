package org.sdg.xdman.gui;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.sdg.xdman.gui.StringResource;

public class AuthDialog {
   static JTextField user = null;
   static JTextField pass = null;
   static Object[] obj;

   public static String[] getAuth() {
      if(user == null) {
         user = new JTextField();
      }

      if(pass == null) {
         pass = new JPasswordField();
      }

      if(obj == null) {
         obj = new Object[4];
         obj[0] = StringResource.getString("USER_NAME");
         obj[1] = user;
         obj[2] = StringResource.getString("PASSWORD");
         obj[3] = pass;
      }

      user.setText("");
      pass.setText("");

      while(true) {
         while(JOptionPane.showOptionDialog((Component)null, obj, StringResource.getString("LBL_CR"), 2, 3, (Icon)null, (Object[])null, (Object)null) == 0) {
            if(user.getText() != null && user.getText().length() >= 1) {
               if(pass.getText() != null && pass.getText().length() >= 1) {
                  return new String[]{user.getText(), pass.getText()};
               }

               JOptionPane.showMessageDialog((Component)null, StringResource.getString("LBL_PASS"));
            } else {
               JOptionPane.showMessageDialog((Component)null, StringResource.getString("LBL_USER"));
            }
         }

         return null;
      }
   }

   public static void main(String[] args) {
      getAuth();
   }
}
