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

import io.zhoudadatacheck.common.config.ZhoudaDataCheckJobConfig;
import io.zhoudadatacheck.common.entity.JobExecutionInfo;
import io.zhoudadatacheck.common.entity.JobExecutionParameter;
import io.zhoudadatacheck.common.entity.JobExecutionRequest;
import io.zhoudadatacheck.common.enums.ExecutionStatus;
import io.zhoudadatacheck.common.utils.JSONUtils;
import io.zhoudadatacheck.engine.config.ZhoudaDataCheckConfigurationManager;
import io.zhoudadatacheck.common.exception.ZhoudaDataCheckException;
import io.zhoudadatacheck.server.repository.entity.Command;
import io.zhoudadatacheck.server.repository.entity.Job;
import io.zhoudadatacheck.server.repository.entity.JobExecution;
import io.zhoudadatacheck.server.repository.entity.JobExecutionResult;
import io.zhoudadatacheck.server.repository.entity.CommonTaskCommand;
import io.zhoudadatacheck.server.repository.entity.CommonTask;
import io.zhoudadatacheck.server.repository.service.*;
import io.zhoudadatacheck.server.utils.DefaultDataSourceInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.zhoudadatacheck.common.ConfigConstants.SRC_CONNECTOR_TYPE;

@Slf4j
@Component
public class JobExternalService {
    
    @Autowired
    private JobExecutionService jobExecutionService;

    @Autowired
    private CommandService commandService;

    @Autowired
    private JobService jobService;

    @Autowired
    private JobExecutionResultService jobExecutionResultService;

    @Autowired
    private ActualValuesService actualValuesService;

    @Autowired
    private CommonTaskCommandService commonTaskCommandService;

    @Autowired
    private CommonTaskService commonTaskService;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private ConfigService configService;

    public Job getJobById(Long id) {
        return jobService.getById(id);
    }

    public JobExecution getJobExecutionById(Long id){
        return jobExecutionService.getById(id);
    }

    public Command getStartCommand(int totalSlot, int currentSlot) {
        return commandService.getStartCommand(totalSlot, currentSlot);
    }

    public List<Command> listKillCommandByExecuteHost(String executeHost) {
        return commandService.listKillCommandByExecuteHost(executeHost);
    }

    public CommonTaskCommand getCatalogCommand(int totalSlot, int currentSlot){
        return commonTaskCommandService.getOne(totalSlot, currentSlot);
    }

    public int deleteCommandById(long id){
        return commandService.deleteById(id);
    }

    public int deleteCatalogCommandById(long id){
        return commonTaskCommandService.deleteById(id);
    }

    public JobExecution executeCommand(Command command){
        return jobExecutionService.getById(command.getJobExecutionId());
    }

    public CommonTask executeCatalogCommand(CommonTaskCommand command){
        return commonTaskService.getById(command.getTaskId());
    }

    public int updateJobExecution(JobExecution jobExecution){
        return jobExecutionService.update(jobExecution);
    }

    public Long createJobExecution(JobExecution jobExecution){
        return jobExecutionService.create(jobExecution);
    }

    public Long insertCommand(Command command){
        return commandService.insert(command);
    }

    public int updateCommand(Command command){
        return commandService.update(command);
    }

    public void updateJobExecutionStatus(Long jobExecutionId, ExecutionStatus status){
        JobExecution jobExecution = getJobExecutionById(jobExecutionId);
        jobExecution.setStatus(status);
        updateJobExecution(jobExecution);
    }

    public void updateJobExecutionRetryTimes(Long jobExecutionId, int times) {
        JobExecution jobExecution = getJobExecutionById(jobExecutionId);
        jobExecution.setRetryTimes(times);
        updateJobExecution(jobExecution);
    }

