package org.apache.commons.net;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketException;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import org.apache.commons.net.DefaultSocketFactory;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ProtocolCommandSupport;

public abstract class SocketClient {
   public static final String NETASCII_EOL = "\r\n";
   private static final SocketFactory __DEFAULT_SOCKET_FACTORY = SocketFactory.getDefault();
   private static final ServerSocketFactory __DEFAULT_SERVER_SOCKET_FACTORY = ServerSocketFactory.getDefault();
   private ProtocolCommandSupport __commandSupport;
   protected int _timeout_ = 0;
   protected Socket _socket_ = null;
   protected int _defaultPort_ = 0;
   protected InputStream _input_ = null;
   protected OutputStream _output_ = null;
   protected SocketFactory _socketFactory_;
   protected ServerSocketFactory _serverSocketFactory_;
   private static final int DEFAULT_CONNECT_TIMEOUT = 0;
   protected int connectTimeout = 0;
   private int receiveBufferSize = -1;
   private int sendBufferSize = -1;
   private Proxy connProxy;

   public SocketClient() {
      this._socketFactory_ = __DEFAULT_SOCKET_FACTORY;
      this._serverSocketFactory_ = __DEFAULT_SERVER_SOCKET_FACTORY;
   }

   protected void _connectAction_() throws IOException {
      this._socket_.setSoTimeout(this._timeout_);
      this._input_ = this._socket_.getInputStream();
      this._output_ = this._socket_.getOutputStream();
   }

   public void connect(InetAddress host, int port) throws SocketException, IOException {
      this._socket_ = this._socketFactory_.createSocket();
      if(this.receiveBufferSize != -1) {
         this._socket_.setReceiveBufferSize(this.receiveBufferSize);
      }

      if(this.sendBufferSize != -1) {
         this._socket_.setSendBufferSize(this.sendBufferSize);
      }

      this._socket_.connect(new InetSocketAddress(host, port), this.connectTimeout);
      this._connectAction_();
   }

   public void connect(String hostname, int port) throws SocketException, IOException {
      this.connect(InetAddress.getByName(hostname), port);
   }

   public void connect(InetAddress host, int port, InetAddress localAddr, int localPort) throws SocketException, IOException {
      this._socket_ = this._socketFactory_.createSocket();
      if(this.receiveBufferSize != -1) {
         this._socket_.setReceiveBufferSize(this.receiveBufferSize);
      }

      if(this.sendBufferSize != -1) {
         this._socket_.setSendBufferSize(this.sendBufferSize);
      }

      this._socket_.bind(new InetSocketAddress(localAddr, localPort));
      this._socket_.connect(new InetSocketAddress(host, port), this.connectTimeout);
      this._connectAction_();
   }

   public void connect(String hostname, int port, InetAddress localAddr, int localPort) throws SocketException, IOException {
      this.connect(InetAddress.getByName(hostname), port, localAddr, localPort);
   }

   public void connect(InetAddress host) throws SocketException, IOException {
      this.connect(host, this._defaultPort_);
   }

   public void connect(String hostname) throws SocketException, IOException {
      this.connect(hostname, this._defaultPort_);
   }

   public void disconnect() throws IOException {
      this.closeQuietly(this._socket_);
      this.closeQuietly((Closeable)this._input_);
      this.closeQuietly((Closeable)this._output_);
      this._socket_ = null;
      this._input_ = null;
      this._output_ = null;
   }

   private void closeQuietly(Socket socket) {
      if(socket != null) {
         try {
            socket.close();
         } catch (IOException var3) {
            ;
         }
      }

   }

   private void closeQuietly(Closeable close) {
      if(close != null) {
         try {
            close.close();
         } catch (IOException var3) {
            ;
         }
      }

   }

   public boolean isConnected() {
      return this._socket_ == null?false:this._socket_.isConnected();
   }

   public boolean isAvailable() {
      if(this.isConnected()) {
         try {
            if(this._socket_.getInetAddress() == null) {
               return false;
            } else if(this._socket_.getPort() == 0) {
               return false;
            } else if(this._socket_.getRemoteSocketAddress() == null) {
               return false;
            } else if(this._socket_.isClosed()) {
               return false;
            } else if(this._socket_.isInputShutdown()) {
               return false;
            } else if(this._socket_.isOutputShutdown()) {
               return false;
            } else {
               this._socket_.getInputStream();
               this._socket_.getOutputStream();
               return true;
            }
         } catch (IOException var2) {
            return false;
         }
      } else {
         return false;
      }
   }

