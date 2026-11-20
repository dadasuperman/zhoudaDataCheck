import React from 'react';
import { useIntl } from 'react-intl';
import DatasourceScopedPage from '@/component/DatasourceScopedPage';
import JobsList from '@/view/Main/HomeDetail/Jobs/JobsList';

const QualityHub = () => {
    const intl = useIntl();
    return (
        <DatasourceScopedPage title={intl.formatMessage({ id: '/main/quality' })}>
            {(datasourceId) => <JobsList datasourceId={datasourceId} />}
        </DatasourceScopedPage>
    );
};

export default QualityHub;
