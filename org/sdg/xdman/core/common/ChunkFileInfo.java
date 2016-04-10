package org.sdg.xdman.core.common;

import java.io.Serializable;
import java.util.Comparator;

class ChunkFileInfo implements Comparator, Serializable {
   private static final long serialVersionUID = -4766221633164988384L;
   String file;
   long start;
   long len;

   ChunkFileInfo() {
   }

   ChunkFileInfo(String f, long s, long l) {
      this.file = f;
      this.start = s;
      this.len = l;
   }

   public int compare(Object o1, Object o2) {
      ChunkFileInfo cf1 = (ChunkFileInfo)o1;
      ChunkFileInfo cf2 = (ChunkFileInfo)o2;
      return cf1.start == cf2.start?0:(cf1.start > cf2.start?1:-1);
   }
}
