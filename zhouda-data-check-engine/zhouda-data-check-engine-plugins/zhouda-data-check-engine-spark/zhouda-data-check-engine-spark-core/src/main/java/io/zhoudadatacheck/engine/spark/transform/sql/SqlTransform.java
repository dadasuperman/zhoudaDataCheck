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
package io.zhoudadatacheck.engine.spark.transform.sql;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import io.zhoudadatacheck.common.config.CheckResult;
import io.zhoudadatacheck.common.config.Config;
import io.zhoudadatacheck.engine.api.env.RuntimeEnvironment;
import io.zhoudadatacheck.engine.spark.api.BaseSparkTransform;
import io.zhoudadatacheck.engine.spark.api.SparkRuntimeEnvironment;

import static io.zhoudadatacheck.common.ConfigConstants.SQL;

public class SqlTransform implements BaseSparkTransform {

    private Config config = new Config();

    @Override
    public void setConfig(Config config) {
        if(config != null) {
            this.config = config;
        }
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public CheckResult checkConfig() {
        if (config.has(SQL)) {
            return new CheckResult(true, "");
        } else {
            return new CheckResult(false, "please specify [sql]");
        }
    }

    @Override
    public void prepare(RuntimeEnvironment prepareEnv) {

    }

    @Override
    public Dataset<Row> process(Dataset<Row> data, SparkRuntimeEnvironment env) {
        return env.sparkSession().sql(config.getString(SQL));
    }

    @Override
    public java.util.Collection<String> getPluginNames() {
        return java.util.Arrays.asList("livy-batch-sql-transform", "spark-batch-sql-transform");
    }
}
