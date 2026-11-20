/* eslint-disable react/jsx-props-no-spreading */
import React, { useMemo } from 'react';
import RcForm, {
    Field, useForm as useRcForm, FormProvider, useWatch as useRcWatch,
} from 'rc-field-form';
import type { FormInstance as RcFormInstance } from 'rc-field-form';

export type FormInstance = RcFormInstance;
export type Rule = any;
export type FormItemProps = {
    name?: string | (string | number)[];
    label?: React.ReactNode;
    rules?: Rule[];
    children?: any;
    style?: React.CSSProperties;
    className?: string;
    valuePropName?: string;
    trigger?: string;
    getValueFromEvent?: (...args: any[]) => any;
    initialValue?: any;
    hidden?: boolean;
    noStyle?: boolean;
    dependencies?: any[];
    shouldUpdate?: boolean | ((prev: any, next: any) => boolean);
    extra?: React.ReactNode;
    help?: React.ReactNode;
    validateStatus?: string;
    required?: boolean;
    colon?: boolean;
    labelCol?: any;
    wrapperCol?: any;
    [key: string]: any;
};

export type FormProps = {
    form?: FormInstance;
    children?: React.ReactNode;
    layout?: 'horizontal' | 'vertical' | 'inline';
    name?: string;
    initialValues?: any;
    onFinish?: (values: any) => void;
    onFinishFailed?: (errorInfo: any) => void;
    onValuesChange?: (changed: any, all: any) => void;
    onFieldsChange?: (...args: any[]) => void;
    style?: React.CSSProperties;
    className?: string;
    labelCol?: any;
    wrapperCol?: any;
    size?: string;
    colon?: boolean;
    preserve?: boolean;
    requiredMark?: boolean | 'optional';
    [key: string]: any;
};

const FormItem: React.FC<FormItemProps> = (props) => {
    const {
        name,
        label,
        rules,
        children,
        style,
        className,
        valuePropName = 'value',
        trigger = 'onChange',
        getValueFromEvent,
        hidden,
        noStyle,
        shouldUpdate,
        dependencies,
        extra,
        help,
        ...rest
    } = props;

    if (shouldUpdate || (!name && (shouldUpdate !== undefined || dependencies))) {
        return (
            <Field shouldUpdate={shouldUpdate as any} dependencies={dependencies}>
                {(...args: any[]) => (typeof children === 'function' ? (children as any)(...args) : children)}
            </Field>
        );
    }

    if (!name) {
        return (
            <div className={className} style={{ marginBottom: noStyle ? 0 : 12, display: hidden ? 'none' : undefined, ...style }}>
                {label ? <div style={{ marginBottom: 4, fontWeight: 500 }}>{label}</div> : null}
                {children}
                {extra}
            </div>
        );
    }

    return (
        <Field
            name={name as any}
            rules={rules}
            valuePropName={valuePropName}
            trigger={trigger}
            getValueFromEvent={getValueFromEvent}
            {...rest}
        >
            {(control, meta) => {
                const child = children as React.ReactElement;
                let merged: React.ReactNode = children;
                if (child && React.isValidElement(child)) {
                    const childProps = (child.props || {}) as any;
                    merged = React.cloneElement(child, {
                        ...childProps,
                        [valuePropName]: control[valuePropName],
                        [trigger]: (...args: any[]) => {
                            control[trigger]?.(...args);
                            childProps[trigger]?.(...args);
                        },
                    });
                }
                if (noStyle) {
                    return (
                        <>
                            {merged}
                            {(help || meta.errors?.[0]) && (
                                <div style={{ color: '#c92a2a', fontSize: 12 }}>{help || meta.errors[0]}</div>
                            )}
                        </>
                    );
                }
                return (
                    <div
                        className={className}
                        style={{
                            marginBottom: 12,
                            display: hidden ? 'none' : undefined,
                            ...style,
                        }}
                    >
                        {label ? (
                            <div style={{ marginBottom: 4, fontWeight: 500, fontSize: 13 }}>
                                {label}
                            </div>
                        ) : null}
                        {merged}
                        {(help || meta.errors?.[0]) && (
                            <div style={{ color: '#c92a2a', fontSize: 12, marginTop: 4 }}>
                                {help || meta.errors[0]}
                            </div>
                        )}
                        {extra}
                    </div>
                );
            }}
        </Field>
    );
};

const InternalForm: React.FC<FormProps> = ({
    form,
    children,
    layout = 'horizontal',
    initialValues,
    onFinish,
    onFinishFailed,
    onValuesChange,
    onFieldsChange,
    style,
    className,
    ...rest
}) => {
    const layoutStyle: React.CSSProperties = layout === 'inline'
        ? { display: 'flex', flexWrap: 'wrap', gap: 8, alignItems: 'flex-end' }
        : layout === 'vertical'
            ? {}
            : {};

    return (
        <RcForm
            form={form}
            initialValues={initialValues}
            onFinish={onFinish}
            onFinishFailed={onFinishFailed}
            onValuesChange={onValuesChange}
            onFieldsChange={onFieldsChange}
            component="form"
            className={className}
            style={{ ...layoutStyle, ...style }}
            {...rest}
        >
            {children}
        </RcForm>
    );
};

type FormType = React.FC<FormProps> & {
    Item: typeof FormItem;
    useForm: typeof useRcForm;
    useWatch: typeof useRcWatch;
    List: typeof RcForm.List;
    Provider: typeof FormProvider;
    ErrorList: React.FC<any>;
};

export const Form = InternalForm as FormType;
Form.Item = FormItem;
Form.useForm = useRcForm;
Form.useWatch = useRcWatch;
Form.List = RcForm.List;
Form.Provider = FormProvider;
Form.ErrorList = () => null;

export { useRcForm as useForm, FormProvider };
export default Form;
