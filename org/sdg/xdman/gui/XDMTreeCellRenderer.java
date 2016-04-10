package org.sdg.xdman.gui;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.sdg.xdman.gui.XDMIconMap;
import org.sdg.xdman.gui.XDMTreeNode;

public class XDMTreeCellRenderer extends DefaultTreeCellRenderer {
   private static final long serialVersionUID = -1014567003371655959L;

   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean leaf, boolean expanded, int row, boolean hasFocus) {
      super.getTreeCellRendererComponent(tree, value, selected, leaf, expanded, row, hasFocus);
      XDMTreeNode node = (XDMTreeNode)value;
      String id = node.id;
      if(id.equals("TREE_DOCUMENTS")) {
         this.setIcon(XDMIconMap.getIcon("DOC"));
      } else if(id.equals("TREE_MUSIC")) {
         this.setIcon(XDMIconMap.getIcon("MUSIC"));
      } else if(id.equals("TREE_PROGRAMS")) {
         this.setIcon(XDMIconMap.getIcon("APP"));
      } else if(id.equals("TREE_VIDEOS")) {
         this.setIcon(XDMIconMap.getIcon("VID"));
      } else if(id.equals("TREE_COMPRESSED")) {
         this.setIcon(XDMIconMap.getIcon("ZIP"));
      } else if(id.equals("TREE_DOCUMENTS")) {
         this.setIcon(XDMIconMap.getIcon("DOC"));
      } else {
         this.setIcon(XDMIconMap.getIcon("FOLDER"));
      }

      return this;
   }

   public XDMTreeCellRenderer() {
      this.setBorder(new EmptyBorder(2, 0, 2, 0));
   }
}
