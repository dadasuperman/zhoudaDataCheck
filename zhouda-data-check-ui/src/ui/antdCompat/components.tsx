/* eslint-disable react/jsx-props-no-spreading, max-len */
import React, { useMemo, useState, useEffect } from 'react';
import {
    Button as MButton,
    TextInput,
    PasswordInput,
    Textarea,
    Select as MSelect,
    MultiSelect,
    Loader,
    Modal as MModal,
    Tabs as MTabs,
    Menu as MMenu,
    Popover as MPopover,
    Tooltip as MTooltip,
    Switch as MSwitch,
    Checkbox as MCheckbox,
    Radio as MRadio,
    Card as MCard,
    Badge,
    Divider as MDivider,
    Alert as MAlert,
    Drawer as MDrawer,
    Pagination as MPagination,
    SegmentedControl,
    Table as MTable,
    Group,
    Stack,
    Box,
    Text,
    Title,
    Space as MSpace,
    NumberInput,
    UnstyledButton,
    ScrollArea,
    Center,
} from '@mantine/core';
import { DatePickerInput, DateTimePicker } from '@mantine/dates';
import dayjs from 'dayjs';
import { Form } from './form';
import { message, notification } from './message';

// ——— Button ———
export const Button: React.FC<any> = ({
    type, danger, loading, children, onClick, disabled, style, className, size, htmlType, ghost, icon, block, ...rest
}) => {
    const variant = type === 'primary' ? 'filled'
        : type === 'link' || type === 'text' ? 'subtle'
            : type === 'dashed' ? 'outline'
                : 'default';
    const color = danger ? 'red' : type === 'primary' ? 'teal' : 'gray';
    return (
        <MButton
            type={htmlType || 'button'}
            variant={ghost ? 'outline' : variant}
            color={color}
            loading={!!loading}
            disabled={disabled}
            onClick={onClick}
            style={{ width: block ? '100%' : undefined, ...style }}
            className={className}
            size={size === 'large' ? 'md' : size === 'small' ? 'xs' : 'sm'}
            leftSection={icon}
            {...rest}
        >
            {children}
        </MButton>
    );
};

// ——— Input ———
const InputInner: React.FC<any> = ({
    prefix, suffix, addonAfter, addonBefore, size, style, value, onChange, allowClear, ...rest
}) => {
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        onChange?.(e);
    };
    return (
        <div style={{ display: 'flex', alignItems: 'center', gap: 4, ...style }}>
            {addonBefore}
            <TextInput
                leftSection={prefix}
                rightSection={suffix}
                value={value ?? ''}
                onChange={handleChange}
                style={{ flex: 1 }}
                size={size === 'large' ? 'md' : size === 'small' ? 'xs' : 'sm'}
                {...rest}
            />
            {addonAfter}
        </div>
    );
};

const Password: React.FC<any> = ({ prefix, size, style, value, onChange, ...rest }) => (
    <PasswordInput
        leftSection={prefix}
        value={value ?? ''}
        onChange={onChange}
        style={style}
        size={size === 'large' ? 'md' : 'sm'}
        {...rest}
    />
);

const TextAreaComp: React.FC<any> = ({ value, onChange, rows, style, ...rest }) => (
    <Textarea
        value={value ?? ''}
        onChange={onChange}
        minRows={rows || 3}
        style={style}
        {...rest}
    />
);

export const Input: any = InputInner;
Input.Password = Password;
Input.TextArea = TextAreaComp;
Input.Search = InputInner;
Input.Group = ({ children, style }: any) => <div style={{ display: 'flex', ...style }}>{children}</div>;

export const InputNumber: React.FC<any> = ({ value, onChange, style, ...rest }) => (
    <NumberInput
        value={value}
        onChange={(v) => onChange?.(v)}
        style={style}
        {...rest}
    />
);

