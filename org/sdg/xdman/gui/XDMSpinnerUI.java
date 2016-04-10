package org.sdg.xdman.gui;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicSpinnerUI;

public class XDMSpinnerUI extends BasicSpinnerUI {
   public static ComponentUI createUI(JComponent c) {
      return new XDMSpinnerUI();
   }

   protected Component createNextButton() {
      BasicArrowButton c = new BasicArrowButton(1);
      c.setName("Spinner.nextButton");
      this.installNextButtonListeners(c);
      return c;
   }

   protected Component createPreviousButton() {
      BasicArrowButton c = new BasicArrowButton(5);
      c.setName("Spinner.previousButton");
      this.installPreviousButtonListeners(c);
      return c;
   }
}
