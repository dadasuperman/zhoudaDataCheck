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
package io.zhoudadatacheck.engine.flink.core;

import io.zhoudadatacheck.common.config.Config;
import io.zhoudadatacheck.common.config.ZhoudaDataCheckJobConfig;
import io.zhoudadatacheck.engine.api.component.Component;
import io.zhoudadatacheck.engine.api.env.RuntimeEnvironment;
import io.zhoudadatacheck.engine.core.BaseZhoudaDataCheckBootstrap;
import io.zhoudadatacheck.engine.core.enums.ConnectorType;
import io.zhoudadatacheck.engine.flink.api.FlinkRuntimeEnvironment;
import io.zhoudadatacheck.engine.flink.api.stream.FlinkStreamSink;
import io.zhoudadatacheck.engine.flink.api.stream.FlinkStreamSource;
import io.zhoudadatacheck.engine.flink.jdbc.sink.JdbcSink;
import io.zhoudadatacheck.engine.flink.jdbc.sink.MySQLSink;
import io.zhoudadatacheck.engine.flink.jdbc.source.JdbcSource;
import io.zhoudadatacheck.engine.flink.transform.sql.SqlTransform;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static io.zhoudadatacheck.engine.api.EngineConstants.PLUGIN_TYPE;
import static io.zhoudadatacheck.engine.api.EngineConstants.TYPE;

@Slf4j
public class FlinkZhoudaDataCheckBootstrap extends BaseZhoudaDataCheckBootstrap {

    public static void main(String[] args) {
        FlinkZhoudaDataCheckBootstrap bootstrap = new FlinkZhoudaDataCheckBootstrap();

        if (args.length == 1) {
            String arg = args[0];
            args[0] = new String(Base64.getDecoder().decode(arg));
            bootstrap.execute(args);
        }
    }

    @Override
    protected RuntimeEnvironment createRuntimeEnvironment(ZhoudaDataCheckJobConfig config) {
        FlinkRuntimeEnvironment runtimeEnvironment = new FlinkRuntimeEnvironment();
        Config runtimeConfig = new Config(config.getEnvConfig().getConfig());
        runtimeConfig.put(TYPE, config.getEnvConfig().getType());
        runtimeEnvironment.setConfig(runtimeConfig);
        runtimeEnvironment.prepare();
        return runtimeEnvironment;
    }

    @Override
    protected List<Component> createSources(ZhoudaDataCheckJobConfig config) {
        return config.getSourceParameters().stream().map(sourceConfig -> {
            FlinkStreamSource source;
            switch (ConnectorType.of(sourceConfig.getPlugin())) {
                case JDBC:
                    source = new JdbcSource();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported flink source plugin: " + sourceConfig.getPlugin());
            }
            sourceConfig.getConfig().put(PLUGIN_TYPE, sourceConfig.getType());
            source.setConfig(new Config(sourceConfig.getConfig()));
            return (Component) source;
        }).collect(Collectors.toList());
    }

    @Override
    protected List<Component> createTransforms(ZhoudaDataCheckJobConfig config) {
        return config.getTransformParameters().stream().map(transformConfig -> {
            SqlTransform transform = new SqlTransform();
            transformConfig.getConfig().put(PLUGIN_TYPE, transformConfig.getType());
            transform.setConfig(new Config(transformConfig.getConfig()));
            return (Component) transform;
        }).collect(Collectors.toList());
    }

    @Override
    protected List<Component> createSinks(ZhoudaDataCheckJobConfig config) {
        return config.getSinkParameters().stream().map(sinkConfig -> {
            FlinkStreamSink sink;
            switch (ConnectorType.of(sinkConfig.getPlugin())) {
                case JDBC:
                    sink = new JdbcSink();
                    break;
                case MYSQL:
                    sink = new MySQLSink();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported flink sink plugin: " + sinkConfig.getPlugin());
            }
            sinkConfig.getConfig().put(PLUGIN_TYPE, sinkConfig.getType());
            sink.setConfig(new Config(sinkConfig.getConfig()));
            return (Component) sink;
        }).collect(Collectors.toList());
    }
}
