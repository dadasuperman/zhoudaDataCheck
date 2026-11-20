const isDevelopment = process.env.NODE_ENV === 'development';
module.exports = {
    presets: [
        ['@babel/preset-env'],
        '@babel/preset-react',
        '@babel/preset-typescript',
    ],
    plugins: [
        isDevelopment && 'react-refresh/babel',
        isDevelopment && 'react-dev-inspector/plugins/babel',
        '@babel/plugin-syntax-dynamic-import',
    ].filter(Boolean),
};
