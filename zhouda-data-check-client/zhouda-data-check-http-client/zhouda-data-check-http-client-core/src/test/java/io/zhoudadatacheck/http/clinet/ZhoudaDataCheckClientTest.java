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
package io.zhoudadatacheck.http.clinet;


import io.zhoudadatacheck.http.client.ZhoudaDataCheckClient;
import io.zhoudadatacheck.http.client.base.ZhoudaDataCheckApiException;
import io.zhoudadatacheck.http.client.request.UserRegisterRequest;
import io.zhoudadatacheck.http.client.request.WorkSpaceCreateRequest;
import io.zhoudadatacheck.http.client.response.ZhoudaDataCheckResponse;
import io.zhoudadatacheck.http.client.response.UserBaseInfo;
import org.junit.Before;
import org.junit.Test;

public class ZhoudaDataCheckClientTest {

    private ZhoudaDataCheckClient client;

    {
        client = new ZhoudaDataCheckClient("http://localhost:5600");
    }

    @Test
    public void register() throws ZhoudaDataCheckApiException{
        ZhoudaDataCheckResponse test = client.register(new UserRegisterRequest("test1", "13435@163.com", "123456", "12436456"));
        System.out.println(test);
    }

    @Before
    public void login() throws ZhoudaDataCheckApiException {
        ZhoudaDataCheckResponse<UserBaseInfo> test = client.login("test1", "123456");
        UserBaseInfo data = test.getData();
        System.out.println(test);
        client.setToken(test.getToken());
    }

    @Test
    public void submitTask() throws ZhoudaDataCheckApiException {
        String json = "{\"name\":\"test\",\"parameter\":{\"metricType\":\"column_length\",\"metricParameter\":{\"table\":\"task\",\"column\":\"parameter\",\"comparator\":\">\",\"length\":1},\"srcConnectorParameter\":{\"type\":\"postgresql\",\"parameters\":{\"database\":\"ZhoudaDataCheck\",\"password\":\"lwslws\",\"port\":\"5432\",\"host\":\"localhost\",\"user\":\"postgres\",\"properties\":\"useUnicode=true&characterEncoding=UTF-8\"}}}}";
        ZhoudaDataCheckResponse task = client.submitTask(json);
        System.out.println(task);
    }

    @Test
    public void taskStatus() throws ZhoudaDataCheckApiException{
        ZhoudaDataCheckResponse response = client.taskStatus(1516045488414031873L);
        System.out.println(response);
    }

    @Test
    public void taskResultInfo() throws ZhoudaDataCheckApiException{
        ZhoudaDataCheckResponse response = client.taskResultInfo(1516045488414031873L);
        System.out.println(response);
    }

    @Test
    public void killTask() throws ZhoudaDataCheckApiException{
        ZhoudaDataCheckResponse response = client.killTask(1516045488414031873L);
        System.out.println(response);
    }

    @Test
    public void metricInfo() throws ZhoudaDataCheckApiException{
        ZhoudaDataCheckResponse response = client.metricInfo("column_length");
        System.out.println(response);
    }

    @Test
    public void metricList() throws ZhoudaDataCheckApiException{
        ZhoudaDataCheckResponse response = client.metricList();
        System.out.println(response);
    }

    @Test
    public void createWorkSpaceByName() throws ZhoudaDataCheckApiException{
        ZhoudaDataCheckResponse test = client.createWorkSpace("test");
        System.out.println(test);
    }

    @Test
    public void createWorkSpaceByCreateRequest() throws ZhoudaDataCheckApiException{
        ZhoudaDataCheckResponse test = client.createWorkSpace(new WorkSpaceCreateRequest("test2"));
        System.out.println(test);
    }

    @Test
    public void listWorkSpace() throws ZhoudaDataCheckApiException {
        ZhoudaDataCheckResponse response = client.listWorkSpace();
        System.out.println(response);
    }
}
