package org.sdg.xdman.gui;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import org.sdg.xdman.gui.XDMButton;
import org.sdg.xdman.gui.XDMIconMap;

public class XDMComboBoxUI extends BasicComboBoxUI {
   static XDMComboBoxUI buttonUI;
   JComponent c;

   public static ComponentUI createUI(JComponent c) {
      return new XDMComboBoxUI();
   }

   protected JButton createArrowButton() {
      XDMButton button = new XDMButton();
      button.setIcon(XDMIconMap.getIcon("DOWN_ARROW"));
      button.setBorderPainted(false);
      button.setFocusPainted(false);
      button.setName("ComboBox.arrowButton");
      return button;
   }

   public void installUI(JComponent c) {
      super.installUI(c);
      this.c = c;
   }
}
