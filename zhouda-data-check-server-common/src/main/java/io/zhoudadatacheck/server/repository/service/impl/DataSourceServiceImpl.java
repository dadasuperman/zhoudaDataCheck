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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.zhoudadatacheck.common.utils.*;
import io.zhoudadatacheck.core.utils.LanguageUtils;
import io.zhoudadatacheck.server.api.dto.bo.catalog.CatalogRefresh;
import io.zhoudadatacheck.server.api.dto.bo.datasource.*;
import io.zhoudadatacheck.common.exception.ZhoudaDataCheckException;
import io.zhoudadatacheck.common.param.*;
import io.zhoudadatacheck.connector.api.ConnectorFactory;
import io.zhoudadatacheck.core.enums.Status;
import io.zhoudadatacheck.server.api.dto.bo.job.schedule.MapParam;
import io.zhoudadatacheck.server.api.dto.bo.task.CommonTaskScheduleCreateOrUpdate;
import io.zhoudadatacheck.server.api.dto.vo.DataSourceVO;
import io.zhoudadatacheck.server.enums.CommonTaskType;
import io.zhoudadatacheck.server.repository.entity.DataSource;
import io.zhoudadatacheck.server.repository.mapper.DataSourceMapper;
import io.zhoudadatacheck.server.repository.service.*;
import io.zhoudadatacheck.core.exception.ZhoudaDataCheckServerException;
import io.zhoudadatacheck.server.utils.ContextHolder;
import io.zhoudadatacheck.spi.PluginDiscovery;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;

import static io.zhoudadatacheck.common.log.SensitiveDataConverter.PWD_PATTERN_1;

@Slf4j
@Service("dataSourceService")
public class DataSourceServiceImpl extends ServiceImpl<DataSourceMapper, DataSource>  implements DataSourceService {

    @Autowired
    private JobService jobService;

    @Autowired
    private CommonTaskService commonTaskService;

    @Autowired
    private CatalogEntityInstanceService catalogEntityInstanceService;

    @Autowired
    private CommonTaskScheduleService commonTaskScheduleService;

    @Override
    public ConnectorResponse testConnect(TestConnectionRequestParam param) {
        ConnectorFactory connectorFactory = PluginDiscovery.getMultiKeyPluginDiscovery(ConnectorFactory.class, ConnectorFactory::getPluginNames).getOrCreatePlugin(param.getType());
        return connectorFactory.getConnector().testConnect(param);
    }

    @Override
    public long insert(DataSourceCreate dataSourceCreate) {
        DataSource dataSource = new DataSource();
        BeanUtils.copyProperties(dataSourceCreate, dataSource);
        String param = dataSourceCreate.getParam();
        String paramCode = "";

        Map<String,String> paramMap = JSONUtils.toMap(param);

        if (MapUtils.isEmpty(paramMap)) {
            return -1L;
        }

        String type = dataSourceCreate.getType();

        ConnectorFactory connectorFactory = PluginDiscovery.getMultiKeyPluginDiscovery(ConnectorFactory.class, ConnectorFactory::getPluginNames).getOrCreatePlugin(type);
        List<String> keyProperties = connectorFactory.getConnector().keyProperties();
        List<String> keyPropertyValueList = new ArrayList<>();
        keyPropertyValueList.add(dataSourceCreate.getType().toLowerCase());
        if (CollectionUtils.isNotEmpty(keyProperties)) {
            keyProperties.forEach(property -> {
                if (StringUtils.isNotEmpty(paramMap.get(property))) {
                    keyPropertyValueList.add(paramMap.get(property).toLowerCase());
                }
            });
        }

        if (CollectionUtils.isNotEmpty(keyPropertyValueList)) {
            paramCode = Md5Utils.getMd5(String.join("@#@", keyPropertyValueList),true);
        }

        try {
            param = CryptionUtils.encryptByAES(param, CommonPropertyUtils.getString(CommonPropertyUtils.AES_KEY, CommonPropertyUtils.AES_KEY_DEFAULT));
        } catch (Exception e) {
            throw new ZhoudaDataCheckException("encrypt datasource param error : {}", e);
        }

        dataSource.setUuid(UUID.randomUUID().toString());
        dataSource.setParam(param);
        dataSource.setParamCode(paramCode);
        dataSource.setCreateTime(LocalDateTime.now());
        dataSource.setUpdateTime(LocalDateTime.now());
        dataSource.setCreateBy(ContextHolder.getUserId());
        dataSource.setUpdateBy(ContextHolder.getUserId());
        baseMapper.insert(dataSource);

        CatalogRefresh catalogRefresh = new CatalogRefresh();
        catalogRefresh.setDatasourceId(dataSource.getId());
        catalogRefresh.setTaskType(CommonTaskType.CATALOG_METADATA_FETCH);
        commonTaskService.refreshCatalog(catalogRefresh);

        CommonTaskScheduleCreateOrUpdate taskScheduleCreateOrUpdate = new CommonTaskScheduleCreateOrUpdate();
        taskScheduleCreateOrUpdate.setDataSourceId(dataSource.getId());
        taskScheduleCreateOrUpdate.setTaskType(CommonTaskType.DATA_QUALITY_REPORT);
        taskScheduleCreateOrUpdate.setType("cycle");
        MapParam mapParam = new MapParam();
        mapParam.setCycle("day");
        Map<String,String> parameter = new HashMap<>();
        parameter.put("minute","5");
        parameter.put("hour","0");
        mapParam.setParameter(parameter);
        taskScheduleCreateOrUpdate.setParam(mapParam);
        taskScheduleCreateOrUpdate.setStartTime(LocalDateTime.now());
        taskScheduleCreateOrUpdate.setEndTime(LocalDateTime.now().plusYears(100));
        commonTaskScheduleService.createOrUpdate(taskScheduleCreateOrUpdate);
        return dataSource.getId();
    }

