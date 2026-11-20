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
package io.zhoudadatacheck.server.api.controller;

import io.zhoudadatacheck.core.constant.ZhoudaDataCheckConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Api(value = "system", tags = "system", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping(value = ZhoudaDataCheckConstants.BASE_API_PATH + "/system", produces = MediaType.APPLICATION_JSON_VALUE)
public class SystemController {

    @Value("${spring.application.name:zhoudaDataCheck-server}")
    private String applicationName;

    @ApiOperation(value = "product info")
    @GetMapping(value = "/info")
    public Object info() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("product", ZhoudaDataCheckConstants.PRODUCT_NAME);
        body.put("application", applicationName);
        return body;
    }
}
