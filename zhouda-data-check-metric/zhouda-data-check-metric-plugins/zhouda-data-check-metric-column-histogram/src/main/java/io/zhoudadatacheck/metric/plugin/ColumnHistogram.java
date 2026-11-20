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
import io.zhoudadatacheck.connector.api.ConnectorFactory;
import io.zhoudadatacheck.metric.api.MetricActualValueType;
import io.zhoudadatacheck.metric.api.MetricDimension;
import io.zhoudadatacheck.metric.api.MetricType;
import io.zhoudadatacheck.metric.plugin.base.BaseSingleTableColumn;
import io.zhoudadatacheck.spi.PluginDiscovery;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.zhoudadatacheck.common.ConfigConstants.METRIC_UNIQUE_KEY;

public class ColumnHistogram extends BaseSingleTableColumn {

    public ColumnHistogram(){
        super();
    }

    @Override
    public String getName() {
        return "column_histogram";
    }

    @Override
    public String getZhName() {
        return "数据分布检查";
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
    public void prepare(Map<String, String> config) {
        super.prepare(config);
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

        String engineType = inputParameter.get("engine_type");
        String where = "";
        switch (engineType){
            case "spark":
                if (!filters.isEmpty()) {
                    where = " where " + String.join(" and ", filters);
                }
                actualValueSql.append(getConnectorFactory().getMetricScript().histogramActualValue(uniqueKey,where));
                break;
            case "local":
                if (!filters.isEmpty()) {
                    where = " where " + String.join(" and ", filters);
                }
                actualValueSql.append(getConnectorFactory(inputParameter).getMetricScript().histogramActualValue(uniqueKey,where));
                break;
            default:
                break;
        }

        executeSql.setSql(actualValueSql.toString());
        executeSql.setErrorOutput(false);
        return executeSql;
    }

    @Override
    public String getActualValueType() {
        return MetricActualValueType.LIST.getDescription();
    }

    @Override
    public List<ZhoudaDataCheckDataType> suitableType() {
        return Arrays.asList(ZhoudaDataCheckDataType.NUMERIC_TYPE, ZhoudaDataCheckDataType.STRING_TYPE, ZhoudaDataCheckDataType.DATE_TIME_TYPE);
    }

    private ConnectorFactory getConnectorFactory() {
        return PluginDiscovery.getMultiKeyPluginDiscovery(ConnectorFactory.class, ConnectorFactory::getPluginNames).getOrCreatePlugin("spark");
    }

    @Override
    public String getPluginName() {
        return "column_histogram";
    }
}
