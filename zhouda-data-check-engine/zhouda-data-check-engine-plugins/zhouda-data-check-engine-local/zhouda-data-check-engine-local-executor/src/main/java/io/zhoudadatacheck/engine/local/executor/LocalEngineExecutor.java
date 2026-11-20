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
package io.zhoudadatacheck.engine.local.executor;

import io.zhoudadatacheck.common.config.Configurations;
import io.zhoudadatacheck.common.entity.JobExecutionRequest;
import io.zhoudadatacheck.common.entity.ProcessResult;
import io.zhoudadatacheck.common.utils.LoggerUtils;
import io.zhoudadatacheck.engine.executor.core.base.AbstractEngineExecutor;
import io.zhoudadatacheck.engine.local.core.LocalZhoudaDataCheckBootstrap;
import org.slf4j.Logger;

public class LocalEngineExecutor extends AbstractEngineExecutor {

    private LocalZhoudaDataCheckBootstrap bootstrap;

    @Override
    public void init(JobExecutionRequest jobExecutionRequest, Logger logger, Configurations configurations) throws Exception {
        logger.info("local engine init job execution [{}]", jobExecutionRequest.getJobExecutionId());
        String threadLoggerInfoName = String.format(LoggerUtils.JOB_LOG_INFO_FORMAT, jobExecutionRequest.getJobExecutionUniqueId());
        Thread.currentThread().setName(threadLoggerInfoName);
        this.jobExecutionRequest = jobExecutionRequest;
        this.logger = logger;
    }

    @Override
    public void execute() throws Exception {
        logger.info("local engine execute job execution [{}]", jobExecutionRequest.getJobExecutionId());
        String[] args = new String[1];
        args[0] = jobExecutionRequest.getApplicationParameter();
        bootstrap = new LocalZhoudaDataCheckBootstrap(this.logger);
        this.processResult = bootstrap.execute(args);
    }

    @Override
    public void after() throws Exception {
        logger.info("local engine job execution [{}] after", jobExecutionRequest.getJobExecutionId());
    }

    @Override
    public void cancel() throws Exception {
        logger.info("local engine cancel job [{}]", jobExecutionRequest.getJobExecutionId());
        if (bootstrap != null) {
            bootstrap.stop();
            this.cancel = true;
        }
    }

    @Override
    public ProcessResult getProcessResult() {
        return this.processResult;
    }

    @Override
    public JobExecutionRequest getTaskRequest() {
        return this.jobExecutionRequest;
    }

    @Override
    protected String buildCommand() {
        return null;
    }

    @Override
    public String getPluginName() {
        return "local";
    }
}
