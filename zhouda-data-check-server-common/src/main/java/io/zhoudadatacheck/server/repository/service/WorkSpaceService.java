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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.zhoudadatacheck.server.api.dto.bo.workspace.InviteUserIntoWorkspace;
import io.zhoudadatacheck.server.api.dto.bo.workspace.RemoveUserOutWorkspace;
import io.zhoudadatacheck.server.api.dto.bo.workspace.WorkSpaceCreate;
import io.zhoudadatacheck.server.api.dto.bo.workspace.WorkSpaceUpdate;
import io.zhoudadatacheck.server.api.dto.vo.UserVO;
import io.zhoudadatacheck.server.api.dto.vo.WorkSpaceVO;
import io.zhoudadatacheck.server.repository.entity.WorkSpace;
import io.zhoudadatacheck.core.exception.ZhoudaDataCheckServerException;

import java.util.List;

public interface WorkSpaceService extends IService<WorkSpace> {

    long insert(WorkSpaceCreate workSpaceCreate) throws ZhoudaDataCheckServerException;

    int update(WorkSpaceUpdate workSpaceUpdate) throws ZhoudaDataCheckServerException;

    WorkSpace getById(long id);

    List<WorkSpaceVO> listByUserId();

    int deleteById(long id);

    long inviteUserIntoWorkspace(InviteUserIntoWorkspace inviteUserIntoWorkspace);

    boolean removeUser(RemoveUserOutWorkspace removeUserOutWorkspace);

    IPage<UserVO> listUserByWorkspaceId(Long workspaceId, Integer pageNumber, Integer pageSize);
}