// ——— Select ———
const SelectInner: React.FC<any> = ({
    options,
    value,
    onChange,
    placeholder,
    style,
    className,
    mode,
    disabled,
    allowClear,
    showSearch,
    filterOption,
    fieldNames,
    children,
    optionFilterProp,
    ...rest
}) => {
    const labelKey = fieldNames?.label || 'label';
    const valueKey = fieldNames?.value || 'value';

    let data: { value: any; label: any }[] = [];
    if (options) {
        data = options.map((o: any) => ({
            value: o[valueKey],
            label: o[labelKey] ?? o.label ?? String(o[valueKey]),
        }));
    } else if (children) {
        const arr = React.Children.toArray(children) as React.ReactElement[];
        data = arr.map((c) => ({
            value: c.props.value,
            label: c.props.children,
        }));
    }

    if (mode === 'multiple' || mode === 'tags') {
        return (
            <MultiSelect
                data={data.map((d) => ({ value: String(d.value), label: String(d.label) }))}
                value={(value || []).map(String)}
                onChange={(v) => onChange?.(v)}
                placeholder={placeholder}
                style={style}
                className={className}
                disabled={disabled}
                searchable={!!showSearch}
                clearable={!!allowClear}
                {...rest}
            />
        );
    }

    return (
        <MSelect
            data={data.map((d) => ({ value: String(d.value), label: String(d.label) }))}
            value={value === undefined || value === null ? null : String(value)}
            onChange={(v) => {
                if (v === null) {
                    onChange?.(undefined);
                    return;
                }
                const raw = data.find((d) => String(d.value) === v);
                onChange?.(raw ? raw.value : v);
            }}
            placeholder={placeholder}
            style={style}
            className={className}
            disabled={disabled}
            searchable={!!showSearch}
            clearable={!!allowClear}
            {...rest}
        />
    );
};
export const Select: any = SelectInner;
Select.Option = ({ children }: any) => <>{children}</>;
Select.OptGroup = ({ children }: any) => <>{children}</>;

// ——— Spin ———
export const Spin: React.FC<any> = ({ spinning = true, children, style, tip, size }) => {
    if (!children) {
        return (
            <Center style={style}>
                <Loader size={size === 'large' ? 'lg' : 'md'} color="teal" />
                {tip ? <Text ml="sm" size="sm">{tip}</Text> : null}
            </Center>
        );
    }
    return (
        <Box pos="relative" style={style}>
            {spinning && (
                <Center
                    style={{
                        position: 'absolute', inset: 0, zIndex: 10, background: 'rgba(255,255,255,0.55)',
                    }}
                >
                    <Loader color="teal" />
                </Center>
            )}
            {children}
        </Box>
    );
};

// ——— Layout ———
const LayoutRoot: React.FC<any> = ({ children, style, className, hasSider }) => (
    <div className={className} style={{ display: 'flex', flexDirection: hasSider ? 'row' : 'column', minHeight: '100%', ...style }}>
        {children}
    </div>
);
const Header: React.FC<any> = ({ children, style, className }) => (
    <header className={className} style={{ flexShrink: 0, ...style }}>{children}</header>
);
const Content: React.FC<any> = ({ children, style, className }) => (
    <main className={className} style={{ flex: 1, minWidth: 0, ...style }}>{children}</main>
);
const Footer: React.FC<any> = ({ children, style, className }) => (
    <footer className={className} style={style}>{children}</footer>
);
const Sider: React.FC<any> = ({
    children, style, className, collapsed, width = 200, collapsedWidth = 80, trigger, onCollapse,
}) => (
    <aside
        className={className}
        style={{
            width: collapsed ? collapsedWidth : width,
            flexShrink: 0,
            position: 'relative',
            transition: 'width 0.2s',
            ...style,
        }}
    >
        {children}
        {trigger !== null && trigger !== undefined && (
            <div onClick={() => onCollapse?.(!collapsed)} style={{ cursor: 'pointer' }}>
                {trigger}
            </div>
        )}
    </aside>
);
export const Layout: any = LayoutRoot;
Layout.Header = Header;
Layout.Content = Content;
Layout.Footer = Footer;
Layout.Sider = Sider;

// ——— Grid ———
export const Row: React.FC<any> = ({ children, gutter, style, className, justify, align, wrap }) => {
    const g = Array.isArray(gutter) ? gutter[0] : gutter || 0;
    return (
        <div
            className={className}
            style={{
                display: 'flex',
                flexWrap: wrap === false ? 'nowrap' : 'wrap',
                justifyContent: justify === 'space-between' ? 'space-between' : justify === 'center' ? 'center' : justify === 'end' ? 'flex-end' : 'flex-start',
                alignItems: align === 'middle' ? 'center' : align || 'stretch',
                marginLeft: -g / 2,
                marginRight: -g / 2,
                ...style,
            }}
        >
            {React.Children.map(children, (child) => {
                if (!React.isValidElement(child)) return child;
                return React.cloneElement(child as any, {
                    style: {
                        paddingLeft: g / 2,
                        paddingRight: g / 2,
                        ...((child as any).props?.style || {}),
                    },
                });
            })}
        </div>
    );
};

