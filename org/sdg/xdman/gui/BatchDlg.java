package org.sdg.xdman.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.sdg.xdman.gui.BatchDownloadListener;
import org.sdg.xdman.gui.StaticResource;
import org.sdg.xdman.gui.StringResource;
import org.sdg.xdman.gui.XDMFrame;
import org.sdg.xdman.gui.XDMIconMap;

public class BatchDlg extends XDMFrame implements ActionListener, DocumentListener, ChangeListener {
   private static final long serialVersionUID = 7844880366020855193L;
   JTextField url;
   JTextField user;
   JTextField pass;
   JTextField first;
   JTextField second;
   JTextField last;
   ArrayList urls;
   JRadioButton num;
   JRadioButton letter;
   JSpinner low;
   JSpinner high;
   JSpinner range;
   JButton ok;
   JButton cancel;
   SpinnerModel lowModelN;
   SpinnerModel highModelN;
   SpinnerModel lowModelA;
   SpinnerModel highModelA;
   BatchDownloadListener listener;

   public BatchDlg(BatchDownloadListener listener) {
      JPanel panel = new JPanel(new BorderLayout());
      this.listener = listener;
      String label_txt = StringResource.getString("BATCH_LBL1");
      JLabel titleLbl = new JLabel(StringResource.getString("BATCH_TTL"));
      titleLbl.setForeground(Color.WHITE);
      titleLbl.setFont(StaticResource.plainFontBig2);
      titleLbl.setBorder(new EmptyBorder(10, 10, 10, 10));
      this.getTitlePanel().add(titleLbl);
      this.setSize(500, 400);
      this.setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
      this.setTitle(StringResource.getString("BATCH_TTL"));
      JTextArea label = new JTextArea(label_txt);
      label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      label.setWrapStyleWord(true);
      label.setLineWrap(true);
      label.setEditable(false);
      label.setOpaque(false);
      panel.add(label, "North");
      this.url = new JTextField();
      this.url.getDocument().addDocumentListener(this);
      this.user = new JTextField(8);
      this.pass = new JPasswordField(8);
      this.first = new JTextField();
      this.first.setEditable(false);
      this.second = new JTextField();
      this.second.setEditable(false);
      this.last = new JTextField();
      this.last.setEditable(false);
      this.num = new JRadioButton(StringResource.getString("BATCH_NUM"), true);
      this.num.addActionListener(this);
      this.letter = new JRadioButton(StringResource.getString("BATCH_LTT"));
      this.letter.addActionListener(this);
      this.lowModelN = new SpinnerNumberModel(0, 0, 9999, 1);
      this.low = new JSpinner(this.lowModelN);
      this.low.addChangeListener(this);
      this.highModelN = new SpinnerNumberModel(50, 0, 9999, 1);
      this.high = new JSpinner(this.highModelN);
      this.high.addChangeListener(this);
      String[] alphas = this.getAlpha();
      this.lowModelA = new SpinnerListModel(Arrays.asList(alphas));
      this.highModelA = new SpinnerListModel(Arrays.asList(alphas));
      this.range = new JSpinner(new SpinnerNumberModel(2, 0, 10, 1));
      this.range.addChangeListener(this);
      this.ok = new JButton(StringResource.getString("MSG_BOX_OK"));
      this.ok.addActionListener(this);
      this.cancel = new JButton(StringResource.getString("CANCEL"));
      this.cancel.addActionListener(this);
      ButtonGroup bg = new ButtonGroup();
      bg.add(this.letter);
      bg.add(this.num);
      JPanel p = new JPanel(new GridBagLayout());
      GridBagConstraints gc = new GridBagConstraints();
      gc.insets = new Insets(5, 5, 5, 5);
      gc.fill = 2;
      gc.gridwidth = 1;
      p.add(new JLabel(StringResource.getString("BATCH_ADDR")), gc);
      gc.gridwidth = 5;
      gc.weightx = 1.0D;
      gc.gridx = 1;
      p.add(this.url, gc);
      gc.gridy = 1;
      gc.gridx = 0;
      gc.gridwidth = 4;
      p.add(new JLabel(StringResource.getString("BATCH_REPLC"), 4), gc);
      gc.gridx = 4;
      gc.gridwidth = 1;
      p.add(this.num, gc);
      gc.gridx = 5;
      p.add(this.letter, gc);
      gc.gridy = 2;
      gc.gridx = 0;
      gc.gridwidth = 6;
      gc.gridx = 0;
      gc.gridwidth = 1;
      p.add(new JLabel(StringResource.getString("BATCH_FROM"), 4), gc);
      gc.gridx = 1;
      p.add(this.low, gc);
      gc.gridx = 2;
      p.add(new JLabel(StringResource.getString("BATCH_TO"), 4), gc);
      gc.gridx = 3;
      p.add(this.high, gc);
      gc.gridx = 4;
      p.add(new JLabel(StringResource.getString("BATCH_WILD"), 4), gc);
      gc.gridx = 5;
      p.add(this.range, gc);
      gc.gridy = 3;
      gc.gridwidth = 1;
      gc.weightx = 1.0D;
      gc.gridx = 0;
      p.add(new JLabel(StringResource.getString("USER_NAME"), 4), gc);
      gc.gridx = 1;
      p.add(this.user, gc);
      gc.gridx = 2;
      p.add(new JLabel(StringResource.getString("PASSWORD"), 4), gc);
      gc.gridx = 3;
      p.add(this.pass, gc);
      gc.weightx = 0.0D;
      gc.gridx = 0;
      gc.gridwidth = 1;
      gc.gridy = 4;
      p.add(new JLabel(StringResource.getString("BATCH_URL1"), 0), gc);
      gc.gridx = 1;
      gc.gridwidth = 5;
      p.add(this.first, gc);
      gc.gridwidth = 1;
      gc.gridx = 0;
      gc.gridy = 5;
      p.add(new JLabel(StringResource.getString("BATCH_URL2"), 0), gc);
      gc.gridx = 1;
      gc.gridwidth = 5;
      p.add(this.second, gc);
      gc.gridwidth = 1;
      gc.gridx = 0;
      gc.gridy = 6;
      p.add(new JLabel(StringResource.getString("BATCH_URLN"), 0), gc);
      gc.gridx = 1;
      gc.gridwidth = 5;
      p.add(this.last, gc);
      Box b = Box.createHorizontalBox();
      b.add(Box.createHorizontalGlue());
      this.ok.setPreferredSize(this.cancel.getPreferredSize());
      b.add(this.ok);
      b.add(Box.createRigidArea(new Dimension(5, 5)));
      b.add(this.cancel);
      b.setBorder(new EmptyBorder(5, 5, 5, 5));
      panel.add(b, "South");
      panel.add(p);
      this.add(panel);
   }