   public void setDefaultPort(int port) {
      this._defaultPort_ = port;
   }

   public int getDefaultPort() {
      return this._defaultPort_;
   }

   public void setDefaultTimeout(int timeout) {
      this._timeout_ = timeout;
   }

   public int getDefaultTimeout() {
      return this._timeout_;
   }

   public void setSoTimeout(int timeout) throws SocketException {
      this._socket_.setSoTimeout(timeout);
   }

   public void setSendBufferSize(int size) throws SocketException {
      this.sendBufferSize = size;
   }

   protected int getSendBufferSize() {
      return this.sendBufferSize;
   }

   public void setReceiveBufferSize(int size) throws SocketException {
      this.receiveBufferSize = size;
   }

   protected int getReceiveBufferSize() {
      return this.receiveBufferSize;
   }

   public int getSoTimeout() throws SocketException {
      return this._socket_.getSoTimeout();
   }

   public void setTcpNoDelay(boolean on) throws SocketException {
      this._socket_.setTcpNoDelay(on);
   }

   public boolean getTcpNoDelay() throws SocketException {
      return this._socket_.getTcpNoDelay();
   }

   public void setKeepAlive(boolean keepAlive) throws SocketException {
      this._socket_.setKeepAlive(keepAlive);
   }

   public boolean getKeepAlive() throws SocketException {
      return this._socket_.getKeepAlive();
   }

   public void setSoLinger(boolean on, int val) throws SocketException {
      this._socket_.setSoLinger(on, val);
   }

   public int getSoLinger() throws SocketException {
      return this._socket_.getSoLinger();
   }

   public int getLocalPort() {
      return this._socket_.getLocalPort();
   }

   public InetAddress getLocalAddress() {
      return this._socket_.getLocalAddress();
   }

   public int getRemotePort() {
      return this._socket_.getPort();
   }

   public InetAddress getRemoteAddress() {
      return this._socket_.getInetAddress();
   }

   public boolean verifyRemote(Socket socket) {
      InetAddress host1 = socket.getInetAddress();
      InetAddress host2 = this.getRemoteAddress();
      return host1.equals(host2);
   }

   public void setSocketFactory(SocketFactory factory) {
      if(factory == null) {
         this._socketFactory_ = __DEFAULT_SOCKET_FACTORY;
      } else {
         this._socketFactory_ = factory;
      }

      this.connProxy = null;
   }

   public void setServerSocketFactory(ServerSocketFactory factory) {
      if(factory == null) {
         this._serverSocketFactory_ = __DEFAULT_SERVER_SOCKET_FACTORY;
      } else {
         this._serverSocketFactory_ = factory;
      }

   }

   public void setConnectTimeout(int connectTimeout) {
      this.connectTimeout = connectTimeout;
   }

   public int getConnectTimeout() {
      return this.connectTimeout;
   }

   public ServerSocketFactory getServerSocketFactory() {
      return this._serverSocketFactory_;
   }

   public void addProtocolCommandListener(ProtocolCommandListener listener) {
      this.getCommandSupport().addProtocolCommandListener(listener);
   }

   public void removeProtocolCommandListener(ProtocolCommandListener listener) {
      this.getCommandSupport().removeProtocolCommandListener(listener);
   }

   protected void fireReplyReceived(int replyCode, String reply) {
      if(this.getCommandSupport().getListenerCount() > 0) {
         this.getCommandSupport().fireReplyReceived(replyCode, reply);
      }

   }

   protected void fireCommandSent(String command, String message) {
      if(this.getCommandSupport().getListenerCount() > 0) {
         this.getCommandSupport().fireCommandSent(command, message);
      }

   }

   protected void createCommandSupport() {
      this.__commandSupport = new ProtocolCommandSupport(this);
   }

   protected ProtocolCommandSupport getCommandSupport() {
      return this.__commandSupport;
   }

   public void setProxy(Proxy proxy) {
      this.setSocketFactory(new DefaultSocketFactory(proxy));
      this.connProxy = proxy;
   }

   public Proxy getProxy() {
      return this.connProxy;
   }
}