    @Override
    public int update(DataSourceUpdate dataSourceUpdate) throws ZhoudaDataCheckException {
        DataSource dataSource = baseMapper.selectById(dataSourceUpdate.getId());
        if (dataSource == null){
            throw new ZhoudaDataCheckException("can not find the datasource");
        }

        BeanUtils.copyProperties(dataSourceUpdate, dataSource);
        String param = dataSourceUpdate.getParam();

        String paramCode = "";

        Map<String,String> paramMap = JSONUtils.toMap(param);

        if (MapUtils.isEmpty(paramMap)) {
            return -1;
        }

        String type = dataSourceUpdate.getType();

        ConnectorFactory connectorFactory = PluginDiscovery.getMultiKeyPluginDiscovery(ConnectorFactory.class, ConnectorFactory::getPluginNames).getOrCreatePlugin(type);
        List<String> keyProperties = connectorFactory.getConnector().keyProperties();
        List<String> keyPropertyValueList = new ArrayList<>();
        keyPropertyValueList.add(dataSourceUpdate.getType().toLowerCase());
        if (CollectionUtils.isNotEmpty(keyProperties)) {
            keyProperties.forEach(property -> {
                if (StringUtils.isNotEmpty(paramMap.get(property))) {
                    keyPropertyValueList.add(paramMap.get(property).toLowerCase());
                }
            });
        }

        if (CollectionUtils.isNotEmpty(keyPropertyValueList)) {
            paramCode = Md5Utils.getMd5(String.join("@#@", keyPropertyValueList),true);
        }

        try {
            param = CryptionUtils.encryptByAES(param
                    ,CommonPropertyUtils.getString(CommonPropertyUtils.AES_KEY, CommonPropertyUtils.AES_KEY_DEFAULT));
        } catch (Exception e) {
            throw new ZhoudaDataCheckException("encrypt datasource param error : {}", e);
        }

        dataSource.setParam(param);
        dataSource.setParamCode(paramCode);
        dataSource.setUpdateTime(LocalDateTime.now());
        dataSource.setUpdateBy(ContextHolder.getUserId());

        return baseMapper.updateById(dataSource);
    }

