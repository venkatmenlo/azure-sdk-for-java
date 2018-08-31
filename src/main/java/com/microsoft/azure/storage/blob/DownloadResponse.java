/*
 * Copyright Microsoft Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microsoft.azure.storage.blob;

import com.microsoft.azure.storage.blob.models.BlobDownloadHeaders;
import com.microsoft.azure.storage.blob.models.ModifiedAccessConditions;
import com.microsoft.rest.v2.RestResponse;
import com.microsoft.rest.v2.http.HttpPipeline;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * {@code DownloadResponse} wraps the protocol-layer response from {@link BlobURL#download(BlobRange,
 * BlobAccessConditions, boolean)} to automatically retry failed reads from the body as appropriate. If the
 * download is interrupted, the {@code DownloadResponse} will make a request to resume the download from where it left
 * off, allowing the user to consume the data as one continuous stream, for any interruptions are hidden. The retry
 * behavior is defined by the options passed to the {@link #body(ReliableDownloadOptions)}. The download will also lock
 * on the blob's etag to ensure consistency.
 * <p>
 * Note that the retries performed as a part of this reader are composed with those of any retries in an {@link
 * HttpPipeline} used in conjunction with this reader. That is, if this object issues a request to resume a download,
 * an underlying pipeline may issue several retries as a part of that request. Furthermore, this reader only retries on
 * network errors; timeouts and unexpected status codes are not retried. Therefore, the behavior of this reader is
 * entirely independent of and in no way coupled to an {@link HttpPipeline}'s retry mechanism.
 */
public final class DownloadResponse {
    private final HTTPGetterInfo info;

    private final RestResponse<BlobDownloadHeaders, Flowable<ByteBuffer>> rawResponse;

    private final Function<HTTPGetterInfo, Single<DownloadResponse>> getter;

    public DownloadResponse(RestResponse<BlobDownloadHeaders, Flowable<ByteBuffer>> response,
            HTTPGetterInfo info, Function<HTTPGetterInfo, Single<DownloadResponse>> getter) {
        Utility.assertNotNull("getter", getter);
        Utility.assertNotNull("info", info);
        Utility.assertNotNull("info.eTag", info.eTag());
        this.rawResponse = response;
        this.info = info;
        this.getter = getter;
    }

    /**
     * Returns the response body which has been modified to enable reliably reading data if desired (if
     * {@code options.maxRetryRequests > 0}. If retries are enabled, if a connection fails while reading, the stream
     * will make additional requests to reestablish a connection and continue reading.
     *
     * @param options
     *         {@link ReliableDownloadOptions}
     * @return A {@code Flowable} which emits the data as {@code ByteBuffer}s.
     */
    public Flowable<ByteBuffer> body(ReliableDownloadOptions options) {
        ReliableDownloadOptions optionsReal = options == null ? new ReliableDownloadOptions() : options;
        if (optionsReal.maxRetryRequests() == 0) {
            return this.rawResponse.body();
        }

        return this.rawResponse.body()
                /*
                Update how much data we have received in case we need to retry and propagate to the user the data we
                have received.
                 */
                .doOnNext(buffer -> {
                    this.info.withOffset(this.info.offset() + buffer.remaining());
                    if (info.count() != null) {
                        this.info.withCount(this.info.count() - buffer.remaining());
                    }
                })
                .onErrorResumeNext(throwable -> {
                    // So far we have tried once but retried zero times.
                    return tryContinueFlowable(throwable, 0, optionsReal);
                });
    }

    private Flowable<ByteBuffer> tryContinueFlowable(Throwable t, int retryCount, ReliableDownloadOptions options) {
        // If all the errors are exhausted, return this error to the user.
        if (retryCount > options.maxRetryRequests() || !(t instanceof IOException)) {
            return Flowable.error(t);
        }
        else {
            try {
                // Get a new response and try reading from it.
                return getter.apply(this.info)
                        .flatMapPublisher(response ->{
                            // Do not compound the number of retries; just get the raw body.
                            ReliableDownloadOptions newOptions = new ReliableDownloadOptions()
                                    .withMaxRetryRequests(0);

                            return response.body(newOptions)
                                    .doOnNext(buffer -> {
                                        this.info.withOffset(this.info.offset() + buffer.remaining());
                                        if (info.count() != null) {
                                            this.info.withCount(this.info.count() - buffer.remaining());
                                        }
                                    })
                                    .onErrorResumeNext(t2 -> {
                                        // Increment the retry count and try again with the new exception.
                                        return tryContinueFlowable(t2, retryCount + 1, options);
                                    });
                        });
            } catch (Exception e) {
                // If the getter fails, return the getter failure to the user.
                return Flowable.error(e);
            }
        }
    }

    public int statusCode() {
        return this.rawResponse.statusCode();
    }

    public BlobDownloadHeaders headers() {
        return this.rawResponse.headers();
    }

    public Map<String, String> rawHeaders() {
        return this.rawResponse.rawHeaders();
    }

    public RestResponse<BlobDownloadHeaders, Flowable<ByteBuffer>> rawResponse() {
        return this.rawResponse;
    }
}
