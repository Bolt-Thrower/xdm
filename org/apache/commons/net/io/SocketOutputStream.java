package org.apache.commons.net.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SocketOutputStream extends FilterOutputStream {
   private final Socket __socket;

   public SocketOutputStream(Socket socket, OutputStream stream) {
      super(stream);
      this.__socket = socket;
   }

   public void write(byte[] buffer, int offset, int length) throws IOException {
      this.out.write(buffer, offset, length);
   }

   public void close() throws IOException {
      super.close();
      this.__socket.close();
   }
}