export const Col: React.FC<any> = ({
    children, span, style, className, flex,
}) => {
    const width = span != null ? `${(span / 24) * 100}%` : undefined;
    return (
        <div className={className} style={{ width, flex, boxSizing: 'border-box', ...style }}>
            {children}
        </div>
    );
};

// ——— Modal ———
let confirmHost: { open: (cfg: any) => void; destroyAll: () => void } | null = null;

export const setConfirmHost = (host: typeof confirmHost) => {
    confirmHost = host;
};

export const Modal: any = ({
    open, visible, title, children, onOk, onCancel, onClose, footer, width, style, className,
    confirmLoading, destroyOnClose, afterClose, maskClosable, okText, cancelText, ...rest
}) => {
    const opened = open ?? visible ?? false;
    const [wasOpen, setWasOpen] = useState(opened);
    useEffect(() => {
        if (wasOpen && !opened) afterClose?.();
        setWasOpen(opened);
    }, [opened]);

    const handleClose = () => {
        onCancel?.();
        onClose?.();
    };

    return (
        <MModal
            opened={!!opened}
            onClose={handleClose}
            title={title}
            size={typeof width === 'number' ? width : width || 'lg'}
            className={className}
            style={style}
            closeOnClickOutside={maskClosable !== false}
            {...rest}
        >
            {children}
            {footer !== null && (
                <Group justify="flex-end" mt="md">
                    {footer !== undefined ? footer : (
                        <>
                            <Button onClick={handleClose}>{cancelText || 'Cancel'}</Button>
                            <Button type="primary" loading={confirmLoading} onClick={onOk}>{okText || 'OK'}</Button>
                        </>
                    )}
                </Group>
            )}
        </MModal>
    );
};

Modal.confirm = (cfg: any) => {
    if (confirmHost) {
        confirmHost.open(cfg);
        return { destroy: () => confirmHost?.destroyAll() };
    }
    // eslint-disable-next-line no-alert
    const ok = window.confirm(String(cfg.content || cfg.title || ''));
    if (ok) cfg.onOk?.();
    else cfg.onCancel?.();
    return { destroy: () => undefined };
};
Modal.info = Modal.confirm;
Modal.success = Modal.confirm;
Modal.error = Modal.confirm;
Modal.warning = Modal.confirm;
Modal.destroyAll = () => confirmHost?.destroyAll();

export const ConfirmHost: React.FC = () => {
    const [cfg, setCfg] = useState<any>(null);
    useEffect(() => {
        setConfirmHost({
            open: (c) => setCfg(c),
            destroyAll: () => setCfg(null),
        });
        return () => setConfirmHost(null);
    }, []);
    if (!cfg) return null;
    return (
        <MModal
            opened
            onClose={() => {
                cfg.onCancel?.();
                setCfg(null);
            }}
            title={cfg.title}
            centered
        >
            <div style={{ display: 'flex', gap: 12, alignItems: 'flex-start' }}>
                {cfg.icon}
                <div>{cfg.content}</div>
            </div>
            <Group justify="flex-end" mt="md">
                <Button onClick={() => { cfg.onCancel?.(); setCfg(null); }}>
                    {cfg.cancelText || 'Cancel'}
                </Button>
                <Button
                    type="primary"
                    onClick={() => {
                        const r = cfg.onOk?.();
                        if (r && typeof r.then === 'function') {
                            r.then(() => setCfg(null));
                        } else {
                            setCfg(null);
                        }
                    }}
                >
                    {cfg.okText || 'OK'}
                </Button>
            </Group>
        </MModal>
    );
};

// ——— Tabs ———
const TabPane: React.FC<any> = ({ children }) => <>{children}</>;

