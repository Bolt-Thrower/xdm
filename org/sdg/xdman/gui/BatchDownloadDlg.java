package org.sdg.xdman.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.gui.BatchItem;
import org.sdg.xdman.gui.BatchTableModel;
import org.sdg.xdman.gui.StaticResource;
import org.sdg.xdman.gui.StringResource;
import org.sdg.xdman.gui.XDMFileChooser;
import org.sdg.xdman.gui.XDMFrame;
import org.sdg.xdman.gui.XDMIconMap;
import org.sdg.xdman.gui.XDMTableHeaderRenderer;
import org.sdg.xdman.util.XDMUtil;

public class BatchDownloadDlg extends XDMFrame implements ActionListener {
   private static final long serialVersionUID = -4717157093775786226L;
   BatchTableModel model;
   JTable table;
   JTextField dir;
   JButton browse;
   JButton ok;
   JButton cancel;
   JButton checkAll;
   JButton uncheckAll;
   JCheckBox startQ;
   DownloadStateListner listener;

   public BatchDownloadDlg() {
      this.setSize(600, 300);
      this.setTitle(StringResource.getString("LIST_TTL"));
      JLabel titleLbl = new JLabel(StringResource.getString("LIST_TTL"));
      titleLbl.setForeground(Color.WHITE);
      titleLbl.setFont(StaticResource.plainFontBig2);
      titleLbl.setBorder(new EmptyBorder(10, 10, 10, 10));
      this.getTitlePanel().add(titleLbl);
      this.setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
      JPanel panel = new JPanel(new BorderLayout());
      panel.add(new JLabel(StringResource.getString("LIST_LBL")), "North");
      this.dir = new JTextField();
      this.browse = new JButton("...");
      this.browse.addActionListener(this);
      this.ok = new JButton(StringResource.getString("MSG_BOX_OK"));
      this.ok.addActionListener(this);
      this.cancel = new JButton(StringResource.getString("CANCEL"));
      this.cancel.addActionListener(this);
      this.checkAll = new JButton(StringResource.getString("LIST_LBL1"));
      this.checkAll.addActionListener(this);
      this.uncheckAll = new JButton(StringResource.getString("LIST_LBL2"));
      this.uncheckAll.addActionListener(this);
      this.startQ = new JCheckBox(StringResource.getString("LIST_LBL3"));
      this.startQ.addActionListener(this);
      this.model = new BatchTableModel();
      this.table = new JTable(this.model);
      this.table.setAutoResizeMode(0);
      this.table.setShowGrid(false);
      if(System.getProperty("xdm.defaulttheme") != null) {
         this.table.getTableHeader().setDefaultRenderer(new XDMTableHeaderRenderer());
      }

      this.table.setFillsViewportHeight(true);
      TableColumnModel cm = this.table.getColumnModel();

      for(int jsp = 0; jsp < cm.getColumnCount(); ++jsp) {
         TableColumn box = cm.getColumn(jsp);
         if(box.getHeaderValue().equals("#")) {
            box.setPreferredWidth(20);
         } else {
            box.setPreferredWidth(200);
         }
      }

      JScrollPane var8 = new JScrollPane(this.table);
      this.table.setFillsViewportHeight(true);
      var8.getViewport().setBackground(Color.WHITE);
      panel.add(var8);
      this.table.addMouseListener(new MouseAdapter() {
         public void mouseReleased(MouseEvent e) {
            int index = BatchDownloadDlg.this.table.getSelectedRow();
            if(index >= 0) {
               int c = BatchDownloadDlg.this.table.getSelectedColumn();
               if(c == 0) {
                  BatchItem item = (BatchItem)BatchDownloadDlg.this.model.batchList.get(index);
                  item.selected = !item.selected;
                  BatchDownloadDlg.this.model.fireTableRowsUpdated(index, index);
               }
            }
         }
      });
      Box var9 = Box.createVerticalBox();
      Box b1 = Box.createHorizontalBox();
      b1.add(new JLabel(StringResource.getString("SAVE_IN")));
      b1.add(Box.createRigidArea(new Dimension(5, 5)));
      this.dir.setMaximumSize(new Dimension(this.dir.getMaximumSize().width, this.dir.getPreferredSize().height));
      b1.add(this.dir);
      b1.add(Box.createRigidArea(new Dimension(5, 5)));
      b1.add(this.browse);
      var9.add(b1);
      b1.setBorder(new EmptyBorder(5, 5, 5, 5));
      Box b2 = Box.createHorizontalBox();
      b2.add(this.checkAll);
      this.checkAll.setPreferredSize(this.uncheckAll.getPreferredSize());
      b2.add(Box.createRigidArea(new Dimension(5, 5)));
      b2.add(this.uncheckAll);
      b2.add(Box.createRigidArea(new Dimension(5, 5)));
      b2.add(this.startQ);
      b2.add(Box.createHorizontalGlue());
      b2.add(this.ok);
      this.ok.setPreferredSize(this.cancel.getPreferredSize());
      b2.add(Box.createRigidArea(new Dimension(5, 5)));
      b2.add(this.cancel);
      var9.add(b2);
      panel.add(var9, "South");
      this.add(panel);
      panel.setBorder(new EmptyBorder(10, 10, 10, 10));
   }

