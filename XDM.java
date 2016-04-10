import org.sdg.xdman.gui.XDMMainWindow;

public class XDM {
   public static void main(String[] args) {
      System.setProperty("awt.useSystemAAFontSettings", "lcd");
      System.setProperty("swing.aatext", "true");
      System.setProperty("sun.java2d.xrender", "false");
      XDMMainWindow.main(args);
   }
}
