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
package io.zhoudadatacheck.server.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.zhoudadatacheck.common.param.ConnectorResponse;
import io.zhoudadatacheck.common.param.TestConnectionRequestParam;
import io.zhoudadatacheck.common.utils.PasswordFilterUtils;
import io.zhoudadatacheck.connector.api.ConnectorFactory;
import io.zhoudadatacheck.core.enums.Status;
import io.zhoudadatacheck.core.exception.ZhoudaDataCheckServerException;
import io.zhoudadatacheck.core.utils.LanguageUtils;
import io.zhoudadatacheck.server.api.dto.bo.storage.ErrorDataStorageCreate;
import io.zhoudadatacheck.server.api.dto.bo.storage.ErrorDataStoragePageParam;
import io.zhoudadatacheck.server.api.dto.bo.storage.ErrorDataStorageUpdate;
import io.zhoudadatacheck.server.api.dto.vo.ErrorDataStorageVO;
import io.zhoudadatacheck.server.repository.entity.ErrorDataStorage;
import io.zhoudadatacheck.server.repository.mapper.ErrorDataStorageMapper;
import io.zhoudadatacheck.server.repository.service.ErrorDataStorageService;
import io.zhoudadatacheck.server.utils.ContextHolder;
import io.zhoudadatacheck.spi.PluginDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static io.zhoudadatacheck.common.log.SensitiveDataConverter.PWD_PATTERN_1;

@Slf4j
@Service("errorDataStorageService")
public class ErrorDataStorageServiceImpl extends ServiceImpl<ErrorDataStorageMapper, ErrorDataStorage> implements ErrorDataStorageService {

    @Override
    public ConnectorResponse testConnect(TestConnectionRequestParam param) {
        ConnectorFactory connectorFactory = PluginDiscovery.getMultiKeyPluginDiscovery(ConnectorFactory.class, ConnectorFactory::getPluginNames).getOrCreatePlugin(param.getType());
        return connectorFactory.getConnector().testConnect(param);
    }

    @Override
    public long create(ErrorDataStorageCreate errorDataStorageCreate) throws ZhoudaDataCheckServerException {
        if (isErrorDataStorageExist(errorDataStorageCreate.getName())) {
            throw new ZhoudaDataCheckServerException(Status.ERROR_DATA_STORAGE_EXIST_ERROR, errorDataStorageCreate.getName());
        }
        ErrorDataStorage errorDataStorage = new ErrorDataStorage();
        BeanUtils.copyProperties(errorDataStorageCreate, errorDataStorage);
        errorDataStorage.setCreateBy(ContextHolder.getUserId());
        errorDataStorage.setCreateTime(LocalDateTime.now());
        errorDataStorage.setUpdateBy(ContextHolder.getUserId());
        errorDataStorage.setUpdateTime(LocalDateTime.now());

        if (baseMapper.insert(errorDataStorage) <= 0) {
            log.info("create errorDataStorage fail : {}", errorDataStorageCreate);
            throw new ZhoudaDataCheckServerException(Status.CREATE_ERROR_DATA_STORAGE_ERROR, errorDataStorageCreate.getName());
        }

        return errorDataStorage.getId();
    }

    @Override
    public int update(ErrorDataStorageUpdate errorDataStorageUpdate) throws ZhoudaDataCheckServerException {

        ErrorDataStorage errorDataStorage = getById(errorDataStorageUpdate.getId());
        if ( errorDataStorage == null) {
            throw new ZhoudaDataCheckServerException(Status.ERROR_DATA_STORAGE_NOT_EXIST_ERROR, errorDataStorageUpdate.getName());
        }

        BeanUtils.copyProperties(errorDataStorageUpdate, errorDataStorage);
        errorDataStorage.setUpdateBy(ContextHolder.getUserId());
        errorDataStorage.setUpdateTime(LocalDateTime.now());

        if (baseMapper.updateById(errorDataStorage) <= 0) {
            log.info("update errorDataStorage fail : {}", errorDataStorageUpdate);
            throw new ZhoudaDataCheckServerException(Status.UPDATE_ERROR_DATA_STORAGE_ERROR, errorDataStorageUpdate.getName());
        }

        return 1;
    }

    @Override
    public ErrorDataStorage getById(long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<ErrorDataStorage> listByWorkspaceId(long workspaceId) {
        List<ErrorDataStorage> list = baseMapper.selectList(new QueryWrapper<ErrorDataStorage>().lambda().eq(ErrorDataStorage::getWorkspaceId, workspaceId));
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(errorDataStorage -> {
                errorDataStorage.setParam(PasswordFilterUtils.convertPasswordToNULL(PWD_PATTERN_1, errorDataStorage.getParam()));
            });
        }

        return list;
    }

    @Override
    public IPage<ErrorDataStorageVO> getErrorDataStoragePage(ErrorDataStoragePageParam errorDataStoragePageParam) {
        Page<ErrorDataStorageVO> page = new Page<>(errorDataStoragePageParam.getPageNumber(), errorDataStoragePageParam.getPageSize());
        IPage<ErrorDataStorageVO> errorDataStoragePage = baseMapper.getErrorDataStoragePage(page,
                errorDataStoragePageParam.getWorkspaceId(),
                errorDataStoragePageParam.getSearchVal(),
                errorDataStoragePageParam.getType(),
                errorDataStoragePageParam.getStartTime(),
                errorDataStoragePageParam.getEndTime());
        List<ErrorDataStorageVO> errorDataStoragePageRecordList = errorDataStoragePage.getRecords();
        for (ErrorDataStorageVO errorDataStorageVO : errorDataStoragePageRecordList) {
            errorDataStorageVO.setParam(PasswordFilterUtils.convertPasswordToNULL(PWD_PATTERN_1, errorDataStorageVO.getParam()));
        }
        return errorDataStoragePage;
    }

    @Override
    public int deleteById(long id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public String getConfigJson(String type) {
        return PluginDiscovery.getMultiKeyPluginDiscovery(ConnectorFactory.class, ConnectorFactory::getPluginNames).getOrCreatePlugin(type).getConfigBuilder().buildErrorDataStorage(!LanguageUtils.isZhContext());
    }

    private boolean isErrorDataStorageExist(String name) {
        ErrorDataStorage user = baseMapper.selectOne(new QueryWrapper<ErrorDataStorage>().lambda().eq(ErrorDataStorage::getName, name));
        return user != null;
    }
}
