/**
 * Ant Design → Mantine compatibility layer.
 * Webpack aliases `antd` / `antd/*` here so existing imports keep compiling.
 */
export * from './components';
export { Form, useForm, FormProvider } from './form';
export type { FormInstance, FormItemProps, FormProps, Rule } from './form';
export { message, notification } from './message';
