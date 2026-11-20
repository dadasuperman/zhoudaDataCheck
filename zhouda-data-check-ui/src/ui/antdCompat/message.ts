import { notifications } from '@mantine/notifications';

type Content = any;

const show = (color: string, content: Content, _duration?: number) => {
    const id = `msg-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`;
    notifications.show({
        id,
        message: content as any,
        color,
        autoClose: (_duration ?? 2) * 1000,
    });
    return id;
};

export const message = {
    config: (_opts?: { top?: number; duration?: number; maxCount?: number }) => undefined,
    success: (content: Content, duration?: number) => show('teal', content, duration),
    error: (content: Content, duration?: number) => show('red', content, duration),
    warning: (content: Content, duration?: number) => show('yellow', content, duration),
    info: (content: Content, duration?: number) => show('blue', content, duration),
    loading: (content: Content, duration?: number) => show('gray', content, duration),
    open: (cfg: { type?: string; content?: Content; duration?: number }) => {
        const map: Record<string, string> = {
            success: 'teal', error: 'red', warning: 'yellow', info: 'blue',
        };
        return show(map[cfg.type || 'info'] || 'blue', cfg.content, cfg.duration);
    },
    destroy: (id?: string) => {
        if (id) notifications.hide(id);
        else notifications.clean();
    },
};

export const notification = {
    success: (args: { message?: Content; description?: Content; duration?: number }) => {
        notifications.show({
            title: args.message as any,
            message: args.description as any,
            color: 'teal',
            autoClose: (args.duration ?? 4.5) * 1000,
        });
    },
    error: (args: { message?: Content; description?: Content; duration?: number }) => {
        notifications.show({
            title: args.message as any,
            message: args.description as any,
            color: 'red',
            autoClose: (args.duration ?? 4.5) * 1000,
        });
    },
    warning: (args: { message?: Content; description?: Content; duration?: number }) => {
        notifications.show({
            title: args.message as any,
            message: args.description as any,
            color: 'yellow',
            autoClose: (args.duration ?? 4.5) * 1000,
        });
    },
    info: (args: { message?: Content; description?: Content; duration?: number }) => {
        notifications.show({
            title: args.message as any,
            message: args.description as any,
            color: 'blue',
            autoClose: (args.duration ?? 4.5) * 1000,
        });
    },
    open: (args: any) => notification.info(args),
    destroy: () => notifications.clean(),
};