    public JobExecutionRequest buildJobExecutionRequest(JobExecution jobExecution) throws ZhoudaDataCheckException {
        // need to convert job parameter to other parameter
        JobExecutionRequest jobExecutionRequest = new JobExecutionRequest();
        jobExecutionRequest.setJobExecutionId(jobExecution.getId());
        jobExecutionRequest.setJobExecutionName(jobExecution.getName());
        JobExecutionParameter jobExecutionParameter = JSONUtils.parseObject(jobExecution.getParameter(),JobExecutionParameter.class);
        if (jobExecutionParameter == null) {
            throw new ZhoudaDataCheckException("JobExecutionParameter can not be null");
        }

        jobExecutionRequest.setExecutePlatformType(jobExecution.getExecutePlatformType());
        //读取配置文件获取环境信息
        jobExecutionRequest.setExecutePlatformParameter(jobExecution.getExecutePlatformParameter());
        jobExecutionRequest.setEngineType(jobExecution.getEngineType());
        jobExecutionRequest.setEngineParameter(jobExecution.getEngineParameter());
        Map<String,String> inputParameter = new HashMap<>();
        Map<String, Object> defaultDataSourceMap = DefaultDataSourceInfoUtils.getDefaultDataSourceConfigMap();
        JobExecutionInfo jobExecutionInfo = new JobExecutionInfo(
                jobExecution.getId(), jobExecution.getName(),
                jobExecution.getEngineType(), jobExecution.getEngineParameter(),
                jobExecution.getErrorDataStorageType(), jobExecution.getErrorDataStorageParameter(), jobExecution.getErrorDataFileName(),
                String.valueOf(defaultDataSourceMap.get(SRC_CONNECTOR_TYPE)), JSONUtils.toJsonString(defaultDataSourceMap),
                jobExecutionParameter);
        ZhoudaDataCheckJobConfig qualityConfig =
                ZhoudaDataCheckConfigurationManager.generateConfiguration(jobExecution.getJobType(), inputParameter, jobExecutionInfo);
        jobExecutionRequest.setApplicationParameter(JSONUtils.toJsonString(qualityConfig));
        jobExecutionRequest.setTenantCode(jobExecution.getTenantCode());
        jobExecutionRequest.setRetryTimes(jobExecution.getRetryTimes());
        jobExecutionRequest.setRetryInterval(jobExecution.getRetryInterval());
        jobExecutionRequest.setTimeout(jobExecution.getTimeout());
        jobExecutionRequest.setTimeoutStrategy(jobExecution.getTimeoutStrategy());
        jobExecutionRequest.setEnv(jobExecution.getEnv());
        return jobExecutionRequest;
    }

    public JobExecutionResult getJobExecutionResultByJobExecutionId(long jobExecutionId) {
        return jobExecutionResultService.getByJobExecutionId(jobExecutionId);
    }

    public List<JobExecutionResult> listErrorJobExecutionResultByJobExecutionId(long jobExecutionId) {
        return jobExecutionResultService.listByErrorJobExecutionId(jobExecutionId);
    }

    public List<JobExecutionResult> listJobExecutionResultByJobExecutionId(long jobExecutionId) {
        return jobExecutionResultService.listByJobExecutionId(jobExecutionId);
    }

    public int deleteJobExecutionResultByJobExecutionId(long jobExecutionId) {
        return jobExecutionResultService.deleteByJobExecutionId(jobExecutionId);
    }

    public int deleteActualValuesByJobExecutionId(long jobExecutionId) {
        return actualValuesService.deleteByJobExecutionId(jobExecutionId);
    }

    public int updateJobExecutionResult(JobExecutionResult jobExecutionResult) {
        return jobExecutionResultService.update(jobExecutionResult);
    }

    public List<JobExecution> getJobExecutionListNeedFailover(String host){
        return jobExecutionService.listNeedFailover(host);
    }

    public List<JobExecution> getJobExecutionListNeedFailover(List<String> host){
        return jobExecutionService.listJobExecutionNotInServerList(host);
    }

    public JobService getJobService() {
        return jobService;
    }

    public DataSourceService getDataSourceService() {
        return dataSourceService;
    }

    public CommonTaskService getCatalogTaskService() {
        return commonTaskService;
    }

    public JobExecutionResultService getJobExecutionResultService() {
        return jobExecutionResultService;
    }

    public void refreshCommonProperties() {
        try {
            this.configService.refreshCommonProperties();
        } catch (Exception e) {
            log.warn("refresh common properties error", e);
        }
    }
}
