import React from 'react';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import enUS from 'antd/locale/en_US';
import { useSelector } from '@/store';

const ConfigProviderWrap: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const { prefixCls, locale } = useSelector((r) => r.commonReducer);
    const getLocale = () => {
        if (locale === 'zh_CN') {
            return zhCN;
        }
        return enUS;
    };
    return (
        <ConfigProvider
            locale={getLocale()}
            prefixCls={prefixCls}
            theme={{
                token: {
                    colorPrimary: '#0F766E',
                    colorInfo: '#0D9488',
                    colorLink: '#0F766E',
                    borderRadius: 6,
                    fontFamily: '"IBM Plex Sans", "DM Sans", "Segoe UI", sans-serif',
                    colorText: '#1E293B',
                    colorBgLayout: '#F1F5F9',
                },
            }}
        >
            {children}
        </ConfigProvider>
    );
};

export {
    ConfigProviderWrap,
};
