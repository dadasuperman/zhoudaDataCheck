import React, { memo } from 'react';
import { Popover, Text } from '@mantine/core';
import { useIntl } from 'react-intl';
import { useSelector, useCommonActions } from '@/store';
import shareData from '@/utils/shareData';
import { DV_LANGUAGE } from '@/utils/constants';

const localMap = {
    zh_CN: '简体中文',
    en_US: 'English',
};

const SwitchLanguage: React.FC<{}> = () => {
    const intl = useIntl();
    const { locale } = useSelector((r) => r.commonReducer);
    const { setLocale } = useCommonActions();
    const swichFn = () => {
        const $locale = locale === 'zh_CN' ? 'en_US' : 'zh_CN';
        setLocale($locale);
        shareData.storageSet(DV_LANGUAGE, $locale);
    };

    return (
        <Popover width={140} position="bottom">
            <Popover.Target>
                <Text
                    component="span"
                    onClick={swichFn}
                    style={{ cursor: 'pointer' }}
                >
                    {localMap[locale]}
                </Text>
            </Popover.Target>
            <Popover.Dropdown>
                <Text size="sm" ta="center">
                    {intl.formatMessage({ id: 'common_switch_language' })}
                </Text>
            </Popover.Dropdown>
        </Popover>
    );
};
export default memo(SwitchLanguage);
