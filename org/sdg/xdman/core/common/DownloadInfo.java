package org.sdg.xdman.core.common;

import java.io.File;
import java.util.UUID;

public class DownloadInfo {
   public UUID id;
   public String url;
   public String file;
   public String status;
   public String length;
   public String downloaded;
   public String speed;
   public String eta;
   public String resume;
   public long[] startoff;
   public long[] len;
   public long[] dwn;
   public long rlen;
   public long rdwn;
   public int prg;
   public String[] stat;
   public String[] dwnld;
   public int state;
   public String msg;
   public String progress;
   public String category;
   public String tempdir;
   public File path;
}
