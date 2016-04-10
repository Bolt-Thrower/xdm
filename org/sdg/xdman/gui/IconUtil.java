package org.sdg.xdman.gui;

import java.util.HashMap;
import javax.swing.Icon;
import org.sdg.xdman.core.common.IXDMConstants;
import org.sdg.xdman.gui.XDMIconMap;

public class IconUtil implements IXDMConstants {
   static HashMap icon = null;

   static Icon getIcon(String cat) {
      if(icon == null) {
         icon = new HashMap();
         icon.put("Documents", "DOC");
         icon.put("Video", "VID");
         icon.put("Programs", "APP");
         icon.put("Compressed", "ZIP");
         icon.put("Music", "MUSIC");
         icon.put("Other", "OTHER");
      }

      return XDMIconMap.getIcon((String)icon.get(cat));
   }
}
