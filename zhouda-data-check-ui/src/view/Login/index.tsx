import React, { useState } from 'react';
import './index.less';
import {
    TextInput, PasswordInput, Button, Stack, Text, Anchor, Paper, Title, Group,
} from '@mantine/core';
import { useForm } from '@mantine/form';
import { useIntl } from 'react-intl';
import shareData from 'src/utils/shareData';
import { DV_STORAGE_LOGIN } from 'src/utils/constants';
import { useHistory } from 'react-router-dom';
import { SwitchLanguage } from '@/component';
import { $http } from '@/http';
import { useCommonActions } from '@/store';

type TLoginValues = {
    username: string,
    password: string,
}

const Login = () => {
    const { setIsDetailPage } = useCommonActions();
    const [loading, setLoading] = useState(false);
    const intl = useIntl();
    const history = useHistory();
    const form = useForm<TLoginValues>({
        initialValues: { username: '', password: '' },
        validate: {
            username: (v) => (v ? null : intl.formatMessage({ id: 'login_username_msg' })),
            password: (v) => (v ? null : intl.formatMessage({ id: 'login_password_msg' })),
        },
    });

    const onFinish = async (values: TLoginValues) => {
        try {
            setLoading(true);
            const res = await $http.post('/login', values, { showWholeData: true });
            shareData.storageSet(DV_STORAGE_LOGIN, {
                ...(res.data),
                token: res.token,
            });
            setIsDetailPage(false);
            history.push('/main/overview');
        } catch (error: any) {
            // handled by http layer
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="dv-login">
            <div className="dv-login__grid" aria-hidden />
            <div className="dv-login__switch-language"><SwitchLanguage /></div>
            <div className="dv-login-containner">
                <Paper className="dv-login-wrap" shadow="xl" radius="md" p="xl">
                    <Stack gap="lg">
                        <div>
                            <Title order={1} className="dv-login-title">
                                {intl.formatMessage({ id: 'brand_name' })}
                            </Title>
                            <Text className="dv-login-tagline" size="sm">
                                Industrial data-quality console
                            </Text>
                        </div>
                        <form onSubmit={form.onSubmit(onFinish)}>
                            <Stack gap="md">
                                <TextInput
                                    size="md"
                                    autoComplete="off"
                                    placeholder={intl.formatMessage({ id: 'login_username_msg' })}
                                    {...form.getInputProps('username')}
                                />
                                <PasswordInput
                                    size="md"
                                    placeholder={intl.formatMessage({ id: 'login_password_msg' })}
                                    {...form.getInputProps('password')}
                                />
                                <Group justify="space-between" mt="xs">
                                    <Anchor href="#/register" className="dv-register-btn" size="sm">
                                        {intl.formatMessage({ id: 'register' })}
                                    </Anchor>
                                    <Button
                                        type="submit"
                                        loading={loading}
                                        color="teal"
                                        size="md"
                                        className="dv-login-submit"
                                    >
                                        {intl.formatMessage({ id: 'login_btn_text' })}
                                    </Button>
                                </Group>
                            </Stack>
                        </form>
                    </Stack>
                </Paper>
            </div>
        </div>
    );
};
export default Login;
