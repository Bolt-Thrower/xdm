package org.sdg.xdman.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import org.sdg.xdman.core.common.Assembler;
import org.sdg.xdman.gui.StringResource;
import org.sdg.xdman.gui.XDMIconMap;

public class AssembleDialog extends JDialog implements Runnable {
   private static final long serialVersionUID = -2063101566749868919L;
   JProgressBar prg;
   JButton btn;
   JLabel lbl;
   boolean stop = false;
   String filename;
   String destdir;
   String tmpdir;
   Thread t;

   private void init() {
      this.setModal(true);
      this.setTitle(StringResource.getString("ASM_TTL"));
      this.setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
      this.setSize(300, 150);
      Box box = Box.createVerticalBox();
      box.setBorder(new EmptyBorder(6, 6, 6, 6));
      Box hbox1 = Box.createHorizontalBox();
      this.lbl = new JLabel(StringResource.getString("ASM_LBL"), 2);
      hbox1.add(this.lbl);
      hbox1.add(Box.createHorizontalGlue());
      this.prg = new JProgressBar();
      this.prg.setIndeterminate(true);
      this.btn = new JButton(StringResource.getString("CANCEL"));
      this.btn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            AssembleDialog.this.stop();
         }
      });
      box.add(Box.createVerticalStrut(10));
      box.add(hbox1);
      box.add(Box.createVerticalGlue());
      box.add(this.prg);
      box.add(Box.createVerticalGlue());
      Box hbox = Box.createHorizontalBox();
      hbox.add(Box.createHorizontalGlue());
      box.add(Box.createVerticalStrut(10));
      hbox.add(this.btn);
      box.add(hbox);
      this.add(box);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            AssembleDialog.this.stop();
         }
      });
   }

   public AssembleDialog(JFrame f) {
      super(f);
      this.init();
   }

   public static void main(String[] args) {
      (new AssembleDialog((JFrame)null)).setVisible(true);
   }

   void startAssemble(String filename, String destdir, String tmpdir) {
      Assembler.stop = false;
      this.destdir = destdir;
      this.tmpdir = tmpdir;
      this.filename = filename;
      this.t = new Thread(this);
      this.t.start();
      this.setLocationRelativeTo((Component)null);
      this.setVisible(true);
   }

   public void run() {
      if(!Assembler.forceAssemble(this.tmpdir, this.destdir, this.filename) && !Assembler.stop) {
         JOptionPane.showMessageDialog(this, StringResource.getString("ASM_FL"));
      }

      this.setVisible(false);
   }

   void stop() {
      Assembler.stop = true;
      if(this.t != null) {
         try {
            this.t.join();
         } catch (InterruptedException var2) {
            ;
         }
      }

      this.setVisible(false);
   }
}
