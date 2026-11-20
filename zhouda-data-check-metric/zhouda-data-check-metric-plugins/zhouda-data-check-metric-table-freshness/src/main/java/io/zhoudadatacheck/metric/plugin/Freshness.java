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
import io.zhoudadatacheck.metric.api.ConfigItem;
import io.zhoudadatacheck.metric.api.MetricDimension;
import io.zhoudadatacheck.metric.api.MetricType;
import io.zhoudadatacheck.metric.plugin.base.BaseSingleTableColumnNotUseView;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Freshness extends BaseSingleTableColumnNotUseView {

    public Freshness(){
        configMap.put("begin_time",new ConfigItem("begin_time", "开始时间", "begin_time"));
        configMap.put("deadline_time",new ConfigItem("deadline_time", "结束时间", "deadline_time"));
        configMap.put("datetime_format",new ConfigItem("datetime_format", "时间格式", "datetime_format"));

        requiredOptions.add("begin_time");
        requiredOptions.add("deadline_time");
        requiredOptions.add("datetime_format");
    }

    @Override
    public String getName() {
        return "freshness";
    }

    @Override
    public String getZhName() {
        return "及时性检查";
    }

    @Override
    public MetricDimension getDimension() {
        return MetricDimension.TIMELINESS;
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

        if (config.containsKey("column") && config.containsKey("datetime_format") && config.containsKey("deadline_time") && config.containsKey("begin_time")) {
            filters.add(getConnectorFactory(config).getMetricScript().timeBetweenWithFormat());
        }
        super.prepare(config);
    }

    @Override
    public List<ZhoudaDataCheckDataType> suitableType() {
        return Arrays.asList(ZhoudaDataCheckDataType.NUMERIC_TYPE, ZhoudaDataCheckDataType.STRING_TYPE, ZhoudaDataCheckDataType.DATE_TIME_TYPE);
    }

    @Override
    public String getPluginName() {
        return "freshness";
    }
}