   public void showDialog(List list, String folder, DownloadStateListner listener) {
      try {
         this.listener = listener;
         this.model.batchList.clear();
         this.dir.setText(folder);

         for(int e = 0; e < list.size(); ++e) {
            this.model.batchList.add((BatchItem)list.get(e));
         }

         this.model.fireTableDataChanged();
         this.setVisible(true);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public void showDialog(String folder, DownloadStateListner listener) {
      this.listener = listener;
      this.model.batchList.clear();
      this.dir.setText(folder);

      try {
         Object e = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
         String txt = "";
         int count = 0;
         if(e != null) {
            txt = e.toString();
            if(txt.length() > 0) {
               String[] urls = txt.split("\n");

               for(int i = 0; i < urls.length; ++i) {
                  BatchItem item = new BatchItem();
                  String url = urls[i];
                  if(XDMUtil.validateURL(url)) {
                     ++count;
                     item.url = url;
                     item.dir = this.dir.getText();
                     item.fileName = XDMUtil.getFileName(url);
                     this.model.batchList.add(item);
                  }
               }
            }

            if(count < 1) {
               JOptionPane.showMessageDialog(this, StringResource.getString("LIST_MSG"));
            } else {
               this.model.fireTableDataChanged();
               this.setVisible(true);
            }
         } else {
            JOptionPane.showMessageDialog(this, StringResource.getString("LIST_MSG"));
         }
      } catch (Exception var10) {
         JOptionPane.showMessageDialog(this, StringResource.getString("LIST_MSG"));
      }
   }

   public void actionPerformed(ActionEvent e) {
      if(e.getSource() == this.browse) {
         JFileChooser i = XDMFileChooser.getFileChooser(1, (File)null);
         if(i.showSaveDialog(this) == 0) {
            this.dir.setText(i.getSelectedFile().getAbsolutePath());

            for(int item = 0; item < this.model.batchList.size(); ++item) {
               ((BatchItem)this.model.batchList.get(item)).dir = this.dir.getText();
            }

            this.model.fireTableDataChanged();
         }
      }

      int var4;
      if(e.getSource() == this.checkAll) {
         for(var4 = 0; var4 < this.model.batchList.size(); ++var4) {
            ((BatchItem)this.model.batchList.get(var4)).selected = true;
         }

         this.model.fireTableDataChanged();
      }

      if(e.getSource() == this.uncheckAll) {
         for(var4 = 0; var4 < this.model.batchList.size(); ++var4) {
            ((BatchItem)this.model.batchList.get(var4)).selected = false;
         }

         this.model.fireTableDataChanged();
      }

      if(e.getSource() == this.cancel) {
         this.setVisible(false);
      }

      if(e.getSource() == this.ok) {
         for(var4 = 0; var4 < this.model.batchList.size(); ++var4) {
            BatchItem var5 = (BatchItem)this.model.batchList.get(var4);
            if(var5.selected) {
               this.listener.add2Queue(var5.url, var5.fileName, var5.dir, var5.user, var5.pass, (String)null, (ArrayList)null, (String)null, this.startQ.isSelected());
            }
         }

         this.listener.startQueue();
         this.setVisible(false);
      }

   }

   public static void main(String[] args) {
      (new BatchDownloadDlg()).showDialog(System.getProperty("user.home"), (DownloadStateListner)null);
   }
}
