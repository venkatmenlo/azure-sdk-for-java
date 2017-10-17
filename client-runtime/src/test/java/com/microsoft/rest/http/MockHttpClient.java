/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.rest.http;

import com.google.common.io.CharStreams;
import com.microsoft.rest.Base64Url;
import com.microsoft.rest.Base64UrlTests;
import com.microsoft.rest.HttpBinJSON;
import com.microsoft.rest.policy.RequestPolicy;
import rx.Single;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This HttpClient attempts to mimic the behavior of http://httpbin.org without ever making a network call.
 */
public class MockHttpClient extends HttpClient {
    private static final HttpHeaders responseHeaders = new HttpHeaders()
            .add("Date", "Fri, 13 Oct 2017 20:33:09 GMT")
            .add("Via", "1.1 vegur")
            .add("Connection", "keep-alive")
            .add("X-Processed-Time", "1.0")
            .add("Access-Control-Allow-Credentials", "true");

    public MockHttpClient() {}

    public MockHttpClient(List<? extends RequestPolicy.Factory> policyFactories) {
        super(policyFactories);
    }

    @Override
    protected Single<HttpResponse> sendRequestInternalAsync(HttpRequest request) {
        HttpResponse response = null;

        try {
            final URI requestUrl = new URI(request.url());
            final String requestHost = requestUrl.getHost();
            if (requestHost.equalsIgnoreCase("httpbin.org")) {
                final String requestPath = requestUrl.getPath();
                final String requestPathLower = requestPath.toLowerCase();
                if (requestPathLower.equals("/anything") || requestPathLower.startsWith("/anything/")) {
                    if ("HEAD".equals(request.httpMethod())) {
                        response = new MockHttpResponse(200, "");
                    } else {
                        final HttpBinJSON json = new HttpBinJSON();
                        json.url = request.url()
                                // This is just to mimic the behavior we've seen with httpbin.org.
                                .replace("%20", " ");
                        json.headers = toMap(request.headers());
                        response = new MockHttpResponse(200, json);
                    }
                }
                else if (requestPathLower.startsWith("/bytes/")) {
                    final String byteCountString = requestPath.substring("/bytes/".length());
                    final int byteCount = Integer.parseInt(byteCountString);
                    response = new MockHttpResponse(200, new byte[byteCount], responseHeaders);
                }
                else if (requestPathLower.startsWith("/base64urlbytes/")) {
                    final String byteCountString = requestPath.substring("/base64urlbytes/".length());
                    final int byteCount = Integer.parseInt(byteCountString);
                    final byte[] bytes = new byte[byteCount];
                    for (int i = 0; i < byteCount; ++i) {
                        bytes[i] = (byte)i;
                    }
                    final Base64Url base64EncodedBytes = Base64Url.encode(bytes);
                    response = new MockHttpResponse(200, base64EncodedBytes, responseHeaders);
                }
                else if (requestPathLower.equals("/base64urllistofbytes")) {
                    final List<String> base64EncodedBytesList = new ArrayList<>();
                    for (int i = 0; i < 3; ++i) {
                        final int byteCount = (i + 1) * 10;
                        final byte[] bytes = new byte[byteCount];
                        for (int j = 0; j < byteCount; ++j) {
                            bytes[j] = (byte)j;
                        }
                        final Base64Url base64UrlEncodedBytes = Base64Url.encode(bytes);
                        base64EncodedBytesList.add(base64UrlEncodedBytes.toString());
                    }
                    response = new MockHttpResponse(200, base64EncodedBytesList, responseHeaders);
                }
                else if (requestPathLower.equals("/base64urllistoflistofbytes")) {
                    final List<List<String>> result = new ArrayList<>();
                    for (int i = 0; i < 2; ++i) {
                        final List<String> innerList = new ArrayList<>();
                        for (int j = 0; j < (i + 1) * 2; ++j) {
                            final int byteCount = (j + 1) * 5;
                            final byte[] bytes = new byte[byteCount];
                            for (int k = 0; k < byteCount; ++k) {
                                bytes[k] = (byte)k;
                            }

                            final Base64Url base64UrlEncodedBytes = Base64Url.encode(bytes);
                            innerList.add(base64UrlEncodedBytes.toString());
                        }
                        result.add(innerList);
                    }
                    response = new MockHttpResponse(200, result, responseHeaders);
                }
                else if (requestPathLower.equals("/delete")) {
                    final HttpBinJSON json = new HttpBinJSON();
                    json.url = request.url();
                    json.data = bodyToString(request);
                    response = new MockHttpResponse(200, json);
                }
                else if (requestPathLower.equals("/get")) {
                    final HttpBinJSON json = new HttpBinJSON();
                    json.url = request.url();
                    json.headers = toMap(request.headers());
                    response = new MockHttpResponse(200, json);
                }
                else if (requestPathLower.equals("/patch")) {
                    final HttpBinJSON json = new HttpBinJSON();
                    json.url = request.url();
                    json.data = bodyToString(request);
                    response = new MockHttpResponse(200, json);
                }
                else if (requestPathLower.equals("/post")) {
                    final HttpBinJSON json = new HttpBinJSON();
                    json.url = request.url();
                    json.data = bodyToString(request);
                    response = new MockHttpResponse(200, json);
                }
                else if (requestPathLower.equals("/put")) {
                    final HttpBinJSON json = new HttpBinJSON();
                    json.url = request.url();
                    json.data = bodyToString(request);
                    response = new MockHttpResponse(200, json, responseHeaders);
                }
                else if (requestPathLower.startsWith("/status/")) {
                    final String statusCodeString = requestPathLower.substring("/status/".length());
                    final int statusCode = Integer.valueOf(statusCodeString);
                    response = new MockHttpResponse(statusCode);
                }
            }
        }
        catch (Exception ignored) {
        }

        return Single.just(response);
    }

    private static String bodyToString(HttpRequest request) throws IOException {
        String result = "";

        final HttpRequestBody body = request.body();
        if (body != null) {
            try (final InputStream bodyStream = body.createInputStream()) {
                result = CharStreams.toString(new InputStreamReader(bodyStream));
            }
        }

        return result;
    }

    private static Map<String, String> toMap(HttpHeaders headers) {
        final Map<String, String> result = new HashMap<>();
        for (final HttpHeader header : headers) {
            result.put(header.name(), header.value());
        }
        return result;
    }
}
