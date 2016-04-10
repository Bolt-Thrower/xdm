package org.sdg.xdman.core.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import org.sdg.xdman.core.common.ChunkFileInfo;
import org.sdg.xdman.util.XDMUtil;

public class Assembler {
   public static boolean stop = false;

   private static void assemble(OutputStream out, ArrayList fileList) throws Exception {
      FileInputStream in = null;
      int count = 0;

      for(int i = 0; i < fileList.size(); ++i) {
         ChunkFileInfo info = (ChunkFileInfo)fileList.get(i);
         System.out.println("Reading..." + info.file);
         in = new FileInputStream(info.file);
         long rem = info.len;
         byte[] buf = new byte[65536];

         do {
            int x = (int)(rem > (long)buf.length?(long)buf.length:rem);
            int r = in.read(buf, 0, x);
            if(stop) {
               throw new InterruptedException();
            }

            if(r == -1) {
               break;
            }

            out.write(buf, 0, r);
            rem -= (long)r;
            count += r;
         } while(rem != 0L);

         in.close();
      }

      out.close();
   }

   public static synchronized boolean forceAssemble(String tempdir, String destdir, String filename) {
      stop = false;

      try {
         File e = new File(tempdir, ".state");
         ObjectInputStream in = new ObjectInputStream(new FileInputStream(e));
         in.readUTF();
         in.readObject();
         in.readUTF();
         in.readUTF();
         in.readUTF();
         in.readLong();
         in.readLong();
         int sz = in.readInt();
         ArrayList fileList = new ArrayList();

         for(int outDir = 0; outDir < sz; ++outDir) {
            fileList.add((ChunkFileInfo)in.readObject());
         }

         Collections.sort(fileList, new ChunkFileInfo());
         File var16 = new File(destdir);
         if(!var16.exists()) {
            var16.mkdirs();
         }

         String finalFileName = XDMUtil.getUniqueFileName(destdir, filename);
         File outFile = new File(destdir, finalFileName);
         FileOutputStream out = new FileOutputStream(outFile);

         try {
            assemble(out, fileList);
            return true;
         } catch (Exception var14) {
            try {
               out.close();
            } catch (Exception var13) {
               ;
            }

            outFile.delete();
            throw var14;
         }
      } catch (Exception var15) {
         var15.printStackTrace();
         return false;
      }
   }
}
