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

import io.zhoudadatacheck.core.aop.RefreshToken;
import io.zhoudadatacheck.core.constant.ZhoudaDataCheckConstants;
import io.zhoudadatacheck.core.exception.ZhoudaDataCheckServerException;
import io.zhoudadatacheck.server.api.dto.bo.config.ConfigCreate;
import io.zhoudadatacheck.server.api.dto.bo.config.ConfigUpdate;
import io.zhoudadatacheck.server.repository.service.ConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "config", tags = "config", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping(value = ZhoudaDataCheckConstants.BASE_API_PATH + "/config", produces = MediaType.APPLICATION_JSON_VALUE)
@RefreshToken
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @ApiOperation(value = "create config")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object createConfig(@Valid @RequestBody ConfigCreate configCreate) throws ZhoudaDataCheckServerException {
        return configService.create(configCreate);
    }

    @ApiOperation(value = "update config")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object updateConfig(@Valid @RequestBody ConfigUpdate configUpdate) throws ZhoudaDataCheckServerException {
        return configService.update(configUpdate)>0;
    }

    @ApiOperation(value = "delete config")
    @DeleteMapping(value = "/{id}")
    public Object deleteConfig(@PathVariable Long id)  {
        return configService.deleteById(id);
    }

    @ApiOperation(value = "page config")
    @GetMapping(value = "/page")
    public Object listByUserId(@RequestParam("workspaceId") Long workspaceId,
                               @RequestParam(value = "searchVal", required = false) String searchVal,
                               @RequestParam("pageNumber") Integer pageNumber,
                               @RequestParam("pageSize") Integer pageSize)  {
        return configService.configPage(workspaceId, searchVal, pageNumber, pageSize);
    }
}
