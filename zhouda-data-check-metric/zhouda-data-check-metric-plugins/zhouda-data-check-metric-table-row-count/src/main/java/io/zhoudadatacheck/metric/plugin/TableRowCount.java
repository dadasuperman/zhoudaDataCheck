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

import io.zhoudadatacheck.common.entity.ExecuteSql;
import io.zhoudadatacheck.common.enums.ZhoudaDataCheckDataType;
import io.zhoudadatacheck.common.utils.StringUtils;
import io.zhoudadatacheck.metric.api.ConfigItem;
import io.zhoudadatacheck.metric.api.MetricDimension;
import io.zhoudadatacheck.metric.api.MetricType;
import io.zhoudadatacheck.metric.plugin.base.BaseSingleTable;

import java.util.*;

import static io.zhoudadatacheck.common.CommonConstants.TABLE;
import static io.zhoudadatacheck.common.ConfigConstants.METRIC_UNIQUE_KEY;

public class TableRowCount extends BaseSingleTable {

    public TableRowCount() {
        super();
    }

    @Override
    public String getName() {
        return "table_row_count";
    }

    @Override
    public String getZhName() {
        return "表行数检查";
    }

    @Override
    public MetricDimension getDimension() {
        return MetricDimension.COMPLETENESS;
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
    public void prepare(Map<String, String> config) {
        super.prepare(config);
    }

    @Override
    public Map<String, ConfigItem> getConfigMap() {
        configMap.put("table",new ConfigItem("table", "表名", "table", true));
        return configMap;
    }

    @Override
    public ExecuteSql getInvalidateItems(Map<String,String> inputParameter) {
        return null;
    }

    @Override
    public ExecuteSql getActualValue(Map<String,String> inputParameter) {
        String uniqueKey = inputParameter.get(METRIC_UNIQUE_KEY);
        ExecuteSql executeSql = new ExecuteSql();
        executeSql.setResultTable("invalidate_count_" + uniqueKey);
        StringBuilder actualValueSql = new StringBuilder();
        actualValueSql.append("select count(1) as actual_value_").append(uniqueKey).append(" from ${table}");
        if (!filters.isEmpty()) {
            actualValueSql.append(" where ").append(String.join(" and ", filters));
        }

        executeSql.setSql(actualValueSql.toString());
        executeSql.setErrorOutput(false);
        return executeSql;
    }

    @Override
    public List<ZhoudaDataCheckDataType> suitableType() {
        return Collections.emptyList();
    }

    @Override
    public boolean supportMultiple() {
        return true;
    }

    @Override
    public List<Map<String, Object>> getMetricParameter(Map<String, Object> metricParameter) {
        ArrayList<Map<String, Object>> result = new ArrayList<>();
        String table = String.valueOf(metricParameter.get(TABLE));
        if (StringUtils.isEmpty(table)) {
            return super.getMetricParameter(metricParameter);
        }

        String[] tables = table.split(",");
        if (tables.length == 1) {
            return super.getMetricParameter(metricParameter);
        } else {
            for (String s : tables) {
                Map<String, Object> newMetricParameter = new HashMap<>(metricParameter);
                newMetricParameter.put("table", s);
                result.add(newMetricParameter);
            }
        }

        return result;
    }

    @Override
    public String getPluginName() {
        return "table_row_count";
    }
}