export const Tabs: any = ({
    items, activeKey, defaultActiveKey, onChange, children, type, style, className, tabBarExtraContent, ...rest
}) => {
    const fromChildren = React.Children.toArray(children) as React.ReactElement[];
    const tabItems = items || fromChildren.map((c) => ({
        key: c.key || c.props?.tabKey || c.props?.tab,
        label: c.props?.tab || c.props?.label,
        children: c.props?.children,
    }));

    const value = String(activeKey ?? defaultActiveKey ?? tabItems[0]?.key ?? '0');

    return (
        <div style={style} className={className}>
            {tabBarExtraContent ? (
                <Group justify="space-between" mb="xs">
                    <div />
                    <div>{tabBarExtraContent}</div>
                </Group>
            ) : null}
            <MTabs
                value={value}
                onChange={(v) => onChange?.(v)}
                variant={type === 'card' ? 'outline' : 'default'}
                {...rest}
            >
                <MTabs.List>
                    {tabItems.map((it: any) => (
                        <MTabs.Tab key={String(it.key)} value={String(it.key)} disabled={it.disabled}>
                            {it.label}
                        </MTabs.Tab>
                    ))}
                </MTabs.List>
                {tabItems.map((it: any) => (
                    <MTabs.Panel key={String(it.key)} value={String(it.key)} pt="sm">
                        {it.children}
                    </MTabs.Panel>
                ))}
            </MTabs>
        </div>
    );
};
Tabs.TabPane = TabPane;

// ——— Menu ———
export const Menu: any = ({
    items, children, onClick, selectedKeys, mode, style, className, selectable, ...rest
}) => {
    const list = items || React.Children.toArray(children).map((c: any) => ({
        key: c.key || c.props?.eventKey,
        label: c.props?.children,
        icon: c.props?.icon,
        disabled: c.props?.disabled,
    }));

    return (
        <nav className={className} style={{ display: 'flex', flexDirection: mode === 'horizontal' ? 'row' : 'column', gap: 4, ...style }} {...rest}>
            {list.filter(Boolean).map((it: any) => {
                const active = selectedKeys?.includes?.(String(it.key)) || selectedKeys?.includes?.(it.key);
                return (
                    <UnstyledButton
                        key={String(it.key)}
                        disabled={it.disabled}
                        onClick={(e) => onClick?.({ key: it.key, keyPath: [it.key], domEvent: e, item: it })}
                        style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: 8,
                            padding: '8px 14px',
                            borderRadius: 6,
                            background: active ? 'rgba(13, 148, 136, 0.12)' : 'transparent',
                            color: active ? '#0F766E' : '#1e293b',
                            fontWeight: active ? 600 : 450,
                            width: '100%',
                            textAlign: 'left',
                        }}
                    >
                        {it.icon}
                        <span>{it.label}</span>
                    </UnstyledButton>
                );
            })}
        </nav>
    );
};
Menu.Item = ({ children }: any) => <>{children}</>;
Menu.SubMenu = ({ children }: any) => <>{children}</>;
Menu.Divider = () => <MDivider my={4} />;
Menu.ItemGroup = ({ children }: any) => <>{children}</>;

// ——— Dropdown ———
const DropdownInner: React.FC<any> = ({
    overlay, menu, children, trigger, open, onVisibleChange, onOpenChange, placement, ...rest
}) => {
    const [opened, setOpened] = useState(!!open);
    useEffect(() => {
        if (open !== undefined) setOpened(open);
    }, [open]);

    const setOpen = (v: boolean) => {
        setOpened(v);
        onVisibleChange?.(v);
        onOpenChange?.(v);
    };

    const content = overlay || (menu ? (
        <Menu items={menu.items} onClick={(info: any) => { menu.onClick?.(info); setOpen(false); }} />
    ) : null);

    return (
        <MPopover opened={opened} onChange={setOpen} position={placement?.includes('bottom') ? 'bottom-end' : 'bottom-start'} {...rest}>
            <MPopover.Target>
                <span onClick={() => (trigger === undefined || trigger?.includes?.('click') ? setOpen(!opened) : undefined)}>
                    {children}
                </span>
            </MPopover.Target>
            <MPopover.Dropdown>{content}</MPopover.Dropdown>
        </MPopover>
    );
};
export const Dropdown: any = DropdownInner;
Dropdown.Button = ({ children, overlay, menu, onClick, ...rest }: any) => (
    <Group gap={0}>
        <Button onClick={onClick} {...rest}>{children}</Button>
        <Dropdown overlay={overlay} menu={menu}>
            <Button>...</Button>
        </Dropdown>
    </Group>
);

// ——— Popover / Popconfirm / Tooltip ———
export const Popover: React.FC<any> = ({
    content, title, children, trigger, placement, ...rest
}) => (
    <MPopover position={placement?.includes('right') ? 'right-end' : 'bottom'} {...rest}>
        <MPopover.Target>
            <span>{children}</span>
        </MPopover.Target>
        <MPopover.Dropdown>
            {title ? <Text fw={600} mb={4}>{title}</Text> : null}
            {content}
        </MPopover.Dropdown>
    </MPopover>
);

