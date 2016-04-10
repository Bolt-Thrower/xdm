package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.OceanTheme;

public class XDMTheme extends OceanTheme {
   FontUIResource fontResource;
   Color gray;
   Color light_gray;
   Color lighter_gray;
   ColorUIResource pm1;
   ColorUIResource pm2;
   ColorUIResource pm3;
   ColorUIResource sc1;
   ColorUIResource sc2;
   ColorUIResource sc3;

   public XDMTheme() {
      this.gray = Color.GRAY;
      this.light_gray = Color.LIGHT_GRAY;
      this.lighter_gray = new Color(230, 230, 230);
      this.pm1 = new ColorUIResource(Color.GRAY);
      this.pm2 = new ColorUIResource(Color.WHITE);
      this.pm3 = new ColorUIResource(this.lighter_gray);
      this.sc1 = new ColorUIResource(Color.GRAY);
      this.sc2 = new ColorUIResource(this.lighter_gray);
      this.sc3 = new ColorUIResource(new Color(240, 240, 240));
      this.fontResource = new FontUIResource(new Font("Dialog", 0, 12));
   }

   public FontUIResource getControlTextFont() {
      return this.fontResource;
   }

   public FontUIResource getWindowTitleFont() {
      return this.fontResource;
   }

   public FontUIResource getUserTextFont() {
      return this.fontResource;
   }

   public FontUIResource getSystemTextFont() {
      return this.fontResource;
   }

   public FontUIResource getSubTextFont() {
      return this.fontResource;
   }

   public FontUIResource getMenuTextFont() {
      return this.fontResource;
   }

   protected ColorUIResource getPrimary1() {
      return this.pm1;
   }

   protected ColorUIResource getPrimary2() {
      return this.pm2;
   }

   protected ColorUIResource getPrimary3() {
      return this.pm3;
   }

   protected ColorUIResource getSecondary1() {
      return this.sc1;
   }

   protected ColorUIResource getSecondary2() {
      return this.sc2;
   }

   protected ColorUIResource getSecondary3() {
      return this.sc3;
   }

   public void addCustomEntriesToTable(UIDefaults table) {
      super.addCustomEntriesToTable(table);
      ColorUIResource dadada = new ColorUIResource(14342874);
      List buttonGradient = Arrays.asList(new Object[]{new Float(1.0F), new Float(0.0F), this.getWhite(), dadada, new ColorUIResource(dadada)});
      table.put("Button.gradient", buttonGradient);
      table.put("ScrollBar.gradient", buttonGradient);
      table.put("RadioButton.gradient", buttonGradient);
      table.put("RadioButtonMenuItem.gradient", buttonGradient);
   }
}
