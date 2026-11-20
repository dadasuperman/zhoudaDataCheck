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
package io.zhoudadatacheck.server.scheduler;

import io.zhoudadatacheck.common.utils.*;
import io.zhoudadatacheck.server.registry.Register;
import io.zhoudadatacheck.server.repository.entity.CommonTaskCommand;
import io.zhoudadatacheck.server.repository.entity.CommonTask;
import io.zhoudadatacheck.server.repository.service.impl.JobExternalService;
import io.zhoudadatacheck.server.utils.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;

import static io.zhoudadatacheck.common.CommonConstants.SLEEP_TIME_MILLIS;
import static io.zhoudadatacheck.common.utils.CommonPropertyUtils.*;

@Slf4j
public class CommonTaskScheduler extends Thread {

    private static final int[] RETRY_BACKOFF = {1, 2, 3, 5, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};

    private final JobExternalService jobExternalService;

    private final CommonTaskManager commonTaskManager;

    private final Register register;

    public CommonTaskScheduler(CommonTaskManager commonTaskManager, Register register){
        this.jobExternalService = SpringApplicationContext.getBean(JobExternalService.class);
        this.commonTaskManager = commonTaskManager;
        this.register = register;
    }

    @Override
    public void run() {
        log.info("common task scheduler started");

        int retryNum = 0;
        while (Stopper.isRunning()) {
            CommonTaskCommand command = null;
            try {
                boolean runCheckFlag = OSUtils.checkResource(
                        CommonPropertyUtils.getDouble(MAX_CPU_LOAD_AVG, MAX_CPU_LOAD_AVG_DEFAULT),
                        CommonPropertyUtils.getDouble(RESERVED_MEMORY, RESERVED_MEMORY_DEFAULT));

                if (!runCheckFlag) {
                    ThreadUtils.sleep(SLEEP_TIME_MILLIS);
                    continue;
                }

                command = jobExternalService.getCatalogCommand(register.getTotalSlot(), register.getSlot());

                if (command != null) {
                    CommonTask task = jobExternalService.executeCatalogCommand(command);
                    if (task != null) {
                        log.info("start submit catalog metadata fetch task : {} ", JSONUtils.toJsonString(task));
                        commonTaskManager.putCommonTask(task);
                        log.info(String.format("submit success, catalog metadata fetch task : %s", task.getParameter()) );
                    } else {
                        log.warn("catalog metadata fetch task {} is null", command.getTaskId());
                    }
                    jobExternalService.deleteCatalogCommandById(command.getId());
                    ThreadUtils.sleep(SLEEP_TIME_MILLIS);
                } else {
                    ThreadUtils.sleep(SLEEP_TIME_MILLIS * 2);
                }

                retryNum = 0;
            } catch (Exception e){
                retryNum++;

                log.error("schedule catalog metadata fetch task error ", e);
                ThreadUtils.sleep(SLEEP_TIME_MILLIS * RETRY_BACKOFF [retryNum % RETRY_BACKOFF.length]);
            }
        }
    }
}