export const Popconfirm: React.FC<any> = ({
    title, description, onConfirm, onCancel, children, okText, cancelText, ...rest
}) => {
    const [opened, setOpened] = useState(false);
    return (
        <MPopover opened={opened} onChange={setOpened} {...rest}>
            <MPopover.Target>
                <span onClick={() => setOpened(true)}>{children}</span>
            </MPopover.Target>
            <MPopover.Dropdown>
                <Text size="sm" mb="sm">{title}</Text>
                {description}
                <Group gap="xs" justify="flex-end">
                    <Button size="small" onClick={() => { onCancel?.(); setOpened(false); }}>{cancelText || 'Cancel'}</Button>
                    <Button size="small" type="primary" onClick={() => { onConfirm?.(); setOpened(false); }}>{okText || 'OK'}</Button>
                </Group>
            </MPopover.Dropdown>
        </MPopover>
    );
};

export const Tooltip: React.FC<any> = ({ title, children, placement }) => (
    <MTooltip label={title} position={placement as any}>
        <span>{children}</span>
    </MTooltip>
);

// ——— DatePicker ———
export const DatePicker: any = ({
    value, onChange, showTime, style, className, placeholder, ...rest
}) => {
    const Comp: any = showTime ? DateTimePicker : DatePickerInput;
    const dateVal = value ? (dayjs.isDayjs(value) ? value.toDate() : value instanceof Date ? value : dayjs(value).toDate()) : null;
    return (
        <Comp
            value={dateVal}
            onChange={(d: Date | null) => {
                const dj = d ? dayjs(d) : null;
                onChange?.(dj, dj ? dj.format(showTime ? 'YYYY-MM-DD HH:mm:ss' : 'YYYY-MM-DD') : '');
            }}
            style={style}
            className={className}
            placeholder={placeholder}
            valueFormat={showTime ? 'YYYY-MM-DD HH:mm:ss' : 'YYYY-MM-DD'}
            {...rest}
        />
    );
};

DatePicker.RangePicker = ({ value, onChange, showTime, style, ...rest }: any) => {
    const Comp: any = showTime ? DateTimePicker : DatePickerInput;
    // simplified: two pickers
    const [start, end] = value || [];
    return (
        <Group gap="xs" style={style}>
            <Comp
                value={start ? (dayjs.isDayjs(start) ? start.toDate() : start) : null}
                onChange={(d: Date | null) => {
                    const s = d ? dayjs(d) : null;
                    const e = end ? dayjs(end) : null;
                    onChange?.([s, e], [s?.format?.() || '', e?.format?.() || '']);
                }}
                {...rest}
            />
            <span>~</span>
            <Comp
                value={end ? (dayjs.isDayjs(end) ? end.toDate() : end) : null}
                onChange={(d: Date | null) => {
                    const s = start ? dayjs(start) : null;
                    const e = d ? dayjs(d) : null;
                    onChange?.([s, e], [s?.format?.() || '', e?.format?.() || '']);
                }}
                {...rest}
            />
        </Group>
    );
};

// ——— Radio / Checkbox / Switch ———
export const Radio: any = ({ children, value, ...rest }: any) => (
    <MRadio value={String(value)} label={children} {...rest} />
);
Radio.Group = ({ children, value, onChange, ...rest }: any) => (
    <MRadio.Group
        value={value !== undefined && value !== null ? String(value) : undefined}
        onChange={(v) => onChange?.({ target: { value: v } })}
        {...rest}
    >
        <Group>{children}</Group>
    </MRadio.Group>
);
Radio.Button = Radio;

export const Checkbox: any = ({ children, checked, onChange, ...rest }: any) => (
    <MCheckbox
        label={children}
        checked={!!checked}
        onChange={(e) => onChange?.(e)}
        {...rest}
    />
);
Checkbox.Group = ({ children, value, onChange, ...rest }: any) => (
    <MCheckbox.Group value={value} onChange={onChange} {...rest}>
        <Group>{children}</Group>
    </MCheckbox.Group>
);

export const Switch: React.FC<any> = ({ checked, onChange, ...rest }) => (
    <MSwitch
        checked={!!checked}
        onChange={(e) => onChange?.(e.currentTarget.checked, e)}
        {...rest}
    />
);

