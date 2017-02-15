package com.gnepux.droidserver;

import java.io.IOException;

/**
 * Created by xupeng on 17/2/14.
 */

public interface IResourceUriHandler {

    boolean accept(String uri);

    void handler(String uri, HttpContext httpContext) throws IOException;

}
