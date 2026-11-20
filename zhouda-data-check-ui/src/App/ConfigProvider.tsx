import React from 'react';
import { MantineProvider, createTheme } from '@mantine/core';
import { Notifications } from '@mantine/notifications';
import { ConfirmHost } from '@/ui/antdCompat';
import '@mantine/core/styles.css';
import '@mantine/notifications/styles.css';
import '@mantine/dates/styles.css';

const theme = createTheme({
    primaryColor: 'teal',
    primaryShade: 7,
    fontFamily: '"IBM Plex Sans", "DM Sans", "Segoe UI", sans-serif',
    headings: {
        fontFamily: '"DM Sans", "IBM Plex Sans", sans-serif',
        fontWeight: '650',
    },
    defaultRadius: 'sm',
    colors: {
        teal: [
            '#f0fdfa',
            '#ccfbf1',
            '#99f6e4',
            '#5eead4',
            '#2dd4bf',
            '#14b8a6',
            '#0d9488',
            '#0f766e',
            '#115e59',
            '#134e4a',
        ],
        slate: [
            '#f8fafc',
            '#f1f5f9',
            '#e2e8f0',
            '#cbd5e1',
            '#94a3b8',
            '#64748b',
            '#475569',
            '#334155',
            '#1e293b',
            '#0f172a',
        ],
    },
    other: {
        brand: 'zhoudaDataCheck',
        accent: '#0F766E',
        ink: '#1e293b',
        surface: '#f1f5f9',
    },
});

const ConfigProviderWrap: React.FC<{ children: React.ReactNode }> = ({ children }) => (
    <MantineProvider theme={theme} defaultColorScheme="light">
        <Notifications position="top-right" zIndex={4000} />
        <ConfirmHost />
        {children}
    </MantineProvider>
);

export {
    ConfigProviderWrap,
};
