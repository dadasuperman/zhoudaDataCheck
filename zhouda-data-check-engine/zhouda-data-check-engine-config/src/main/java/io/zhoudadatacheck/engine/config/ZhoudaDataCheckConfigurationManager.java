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
package io.zhoudadatacheck.engine.config;

import io.zhoudadatacheck.common.config.ZhoudaDataCheckJobConfig;
import io.zhoudadatacheck.common.entity.JobExecutionInfo;
import io.zhoudadatacheck.common.entity.JobExecutionParameter;

import io.zhoudadatacheck.common.entity.job.BaseJobParameter;
import io.zhoudadatacheck.common.entity.job.DataQualityJobParameter;
import io.zhoudadatacheck.common.enums.JobType;
import io.zhoudadatacheck.common.exception.ZhoudaDataCheckException;
import io.zhoudadatacheck.common.utils.StringUtils;
import io.zhoudadatacheck.metric.api.SqlMetric;
import io.zhoudadatacheck.spi.PluginDiscovery;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ZhoudaDataCheckConfigurationManager {

    private static ZhoudaDataCheckJobConfig buildDataQualityConfiguration(JobConfigurationBuilder builder) throws ZhoudaDataCheckException {
        builder.buildName();
        builder.buildEnvConfig();
        builder.buildSourceConfigs();
        builder.buildTransformConfigs();
        builder.buildSinkConfigs();
        return builder.build();
    }

    public static ZhoudaDataCheckJobConfig generateConfiguration(JobType jobType,
            Map<String, String> inputParameter,
            JobExecutionInfo jobExecutionInfo) throws ZhoudaDataCheckException {

        if (jobExecutionInfo == null) {
            throw new ZhoudaDataCheckException("jobExecutionInfo can not be null");
        }

        if (jobExecutionInfo.getJobExecutionParameter() == null) {
            throw new ZhoudaDataCheckException("task parameter can not be null");
        }

        JobExecutionParameter jobExecutionParameter = jobExecutionInfo.getJobExecutionParameter();
        List<BaseJobParameter> jobParameters = jobExecutionParameter.getMetricParameterList();
        if (CollectionUtils.isEmpty(jobParameters)) {
            throw new ZhoudaDataCheckException("metric parameter can not be null");
        }

        SqlMetric sqlMetric = null;
        for (BaseJobParameter jobParameter : jobParameters) {
            String metricType = jobParameter.getMetricType();
            if (StringUtils.isEmpty(metricType)) {
                throw new ZhoudaDataCheckException("metric type can not be null");
            }

            sqlMetric = PluginDiscovery.getMultiKeyPluginDiscovery(SqlMetric.class, SqlMetric::getPluginNames)
                    
                    .getNewPlugin(metricType);

            if (sqlMetric == null) {
                throw new ZhoudaDataCheckException("can not find the metric: " + metricType);
            }
        }

        if (sqlMetric == null) {
            throw new ZhoudaDataCheckException("can not find the metric");
        }

        JobConfigurationBuilder builder = PluginDiscovery.getMultiKeyPluginDiscovery(JobConfigurationBuilder.class, JobConfigurationBuilder::getPluginNames)
                
                .getNewPlugin(jobExecutionInfo.getEngineType() + "_" + sqlMetric.getType().getDescription());

        if (jobType == JobType.DATA_PROFILE) {
            builder = PluginDiscovery.getMultiKeyPluginDiscovery(JobConfigurationBuilder.class, JobConfigurationBuilder::getPluginNames)
                    
                    .getNewPlugin(jobExecutionInfo.getEngineType() + "_data_profile");
        }

        builder.init(inputParameter, jobExecutionInfo);

        return buildDataQualityConfiguration(builder);
    }

}
