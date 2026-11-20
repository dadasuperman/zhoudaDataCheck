import React, { lazy } from 'react';
import {
    DatabaseOutlined,
    WarningOutlined,
    UsergroupAddOutlined,
    CloseCircleOutlined,
    TagOutlined,
    FormOutlined,
    SafetyCertificateOutlined,
    DashboardOutlined,
    ScheduleOutlined,
    AuditOutlined,
} from '@ant-design/icons';
import { TRouter } from './type';

const router: TRouter = {
    'yh-overview': {
        path: '/main/overview',
        key: '/main/overview',
        icon: <DashboardOutlined />,
        component: lazy(() => import(/* webpackChunkName: 'view-overview' */ '@/view/Main/OverviewHub')),
    },
    'yh-quality': {
        path: '/main/quality',
        key: '/main/quality',
        icon: <ScheduleOutlined />,
        component: lazy(() => import(/* webpackChunkName: 'view-quality' */ '@/view/Main/QualityHub')),
    },
    'yh-executions': {
        path: '/main/executions',
        key: '/main/executions',
        icon: <AuditOutlined />,
        component: lazy(() => import(/* webpackChunkName: 'view-executions' */ '@/view/Main/ExecutionHub')),
    },
    'dv-home': {
        path: '/main/home',
        key: '/main/home',
        icon: <DatabaseOutlined />,
        component: lazy(() => import(/* webpackChunkName: 'view-home' */ '@/view/Main/Home')),
    },
    'dv-warning': {
        path: '/main/warning',
        key: '/main/warning',
        icon: <WarningOutlined />,
        component: lazy(() => import(/* webpackChunkName: 'view-warning' */ '@/view/Main/Warning')),
    },
    'dv-errorDataManage': {
        path: '/main/errorDataManage',
        key: '/main/errorDataManage',
        icon: <CloseCircleOutlined />,
        component: lazy(() => import(/* webpackChunkName: 'view-warning' */ '@/view/Main/ErrorDataManage')),
    },
    'dv-userManage': {
        path: '/main/userManage',
        key: '/main/userManage',
        icon: <UsergroupAddOutlined />,
        component: lazy(() => import(/* webpackChunkName: 'view-warning' */ '@/view/Main/UserManage')),
    },
    'dv-home-detail': {
        path: '/main/detail/:id',
        key: '/main/detail',
        menuHide: true,
        label: '',
        exact: false,
        icon: null,
        component: lazy(() => import(/* webpackChunkName: 'view-home-detail' */ '@/view/Main/HomeDetail')),
    },
    'dv-label': {
        path: '/main/label',
        key: '/main/label',
        icon: <TagOutlined />,
        menuHide: true,
        component: lazy(() => import(/* webpackChunkName: 'view-label' */ '@/view/Main/Label')),
    },
    'dv-config': {
        path: '/main/config',
        key: '/main/config',
        icon: <FormOutlined />,
        menuHide: true,
        component: lazy(() => import(/* webpackChunkName: 'view-label' */ '@/view/Main/Config')),
    },
    'dv-token': {
        path: '/main/tokenManager',
        key: '/main/tokenManager',
        icon: <SafetyCertificateOutlined />,
        menuHide: true,
        component: lazy(() => import(/* webpackChunkName: 'view-label' */ '@/view/Main/TokenManager')),
    },
};

export default router;
