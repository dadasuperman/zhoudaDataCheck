import React from 'react';
import { useIntl } from 'react-intl';
import DatasourceScopedPage from '@/component/DatasourceScopedPage';
import Dashboard from '@/view/Main/HomeDetail/Dashboard';

const OverviewHub = () => {
    const intl = useIntl();
    return (
        <DatasourceScopedPage title={intl.formatMessage({ id: '/main/overview' })}>
            {(datasourceId) => <Dashboard datasourceId={datasourceId} />}
        </DatasourceScopedPage>
    );
};

export default OverviewHub;
