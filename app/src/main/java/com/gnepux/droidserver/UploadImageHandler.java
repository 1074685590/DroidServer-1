package com.gnepux.droidserver;

import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Created by xupeng on 17/2/14.
 */

public class UploadImageHandler implements IResourceUriHandler {

    private String acceptPrefix = "/upload_image/";

    private Context context;

    public UploadImageHandler(Context context) {
        this.context = context;
    }

    @Override
    public boolean accept(String uri) {
        return uri.startsWith(acceptPrefix);
    }

    @Override
    public void handler(String uri, HttpContext httpContext) throws IOException {
        try {
//            String tmpPath = "/mnt/sdcard/test_upload.jpg";
            String tmpPath = context.getCacheDir().getPath() + "/test_upload.jpg";
            String length = httpContext.getRequestHeaderValue("Content-Length");
            long totalLength = Long.parseLong(length);
            FileOutputStream fos = new FileOutputStream(tmpPath);
            InputStream nis = httpContext.getUnderlySocket().getInputStream();

            byte[] buffer = new byte[10240];
            int nReaded = 0;
            long nLeftLength = totalLength;
            while ((nLeftLength > 0  && (nReaded = nis.read(buffer)) > 0)) {
                fos.write(buffer, 0, nReaded);
                nLeftLength -= nReaded;
            }

            fos.close();

            onImageLoaded(tmpPath);

            OutputStream nos = httpContext.getUnderlySocket().getOutputStream();

            PrintStream printer = new PrintStream(nos);
            printer.println("HTTP/1.1 200 OK");
            printer.println();
            printer.flush();
//            printer.close();

        } catch (Exception e) {
            Log.e("xupeng", e.getMessage());
        }
    }

    protected void onImageLoaded(String path) {

    }
}