// ——— Misc ———
export const Card: any = ({ title, children, extra, style, className, bordered, ...rest }: any) => (
    <MCard withBorder={bordered !== false} style={style} className={className} {...rest}>
        {(title || extra) && (
            <Group justify="space-between" mb="sm">
                <Text fw={600}>{title}</Text>
                {extra}
            </Group>
        )}
        {children}
    </MCard>
);
Card.Meta = ({ title, description }: any) => (
    <div>
        <Text fw={600}>{title}</Text>
        <Text size="sm" c="dimmed">{description}</Text>
    </div>
);

export const Tag: React.FC<any> = ({
    children, color, closable, onClose, style,
}) => (
    <Badge
        color={color === 'success' ? 'teal' : color === 'processing' ? 'blue' : color === 'error' || color === 'red' ? 'red' : color === 'default' ? 'gray' : (color || 'gray')}
        style={style}
        rightSection={closable ? (
            <span
                style={{ cursor: 'pointer', marginLeft: 4 }}
                onClick={(e) => onClose?.(e)}
            >
                ×
            </span>
        ) : undefined}
    >
        {children}
    </Badge>
);

export const Divider: React.FC<any> = ({ children, style, orientation, ...rest }) => (
    <MDivider label={children} style={style} {...rest} />
);

export const Alert: React.FC<any> = ({
    message: msg, description, type, style, showIcon, ...rest
}) => (
    <MAlert
        title={msg}
        color={type === 'error' ? 'red' : type === 'success' ? 'teal' : type === 'warning' ? 'yellow' : 'blue'}
        style={style}
        {...rest}
    >
        {description}
    </MAlert>
);

export const Empty: any = ({ description, image, children, style }: any) => (
    <Center style={{ padding: 40, flexDirection: 'column', ...style }}>
        <Text c="dimmed" size="sm">{description || 'No data'}</Text>
        {children}
    </Center>
);
Empty.PRESENTED_IMAGE_SIMPLE = 'simple';

export const Drawer: React.FC<any> = ({
    open, visible, onClose, title, children, width, placement, ...rest
}) => (
    <MDrawer
        opened={!!(open ?? visible)}
        onClose={onClose}
        title={title}
        position={placement === 'left' ? 'left' : 'right'}
        size={width || 400}
        {...rest}
    >
        {children}
    </MDrawer>
);

export const Pagination: React.FC<any> = ({
    current, pageSize, total, onChange, onShowSizeChange, showSizeChanger, style, ...rest
}) => (
    <MPagination
        value={current || 1}
        total={Math.max(1, Math.ceil((total || 0) / (pageSize || 10)))}
        onChange={(p) => onChange?.(p, pageSize)}
        style={style}
        {...rest}
    />
);

export const Segmented: React.FC<any> = ({ options, value, onChange, ...rest }) => (
    <SegmentedControl
        data={(options || []).map((o: any) => (typeof o === 'string' ? o : { value: o.value, label: o.label }))}
        value={value}
        onChange={onChange}
        {...rest}
    />
);

export const Space: any = ({ children, size, direction, style, wrap, ...rest }: any) => (
    <Group
        gap={typeof size === 'number' ? size : size === 'large' ? 'md' : 'sm'}
        style={{ flexDirection: direction === 'vertical' ? 'column' : 'row', ...style }}
        wrap={wrap ? 'wrap' : undefined}
        {...rest}
    >
        {children}
    </Group>
);
Space.Compact = ({ children, style }: any) => <Group gap={0} style={style}>{children}</Group>;

export const Typography: any = {
    Title: ({ children, level, style, ...rest }: any) => (
        <Title order={Math.min(6, level || 2) as any} style={style} {...rest}>{children}</Title>
    ),
    Text: ({ children, ...rest }: any) => <Text {...rest}>{children}</Text>,
    Paragraph: ({ children, ...rest }: any) => <Text component="p" {...rest}>{children}</Text>,
    Link: ({ children, ...rest }: any) => <Text component="a" c="teal" {...rest}>{children}</Text>,
};

