import React, { useState } from 'react';
import { AppShell, Burger, Group, ActionIcon, Tooltip } from '@mantine/core';
import AsideMenu, { MenuItem } from '../MenuAside';
import './index.less';

type TMainProps = {
    menus: MenuItem[],
    visible?: boolean,
    children?: React.ReactNode
};

const MenuLayout: React.FC<TMainProps> = ({ children, menus, visible = true }) => {
    const [collapsed, setCollapsed] = useState(false);

    if (!visible) {
        return <>{children}</>;
    }

    return (
        <AppShell
            navbar={{
                width: collapsed ? 72 : 248,
                breakpoint: 'sm',
                collapsed: { mobile: collapsed, desktop: false },
            }}
            padding="md"
            classNames={{
                main: 'zd-appshell-main',
                navbar: 'zd-appshell-navbar',
            }}
        >
            <AppShell.Navbar p={0} className="zd-appshell-navbar">
                <div className="zd-navbar-inner">
                    <Group justify="flex-end" px="xs" pt="xs" mb={4}>
                        <Tooltip label={collapsed ? 'Expand' : 'Collapse'} position="right">
                            <ActionIcon
                                variant="subtle"
                                color="gray"
                                onClick={() => setCollapsed((c) => !c)}
                                aria-label="Toggle navbar"
                            >
                                <Burger opened={!collapsed} size="sm" color="#94a3b8" />
                            </ActionIcon>
                        </Tooltip>
                    </Group>
                    <AsideMenu menus={menus} collapsed={collapsed} />
                </div>
            </AppShell.Navbar>
            <AppShell.Main>
                {children}
            </AppShell.Main>
        </AppShell>
    );
};

export default MenuLayout;
