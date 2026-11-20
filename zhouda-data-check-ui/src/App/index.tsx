import React, { Suspense, useState } from 'react';
import {
    HashRouter as Router, Route, Switch, Redirect,
} from 'react-router-dom';
import { Provider } from 'react-redux';
import { Center, Loader } from '@mantine/core';
import store, { useCommonActions } from '@/store';
import { ConfigProviderWrap } from './ConfigProvider';
import { generateRoute } from '../router/useRoute';
import { routerNoLogin } from '../router';
import { IntlWrap } from '@/locale';
import { useMount } from '@/common';
import shareData from '@/utils/shareData';
import { DV_LANGUAGE, DV_STORAGE_LOGIN } from '@/utils/constants';

const Main = React.lazy(() => import('view/Main'));
function App() {
    return (
        <Router>
            <Suspense fallback={(
                <Center style={{ width: '100vw', height: '100vh' }}>
                    <Loader color="teal" size="lg" />
                </Center>
            )}
            >
                <Switch>
                    {
                        routerNoLogin.map((item) => generateRoute(item))
                    }
                    <Route key="/main" path="/main">
                        <Main />
                    </Route>
                    <Redirect from="/" to={shareData.storageGet(DV_STORAGE_LOGIN) ? '/main' : '/login'} />
                </Switch>
            </Suspense>
        </Router>
    );
}

export const ProviderInner = () => {
    const { setLocale } = useCommonActions();
    const [loading, setLoading] = useState(true);
    useMount(() => {
        const language = shareData.storageGet(DV_LANGUAGE) || (navigator.language.indexOf('zh') ? 'zh_CN' : 'en_US');
        setLocale(language);
        setLoading(false);
    });
    if (loading) {
        return <div />;
    }
    return (
        <IntlWrap>
            <ConfigProviderWrap>
                <App />
            </ConfigProviderWrap>
        </IntlWrap>
    );
};

export default function () {
    return (
        <Provider store={store}>
            <ProviderInner />
        </Provider>
    );
}
