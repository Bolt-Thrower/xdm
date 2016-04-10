package org.sdg.xdman.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.sdg.xdman.gui.HelpListModel;
import org.sdg.xdman.gui.StringResource;
import org.sdg.xdman.gui.XDMIconMap;
import org.sdg.xdman.gui.XDMToolBarButtonUI;

public class HelpDialog extends JFrame implements ListSelectionListener, ActionListener {
   private static final long serialVersionUID = 2861769092407816472L;
   JButton back;
   JButton next;
   JEditorPane htmlPane;
   HelpListModel model;
   JList helpList;
   private static HelpDialog dlg;
   HashMap map = new HashMap();
   String[] keys = new String[]{"BROWSER_INTEGRATION", "CAPTURE_VIDEO", "REFRESH_LINK"};
   String[] values = new String[]{"Browser integration", "How to save videos", "Refresh broken download"};

   public static HelpDialog getHelpDialog() {
      if(dlg == null) {
         dlg = new HelpDialog();
      }

      return dlg;
   }

   public HelpDialog() {
      this.setTitle(StringResource.getString("DEFAULT_TITLE"));
      this.setSize(640, 480);
      this.htmlPane = new JEditorPane();
      JPanel panel = new JPanel(new BorderLayout(5, 5));
      panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      panel.add(new JScrollPane(this.htmlPane));
      this.add(panel);
      this.model = new HelpListModel();
      this.helpList = new JList(this.values);
      JPanel p = new JPanel(new BorderLayout());
      p.add(this.createToolBar(), "North");
      p.add(new JScrollPane(this.helpList));
      panel.add(p, "West");
      this.setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
      this.helpList.addListSelectionListener(this);
      this.htmlPane.setEditable(false);
      this.map.put(this.keys[0], this.getClass().getResource("/help/browser_integration.html"));
      this.map.put(this.keys[1], this.getClass().getResource("/help/video_download.html"));
      this.map.put(this.keys[2], this.getClass().getResource("/help/refresh_link.html"));
   }

   JToolBar createToolBar() {
      JToolBar toolbar = new JToolBar();
      this.next = new JButton(XDMIconMap.getIcon("NEXT_ICON"));
      XDMToolBarButtonUI btnUI = new XDMToolBarButtonUI();
      this.next.setUI(btnUI);
      this.next.addActionListener(this);
      this.next.setRolloverIcon(XDMIconMap.getIcon("NEXT_R_ICON"));
      this.next.setContentAreaFilled(false);
      this.next.setFocusPainted(false);
      this.back = new JButton(XDMIconMap.getIcon("BACK_ICON"));
      this.back.setUI(btnUI);
      this.back.setContentAreaFilled(false);
      this.back.setFocusPainted(false);
      this.back.addActionListener(this);
      this.back.setRolloverIcon(XDMIconMap.getIcon("BACK_R_ICON"));
      toolbar.add(this.back);
      toolbar.add(this.next);
      return toolbar;
   }

   public void valueChanged(ListSelectionEvent e) {
      int index = this.helpList.getSelectedIndex();
      if(index >= 0) {
         String key = this.keys[index];
         this.setDocument(key);
      }
   }

   public void setDocument(String page) {
      try {
         this.setPage(page);
      } catch (Exception var3) {
         ;
      }

   }

   void setPage(String key) throws IOException {
      this.htmlPane.setPage((URL)this.map.get(key));
   }

   public void addPages(HashMap map) {
      this.model.map = map;
      this.helpList.setModel(this.model);
   }

   public void actionPerformed(ActionEvent e) {
      int index;
      if(e.getSource() == this.back) {
         index = this.helpList.getSelectedIndex();
         if(index <= 0) {
            index = 0;
         } else {
            --index;
         }

         this.helpList.setSelectedIndex(index);
      }

      if(e.getSource() == this.next) {
         index = this.helpList.getSelectedIndex();
         if(index < 0) {
            index = 0;
         } else if(index < this.keys.length - 1) {
            ++index;
         }

         this.helpList.setSelectedIndex(index);
      }

   }
}
