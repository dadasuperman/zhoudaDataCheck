import React, { useState } from 'react';
import { Layout } from 'antd';
import { MenuUnfoldOutlined, MenuFoldOutlined } from '@ant-design/icons';
import AsideMenu, { MenuItem } from '../MenuAside';
import './index.less';

const { Content, Sider } = Layout;

type TMainProps = {
    menus: MenuItem[],
    visible?: boolean,
    children?: React.ReactNode
};

const MenuLayout: React.FC<TMainProps> = ({ children, menus, visible = true }) => {
    const [collapsed, setCollapsed] = useState(false);
    const onCollapse = (bool: boolean) => {
        setCollapsed(bool);
    };
    if (!visible) {
        return <>{children}</>;
    }
    return (
        <Layout className="zd-main-layout">
            <Layout>
                <Sider
                    className="zd-sider"
                    style={{
                        height: '100vh',
                        overflow: 'auto',
                    }}
                    width={248}
                    collapsedWidth={72}
                    trigger={(
                        <div style={{ position: 'absolute', right: 15, fontSize: 16 }}>
                            {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
                        </div>
                    )}
                    collapsed={collapsed}
                    onCollapse={onCollapse}
                    collapsible
                >
                    <AsideMenu menus={menus} />
                </Sider>
                <Layout className="zd-content-layout">
                    <Content className="zd-content">
                        {children}
                    </Content>
                </Layout>
            </Layout>
        </Layout>
    );
};

export default MenuLayout;
