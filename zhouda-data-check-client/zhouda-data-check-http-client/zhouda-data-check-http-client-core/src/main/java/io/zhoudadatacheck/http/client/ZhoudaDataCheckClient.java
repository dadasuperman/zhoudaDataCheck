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
package io.zhoudadatacheck.http.client;

import io.zhoudadatacheck.http.client.base.ZhoudaDataCheckApiEnum;
import io.zhoudadatacheck.http.client.base.ZhoudaDataCheckApiException;
import io.zhoudadatacheck.http.client.base.ZhoudaDataCheckBaseClient;
import io.zhoudadatacheck.http.client.request.*;
import io.zhoudadatacheck.http.client.response.ZhoudaDataCheckResponse;
import io.zhoudadatacheck.http.client.response.UserBaseInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.Serializable;
import java.util.Objects;
import java.util.Properties;

public class ZhoudaDataCheckClient extends ZhoudaDataCheckBaseClient {

    private Log log = LogFactory.getLog(ZhoudaDataCheckClient.class);

    public ZhoudaDataCheckClient(String baseUrl){
        super(baseUrl, new Properties(), "test", null);
    }

    /**
     * submit task
     * @param params
     * @return
     */
    public ZhoudaDataCheckResponse submitTask(String params) throws ZhoudaDataCheckApiException {
        ZhoudaDataCheckResponse res = callAPI(ZhoudaDataCheckApiEnum.TASK_SUBMIT_API.getZhoudaDataCheckApi(), params);
        return res;
    }

    /**
     * submit task
     * @param request
     * @return
     */
    public ZhoudaDataCheckResponse submitTask(SubmitTaskRequest request) throws ZhoudaDataCheckApiException {
        ZhoudaDataCheckResponse res = callAPI(ZhoudaDataCheckApiEnum.TASK_SUBMIT_API.getZhoudaDataCheckApi(), request);
        return res;
    }

    /**
     * kill task
     * @param taskId
     * @return
     * @throws ZhoudaDataCheckApiException
     */
    public ZhoudaDataCheckResponse killTask(Long taskId) throws ZhoudaDataCheckApiException {
       return callApiWithPathParam(ZhoudaDataCheckApiEnum.TASK_KILL_API, taskId);
    }

    /**
     * get task status
     * @param taskId
     * @return task status
     * @throws ZhoudaDataCheckApiException
     */
    public ZhoudaDataCheckResponse taskStatus(Long taskId) throws ZhoudaDataCheckApiException {
        return callApiWithPathParam(ZhoudaDataCheckApiEnum.TASK_STATUS_API, taskId);
    }

    /**
     * get task result info
     * @param taskId
     * @return task result
     * @throws ZhoudaDataCheckApiException
     */
    public ZhoudaDataCheckResponse taskResultInfo(Long taskId) throws ZhoudaDataCheckApiException {
        return callApiWithPathParam(ZhoudaDataCheckApiEnum.TASK_RESULT_API, taskId);
    }

    /**
     * get metric list
     * @return metric list
     * @throws ZhoudaDataCheckApiException
     */
    public ZhoudaDataCheckResponse metricList() throws ZhoudaDataCheckApiException {
        return callAPI(ZhoudaDataCheckApiEnum.METRIC_LIST_API);
    }

    /**
     * get metric info by metric name
     * @param name metric name
     * @return metric info
     * @throws ZhoudaDataCheckApiException
     */
    public ZhoudaDataCheckResponse metricInfo(String name) throws ZhoudaDataCheckApiException {
        return callApiWithPathParam(ZhoudaDataCheckApiEnum.METRIC_INFO_API, name);
    }

    public ZhoudaDataCheckResponse createWorkSpace(String spaceName) throws ZhoudaDataCheckApiException {
        ZhoudaDataCheckResponse result = callAPI(ZhoudaDataCheckApiEnum.CREATE_WORKSPACE.getZhoudaDataCheckApi(), new WorkSpaceCreateRequest(spaceName));
        return result;
    }

    public ZhoudaDataCheckResponse createWorkSpace(WorkSpaceCreateRequest spaceName) throws ZhoudaDataCheckApiException {
        ZhoudaDataCheckResponse result = callAPI(ZhoudaDataCheckApiEnum.CREATE_WORKSPACE.getZhoudaDataCheckApi(), spaceName);
        return result;
    }

    public ZhoudaDataCheckResponse updateWorkSpace(WorkSpaceUpdateRequest update) throws ZhoudaDataCheckApiException {
        ZhoudaDataCheckResponse result = callAPI(ZhoudaDataCheckApiEnum.UPDATE_WORKSPACE.getZhoudaDataCheckApi(), update);
        return result;
    }

    public ZhoudaDataCheckResponse deleteWorkSpace(Long workspaceId) throws ZhoudaDataCheckApiException {
        ZhoudaDataCheckResponse result = callApiWithPathParam(ZhoudaDataCheckApiEnum.DELETE_WORKSPACE, workspaceId);
        return result;
    }

    public ZhoudaDataCheckResponse listWorkSpace() throws ZhoudaDataCheckApiException {
        ZhoudaDataCheckResponse result = callAPI(ZhoudaDataCheckApiEnum.LIST_WORKSPACE);
        return result;
    }

    public ZhoudaDataCheckResponse register(UserRegisterRequest registerRequest) throws ZhoudaDataCheckApiException {
        ZhoudaDataCheckResponse result = callAPI(ZhoudaDataCheckApiEnum.REGISTER.getZhoudaDataCheckApi(), registerRequest);
        return result;
    }

    public ZhoudaDataCheckResponse login(UserLoginRequest loginRequest) throws ZhoudaDataCheckApiException {
        ZhoudaDataCheckResponse result = callAPI(ZhoudaDataCheckApiEnum.LOGIN.getZhoudaDataCheckApi(), loginRequest);
        return result;
    }

    public ZhoudaDataCheckResponse<UserBaseInfo> login(String name, String password) throws ZhoudaDataCheckApiException {
        ZhoudaDataCheckResponse<UserBaseInfo> result = callAPI(ZhoudaDataCheckApiEnum.LOGIN.getZhoudaDataCheckApi(), new UserLoginRequest(name, password));
        return result;
    }

    private <T> ZhoudaDataCheckResponse<T> callApiWithPathParam(ZhoudaDataCheckApiEnum ZhoudaDataCheckAPI, Serializable id) throws ZhoudaDataCheckApiException {
        if (Objects.isNull(id)){
            log.error("task id must not null!");
            throw new ZhoudaDataCheckApiException("task id must not null!");
        }
        ZhoudaDataCheckResponse result = callAPI(ZhoudaDataCheckAPI.getZhoudaDataCheckApi(String.valueOf(id)), null);
        return result;
    }

    private <T> ZhoudaDataCheckResponse<T> callAPI(ZhoudaDataCheckApiEnum ZhoudaDataCheckAPI) throws ZhoudaDataCheckApiException {
        ZhoudaDataCheckResponse result = callAPI(ZhoudaDataCheckAPI.getZhoudaDataCheckApi(), null);
        return result;
    }
}
