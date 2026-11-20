import React, { useState } from 'react';
import { useIntl } from 'react-intl';
import {
    TextInput, Button, Paper, Title, Stack, Center,
} from '@mantine/core';
import { Modal } from '@/ui/antdCompat';
import { useForm as useAntdForm } from '@/ui/antdCompat/form';
import { useHistory } from 'react-router-dom';
import { FormRender, IFormRender } from '@/common';
import { useVerificationCode } from '@/hooks';
import { PWD_REG, EMAIL_REG, CODE_REG } from '@/utils/constants';
import { $http } from '@/http';
import { GoBack } from '@/component';
import './index.less';

const Index = () => {
    const history = useHistory();
    const intl = useIntl();
    const [loading, setLoading] = useState(false);
    const { RenderImage, verificationCodeJwt } = useVerificationCode();
    const [form] = useAntdForm();
    const inputTip = intl.formatMessage({ id: 'common_input_tip' });
    const requiredTop = intl.formatMessage({ id: 'common_required_top' });
    const userNameText = intl.formatMessage({ id: 'userName_text' });
    const passwordText = intl.formatMessage({ id: 'password_text' });
    const emailText = intl.formatMessage({ id: 'email_text' });
    const verificationCodeText = intl.formatMessage({ id: 'verification_code_text' });
    const patternTip = intl.formatMessage({ id: 'common_input_pattern_tip' });
    const phoneText = intl.formatMessage({ id: 'phone_text' });

    const schema: IFormRender = {
        name: 'user-register-form',
        layout: 'vertical',
        formItemProps: {
            style: { marginBottom: 10 },
        },
        meta: [
            {
                label: userNameText,
                name: 'username',
                rules: [
                    { required: true, message: `${userNameText}${requiredTop}` },
                    { pattern: /^[\u4E00-\u9FA5_a-zA-Z0-9]{2,32}$/, message: `${patternTip}${userNameText}` },
                ],
                widget: <TextInput autoComplete="off" placeholder={`${inputTip}${userNameText}`} />,
            },
            {
                label: (
                    <>
                        {passwordText}
                        {' '}
                        (
                        {intl.formatMessage({ id: 'password_tip' })}
                        )
                    </>
                ),
                name: 'password',
                rules: [
                    { required: true, message: `${passwordText}${requiredTop}` },
                    { pattern: PWD_REG, message: `${patternTip}${passwordText}` },
                ],
                widget: <TextInput autoComplete="off" placeholder={`${inputTip}${passwordText}`} />,
            },
            {
                label: emailText,
                name: 'email',
                rules: [
                    { required: true, message: `${emailText}${requiredTop}` },
                    { pattern: EMAIL_REG, message: `${patternTip}${emailText}` },
                ],
                widget: <TextInput autoComplete="off" placeholder={`${inputTip}${emailText}`} />,
            },
            {
                label: phoneText,
                name: 'phone',
                rules: [
                    { required: true, message: `${phoneText}${requiredTop}` },
                ],
                widget: <TextInput autoComplete="off" placeholder={`${inputTip}${phoneText}`} />,
            },
            {
                label: `${verificationCodeText}`,
                name: 'verificationCode',
                rules: [
                    { required: true, message: `${verificationCodeText}${requiredTop}` },
                    { pattern: CODE_REG, message: `${patternTip}${verificationCodeText}` },
                ],
                widget: <TextInput
                    autoComplete="off"
                    rightSection={(
                        <RenderImage
                            style={{
                                display: 'inline-block',
                                height: '30px',
                            }}
                        />
                    )}
                    rightSectionWidth={100}
                />,
            },
        ],
    };
    const onRegister = async (values = {}) => {
        try {
            setLoading(true);
            const res = await $http.post('/register', {
                ...values,
                verificationCodeJwt,
            });
            if (res?.result) {
                Modal.confirm({
                    content: intl.formatMessage({ id: 'register_success' }),
                    onOk() {
                        Modal.destroyAll();
                        history.push('/login');
                    },
                    onCancel() {
                        window.location.reload();
                    },
                });
            }
        } catch (error: any) {
        } finally {
            setLoading(false);
        }
    };
    const btnClick = async () => {
        try {
            const values = await form.validateFields();
            onRegister(values);
        } catch (error) {}
    };
    return (
        <div className="dv-gray-bg" style={{ position: 'relative' }}>
            <GoBack
                style={{
                    position: 'absolute',
                    left: 15,
                    top: 15,
                    fontSize: 16,
                    color: '#f8fafc',
                    zIndex: 2,
                }}
            >
                {intl.formatMessage({ id: 'common_back' })}
            </GoBack>
            <Center mih="100vh" p="md">
                <Paper shadow="xl" radius="md" p="xl" style={{ width: minWidth(420), maxWidth: 480 }}>
                    <Stack>
                        <Title order={2} ta="center" c="teal.8" style={{ fontFamily: '"DM Sans", sans-serif' }}>
                            {intl.formatMessage({ id: 'register_title' })}
                        </Title>
                        <FormRender {...schema} form={form} />
                        <Button
                            loading={loading}
                            onClick={btnClick}
                            fullWidth
                            size="md"
                            color="teal"
                        >
                            {intl.formatMessage({ id: 'register_btn' })}
                        </Button>
                    </Stack>
                </Paper>
            </Center>
        </div>
    );
};

function minWidth(n: number) {
    return `min(100%, ${n}px)`;
}

export default Index;
