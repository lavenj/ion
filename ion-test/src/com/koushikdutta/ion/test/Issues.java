package com.koushikdutta.ion.test;

import android.graphics.Bitmap;
import android.test.AndroidTestCase;
import android.util.Log;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.koushikdutta.ion.Ion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by koush on 10/27/13.
 */
public class Issues extends AndroidTestCase {
    public void testIssue74() throws Exception {
        String data = Ion.with(getContext(), "https://raw.github.com/koush/AndroidAsync/master/AndroidAsyncTest/testdata/test.json")
        .setLogging("MyLogs", Log.VERBOSE)
        .asString().get();

        String data2 = Ion.with(getContext(), "https://raw.github.com/koush/AndroidAsync/master/AndroidAsyncTest/testdata/test.json")
        .setLogging("MyLogs", Log.VERBOSE)
        .asString().get();

        assertEquals(data, data2);
    }

    public void testIssue126() throws Exception {
        Bitmap bitmap = Ion.with(getContext())
        .load("http://bdc.tsingyuan.cn/api/img?w=advanced")
        .setLogging("Issue126", Log.VERBOSE)
        .asBitmap()
        .get();

        assertNotNull(bitmap);
        assertTrue(bitmap.getWidth() > 0);
    }

    public void testIssue146() throws Exception {
        AsyncHttpServer httpServer = new AsyncHttpServer();
        httpServer.get("/", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                response.getHeaders().getHeaders().set("Cache-Control", "max-age=300");
                response.send(request.getQuery().size() + "");
            }
        });
        AsyncServer asyncServer = new AsyncServer();
        try {
            int localPort = httpServer.listen(asyncServer, 0).getLocalPort();
            String s1 = Ion.with(getContext())
            .load("http://localhost:" + localPort)
            .addQuery("query1", "q")
            .asString()
            .get();

            String s2 = Ion.with(getContext())
            .load("http://localhost:" + localPort)
            .addQuery("query1", "q")
            .addQuery("query2", "qq")
            .asString()
            .get();

            String s3 = Ion.with(getContext())
            .load("http://localhost:" + localPort)
            .addQuery("query1", "q")
            .asString()
            .get();

            assertEquals(s1, "1");
            assertEquals(s2, "2");
            assertEquals(s3, "1");
        }
        finally {
            asyncServer.stop();
        }
    }

    public void testIssue200() throws Exception {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("email", Arrays.asList("mail@mail.pl"));
        params.put("password", Arrays.asList("pass"));

        String val = Ion.with(getContext())
        .load("https://koush.clockworkmod.com/test/echo")
        .setLogging("Issue200", Log.VERBOSE)
        .setBodyParameters(params)
        .asString()
        .get(2000, TimeUnit.MILLISECONDS);

        System.out.println(val);
    }
}
