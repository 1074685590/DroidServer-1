package com.gnepux.droidserver;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xupeng on 17/2/14.
 */

public class SimpleHttpServer {

    private static final String TAG = "xupeng";
    
    private final WebConfiguration webConfig;

    private final ExecutorService threadPool;

    private boolean isEnable;

    private ServerSocket socket;

    private Set<IResourceUriHandler> resourceHandler;

    public SimpleHttpServer(WebConfiguration webConfig) {
        this.webConfig = webConfig;
        threadPool = Executors.newCachedThreadPool();
        resourceHandler = new HashSet<>();
    }

    /**
     * 启动server(异步)
     */
    public void startAsync() {
        
        isEnable = true;
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                doProcSync();
            }
        }).start();
    }

    /**
     * 停止server(异步)
     */
    public void stopAsync() throws IOException {
        if (!isEnable) {
            return;
        }
        isEnable = false;
        socket.close();
        socket = null;
    }

    private void doProcSync() {
        InetSocketAddress socketAddr = new InetSocketAddress(webConfig.getPort());
        try {
            socket = new ServerSocket();
            socket.bind(socketAddr);
            while (isEnable) {
                final Socket remotePeer = socket.accept();
                threadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "a remote peer accepted..." + 
                                remotePeer.getRemoteSocketAddress().toString());
                        onAcceceptRemotePeer(remotePeer);
                    }
                });
            }
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }

    public void registerResourceHander(IResourceUriHandler handler) {
        resourceHandler.add(handler);
    }

    private void onAcceceptRemotePeer(Socket remotePeer) {
        try {
//            remotePeer.getOutputStream().write("连接上了".getBytes());
            HttpContext httpContext = new HttpContext();
            httpContext.setUnderlySocket(remotePeer);
            InputStream nis = remotePeer.getInputStream();
            String headLine = null;
            String resourceUri = headLine = StreamTookit.readLine(nis).split(" ")[1];
            Log.d(TAG, "resourceUri:" + resourceUri);
            while ((headLine = StreamTookit.readLine(nis)) != null) {
                if (headLine.equals("\r\n")) {
                    break;
                }
                String[] pair = headLine.split(":");
                httpContext.addRequestHeader(pair[0], pair[1].replace("\r\n", "").trim());
                Log.d(TAG, "headline = " + headLine);
            }

            for (IResourceUriHandler handler : resourceHandler) {
                if (!handler.accept(resourceUri)) {
                    continue;
                }
                handler.handler(resourceUri, httpContext);
            }

        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }
}
