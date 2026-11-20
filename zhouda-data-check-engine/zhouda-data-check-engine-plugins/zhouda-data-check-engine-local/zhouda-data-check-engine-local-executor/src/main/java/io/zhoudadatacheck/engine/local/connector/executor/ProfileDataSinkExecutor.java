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
package io.zhoudadatacheck.engine.local.connector.executor;

import io.zhoudadatacheck.common.config.Config;
import io.zhoudadatacheck.common.exception.ZhoudaDataCheckException;
import io.zhoudadatacheck.engine.local.api.LocalRuntimeEnvironment;
import io.zhoudadatacheck.engine.local.api.entity.ConnectionHolder;
import io.zhoudadatacheck.engine.local.api.utils.LoggerFactory;
import org.slf4j.Logger;

import java.util.Map;

public class ProfileDataSinkExecutor extends BaseDataSinkExecutor {

    protected Logger log = LoggerFactory.getLogger(ProfileDataSinkExecutor.class);

    public ProfileDataSinkExecutor(Config config, LocalRuntimeEnvironment env) {
        super(config, env);
        if (env.getMetadataConnection() == null) {
            env.setMetadataConnection(new ConnectionHolder(config));
        }
    }

    @Override
    public void execute(Map<String, String> inputParameter) throws ZhoudaDataCheckException {
        try {
            innerExecute(inputParameter);
        } catch (Exception e) {
            log.error("sink profile data error : ", e);
            throw new ZhoudaDataCheckException("sink profile data error", e);
        } finally {
            after(env, config);
        }
    }
}
