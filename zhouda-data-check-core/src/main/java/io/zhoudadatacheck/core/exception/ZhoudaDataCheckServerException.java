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
package io.zhoudadatacheck.core.exception;

import io.zhoudadatacheck.common.exception.ZhoudaDataCheckException;
import io.zhoudadatacheck.core.enums.Status;
import org.apache.commons.collections4.CollectionUtils;

import java.text.MessageFormat;
import java.util.Arrays;

public class ZhoudaDataCheckServerException extends ZhoudaDataCheckException {

    private Status status;

    public ZhoudaDataCheckServerException(String message) {
        super(message);
    }

    public ZhoudaDataCheckServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZhoudaDataCheckServerException(Throwable cause) {
        super(cause);
    }

    public ZhoudaDataCheckServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ZhoudaDataCheckServerException(Status status) {
        super(status.getMsg());
        this.status = status;
    }

    public ZhoudaDataCheckServerException(Status status, Throwable cause) {
        super(status.getMsg(), cause);
        this.status = status;
    }

    public ZhoudaDataCheckServerException(Status status, Object... statusParams) {
        super(CollectionUtils.isEmpty(Arrays.asList(statusParams)) ? status.getMsg() : MessageFormat.format(status.getMsg(), statusParams));
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
