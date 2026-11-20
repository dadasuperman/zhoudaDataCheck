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
package io.zhoudadatacheck.server.dqc.coordinator.quartz;

import io.zhoudadatacheck.common.utils.DateUtils;
import io.zhoudadatacheck.core.constant.ZhoudaDataCheckConstants;
import io.zhoudadatacheck.server.api.dto.bo.catalog.CatalogRefresh;
import io.zhoudadatacheck.server.enums.CommonTaskType;
import io.zhoudadatacheck.server.repository.entity.DataSource;
import io.zhoudadatacheck.server.repository.service.CommonTaskService;
import io.zhoudadatacheck.server.repository.service.impl.JobExternalService;
import io.zhoudadatacheck.server.utils.SpringApplicationContext;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class CatalogTaskScheduleJob implements org.quartz.Job {

    /**
     * logger of FlowScheduleJob
     */
    private static final Logger logger = LoggerFactory.getLogger(CatalogTaskScheduleJob.class);

    public JobExternalService getJobExternalService(){
        return SpringApplicationContext.getBean(JobExternalService.class);
    }

    /**
     * Called by the Scheduler when a Trigger fires that is associated with the Job
     *
     * @param context JobExecutionContext
     */
    @Override
    public void execute(JobExecutionContext context) {

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        Long dataSourceId = dataMap.getLong(ZhoudaDataCheckConstants.DATASOURCE_ID);

        LocalDateTime scheduleTime = DateUtils.date2LocalDateTime(context.getScheduledFireTime());
        LocalDateTime fireTime = DateUtils.date2LocalDateTime(context.getFireTime());

        logger.info("scheduled fire time :{}, fire time :{}, dataSource id :{}", scheduleTime, fireTime, dataSourceId);
        logger.info("scheduled start work , dataSource id :{} ", dataSourceId);

        CommonTaskService commonTaskService = getJobExternalService().getCatalogTaskService();
        CatalogRefresh catalogRefresh = new CatalogRefresh();
        catalogRefresh.setDatasourceId(dataSourceId);
        if (dataMap.get(ZhoudaDataCheckConstants.COMMON_TASK_TYPE) != null) {
            Integer taskType = dataMap.getInt(ZhoudaDataCheckConstants.COMMON_TASK_TYPE);
            catalogRefresh.setTaskType(CommonTaskType.of(taskType));
        }

        DataSource dataSource = getJobExternalService().getDataSourceService().getDataSourceById(dataSourceId);
        if (dataSource == null) {
            logger.warn("dataSource {} is null", dataSourceId);
            return;
        }

        commonTaskService.refreshCatalog(catalogRefresh);
    }

}
