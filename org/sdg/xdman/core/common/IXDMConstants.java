package org.sdg.xdman.core.common;

public interface IXDMConstants {
   int CONNECTING = 10;
   int DOWNLOADING = 20;
   int FAILED = 30;
   int STOPPED = 40;
   int COMPLETE = 50;
   int ASSEMBLING = 60;
   int REDIRECTING = 70;
   String COMPRESSED = "Compressed";
   String DOCUMENTS = "Documents";
   String MUSIC = "Music";
   String PROGRAMS = "Programs";
   String VIDEO = "Video";
   String OTHER = "Other";
   int CONNECT_ERR = 0;
   int CONTENT_ERR = 1;
   int SESSION_ERR = 2;
   int RESP_ERR = 3;
   int RESUME_ERR = 4;
   int UNKNOWN_ERR = 5;
   int AUTH_ERR = 6;
   int PROXY_AUTH_ERR = 7;
   String[] errMsg = new String[]{"CONNECT_ERR", "FILE_CHANGED", "SESSION_END", "RESP_ERR", "NO_RESUME", "DWN_ERR", "HTTP_AUTH_ERR", "PROXY_AUTH_FAILED"};
}
