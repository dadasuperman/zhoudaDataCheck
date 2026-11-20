/* eslint-disable array-callback-return */
import React, { memo, useCallback, useState } from 'react';
import {
    NavLink, Select, Popover, Stack, Text, Group, UnstyledButton, Divider, ScrollArea,
} from '@mantine/core';
import {
    RouteComponentProps, withRouter,
} from 'react-router-dom';
import useWatch from '@Editor/common/useWatch';
import EhPng from 'assets/images/en.svg';
import ChPng from 'assets/images/ch.svg';
import './index.less';
import { useIntl } from 'react-intl';
import { useSelector, useCommonActions, useWorkSpaceActions } from '@/store';
import shareData from '@/utils/shareData';
import { DV_LANGUAGE, DV_STORAGE_LOGIN, DV_WORKSPACE_ID } from '@/utils/constants';
import { useAddSpace } from '../../Header/WorkSpaceSwitch/useAddSpace';
import { useResetPassword } from '../../Header/WorkSpaceSwitch/useResetPassword';

const Eh = memo(({ style, onClick }: any) => <img style={style} className="escape" src={EhPng} onClick={onClick} alt="en" />);
const Ch = memo(({ style, onClick }: any) => <img style={style} className="escape" src={ChPng} onClick={onClick} alt="zh" />);

export type MenuItem = {
    label: React.ReactNode,
    key: string,
    path: string,
    icon?: React.ReactNode,
    children?: MenuItem[],
    menuHide?: boolean,
    exact?: boolean,
    [key: string]: any,
}

interface TMenuAside extends RouteComponentProps {
    menus: MenuItem[]
    collapsed?: boolean
}

const MenuAside: React.FC<TMenuAside> = ({ menus, history, collapsed }) => {
    const [activeKeys, setActiveKeys] = useState<string[]>(['']);
    const { workspaceId, spaceList } = useSelector((r) => r.workSpaceReducer);
    const { loginInfo } = useSelector((r) => r.userReducer);
    const intl = useIntl();
    const { Render: RenderCreateSpace, show } = useAddSpace({});
    const { Render: RenderResetPassword, show1 } = useResetPassword({});

    const setActiveKeyRedirect = (redirect = false) => {
        const url = window.location.href;
        const match = menus.find((item) => {
            if (item.path.includes(':id')) {
                const pathArr = item.path.split(':id');
                return url.includes(pathArr[0]) && url.includes(pathArr[1]);
            }
            if (url.includes(item.path)) {
                return true;
            }
        });
        const currItem = (match || menus[0]) as MenuItem;
        if (!currItem) {
            return;
        }
        if (!activeKeys.includes(currItem.key as string)) {
            setActiveKeys([currItem.key as string]);
        }
        if (!match && redirect) {
            history.replace(currItem.path);
        }
    };
    useWatch(menus, () => setActiveKeyRedirect(true), { immediate: true });

    const clickMenuItem = useCallback((key: string) => {
        setActiveKeys([key]);
        history.push(key);
    }, [history]);

    const handleMenuClick = () => {
        window.localStorage.removeItem(DV_STORAGE_LOGIN);
        history.push('/login');
    };
    const { setCurrentSpace } = useWorkSpaceActions();

    const onChangeSpace = (id: any) => {
        setCurrentSpace(id);
        shareData.sessionSet(`${DV_WORKSPACE_ID}_${loginInfo.id}`, id);
        if (!window.location.href.includes('/main/overview')) {
            history.push('/main/overview');
        }
    };
    const onShowSpace = (val: any) => {
        show(val);
    };

    const onShowResetPassword = (val: any) => {
        show1(val);
    };

    const spaceOptions = (spaceList || []).map((s: any) => ({
        value: String(s.id),
        label: s.name,
    }));

    const userPanel = (
        <Stack gap="sm" style={{ minWidth: 220 }}>
            <Group gap="sm">
                <div className="dv-user dv-user--panel">AD</div>
                <Text size="sm" fw={600}>Account</Text>
            </Group>
            <Divider />
            <div>
                <Group justify="space-between" mb={6}>
                    <Text size="xs" c="dimmed">{intl.formatMessage({ id: 'header_top_workspace' })}</Text>
                    <UnstyledButton
                        style={{ fontSize: 12, color: '#0F766E' }}
                        onClick={() => onShowSpace(undefined)}
                    >
                        + {intl.formatMessage({ id: 'common_create_btn' })}
                    </UnstyledButton>
                </Group>
                <Select
                    size="xs"
                    data={spaceOptions}
                    value={workspaceId != null ? String(workspaceId) : null}
                    onChange={(v) => v && onChangeSpace(Number.isNaN(+v) ? v : +v)}
                    searchable
                />
            </div>
            <UnstyledButton onClick={() => onShowResetPassword(loginInfo.id)} style={{ fontSize: 13 }}>
                {intl.formatMessage({ id: 'common_reset_password' })}
            </UnstyledButton>
            <UnstyledButton onClick={handleMenuClick} style={{ fontSize: 13, color: '#b91c1c' }}>
                {intl.formatMessage({ id: 'common_Logout' })}
            </UnstyledButton>
        </Stack>
    );

    const $menus = menus.filter((item) => (!item.menuHide));

    const { locale } = useSelector((r) => r.commonReducer);
    const { setLocale } = useCommonActions();
    const swichFn = () => {
        const $locale = locale === 'zh_CN' ? 'en_US' : 'zh_CN';
        setLocale($locale);
        shareData.storageSet(DV_LANGUAGE, $locale);
    };

    return (
        <div className={`zd-aside ${collapsed ? 'zd-aside--collapsed' : ''}`}>
            <div className="yh-brand">
                {collapsed ? 'zD' : intl.formatMessage({ id: 'brand_name' })}
                {!collapsed && <span className="yh-brand__sub">Data Quality Console</span>}
            </div>
            <ScrollArea style={{ flex: 1 }} offsetScrollbars>
                <Stack gap={4} px="xs" mt="sm">
                    {$menus.map((item) => {
                        const active = activeKeys.includes(item.key as string);
                        return (
                            <NavLink
                                key={item.key}
                                label={collapsed ? null : item.label}
                                leftSection={item.icon}
                                active={active}
                                onClick={() => clickMenuItem(item.key)}
                                className="zd-navlink"
                                variant="filled"
                                color="teal"
                            />
                        );
                    })}
                </Stack>
            </ScrollArea>
            <div className="zd-aside__footer">
                {locale === 'zh_CN' ? (
                    <Ch
                        style={{ cursor: 'pointer', width: 40 }}
                        onClick={swichFn}
                    />
                ) : (
                    <Eh
                        style={{ cursor: 'pointer', width: 40 }}
                        onClick={swichFn}
                    />
                )}
                <Popover position="right-end" shadow="md" width={260}>
                    <Popover.Target>
                        <span className="dv-user" role="button" tabIndex={0}>AD</span>
                    </Popover.Target>
                    <Popover.Dropdown>{userPanel}</Popover.Dropdown>
                </Popover>
            </div>
            <RenderCreateSpace />
            <RenderResetPassword />
        </div>
    );
};

export default withRouter(MenuAside);
