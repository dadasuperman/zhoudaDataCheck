/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zhoudadatacheck.http.client.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import io.zhoudadatacheck.http.client.response.ZhoudaDataCheckResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public abstract class ZhoudaDataCheckBaseClient {

    private Log log = LogFactory.getLog(ZhoudaDataCheckBaseClient.class);
    private String baseURL = null;
    private Cookie cookie = null;
    private boolean retryEnabled = false;
    private Properties configuration = null;
    private int retryTimes = 1;
    private int sleepBetweenRetries = 1000;
    protected WebResource service;
    protected ObjectMapper objectMapper;

    @Getter
    @Setter
    private String token;

    protected ZhoudaDataCheckBaseClient(String baseUrl, Properties configuration, String user, Cookie cookie) {
        this.baseURL = baseUrl;
        objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        Client client = getClient(configuration, user);
        service = client.resource(baseUrl);
        this.cookie = cookie;
    }

    protected Client getClient(Properties configuration, String user) {
        Integer readTimeout = (Integer) configuration.getOrDefault(ZhoudaDataCheckClientConfig.CLIENT_READ_TIMEOUT_MSECS, 6000);
        Integer connectTimeout = (Integer) configuration.getOrDefault(ZhoudaDataCheckClientConfig.CLIENT_CONNECT_TIMEOUT_MESCS, 6000);
        retryTimes = (Integer) configuration.getOrDefault(ZhoudaDataCheckClientConfig.CLIENT_RETRY_TIMES, 1);
        sleepBetweenRetries = (Integer) configuration.getOrDefault(ZhoudaDataCheckClientConfig.CLIENT_SLEEP_BETWEEN_RETRY, 1000);

        final URLConnectionClientHandler handler = new URLConnectionClientHandler();
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = new Client(handler, clientConfig);
        client.setReadTimeout(readTimeout);
        client.setConnectTimeout(connectTimeout);
        return client;
    }

    protected <T> ZhoudaDataCheckResponse<T> callAPI(ZhoudaDataCheckAPI api, WebResource service, Object requestObject) throws ZhoudaDataCheckApiException {
        ClientResponse clientResponse = null;
        int i = 0;
        do {

            log.debug("------------------------------------------------------");
            log.debug(String.format("start call %s method {%s}", api.getPath(), api.getMethod()));
            log.debug(String.format("ConsumerMediaType : %s ", api.getConsumerMediaType()));
            log.debug(String.format("ProviderMediaType : %s ", api.getProviderMediaType()));
            if (requestObject != null) {
                log.debug(String.format("Request : %s", requestObject));
            }
            WebResource realWebService = service.path(api.getPath());
            WebResource.Builder requestBuilder = realWebService.getRequestBuilder();
            requestBuilder
                    .accept(api.getProviderMediaType())
                    .type(api.getConsumerMediaType());

            // Set cookie i
            if (Objects.nonNull(cookie)) {
                requestBuilder.cookie(cookie);
            }
            //Set token
            if (Objects.nonNull(token)){
                requestBuilder.header("Authorization", token);
            }

            if (Objects.nonNull(requestObject) && !(requestObject instanceof String)) {
                try {
                    requestObject = objectMapper.writeValueAsString(requestObject);
                } catch (JsonProcessingException e) {
                    log.error("json parse error", e);
                }
            }

            clientResponse = requestBuilder.method(api.getMethod(), ClientResponse.class, requestObject);
            log.debug(String.format("Response Status is  : %s", clientResponse.getStatus()));

            if (!log.isDebugEnabled()) {
                log.info(String.format("method=%s path=%s contentType=%s accept=%s status=%s", api.getMethod(),
                        api.getPath(), api.getConsumerMediaType(), api.getProviderMediaType(), clientResponse.getStatus()));
            }

            if (api.getConsumerMediaType().contains(MediaType.APPLICATION_JSON)) {
                String result = clientResponse.getEntity(String.class);
                ZhoudaDataCheckResponse<T> response = null;
                try {
                    response = (ZhoudaDataCheckResponse<T>) objectMapper.readValue(result, api.getResultType());
                } catch (JsonProcessingException e) {
                    log.error("json parse error", e);
                }
                return response;
            }

            try {
                TimeUnit.MICROSECONDS.sleep(sleepBetweenRetries);
            } catch (InterruptedException e) {
                log.error("sleep error");
            }
            i++;
        } while (i < retryTimes);

        throw new ZhoudaDataCheckApiException("unknown response!");
    }

    protected <T> ZhoudaDataCheckResponse<T> callAPI(ZhoudaDataCheckAPI api, Object requestObject) throws ZhoudaDataCheckApiException {
        return callAPI(api, service, requestObject);
    }

}
