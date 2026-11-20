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

import io.zhoudadatacheck.common.enums.ZhoudaDataCheckDataType;
import io.zhoudadatacheck.metric.api.MetricDimension;
import io.zhoudadatacheck.metric.api.MetricDirectionType;
import io.zhoudadatacheck.metric.api.MetricType;
import io.zhoudadatacheck.metric.plugin.base.BaseSingleTableColumnNotUseView;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ColumnNull extends BaseSingleTableColumnNotUseView {

    public ColumnNull(){
        super();
    }

    @Override
    public String getName() {
        return "column_null";
    }

    @Override
    public String getZhName() {
        return "空值检查";
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
        return true;
    }

    @Override
    public void prepare(Map<String, String> config) {
        if (config.containsKey("column")) {
            filters.add(getConnectorFactory(config).getMetricScript().columnIsNull());
        }
        super.prepare(config);
    }

    @Override
    public List<ZhoudaDataCheckDataType> suitableType() {
        return Arrays.asList(ZhoudaDataCheckDataType.NUMERIC_TYPE, ZhoudaDataCheckDataType.STRING_TYPE, ZhoudaDataCheckDataType.DATE_TIME_TYPE);
    }

    @Override
    public MetricDirectionType getDirectionType() {
        return MetricDirectionType.NEGATIVE;
    }

    @Override
    public String getPluginName() {
        return "column_null";
    }
}
