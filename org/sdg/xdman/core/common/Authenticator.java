package org.sdg.xdman.core.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Observable;
import org.sdg.xdman.core.common.Credential;

public class Authenticator extends Observable implements Serializable {
   private static final long serialVersionUID = -7089181343138243013L;
   public static HashMap auth = new HashMap();
   static Authenticator me;
   File file;

   public void load(File file) {
      me.file = file;
      ObjectInputStream in = null;

      try {
         in = new ObjectInputStream(new FileInputStream(file));
         auth = (HashMap)in.readObject();
         System.out.println(auth);
      } catch (Exception var12) {
         var12.printStackTrace();
      } finally {
         try {
            in.close();
         } catch (Exception var11) {
            ;
         }

      }

      me = new Authenticator();
      me.file = file;
   }

   public void save() {
      ObjectOutputStream out = null;

      try {
         out = new ObjectOutputStream(new FileOutputStream(me.file));
         out.writeObject(auth);
         System.out.println(auth);
      } catch (Exception var11) {
         var11.printStackTrace();
      } finally {
         try {
            out.close();
         } catch (Exception var10) {
            ;
         }

      }

   }

   public static Authenticator getInstance() {
      if(me == null) {
         me = new Authenticator();
      }

      return me;
   }

   public Credential getCredential(String host) {
      return (Credential)auth.get(host);
   }

   public void addCreditential(Credential c) {
      auth.put(c.host, c);
      this.setChanged();
      this.notifyObservers();
   }

   public void removeCreditential(String host) {
      auth.remove(host);
      this.setChanged();
      this.notifyObservers();
   }
}