   static ArrayList generateBatchURL(String base, boolean isNum, int width, int low, int high) {
      ArrayList list = new ArrayList();
      if(base != null && base.length() >= 1) {
         int index = base.indexOf(42);
         if(index < 0) {
            return list;
         } else {
            if(!isNum) {
               width = 1;
            }

            int l;
            int h;
            if(low > high) {
               l = high;
               h = low;
            } else {
               l = low;
               h = high;
            }

            int lw;
            if(isNum) {
               lw = String.valueOf(low).length();
            } else {
               lw = 1;
            }

            if(lw > width) {
               width = lw;
            }

            for(int i = l; i <= h; ++i) {
               StringBuffer buf = new StringBuffer();
               if(isNum) {
                  buf.append(String.valueOf(i));
               } else {
                  buf.append((char)i);
               }

               if(buf.length() < width) {
                  for(int string = 0; string < width - buf.length(); ++string) {
                     buf.insert(0, '0');
                  }
               }

               if(buf.length() > width) {
                  break;
               }

               String var15 = base.replace("*", buf.toString());

               try {
                  new URL(var15);
                  list.add(var15);
               } catch (Exception var14) {
                  ;
               }
            }

            return list;
         }
      } else {
         return list;
      }
   }

   public void actionPerformed(ActionEvent e) {
      if(e.getSource() == this.letter) {
         this.range.setEnabled(false);
         this.low.setModel(this.lowModelA);
         this.high.setModel(this.highModelA);
         this.high.setValue("Z");
         this.update(this.url.getText());
      }

      if(e.getSource() == this.num) {
         this.range.setEnabled(true);
         this.low.setModel(this.lowModelN);
         this.high.setModel(this.highModelN);
         this.update(this.url.getText());
      }

      if(e.getSource() == this.ok) {
         if(this.urls == null || this.urls.size() < 1) {
            JOptionPane.showMessageDialog(this, StringResource.getString("INVALID_URL"));
            return;
         }

         this.setVisible(false);
         if(this.listener != null) {
            this.listener.initBatchDownload(this.urls, this.user.getText(), this.pass.getText());
         }
      }

      if(e.getSource() == this.cancel) {
         this.setVisible(false);
      }

   }

   void update(DocumentEvent e) {
      try {
         Document ex = e.getDocument();
         int len = ex.getLength();
         String text = ex.getText(0, len);
         this.update(text);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   void update(String text) {
      try {
         int err;
         int l;
         int h;
         if(this.num.isSelected()) {
            err = Integer.parseInt(this.range.getValue().toString());
            l = Integer.parseInt(this.low.getValue().toString());
            h = Integer.parseInt(this.high.getValue().toString());
         } else {
            err = this.range.getValue().toString().charAt(0);
            l = this.low.getValue().toString().charAt(0);
            h = this.high.getValue().toString().charAt(0);
         }

         if(text.startsWith("http://") || text.startsWith("https://") || text.startsWith("ftp://")) {
            this.urls = generateBatchURL(text, this.num.isSelected(), err, l, h);
            this.first.setText("");
            this.second.setText("");
            this.last.setText("");
            if(this.urls.size() == 1) {
               this.first.setText((String)this.urls.get(0));
            } else if(this.urls.size() >= 2) {
               this.first.setText((String)this.urls.get(0));
               this.second.setText((String)this.urls.get(1));
            }

            int lindex = this.urls.size() - 1;
            if(lindex >= 0) {
               this.last.setText((String)this.urls.get(lindex));
            }
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }

   String[] getAlpha() {
      String[] arr = new String[52];
      int count = 0;

      int i;
      for(i = 65; i <= 90; ++i) {
         arr[count++] = String.valueOf((char)i);
      }

      for(i = 97; i <= 122; ++i) {
         arr[count++] = String.valueOf((char)i);
      }

      return arr;
   }

   public void changedUpdate(DocumentEvent e) {
      this.update(e);
   }

   public void insertUpdate(DocumentEvent e) {
      this.update(e);
   }

   public void removeUpdate(DocumentEvent e) {
      this.update(e);
   }

   public void stateChanged(ChangeEvent e) {
      this.update(this.url.getText());
   }
}
