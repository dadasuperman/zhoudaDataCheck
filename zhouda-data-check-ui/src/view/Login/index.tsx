import React, { useState } from 'react';
import './index.less';
import {
    Form, Input, Button,
} from 'antd';
import { UserOutlined, LockOutlined, ArrowRightOutlined } from '@ant-design/icons';
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
    const [form] = Form.useForm();
    const intl = useIntl();
    const history = useHistory();
    const onFinish = async () => {
        form.validateFields().then(async (values: TLoginValues) => {
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
        }).catch(() => {
            // validation failed
        });
    };
    return (
        <div className="dv-login">
            <div className="dv-login__grid" aria-hidden />
            <div className="dv-login__switch-language"><SwitchLanguage /></div>
            <div className="dv-login-containner">
                <div className="dv-login-wrap">
                    <div className="dv-login-title main-color">
                        {intl.formatMessage({ id: 'brand_name' })}
                    </div>
                    <p className="dv-login-tagline">Industrial data-quality console</p>
                    <Form
                        form={form}
                        layout="vertical"
                        name="dv-login"
                        onFinish={onFinish}
                    >
                        <Form.Item
                            name="username"
                            style={{ marginBottom: 15 }}
                            rules={[{ required: true, message: intl.formatMessage({ id: 'login_username_msg' }) }]}
                        >
                            <Input
                                autoComplete="off"
                                style={{ height: 50 }}
                                size="large"
                                prefix={<UserOutlined />}
                                placeholder={intl.formatMessage({ id: 'login_username_msg' })}
                            />
                        </Form.Item>

                        <Form.Item
                            name="password"
                            style={{ marginBottom: 15 }}
                            rules={[{ required: true, message: intl.formatMessage({ id: 'login_password_msg' }) }]}
                        >
                            <Input.Password
                                style={{ height: 50 }}
                                size="large"
                                prefix={<LockOutlined />}
                                placeholder={intl.formatMessage({ id: 'login_password_msg' })}
                            />
                        </Form.Item>
                        <p className="dv-login-btn">
                            <a href="#/register" className="dv-register-btn">
                                {intl.formatMessage({ id: 'register' })}
                            </a>
                            <Button
                                type="primary"
                                htmlType="submit"
                                loading={loading}
                                className="dv-login-submit"
                                size="large"
                            >
                                {intl.formatMessage({ id: 'login_btn_text' })}
                                <ArrowRightOutlined />
                            </Button>
                        </p>
                    </Form>
                </div>
            </div>
        </div>
    );
};
export default Login;
