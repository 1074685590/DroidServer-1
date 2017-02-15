package com.gnepux.droidserver;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Created by xupeng on 17/2/14.
 */

public class ResourceInAssetHandler implements IResourceUriHandler {

    private String acceptPrefix = "/static/";

    private Context context;

    public ResourceInAssetHandler(Context context) {
        this.context = context;
    }

    @Override
    public boolean accept(String uri) {
        return uri.startsWith(acceptPrefix);
    }

    @Override
    public void handler(String uri, HttpContext httpContext) throws IOException {

        int startIndex = acceptPrefix.length();
        String assetsPath = uri.substring(startIndex);
        InputStream fis = context.getAssets().open(assetsPath);
        byte[] raw = StreamTookit.readRawFromStream(fis);
        fis.close();

        OutputStream nos = httpContext.getUnderlySocket().getOutputStream();
        PrintStream printer = new PrintStream(nos);
        printer.println("HTTP/1.1 200 OK");
        printer.println("Content-Length:" + raw.length);
        if (assetsPath.endsWith(".html") || assetsPath.endsWith(".htm")) {
            printer.println("Content-Type:text/html");
        } else if (assetsPath.endsWith(".js")) {
            printer.println("Content-Type:text/js");
        } else if (assetsPath.endsWith(".css")) {
            printer.println("Content-Type:text/css");
        } else if (assetsPath.endsWith(".jpg")) {
            printer.println("Content-Type:text/jpg");
        } else if (assetsPath.endsWith(".png")) {
            printer.println("Content-Type:text/png");
        }
        printer.println();
//        printer.println(raw);
        printer.write(raw);
        printer.flush();
        printer.close();
    }
}
