import React, { useState } from 'react';
import { useIntl } from 'react-intl';
import {
    Input, Button, Modal, Form,
} from 'antd';
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
    const [form] = Form.useForm();
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
                widget: <Input autoComplete="off" placeholder={`${inputTip}${userNameText}`} />,
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
                widget: <Input.Password autoComplete="off" placeholder={`${inputTip}${passwordText}`} />,
            },
            {
                label: emailText,
                name: 'email',
                rules: [
                    { required: true, message: `${emailText}${requiredTop}` },
                    { pattern: EMAIL_REG, message: `${patternTip}${emailText}` },
                ],
                widget: <Input autoComplete="off" placeholder={`${inputTip}${emailText}`} />,
            },
            {
                label: phoneText,
                name: 'phone',
                rules: [
                    { required: true, message: `${phoneText}${requiredTop}` },
                ],
                widget: <Input autoComplete="off" placeholder={`${inputTip}${phoneText}`} />,
            },
            {
                label: `${verificationCodeText}`,
                name: 'verificationCode',
                rules: [
                    { required: true, message: `${verificationCodeText}${requiredTop}` },
                    { pattern: CODE_REG, message: `${patternTip}${verificationCodeText}` },
                ],
                widget: (
                    <Input
                        autoComplete="off"
                        suffix={(
                            <RenderImage
                                style={{
                                    display: 'inline-block',
                                    height: '30px',
                                }}
                            />
                        )}
                    />
                ),
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
            // handled by http
        } finally {
            setLoading(false);
        }
    };
    const btnClick = async () => {
        try {
            const values = await form.validateFields();
            onRegister(values);
        } catch (error) {
            // validation failed
        }
    };
    return (
        <div className="dv-gray-bg" style={{ position: 'relative', minHeight: '100vh' }}>
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
            <div className="dv-register-wrap">
                <div className="dv-register-card">
                    <h2 className="dv-register-title">
                        {intl.formatMessage({ id: 'register_title' })}
                    </h2>
                    <FormRender {...schema} form={form} />
                    <Button
                        type="primary"
                        loading={loading}
                        onClick={btnClick}
                        block
                        size="large"
                        style={{ marginTop: 8 }}
                    >
                        {intl.formatMessage({ id: 'register_btn' })}
                    </Button>
                </div>
            </div>
        </div>
    );
};

export default Index;
