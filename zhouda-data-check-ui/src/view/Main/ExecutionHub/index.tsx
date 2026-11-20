import React from 'react';
import { useIntl } from 'react-intl';
import DatasourceScopedPage from '@/component/DatasourceScopedPage';
import JobExecutionLogs from '@/view/Main/HomeDetail/JobExecutionLogs';

const ExecutionHub = () => {
    const intl = useIntl();
    return (
        <DatasourceScopedPage title={intl.formatMessage({ id: '/main/executions' })}>
            {(datasourceId) => <JobExecutionLogs datasourceId={datasourceId} />}
        </DatasourceScopedPage>
    );
};

export default ExecutionHub;
