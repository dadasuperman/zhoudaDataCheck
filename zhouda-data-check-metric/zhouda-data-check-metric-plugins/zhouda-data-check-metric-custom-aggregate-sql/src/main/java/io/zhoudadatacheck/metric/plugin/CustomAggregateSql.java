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
package io.zhoudadatacheck.metric.plugin;

import java.util.*;

import io.zhoudadatacheck.common.config.CheckResult;
import io.zhoudadatacheck.common.config.ConfigChecker;
import io.zhoudadatacheck.common.entity.ExecuteSql;
import io.zhoudadatacheck.common.enums.ZhoudaDataCheckDataType;
import io.zhoudadatacheck.common.utils.StringUtils;
import io.zhoudadatacheck.metric.api.ConfigItem;
import io.zhoudadatacheck.metric.api.MetricDimension;
import io.zhoudadatacheck.metric.api.MetricType;
import io.zhoudadatacheck.metric.api.SqlMetric;

import static io.zhoudadatacheck.common.CommonConstants.TABLE;
import static io.zhoudadatacheck.common.ConfigConstants.*;
import static io.zhoudadatacheck.common.ConfigConstants.METRIC_UNIQUE_KEY;

public class CustomAggregateSql implements SqlMetric {

    private final Set<String> requiredOptions = new HashSet<>();

    private final HashMap<String,ConfigItem> configMap = new HashMap<>();

    public CustomAggregateSql() {
//        configMap.put("database",new ConfigItem("table", "表名", "table"));
        configMap.put("actual_aggregate_sql", new ConfigItem("actual_aggregate_sql","自定义聚合SQL","actual_aggregate_sql"));
//        configMap.put("filter", new ConfigItem("filter", "过滤条件", "filter"));

        requiredOptions.add("actual_aggregate_sql");
//        requiredOptions.add("table");
    }

    @Override
    public String getName() {
        return "custom_aggregate_sql";
    }

    @Override
    public String getZhName() {
        return "自定义聚合SQL";
    }

    @Override
    public MetricDimension getDimension() {
        return MetricDimension.ACCURACY;
    }

    @Override
    public MetricType getType() {
        return MetricType.SINGLE_TABLE;
    }

    @Override
    public boolean isInvalidateItemsCanOutput() {
        return false;
    }

    @Override
    public CheckResult validateConfig(Map<String, Object> config) {
        return ConfigChecker.checkConfig(config, requiredOptions);
    }

    @Override
    public void prepare(Map<String, String> config) {

    }

    @Override
    public Map<String, ConfigItem> getConfigMap() {
        return configMap;
    }

    @Override
    public ExecuteSql getInvalidateItems(Map<String,String> inputParameter) {
        return null;
    }

    @Override
    public ExecuteSql getActualValue(Map<String,String> inputParameter) {
        inputParameter.put(ACTUAL_TABLE, inputParameter.get(TABLE));
        String actualAggregateSql = inputParameter.get(ACTUAL_AGGREGATE_SQL);
        if (StringUtils.isNotEmpty(actualAggregateSql)) {
            if (actualAggregateSql.contains("as actual_value")) {
                actualAggregateSql = actualAggregateSql.replace("as actual_value", "as actual_value_" + inputParameter.get(METRIC_UNIQUE_KEY));
            } else if (actualAggregateSql.contains("AS actual_value")) {
                actualAggregateSql = actualAggregateSql.replace("AS actual_value", "as actual_value_" + inputParameter.get(METRIC_UNIQUE_KEY));
            }
        }
        return new ExecuteSql(actualAggregateSql, inputParameter.get(TABLE));
    }

    @Override
    public List<ZhoudaDataCheckDataType> suitableType() {
        return Collections.emptyList();
    }

    @Override
    public boolean isCustomSql() {
        return true;
    }

    @Override
    public String getPluginName() {
        return "custom_aggregate_sql";
    }
}
