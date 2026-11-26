/* eslint-disable array-callback-return */
import React, { memo, useCallback, useState } from 'react';
import { Menu, Popover, Select } from 'antd';
import {
    RouteComponentProps, withRouter,
} from 'react-router-dom';
import useWatch from '@Editor/common/useWatch';
import EhPng from 'assets/images/en.svg';
import ChPng from 'assets/images/ch.svg';
import './index.less';
import { LogoutOutlined, PlusOutlined, EditOutlined } from '@ant-design/icons';
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
}

const MenuAside: React.FC<TMenuAside> = ({ menus, history }) => {
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
    const clickMenuItem = useCallback((obj: any) => {
        setActiveKeys(obj.key);
        history.push(obj.key);
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

    const content = (
        <div className="dv-user-popover">
            <div className="dv-user-popover__head">
                <span className="dv-user">AD</span>
                <span className="dv-user-popover__title">Account</span>
            </div>
            <div className="dv-user-popover__section">
                <div className="dv-user-popover__row">
                    <span>{intl.formatMessage({ id: 'header_top_workspace' })}</span>
                    <a onClick={() => onShowSpace(undefined)}>
                        <PlusOutlined style={{ marginRight: 4 }} />
                        {intl.formatMessage({ id: 'common_create_btn' })}
                    </a>
                </div>
                <Select
                    showSearch
                    style={{ width: '100%' }}
                    filterOption={(input, option) => (option?.children as unknown as string)
                        ?.toLowerCase?.()
                        ?.indexOf(input.toLowerCase()) >= 0}
                    options={(spaceList || []).map((s: any) => ({ label: s.name, value: s.id }))}
                    value={workspaceId}
                    onChange={onChangeSpace}
                />
            </div>
            <div
                className="dv-user-popover__action"
                onClick={() => onShowResetPassword(loginInfo.id)}
            >
                <EditOutlined style={{ marginRight: 8 }} />
                {intl.formatMessage({ id: 'common_reset_password' })}
            </div>
            <div className="dv-user-popover__action dv-user-popover__action--danger" onClick={handleMenuClick}>
                <LogoutOutlined style={{ marginRight: 8 }} />
                {intl.formatMessage({ id: 'common_Logout' })}
            </div>
        </div>
    );

    const $menus = menus.map((item) => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const { exact, ...rest } = item;
        return {
            ...rest,
        };
    }).filter((item) => (!item.menuHide)) as MenuItem[];

    const { locale } = useSelector((r) => r.commonReducer);
    const { setLocale } = useCommonActions();
    const swichFn = () => {
        const $locale = locale === 'zh_CN' ? 'en_US' : 'zh_CN';
        setLocale($locale);
        shareData.storageSet(DV_LANGUAGE, $locale);
    };

    return (
        <div className="dv-menu">
            <div className="yh-brand">
                {intl.formatMessage({ id: 'brand_name' })}
                <span className="yh-brand__sub">Data Quality Console</span>
            </div>
            <Menu
                selectedKeys={activeKeys}
                mode="inline"
                theme="dark"
                className="dv-menu-list"
                onClick={clickMenuItem}
                items={$menus.map((item) => ({
                    key: item.key,
                    icon: item.icon,
                    label: item.label,
                }))}
            />
            <div className="dv-menu__footer">
                {
                    locale === 'zh_CN' ? (
                        <Ch style={{ cursor: 'pointer', width: 40 }} onClick={swichFn} />
                    ) : (
                        <Eh style={{ cursor: 'pointer', width: 40 }} onClick={swichFn} />
                    )
                }
                <Popover content={content} placement="rightBottom" trigger="click" overlayClassName="dv-user-popover-overlay">
                    <span className="dv-user" role="button" tabIndex={0}>AD</span>
                </Popover>
            </div>
            <RenderCreateSpace />
            <RenderResetPassword />
        </div>
    );
};

export default withRouter(MenuAside);
