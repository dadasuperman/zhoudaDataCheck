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


import com.fasterxml.jackson.core.type.TypeReference;
import io.zhoudadatacheck.http.client.request.UserLoginResult;
import io.zhoudadatacheck.http.client.response.ZhoudaDataCheckResponse;
import io.zhoudadatacheck.http.client.response.TaskResult;
import io.zhoudadatacheck.http.client.response.UserBaseInfo;
import io.zhoudadatacheck.http.client.response.WorkSpace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public enum ZhoudaDataCheckApiEnum {

    //TASK
    TASK_SUBMIT_API(new ZhoudaDataCheckAPI("/api/v1/task/submit", HttpMethod.POST, Response.Status.OK, new TypeReference<ZhoudaDataCheckResponse<Long>>() {})),
    TASK_KILL_API(new ZhoudaDataCheckAPI("/api/v1/task/kill/%s", HttpMethod.DELETE, Response.Status.OK, new TypeReference<ZhoudaDataCheckResponse<Long>>() {})),
    TASK_STATUS_API(new ZhoudaDataCheckAPI("/api/v1/task/status/%s", HttpMethod.GET, Response.Status.OK, new TypeReference<ZhoudaDataCheckResponse<String>>() {})),
    TASK_RESULT_API(new ZhoudaDataCheckAPI("/api/v1/task/result/%s", HttpMethod.GET, Response.Status.OK, new TypeReference<ZhoudaDataCheckResponse<TaskResult>>() {})),
    //METRIC
    METRIC_LIST_API(new ZhoudaDataCheckAPI("/api/v1/metric/list", HttpMethod.GET, Response.Status.OK, new TypeReference<ZhoudaDataCheckResponse<Set<String>>>() {})),
    METRIC_INFO_API(new ZhoudaDataCheckAPI("/api/v1/metric/info/%s", HttpMethod.GET, Response.Status.OK, new TypeReference<ZhoudaDataCheckResponse<HashMap<String, String>>>() {})),
    //WORKSPACE
    CREATE_WORKSPACE(new ZhoudaDataCheckAPI("/api/v1/workspace", HttpMethod.POST, Response.Status.OK, new TypeReference<ZhoudaDataCheckResponse<Long>>() {})),
    UPDATE_WORKSPACE(new ZhoudaDataCheckAPI("/api/v1/workspace", HttpMethod.PUT, Response.Status.OK, new TypeReference<ZhoudaDataCheckResponse<Integer>>() {})),
    DELETE_WORKSPACE(new ZhoudaDataCheckAPI("/api/v1/workspace/%s", HttpMethod.DELETE, Response.Status.OK, new TypeReference<ZhoudaDataCheckResponse<Integer>>() {})),
    LIST_WORKSPACE(new ZhoudaDataCheckAPI("/api/v1/workspace/list", HttpMethod.GET, Response.Status.OK, new TypeReference<ZhoudaDataCheckResponse<List<WorkSpace>>>() {})),
    //USER
    UPDATE_USER(new ZhoudaDataCheckAPI("/api/v1/user/update", HttpMethod.PUT, Response.Status.OK, new TypeReference<ZhoudaDataCheckResponse<UserLoginResult>>() {})),
    RESET_PASSWORD(new ZhoudaDataCheckAPI("/api/v1/user/resetPassword", HttpMethod.POST, Response.Status.OK, new TypeReference<ZhoudaDataCheckResponse<UserLoginResult>>() {})),
    LOGIN(new ZhoudaDataCheckAPI("/api/v1/login", HttpMethod.POST, Response.Status.OK, new TypeReference<ZhoudaDataCheckResponse<UserLoginResult>>() {})),
    REGISTER(new ZhoudaDataCheckAPI("/api/v1/register", HttpMethod.POST, Response.Status.OK, new TypeReference<ZhoudaDataCheckResponse<UserBaseInfo>>() {})),
    ;
    private Log log = LogFactory.getLog(ZhoudaDataCheckBaseClient.class);

    private final ZhoudaDataCheckAPI zhoudaDataCheckApi;

    ZhoudaDataCheckApiEnum(ZhoudaDataCheckAPI zhoudaDataCheckApi) {
        this.zhoudaDataCheckApi = zhoudaDataCheckApi;
    }

    public ZhoudaDataCheckAPI getZhoudaDataCheckApi(String... path) {
        String formattedPath = String.format(zhoudaDataCheckApi.getPath(), path);
        return new ZhoudaDataCheckAPI(formattedPath, zhoudaDataCheckApi.getMethod(), zhoudaDataCheckApi.getExpectStatus(), getZhoudaDataCheckApi().getResultType());
    }

    public ZhoudaDataCheckAPI getZhoudaDataCheckApi() {
        if (zhoudaDataCheckApi.getPath().contains("%s")) {
            log.error(String.format("%s must be init with path", zhoudaDataCheckApi.getPath()));
        }
        return zhoudaDataCheckApi;
    }
}
