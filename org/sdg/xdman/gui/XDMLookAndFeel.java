package org.sdg.xdman.gui;

import java.awt.Color;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import org.sdg.xdman.gui.XDMButtonUI;
import org.sdg.xdman.gui.XDMComboBoxUI;
import org.sdg.xdman.gui.XDMMenuItemUI;
import org.sdg.xdman.gui.XDMMenuUI;
import org.sdg.xdman.gui.XDMProgressBarUI;
import org.sdg.xdman.gui.XDMScrollBarUI;
import org.sdg.xdman.gui.XDMSpinnerUI;
import org.sdg.xdman.gui.XDMTheme;
import org.sdg.xdman.gui.XDMTreeUI;

public class XDMLookAndFeel extends MetalLookAndFeel {
   private static final long serialVersionUID = 6437510613485554397L;

   public XDMLookAndFeel() {
      setCurrentTheme(new XDMTheme());
   }

   public void initClassDefaults(UIDefaults table) {
      super.initClassDefaults(table);
      table.putDefaults(new Object[]{"ButtonUI", XDMButtonUI.class.getName(), "ScrollBarUI", XDMScrollBarUI.class.getName(), "MenuItemUI", XDMMenuItemUI.class.getName(), "MenuUI", XDMMenuUI.class.getName(), "CheckBoxMenuItemUI", XDMMenuItemUI.class.getName(), "TreeUI", XDMTreeUI.class.getName(), "SpinnerUI", XDMSpinnerUI.class.getName(), "ProgressBarUI", XDMProgressBarUI.class.getName(), "ComboBoxUI", XDMComboBoxUI.class.getName()});
      System.setProperty("xdm.defaulttheme", "true");
      UIManager.put("TabbedPane.selected", new Color(220, 220, 220));
      UIManager.put("TabbedPane.borderHightlightColor", Color.LIGHT_GRAY);
      UIManager.put("TabbedPane.contentAreaColor", Color.LIGHT_GRAY);
      UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);
      UIManager.put("Table.focusCellHighlightBorder", new EmptyBorder(1, 1, 1, 1));
      UIManager.put("OptionPane.background", new ColorUIResource(Color.WHITE));
      UIManager.put("Panel.background", new ColorUIResource(Color.WHITE));
      UIManager.put("CheckBox.background", new ColorUIResource(Color.WHITE));
   }

   protected void initComponentDefaults(UIDefaults table) {
      super.initComponentDefaults(table);
      table.putDefaults(new Object[]{"ComboBox.selectionBackground", Color.LIGHT_GRAY});
   }

   public String getName() {
      return "Default";
   }

   public String getID() {
      return "Default";
   }

   public String getDescription() {
      return "Default theme for XDM";
   }

   public boolean isNativeLookAndFeel() {
      return false;
   }

   public boolean isSupportedLookAndFeel() {
      return true;
   }
}
