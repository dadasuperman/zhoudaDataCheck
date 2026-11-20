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
package io.zhoudadatacheck.server;

import com.zaxxer.hikari.HikariDataSource;
import io.zhoudadatacheck.common.datasource.jdbc.JdbcDataSourceManager;
import io.zhoudadatacheck.common.utils.CommonPropertyUtils;
import io.zhoudadatacheck.common.utils.Stopper;
import io.zhoudadatacheck.common.utils.ThreadUtils;
import io.zhoudadatacheck.core.constant.ZhoudaDataCheckConstants;
import io.zhoudadatacheck.registry.api.Registry;
import io.zhoudadatacheck.server.scheduler.CommonTaskManager;
import io.zhoudadatacheck.server.scheduler.CommonTaskScheduler;
import io.zhoudadatacheck.server.scheduler.CommonTaskFailover;
import io.zhoudadatacheck.server.dqc.coordinator.cache.JobExecutionResponseProcessor;
import io.zhoudadatacheck.server.registry.Register;
import io.zhoudadatacheck.server.dqc.coordinator.cache.JobExecuteManager;
import io.zhoudadatacheck.server.dqc.coordinator.failover.JobExecutionFailover;
import io.zhoudadatacheck.server.dqc.coordinator.runner.JobScheduler;
import io.zhoudadatacheck.server.registry.RegistryHolder;
import io.zhoudadatacheck.server.utils.SpringApplicationContext;
import io.zhoudadatacheck.spi.PluginDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

@SpringBootApplication(scanBasePackages = {"io.zhoudadatacheck"},
        exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class ZhoudaDataCheckServer {

    private static final Logger logger = LoggerFactory.getLogger(ZhoudaDataCheckServer.class);

    @Autowired
    private SpringApplicationContext springApplicationContext;

    @Autowired
    private Environment environment;

    @Autowired
    private RegistryHolder registryHolder;

    private Register register;

    private JobExecuteManager jobExecuteManager;

    private JobExecutionFailover jobExecutionFailover;

    public static void main(String[] args) {
        Thread.currentThread().setName(ZhoudaDataCheckConstants.THREAD_NAME_COORDINATOR_SERVER);
        SpringApplication.run(ZhoudaDataCheckServer.class);
    }

    @PostConstruct
    private void initializeAndStart() throws Exception {
        logger.info("zhoudaDataCheck server start");

        initCommonProperties();

        jobExecuteManager = new JobExecuteManager();
        jobExecuteManager.start();

        JobExecutionResponseProcessor.getInstance().setJobExecuteManager(jobExecuteManager);

        CommonTaskManager commonTaskManager = new CommonTaskManager();
        commonTaskManager.start();

        CommonTaskFailover commonTaskFailover = new CommonTaskFailover(commonTaskManager);

        jobExecutionFailover = new JobExecutionFailover(jobExecuteManager);

        Registry registry = PluginDiscovery.getMultiKeyPluginDiscovery(Registry.class, Registry::getPluginNames)
                
                .getOrCreatePlugin(CommonPropertyUtils
                        .getString(CommonPropertyUtils.REGISTRY_TYPE, CommonPropertyUtils.REGISTRY_TYPE_DEFAULT));
        registry.init(CommonPropertyUtils.getProperties());
        registryHolder.setRegistry(registry);

        register = new Register(registry, jobExecutionFailover, commonTaskFailover);
        register.start();

        //start job scheduler
        JobScheduler jobScheduler = new JobScheduler(jobExecuteManager, register);
        jobScheduler.start();

        CommonTaskScheduler commonTaskScheduler = new CommonTaskScheduler(commonTaskManager, register);
        commonTaskScheduler.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> close("shutdownHook")));
    }

    /**
     * gracefully close
     * @param cause close cause
     */
    private void close(String cause) {

        try {
            //execute only once
            if(Stopper.isStopped()){
                return;
            }

            logger.info("server is stopping ..., cause : {}", cause);

            // set stop signal is true
            Stopper.stop();

            ThreadUtils.sleep(2000);

            this.jobExecuteManager.close();
            this.register.close();
            this.jobExecutionFailover.close();
            JdbcDataSourceManager.getInstance().close();
        } catch (Exception e) {
            logger.error("coordinator server stop exception ", e);
            System.exit(-1);
        }
    }

    private void initCommonProperties(){
        javax.sql.DataSource defaultDataSource =
                SpringApplicationContext.getBean(javax.sql.DataSource.class);
        HikariDataSource hikariDataSource = (HikariDataSource)defaultDataSource;
        CommonPropertyUtils.setValue("driver",hikariDataSource.getDriverClassName());
        CommonPropertyUtils.setValue("url",hikariDataSource.getJdbcUrl());
        CommonPropertyUtils.setValue("username", hikariDataSource.getUsername());
        CommonPropertyUtils.setValue("password", hikariDataSource.getPassword());
        CommonPropertyUtils.setValue("server.port", environment.getProperty("server.port"));

        String registryType = CommonPropertyUtils
                .getString(CommonPropertyUtils.REGISTRY_TYPE, CommonPropertyUtils.REGISTRY_TYPE_DEFAULT);
        if ("default".equals(registryType)) {
            CommonPropertyUtils.setValue(CommonPropertyUtils.REGISTRY_TYPE,
                    hikariDataSource.getJdbcUrl().contains("mysql") ? "mysql" : "postgresql");
        }
    }

}