export const List: any = ({ dataSource, renderItem, header, style, bordered, loading, ...rest }: any) => (
    <Stack gap="xs" style={style} {...rest}>
        {header}
        {(dataSource || []).map((item: any, i: number) => (
            <div key={i}>{renderItem?.(item, i)}</div>
        ))}
    </Stack>
);
List.Item = ({ children, actions, ...rest }: any) => (
    <Group justify="space-between" {...rest}>
        <div>{children}</div>
        {actions}
    </Group>
);
List.Item.Meta = ({ title, description, avatar }: any) => (
    <Group>
        {avatar}
        <div>
            <Text fw={600}>{title}</Text>
            <Text size="sm" c="dimmed">{description}</Text>
        </div>
    </Group>
);

// ——— Table ———
export type ColumnsType<T = any> = Array<{
    title?: React.ReactNode;
    dataIndex?: string | string[];
    key?: string;
    width?: number | string;
    render?: (value: any, record: T, index: number) => React.ReactNode;
    ellipsis?: boolean;
    fixed?: any;
    align?: any;
    sorter?: any;
    filters?: any;
    onFilter?: any;
    [key: string]: any;
}>;
export type TableColumnsType<T = any> = ColumnsType<T>;

function getByPath(obj: any, path?: string | string[]) {
    if (!path) return undefined;
    if (Array.isArray(path)) return path.reduce((a, k) => a?.[k], obj);
    return obj?.[path];
}

export const Table: React.FC<any> = ({
    columns = [],
    dataSource = [],
    rowKey,
    pagination,
    loading,
    style,
    className,
    size,
    bordered,
    onChange,
    scroll,
    rowSelection,
    ...rest
}) => {
    const getKey = (record: any, index: number) => {
        if (typeof rowKey === 'function') return rowKey(record);
        if (typeof rowKey === 'string') return record[rowKey];
        return record.key ?? record.id ?? index;
    };

    const page = pagination && pagination !== false ? pagination : null;
    const pageSize = page?.pageSize || 10;
    const current = page?.current || 1;
    const total = page?.total ?? dataSource.length;
    const sliced = page
        ? (page.total != null ? dataSource : dataSource.slice((current - 1) * pageSize, current * pageSize))
        : dataSource;

    return (
        <Spin spinning={!!loading}>
            <ScrollArea style={scroll?.x ? { maxWidth: '100%' } : undefined}>
                <MTable
                    striped
                    highlightOnHover
                    withTableBorder={bordered}
                    className={className}
                    style={style}
                    {...rest}
                >
                    <MTable.Thead>
                        <MTable.Tr>
                            {columns.map((col: any, i: number) => (
                                <MTable.Th key={col.key || col.dataIndex || i} style={{ width: col.width }}>
                                    {col.title}
                                </MTable.Th>
                            ))}
                        </MTable.Tr>
                    </MTable.Thead>
                    <MTable.Tbody>
                        {sliced.map((record: any, index: number) => (
                            <MTable.Tr key={getKey(record, index)}>
                                {columns.map((col: any, ci: number) => {
                                    const val = getByPath(record, col.dataIndex);
                                    const content = col.render ? col.render(val, record, index) : val;
                                    return (
                                        <MTable.Td key={col.key || col.dataIndex || ci}>
                                            {content}
                                        </MTable.Td>
                                    );
                                })}
                            </MTable.Tr>
                        ))}
                        {!sliced.length && (
                            <MTable.Tr>
                                <MTable.Td colSpan={columns.length}>
                                    <Empty description="No data" />
                                </MTable.Td>
                            </MTable.Tr>
                        )}
                    </MTable.Tbody>
                </MTable>
            </ScrollArea>
            {page && (
                <Group justify="flex-end" mt="sm">
                    <Pagination
                        current={current}
                        pageSize={pageSize}
                        total={total}
                        onChange={(p: number, ps: number) => {
                            page.onChange?.(p, ps || pageSize);
                            onChange?.({ current: p, pageSize: ps || pageSize }, {}, {});
                        }}
                    />
                </Group>
            )}
        </Spin>
    );
};

// ——— Tree (minimal) ———
export type DataNode = {
    title?: React.ReactNode;
    key: string | number;
    children?: DataNode[];
    isLeaf?: boolean;
    icon?: React.ReactNode;
    disabled?: boolean;
    [key: string]: any;
};
export type EventDataNode = DataNode & { expanded?: boolean };

