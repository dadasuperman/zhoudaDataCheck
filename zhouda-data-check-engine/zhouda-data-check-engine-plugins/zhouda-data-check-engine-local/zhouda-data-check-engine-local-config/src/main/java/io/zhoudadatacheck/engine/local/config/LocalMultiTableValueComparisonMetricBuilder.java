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
package io.zhoudadatacheck.engine.local.config;

import io.zhoudadatacheck.common.config.SinkConfig;
import io.zhoudadatacheck.common.config.TransformConfig;
import io.zhoudadatacheck.common.config.enums.SinkType;
import io.zhoudadatacheck.common.config.enums.TransformType;
import io.zhoudadatacheck.common.entity.ExecuteSql;
import io.zhoudadatacheck.common.entity.job.BaseJobParameter;
import io.zhoudadatacheck.common.exception.ZhoudaDataCheckException;
import io.zhoudadatacheck.common.utils.JSONUtils;
import io.zhoudadatacheck.common.utils.StringUtils;
import io.zhoudadatacheck.connector.api.ConnectorFactory;
import io.zhoudadatacheck.engine.config.MetricParserUtils;
import io.zhoudadatacheck.metric.api.ExpectedValue;
import io.zhoudadatacheck.metric.api.SqlMetric;
import io.zhoudadatacheck.spi.PluginDiscovery;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.zhoudadatacheck.common.CommonConstants.TABLE2;
import static io.zhoudadatacheck.common.ConfigConstants.*;
import static io.zhoudadatacheck.engine.config.MetricParserUtils.generateUniqueCode;

public class LocalMultiTableValueComparisonMetricBuilder extends BaseLocalConfigurationBuilder {

    @Override
    public void buildTransformConfigs() {
        List<BaseJobParameter> metricJobParameterList = jobExecutionParameter.getMetricParameterList();
        if (CollectionUtils.isNotEmpty(metricJobParameterList)) {
            for (BaseJobParameter parameter : metricJobParameterList) {
                String metricUniqueKey = getMetricUniqueKey(parameter);
                Map<String, String> metricInputParameter = metric2InputParameter.get(metricUniqueKey);
                metricInputParameter.put(METRIC_UNIQUE_KEY, metricUniqueKey);
                String metricType = parameter.getMetricType();
                SqlMetric sqlMetric = PluginDiscovery.getMultiKeyPluginDiscovery(SqlMetric.class, SqlMetric::getPluginNames)
                        
                        .getNewPlugin(metricType);

                MetricParserUtils.operateInputParameter(metricInputParameter, sqlMetric, jobExecutionInfo);

                List<TransformConfig> transformConfigs = new ArrayList<>();
                //get actual value execute sql
                MetricParserUtils.setTransformerConfig(
                        metricInputParameter,
                        transformConfigs,
                        getActualValueExecuteSql(metricInputParameter),
                        TransformType.ACTUAL_VALUE.getDescription());

                // get expected value execute sql
                MetricParserUtils.setTransformerConfig(
                        metricInputParameter,
                        transformConfigs,
                        getExceptedValueExecuteSql(metricInputParameter),
                        TransformType.EXPECTED_VALUE_FROM_TARGET_SOURCE.getDescription());

                metricInputParameter.put(UNIQUE_CODE, StringUtils.wrapperSingleQuotes(generateUniqueCode(metricInputParameter)));

                configuration.setTransformParameters(transformConfigs);

                metric2InputParameter.put(metricUniqueKey, metricInputParameter);
            }
        }
    }

    private ExecuteSql getActualValueExecuteSql(Map<String, String> inputParameter) {
        inputParameter.put(ACTUAL_TABLE, inputParameter.get(TABLE));
        String actualExecuteSql = inputParameter.get(ACTUAL_EXECUTE_SQL);
        if (StringUtils.isNotEmpty(actualExecuteSql)) {
            actualExecuteSql = actualExecuteSql.replace("as actual_value", "as actual_value_" + inputParameter.get(METRIC_UNIQUE_KEY));
        }
        return new ExecuteSql(actualExecuteSql, inputParameter.get(TABLE));
    }

    private ExecuteSql getExceptedValueExecuteSql(Map<String, String> inputParameter) {
        inputParameter.put(EXPECTED_TABLE, inputParameter.get(TABLE2));
        String expectedExecuteSql = inputParameter.get(EXPECTED_EXECUTE_SQL);
        if (StringUtils.isNotEmpty(expectedExecuteSql)) {
            expectedExecuteSql = expectedExecuteSql.replace("as expected_value", "as expected_value_" + inputParameter.get(METRIC_UNIQUE_KEY));
        }
        return new ExecuteSql(expectedExecuteSql, inputParameter.get(TABLE2));
    }

    @Override
    public void buildSinkConfigs() throws ZhoudaDataCheckException {
        List<SinkConfig> sinkConfigs = new ArrayList<>();

        List<BaseJobParameter> metricJobParameterList = jobExecutionParameter.getMetricParameterList();
        if (CollectionUtils.isNotEmpty(metricJobParameterList)) {
            for (BaseJobParameter parameter : metricJobParameterList) {
                String metricUniqueKey = getMetricUniqueKey(parameter);
                Map<String, String> metricInputParameter = metric2InputParameter.get(metricUniqueKey);
                metricInputParameter.put(METRIC_UNIQUE_KEY, metricUniqueKey);
                String expectedType = jobExecutionInfo.getEngineType() + "_" + parameter.getExpectedType();
                ExpectedValue expectedValue = PluginDiscovery.getMultiKeyPluginDiscovery(ExpectedValue.class, ExpectedValue::getPluginNames)
                        .getNewPlugin(expectedType);

                String validateResultSinkSql = SinkSqlBuilder.getJobExecutionResultSql()
                        .replace("${actual_value}", "${actual_value_" + metricUniqueKey + "}")
                        .replace("${expected_value}", "${expected_value_" + metricUniqueKey + "}");
                //get the task data storage parameter
                SinkConfig taskResultSinkConfig = getValidateResultDataSinkConfig(
                        expectedValue, validateResultSinkSql,  "dv_job_execution_result", metricInputParameter);
                taskResultSinkConfig.setType(SinkType.VALIDATE_RESULT.getDescription());
                sinkConfigs.add(taskResultSinkConfig);

                String actualValueSinkSql = SinkSqlBuilder.getActualValueSql()
                        .replace("${actual_value}", "${actual_value_" + metricUniqueKey + "}")
                        .replace("${expected_value}", "${expected_value_" + metricUniqueKey + "}");
                //get the actual value storage parameter
                SinkConfig actualValueSinkConfig = getValidateResultDataSinkConfig(
                        expectedValue, actualValueSinkSql, "dv_actual_values", metricInputParameter);
                actualValueSinkConfig.setType(SinkType.ACTUAL_VALUE.getDescription());
                sinkConfigs.add(actualValueSinkConfig);
            }
        }

        configuration.setSinkParameters(sinkConfigs);
    }

    @Override
    public java.util.Collection<String> getPluginNames() {
        return java.util.Collections.singletonList("local_multi_table_value_comparison");
    }
}
