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

import io.zhoudadatacheck.common.config.CheckResult;
import io.zhoudadatacheck.common.entity.ExecuteSql;
import io.zhoudadatacheck.common.enums.ZhoudaDataCheckDataType;
import io.zhoudadatacheck.metric.api.ConfigItem;
import io.zhoudadatacheck.metric.api.MetricDimension;
import io.zhoudadatacheck.metric.api.MetricType;
import io.zhoudadatacheck.metric.api.SqlMetric;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MultiTableValueComparison implements SqlMetric {

    @Override
    public String getName() {
        return "multi_table_value_comparison";
    }

    @Override
    public String getZhName() {
        return "两表值比对";
    }

    @Override
    public MetricDimension getDimension() {
        return MetricDimension.ACCURACY;
    }

    @Override
    public MetricType getType() {
        return MetricType.MULTI_TABLE_VALUE_COMPARISON;
    }

    @Override
    public boolean isInvalidateItemsCanOutput() {
        return false;
    }

    @Override
    public ExecuteSql getInvalidateItems(Map<String,String> inputParameter) {
        return null;
    }

    @Override
    public ExecuteSql getActualValue(Map<String,String> inputParameter) {
        return null;
    }

    @Override
    public CheckResult validateConfig(Map<String, Object> config) {
        return null;
    }

    @Override
    public Map<String, ConfigItem> getConfigMap() {
        return null;
    }

    @Override
    public void prepare(Map<String, String> config) {

    }

    @Override
    public List<ZhoudaDataCheckDataType> suitableType() {
        return Collections.emptyList();
    }

    @Override
    public String getPluginName() {
        return "multi_table_value_comparison";
    }
}
