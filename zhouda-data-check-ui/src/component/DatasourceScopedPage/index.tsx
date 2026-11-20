import React, { useEffect, useState } from 'react';
import { Select, Spin } from 'antd';
import { useIntl } from 'react-intl';
import { useSelector } from '@/store';
import { $http } from '@/http';

type Props = {
    title: string;
    children: (datasourceId: string | number) => React.ReactNode;
};

/**
 * Top-level page shell: pick a datasource once, then render child content.
 * Avoids drilling into datasource detail for quality / logs / overview.
 */
const DatasourceScopedPage: React.FC<Props> = ({ title, children }) => {
    const intl = useIntl();
    const { workspaceId } = useSelector((r) => r.workSpaceReducer);
    const [loading, setLoading] = useState(true);
    const [list, setList] = useState<{ id: number | string; name: string }[]>([]);
    const [dsId, setDsId] = useState<number | string | undefined>();

    useEffect(() => {
        let cancelled = false;
        (async () => {
            try {
                setLoading(true);
                const res = (await $http.get('/datasource/page', {
                    workSpaceId: workspaceId,
                    pageNumber: 1,
                    pageSize: 9999,
                })) || {};
                const records = res?.records || [];
                if (cancelled) return;
                setList(records);
                if (records.length && !dsId) {
                    setDsId(records[0].id);
                }
            } catch (e) {
                // ignore
            } finally {
                if (!cancelled) setLoading(false);
            }
        })();
        return () => {
            cancelled = true;
        };
    }, [workspaceId]);

    return (
        <div className="yh-scoped-page">
            <div className="yh-scoped-page__bar">
                <h2 className="yh-scoped-page__title">{title}</h2>
                <div className="yh-scoped-page__picker">
                    <span className="yh-scoped-page__label">
                        {intl.formatMessage({ id: 'common_dataSource' })}
                    </span>
                    <Select
                        showSearch
                        style={{ width: 280 }}
                        placeholder={intl.formatMessage({ id: 'header_top_search_msg' })}
                        optionFilterProp="children"
                        value={dsId}
                        onChange={setDsId}
                        filterOption={(input, option: any) =>
                            String(option?.children ?? '').toLowerCase().includes(input.toLowerCase())
                        }
                    >
                        {list.map((item) => (
                            <Select.Option key={item.id} value={item.id}>
                                {item.name}
                            </Select.Option>
                        ))}
                    </Select>
                </div>
            </div>
            <div className="yh-scoped-page__body">
                {loading || !dsId ? (
                    <div className="yh-scoped-page__loading">
                        <Spin />
                    </div>
                ) : (
                    children(dsId)
                )}
            </div>
        </div>
    );
};

export default DatasourceScopedPage;