const TreeNode: React.FC<{
    node: DataNode;
    selectedKeys?: (string | number)[];
    expandedKeys?: (string | number)[];
    onSelect?: any;
    onExpand?: any;
    titleRender?: any;
    loadData?: any;
}> = ({
    node, selectedKeys, expandedKeys, onSelect, onExpand, titleRender, loadData,
}) => {
    const expanded = expandedKeys?.includes(node.key);
    const selected = selectedKeys?.includes(node.key);
    return (
        <div style={{ marginLeft: 12 }}>
            <div
                style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: 4,
                    padding: '2px 6px',
                    borderRadius: 4,
                    background: selected ? 'rgba(13,148,136,0.12)' : undefined,
                    cursor: 'pointer',
                }}
                onClick={() => onSelect?.([node.key], { node, selected: !selected })}
            >
                {node.children || !node.isLeaf ? (
                    <span
                        onClick={(e) => {
                            e.stopPropagation();
                            const next = expanded
                                ? (expandedKeys || []).filter((k) => k !== node.key)
                                : [...(expandedKeys || []), node.key];
                            onExpand?.(next, { node, expanded: !expanded });
                            if (!expanded && loadData) loadData(node);
                        }}
                        style={{ width: 14 }}
                    >
                        {expanded ? '▾' : '▸'}
                    </span>
                ) : <span style={{ width: 14 }} />}
                {node.icon}
                <span>{titleRender ? titleRender(node) : node.title}</span>
            </div>
            {expanded && (node.children || []).map((c) => (
                <TreeNode
                    key={c.key}
                    node={c}
                    selectedKeys={selectedKeys}
                    expandedKeys={expandedKeys}
                    onSelect={onSelect}
                    onExpand={onExpand}
                    titleRender={titleRender}
                    loadData={loadData}
                />
            ))}
        </div>
    );
};

export const Tree: React.FC<any> = ({
    treeData = [], selectedKeys, expandedKeys, onSelect, onExpand, titleRender, loadData, style, ...rest
}) => (
    <div style={style} {...rest}>
        {treeData.map((n: DataNode) => (
            <TreeNode
                key={n.key}
                node={n}
                selectedKeys={selectedKeys}
                expandedKeys={expandedKeys}
                onSelect={onSelect}
                onExpand={onExpand}
                titleRender={titleRender}
                loadData={loadData}
            />
        ))}
    </div>
);

export type TreeProps = React.ComponentProps<typeof Tree>;

// ——— ConfigProvider stub ———
export const ConfigProvider: React.FC<any> = ({ children }) => <>{children}</>;

export { Form, message, notification };
export type { FormInstance, FormItemProps, FormProps } from './form';
export type MenuProps = { onClick?: (info: any) => void; items?: any[]; [k: string]: any };
export type TabsProps = React.ComponentProps<typeof Tabs>;
export type ModalProps = {
    open?: boolean;
    visible?: boolean;
    title?: React.ReactNode;
    onOk?: (...args: any[]) => any;
    onCancel?: (...args: any[]) => any;
    afterClose?: () => void;
    destroyOnClose?: boolean;
    width?: number | string;
    footer?: React.ReactNode;
    children?: React.ReactNode;
    [k: string]: any;
};
export type DatePickerProps = any;
export type SelectProps = any;
export type Gutter = number | [number, number];
export type RowProps = React.ComponentProps<typeof Row>;
export type ColProps = React.ComponentProps<typeof Col>;

/** Minimal Cascader stand-in (dashboard filters). */
export const Cascader: React.FC<any> = ({
    options = [], value, onChange, placeholder, style, className, changeOnSelect, ...rest
}) => {
    const flat = useMemo(() => {
        const out: { value: string; label: string }[] = [];
        const walk = (nodes: any[], prefix: string[] = [], labels: string[] = []) => {
            (nodes || []).forEach((n) => {
                const path = [...prefix, String(n.value)];
                const labs = [...labels, String(n.label ?? n.value)];
                if (!n.children?.length) {
                    out.push({ value: path.join('/'), label: labs.join(' / ') });
                } else {
                    walk(n.children, path, labs);
                    if (changeOnSelect) out.push({ value: path.join('/'), label: labs.join(' / ') });
                }
            });
        };
        walk(options);
        return out;
    }, [options, changeOnSelect]);

    const current = Array.isArray(value) ? value.join('/') : value;
    return (
        <MSelect
            data={flat}
            value={current || null}
            onChange={(v) => onChange?.(v ? v.split('/') : [], [])}
            placeholder={placeholder}
            style={style}
            className={className}
            clearable
            searchable
            {...rest}
        />
    );
};
