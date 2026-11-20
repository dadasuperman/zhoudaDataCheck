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
package io.zhoudadatacheck.server.api.inteceptor;

import io.zhoudadatacheck.common.utils.StringUtils;
import io.zhoudadatacheck.core.constant.ZhoudaDataCheckConstants;
import io.zhoudadatacheck.server.api.annotation.AuthIgnore;
import io.zhoudadatacheck.core.enums.Status;
import io.zhoudadatacheck.server.api.annotation.CheckTokenExist;
import io.zhoudadatacheck.server.repository.entity.User;
import io.zhoudadatacheck.server.repository.service.AccessTokenService;
import io.zhoudadatacheck.server.repository.service.UserService;
import io.zhoudadatacheck.core.exception.ZhoudaDataCheckServerException;
import io.zhoudadatacheck.server.utils.ContextHolder;
import io.zhoudadatacheck.core.utils.TokenManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Resource
    private TokenManager tokeManager;

    @Resource
    private UserService userService;

    @Resource
    private AccessTokenService accessTokenService;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {

        HandlerMethod handlerMethod = null;
        try {
            handlerMethod = (HandlerMethod) handler;
        } catch (Exception e) {
            response.setStatus(Status.REQUEST_ERROR.getCode());
            return false;
        }
        
        Method method = handlerMethod.getMethod();

        AuthIgnore ignoreAuthMethod = method.getAnnotation(AuthIgnore.class);

        if (null != ignoreAuthMethod) {
            return true;
        }

        CheckTokenExist checkTokenExist = method.getAnnotation(CheckTokenExist.class);

        String token = request.getHeader(ZhoudaDataCheckConstants.TOKEN_HEADER_STRING);

        if (StringUtils.isEmpty(token)){
            token = request.getParameter(ZhoudaDataCheckConstants.TOKEN_HEADER_STRING);
            if (StringUtils.isEmpty(token)) {
                throw new ZhoudaDataCheckServerException(Status.TOKEN_IS_NULL_ERROR);
            }
        }

        if (checkTokenExist != null) {
            if (!accessTokenService.checkTokenExist(token)) {
                throw new ZhoudaDataCheckServerException(Status.INVALID_TOKEN, token);
            }
        }

        String username = tokeManager.getUsername(token);
        User user = userService.getByUsername(username);
        if (null == user) {
            throw new ZhoudaDataCheckServerException(Status.INVALID_TOKEN, token);
        }
        request.setAttribute(ZhoudaDataCheckConstants.LOGIN_USER, user);

        if (!tokeManager.validateToken(token, username, tokeManager.getPassword(token))) {
            throw new ZhoudaDataCheckServerException(Status.INVALID_TOKEN, token);
        }

        ContextHolder.setParam(ZhoudaDataCheckConstants.LOGIN_USER, user);
        ContextHolder.setParam(ZhoudaDataCheckConstants.TOKEN, token);

        return true;
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
        ContextHolder.removeAll();
    }
}
