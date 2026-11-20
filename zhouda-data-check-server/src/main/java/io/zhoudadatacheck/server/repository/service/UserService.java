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
package io.zhoudadatacheck.server.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.zhoudadatacheck.common.exception.ZhoudaDataCheckException;
import io.zhoudadatacheck.server.api.dto.bo.user.*;
import io.zhoudadatacheck.server.api.dto.vo.UserBaseInfo;
import io.zhoudadatacheck.server.api.dto.vo.UserLoginResult;
import io.zhoudadatacheck.server.repository.entity.User;

public interface UserService extends IService<User> {

    User getByUsername(String username);

    UserLoginResult login(UserLogin userLogin) throws ZhoudaDataCheckException;

    UserBaseInfo register(UserRegister userRegister) throws ZhoudaDataCheckException;

    Boolean updateUserInfo(UserUpdate userUpdate);

    Boolean resetPassword(UserResetPassword userResetPassword);
}
