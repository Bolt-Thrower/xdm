package org.sdg.xdman.gui;

import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import org.sdg.xdman.gui.XDMIconMap;

public class XDMTreeUI extends BasicTreeUI {
   public static ComponentUI createUI(JComponent c) {
      return new XDMTreeUI();
   }

   public Icon getExpandedIcon() {
      return XDMIconMap.getIcon("EXPAND_ICON");
   }

   protected void paintHorizontalLine(Graphics arg0, JComponent arg1, int arg2, int arg3, int arg4) {
   }

   protected void paintVerticalLine(Graphics arg0, JComponent arg1, int arg2, int arg3, int arg4) {
   }

   public Icon getCollapsedIcon() {
      return XDMIconMap.getIcon("COLLAPSE_ICON");
   }
}