    @Override
    public DataSource getDataSourceById(long id) {
        DataSource dataSourceVO = new DataSource();

        DataSource dataSource = baseMapper.selectById(id);
        if (dataSource == null) {
            return null;
        }

        BeanUtils.copyProperties(dataSource, dataSourceVO);

        String param = dataSource.getParam();

        try {
            param = CryptionUtils.decryptByAES(param
                    ,CommonPropertyUtils.getString(CommonPropertyUtils.AES_KEY, CommonPropertyUtils.AES_KEY_DEFAULT));
        } catch (Exception e) {
            throw new ZhoudaDataCheckException("encrypt datasource param error : ", e);
        }

        dataSourceVO.setParam(param);

        return dataSourceVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(long id) {
        DataSource dataSource = getById(id);
        if (dataSource != null) {
            catalogEntityInstanceService.deleteEntityByUUID(dataSource.getUuid());
            jobService.deleteByDataSourceId(id);
            commonTaskService.deleteByDataSourceId(id);
            removeById(id);
            return 1;
        }

        return 0;
    }

    @Override
    public IPage<DataSourceVO> getDataSourcePage(String searchVal, Long workspaceId, Integer pageNumber, Integer pageSize) {
        Page<DataSourceVO> page = new Page<>(pageNumber, pageSize);
        IPage<DataSourceVO> dataSources = baseMapper.getDataSourcePage(page, searchVal, workspaceId);
        dataSources.getRecords().forEach(dataSourceVO -> {
            String param = dataSourceVO.getParam();

            try {
                param = CryptionUtils.decryptByAES(param
                        ,CommonPropertyUtils.getString(CommonPropertyUtils.AES_KEY, CommonPropertyUtils.AES_KEY_DEFAULT));
            } catch (Exception e) {
                throw new ZhoudaDataCheckException("encrypt datasource param error : {}", e);
            }

            dataSourceVO.setParam(PasswordFilterUtils.convertPasswordToNULL(PWD_PATTERN_1, param));
        });
        return dataSources;
    }

    @Override
    public List<DataSource> listByWorkSpaceId(long workspaceId) {
        return baseMapper.selectList(new QueryWrapper<DataSource>().lambda().eq(DataSource::getWorkspaceId, workspaceId));
    }

    @Override
    public List<DataSource> listByWorkSpaceIdAndType(long workspaceId, String type) {
        return baseMapper.selectList(new QueryWrapper<DataSource>().lambda().eq(DataSource::getWorkspaceId, workspaceId).eq(DataSource::getType, type));
    }

    @Override
    public List<DataSourceInfo> listByInfo(DataSourceKeyProperties dataSourceKeyProperties) {
        Map<String,String> paramMap = dataSourceKeyProperties.getParam();
        String type = dataSourceKeyProperties.getType();
        String paramCode = "";
        ConnectorFactory connectorFactory = PluginDiscovery.getMultiKeyPluginDiscovery(ConnectorFactory.class, ConnectorFactory::getPluginNames).getOrCreatePlugin(type);
        List<String> keyProperties = connectorFactory.getConnector().keyProperties();
        List<String> keyPropertyValueList = new ArrayList<>();
        keyPropertyValueList.add(type.toLowerCase());
        if (CollectionUtils.isNotEmpty(keyProperties)) {
            keyProperties.forEach(property -> {
                if (StringUtils.isNotEmpty(paramMap.get(property))) {
                    keyPropertyValueList.add(paramMap.get(property).toLowerCase());
                }
            });
        }

        if (CollectionUtils.isNotEmpty(keyPropertyValueList)) {
            paramCode = Md5Utils.getMd5(String.join("@#@", keyPropertyValueList),true);
        }

        if (StringUtils.isEmpty(paramCode)) {
            return  new ArrayList<>();
        }

        List<DataSource> dataSourceList = list(new LambdaQueryWrapper<DataSource>().eq(DataSource::getParamCode, paramCode));
        if (CollectionUtils.isEmpty(dataSourceList)) {
            return  new ArrayList<>();
        }

        List<DataSourceInfo> dataSources = new ArrayList<>();
        dataSourceList.forEach(dataSource -> {
            DataSourceInfo dataSourceInfo = new DataSourceInfo();
            BeanUtils.copyProperties(dataSource, dataSourceInfo);
            dataSources.add(dataSourceInfo);
        });

        return dataSources;
    }

    @Override
    public Object getDatabaseList(Long id) throws ZhoudaDataCheckServerException {

        DataSource dataSource = getDataSourceById(id);
        GetDatabasesRequestParam param = new GetDatabasesRequestParam();
        param.setType(dataSource.getType());
        param.setDataSourceParam(dataSource.getParam());

        Object result = null;
        ConnectorFactory connectorFactory = PluginDiscovery.getMultiKeyPluginDiscovery(ConnectorFactory.class, ConnectorFactory::getPluginNames).getOrCreatePlugin(param.getType());
        try {
            ConnectorResponse response = connectorFactory.getConnector().getDatabases(param);
            result = response.getResult();
        } catch (SQLException e) {
            log.error(MessageFormat.format(Status.GET_DATABASE_LIST_ERROR.getMsg(), dataSource.getName()), e);
            throw new ZhoudaDataCheckServerException(Status.GET_DATABASE_LIST_ERROR, dataSource.getName());
        }

        return result;
    }

    @Override
    public Object getTableList(Long id, String database) throws ZhoudaDataCheckServerException {
        DataSource dataSource = getDataSourceById(id);
        GetTablesRequestParam param = new GetTablesRequestParam();
        param.setType(dataSource.getType());
        param.setDataSourceParam(dataSource.getParam());
        param.setDatabase(database);

        Object result = null;
        ConnectorFactory connectorFactory = PluginDiscovery.getMultiKeyPluginDiscovery(ConnectorFactory.class, ConnectorFactory::getPluginNames).getOrCreatePlugin(param.getType());
        try {
            ConnectorResponse response = connectorFactory.getConnector().getTables(param);
            result = response.getResult();
        } catch (SQLException e) {
            log.error(MessageFormat.format(Status.GET_TABLE_LIST_ERROR.getMsg(), dataSource.getName(), database), e);
            throw new ZhoudaDataCheckServerException(Status.GET_TABLE_LIST_ERROR, dataSource.getName(), database);
        }

        return result;
    }

    @Override
    public Object getColumnList(Long id, String database, String table) throws ZhoudaDataCheckServerException {
        DataSource dataSource = getDataSourceById(id);
        GetColumnsRequestParam param = new GetColumnsRequestParam();
        param.setType(dataSource.getType());
        param.setDataSourceParam(dataSource.getParam());
        param.setDataBase(database);
        param.setTable(table);

        Object result = null;
        ConnectorFactory connectorFactory = PluginDiscovery.getMultiKeyPluginDiscovery(ConnectorFactory.class, ConnectorFactory::getPluginNames).getOrCreatePlugin(param.getType());
        try {
            ConnectorResponse response = connectorFactory.getConnector().getColumns(param);
            result = response.getResult();
        } catch (SQLException e) {
            log.error(MessageFormat.format(Status.GET_COLUMN_LIST_ERROR.getMsg(), dataSource.getName(), database, table), e);
            throw new ZhoudaDataCheckServerException(Status.GET_COLUMN_LIST_ERROR, dataSource.getName(), database, table);
        }

        return result;
    }

    @Override
    public Object executeScript(ExecuteRequest request) throws ZhoudaDataCheckServerException {
        DataSource dataSource = getDataSourceById(request.getDatasourceId());
        ExecuteRequestParam param = new ExecuteRequestParam();
        param.setType(dataSource.getType());
        param.setDataSourceParam(dataSource.getParam());
        param.setScript(request.getScript());
        Object result = null;
        ConnectorFactory connectorFactory = PluginDiscovery.getMultiKeyPluginDiscovery(ConnectorFactory.class, ConnectorFactory::getPluginNames).getOrCreatePlugin(param.getType());
        try {
            ConnectorResponse response = connectorFactory.getExecutor().queryForList(param);
            result = response.getResult();
        } catch (Exception e) {
            log.error(MessageFormat.format(Status.EXECUTE_SCRIPT_ERROR.getMsg(), request.getScript()), e);
            throw new ZhoudaDataCheckServerException(Status.GET_TABLE_LIST_ERROR, request.getScript());
        }

        return result;
    }

    @Override
    public String getConfigJson(String type) {
        PluginDiscovery<ConnectorFactory> connectorDiscovery =
                PluginDiscovery.getMultiKeyPluginDiscovery(ConnectorFactory.class, ConnectorFactory::getPluginNames);
        if (!connectorDiscovery.getSupportedPlugins().contains(type)) {
            throw new ZhoudaDataCheckServerException(Status.DATASOURCE_TYPE_NOT_SUPPORT_ERROR, type);
        }

        ConnectorFactory connectorFactory = connectorDiscovery.getOrCreatePlugin(type);
        if (connectorFactory == null || connectorFactory.getConfigBuilder() == null) {
            throw new ZhoudaDataCheckServerException(Status.DATASOURCE_TYPE_NOT_SUPPORT_ERROR, type);
        }

        return connectorFactory.getConfigBuilder().build(!LanguageUtils.isZhContext());
    }
}
