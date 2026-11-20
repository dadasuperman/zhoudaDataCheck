/* eslint-disable max-len, react/jsx-props-no-spreading */
/**
 * Thin @ant-design/icons → SVG replacements (no ant-design dependency).
 * Unknown icon names resolve to a generic glyph via Proxy.
 */
import React from 'react';

type IconProps = React.SVGProps<SVGSVGElement> & {
    style?: React.CSSProperties;
    className?: string;
    spin?: boolean;
    twoToneColor?: string;
    onClick?: (e: React.MouseEvent) => void;
};

const base = (d: string, viewBox = '0 0 24 24') => {
    const Icon: React.FC<IconProps> = ({ style, className, spin, onClick, ...rest }) => (
        <span
            role="img"
            className={className}
            onClick={onClick}
            style={{
                display: 'inline-flex',
                alignItems: 'center',
                justifyContent: 'center',
                lineHeight: 1,
                fontSize: '1em',
                verticalAlign: '-0.125em',
                ...(spin ? { animation: 'zd-spin 1s linear infinite' } : {}),
                ...style,
            }}
        >
            <svg
                viewBox={viewBox}
                width="1em"
                height="1em"
                fill="currentColor"
                aria-hidden
                {...rest}
            >
                <path d={d} />
            </svg>
        </span>
    );
    return Icon;
};

const fallbackPath = 'M12 2a10 10 0 100 20 10 10 0 000-20zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z';
const Fallback = base(fallbackPath);

const named: Record<string, React.FC<IconProps>> = {
    MenuUnfoldOutlined: base('M3 6h18v2H3V6zm0 5h12v2H3v-2zm0 5h18v2H3v-2z'),
    MenuFoldOutlined: base('M3 6h18v2H3V6zm6 5h12v2H9v-2zm-6 5h18v2H3v-2z'),
    LogoutOutlined: base('M16 13v-2H7V8l-5 4 5 4v-3h9zm3-10H11c-1.1 0-2 .9-2 2v3h2V5h8v14h-8v-3H9v3c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2z'),
    PlusOutlined: base('M19 11h-6V5h-2v6H5v2h6v6h2v-6h6z'),
    EditOutlined: base('M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04a1 1 0 000-1.41l-2.34-2.34a1 1 0 00-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z'),
    UserOutlined: base('M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8V22h19.2v-2.8c0-3.2-6.4-4.8-9.6-4.8z'),
    LockOutlined: base('M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1s3.1 1.39 3.1 3.1v2z'),
    ArrowRightOutlined: base('M12 4l-1.41 1.41L16.17 11H4v2h12.17l-5.58 5.59L12 20l8-8z'),
    ArrowLeftOutlined: base('M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20z'),
    CheckOutlined: base('M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z'),
    DeleteOutlined: base('M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z'),
    EllipsisOutlined: base('M6 10c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm6 0c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm6 0c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z'),
    DownloadOutlined: base('M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z'),
    SyncOutlined: base('M12 4V1L8 5l4 4V6c3.31 0 6 2.69 6 6 0 1.01-.25 1.97-.7 2.8l1.46 1.46C19.54 15.03 20 13.57 20 12c0-4.42-3.58-8-8-8zm0 14c-3.31 0-6-2.69-6-6 0-1.01.25-1.97.7-2.8L5.24 7.74C4.46 8.97 4 10.43 4 12c0 4.42 3.58 8 8 8v3l4-4-4-4v3z'),
    FullscreenExitOutlined: base('M5 16h3v3h2v-5H5v2zm3-8H5v2h5V5H8v3zm6 11h2v-3h3v-2h-5v5zm2-11V5h-2v5h5V8h-3z'),
    LoginOutlined: base('M10 17l5-5-5-5v3H3v4h7v3zm9-14H5c-1.1 0-2 .9-2 2v3h2V5h14v14H5v-3H3v3c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2z'),
    IdcardOutlined: base('M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 14H4V6h16v12zM6 10h4v4H6v-4zm6 0h6v2h-6v-2zm0 4h4v2h-4v-2z'),
    TableOutlined: base('M3 3h18v18H3V3zm2 2v4h4V5H5zm6 0v4h8V5h-8zM5 11v4h4v-4H5zm6 0v4h8v-4h-8zM5 17v2h4v-2H5zm6 0v2h8v-2h-8z'),
    DatabaseOutlined: base('M12 3C7 3 3 4.79 3 7v10c0 2.21 4 4 9 4s9-1.79 9-4V7c0-2.21-4-4-9-4zm0 2c3.87 0 7 .9 7 2s-3.13 2-7 2-7-.9-7-2 .13-2 7-2zm0 14c-3.87 0-7-.9-7-2v-1.14C6.5 16.5 9.07 17 12 17s5.5-.5 7-1.14V17c0 1.1-3.13 2-7 2zm0-4c-3.87 0-7-.9-7-2v-1.14C6.5 12.5 9.07 13 12 13s5.5-.5 7-1.14V13c0 1.1-3.13 2-7 2z'),
    WarningOutlined: base('M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z'),
    UsergroupAddOutlined: base('M15 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm-9-2V7H4v3H1v2h3v3h2v-3h3v-2H6zm9 4c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z'),
    CloseCircleOutlined: base('M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z'),
    TagOutlined: base('M21.41 11.58l-9-9C12.05 2.22 11.55 2 11 2H4c-1.1 0-2 .9-2 2v7c0 .55.22 1.05.59 1.42l9 9c.36.36.86.58 1.41.58s1.05-.22 1.41-.59l7-7c.37-.36.59-.86.59-1.41s-.23-1.06-.59-1.42zM5.5 7C4.67 7 4 6.33 4 5.5S4.67 4 5.5 4 7 4.67 7 5.5 6.33 7 5.5 7z'),
    FormOutlined: base('M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34a.996.996 0 00-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z'),
    SafetyCertificateOutlined: base('M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm-2 16l-4-4 1.41-1.41L10 14.17l6.59-6.59L18 9l-8 8z'),
    DashboardOutlined: base('M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z'),
    ScheduleOutlined: base('M19 4h-1V2h-2v2H8V2H6v2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V10h14v10zm0-12H5V6h14v2zM7 12h5v5H7v-5z'),
    AuditOutlined: base('M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z'),
    UnorderedListOutlined: base('M4 6h2v2H4V6zm0 5h2v2H4v-2zm0 5h2v2H4v-2zm4-10h12v2H8V6zm0 5h12v2H8v-2zm0 5h12v2H8v-2z'),
    HistoryOutlined: base('M13 3a9 9 0 00-9 9H1l3.89 3.89.07.14L9 12H6a7 7 0 117 7 7.1 7.1 0 01-5.08-2.18l-1.42 1.42A9 9 0 1013 3zm-1 5v5l4.28 2.54.72-1.21-3.5-2.08V8H12z'),
    BarChartOutlined: base('M5 9.2h3V19H5V9.2zM10.6 5h2.8v14h-2.8V5zm5.6 8H19v6h-2.8v-6z'),
    ProfileOutlined: base('M14 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z'),
    ReloadOutlined: base('M12 4V1L8 5l4 4V6c3.31 0 6 2.69 6 6 0 1.01-.25 1.97-.7 2.8l1.46 1.46C19.54 15.03 20 13.57 20 12c0-4.42-3.58-8-8-8zm0 14c-3.31 0-6-2.69-6-6 0-1.01.25-1.97.7-2.8L5.24 7.74C4.46 8.97 4 10.43 4 12c0 4.42 3.58 8 8 8v3l4-4-4-4v3z'),
    RightOutlined: base('M10 6l-1.41 1.41L13.17 12l-4.58 4.59L10 18l6-6z'),
    LeftOutlined: base('M14 6l1.41 1.41L10.83 12l4.58 4.59L14 18l-6-6z'),
    DownOutlined: base('M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6z'),
    UpOutlined: base('M7.41 15.41L12 10.83l4.59 4.58L18 14l-6-6-6 6z'),
    NodeIndexOutlined: base('M6 10c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm12 0c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm-6 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0-12c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z'),
    AimOutlined: base('M12 8c-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4-1.79-4-4-4zm8.94 3A8.994 8.994 0 0013 3.06V1h-2v2.06A8.994 8.994 0 003.06 11H1v2h2.06A8.994 8.994 0 0011 20.94V23h2v-2.06A8.994 8.994 0 0020.94 13H23v-2h-2.06zM12 19c-3.87 0-7-3.13-7-7s3.13-7 7-7 7 3.13 7 7-3.13 7-7 7z'),
    ZoomInOutlined: base('M15.5 14h-.79l-.28-.27A6.47 6.47 0 0016 9.5 6.5 6.5 0 109.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14zM12 10h-2v2H9v-2H7V9h2V7h1v2h2v1z'),
    ZoomOutOutlined: base('M15.5 14h-.79l-.28-.27A6.47 6.47 0 0016 9.5 6.5 6.5 0 109.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14zM7 9h5v1H7V9z'),
    CompressOutlined: base('M5 16h3v3h2v-5H5v2zm3-8H5v2h5V5H8v3zm6 11h2v-3h3v-2h-5v5zm2-11V5h-2v5h5V8h-3z'),
    CodeOutlined: base('M9.4 16.6L4.8 12l4.6-4.6L8 6l-6 6 6 6 1.4-1.4zm5.2 0l4.6-4.6-4.6-4.6L16 6l6 6-6 6-1.4-1.4z'),
    MinusCircleOutlined: base('M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11H7v-2h10v2z'),
    SearchOutlined: base('M15.5 14h-.79l-.28-.27A6.47 6.47 0 0016 9.5 6.5 6.5 0 109.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z'),
    CloseOutlined: base('M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z'),
    SettingOutlined: base('M19.14 12.94c.04-.31.06-.63.06-.94s-.02-.63-.06-.94l2.03-1.58a.49.49 0 00.12-.61l-1.92-3.32a.49.49 0 00-.59-.22l-2.39.96a7.05 7.05 0 00-1.62-.94l-.36-2.54A.49.49 0 0014 2h-4a.49.49 0 00-.5.42l-.36 2.54c-.59.24-1.13.55-1.62.94l-2.39-.96a.49.49 0 00-.59.22L2.75 8.87a.48.48 0 00.12.61l2.03 1.58c-.04.31-.06.63-.06.94s.02.63.06.94l-2.03 1.58a.49.49 0 00-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.39 1.04.71 1.62.94l.36 2.54c.05.24.24.41.48.41h4c.24 0 .44-.17.5-.41l.36-2.54c.59-.23 1.13-.55 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32a.49.49 0 00-.12-.61l-2.03-1.58zM12 15.6A3.6 3.6 0 1112 8.4a3.6 3.6 0 010 7.2z'),
    CopyOutlined: base('M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z'),
    FundOutlined: base('M3.5 18.49l6-6.01 4 4L22 6.92l-1.41-1.41-7.09 7.97-4-4L2 16.99z'),
};

// Named exports for static imports
export const MenuUnfoldOutlined = named.MenuUnfoldOutlined;
export const MenuFoldOutlined = named.MenuFoldOutlined;
export const LogoutOutlined = named.LogoutOutlined;
export const PlusOutlined = named.PlusOutlined;
export const EditOutlined = named.EditOutlined;
export const UserOutlined = named.UserOutlined;
export const LockOutlined = named.LockOutlined;
export const ArrowRightOutlined = named.ArrowRightOutlined;
export const ArrowLeftOutlined = named.ArrowLeftOutlined;
export const CheckOutlined = named.CheckOutlined;
export const DeleteOutlined = named.DeleteOutlined;
export const EllipsisOutlined = named.EllipsisOutlined;
export const DownloadOutlined = named.DownloadOutlined;
export const SyncOutlined = named.SyncOutlined;
export const FullscreenExitOutlined = named.FullscreenExitOutlined;
export const LoginOutlined = named.LoginOutlined;
export const IdcardOutlined = named.IdcardOutlined;
export const TableOutlined = named.TableOutlined;
export const DatabaseOutlined = named.DatabaseOutlined;
export const WarningOutlined = named.WarningOutlined;
export const UsergroupAddOutlined = named.UsergroupAddOutlined;
export const CloseCircleOutlined = named.CloseCircleOutlined;
export const TagOutlined = named.TagOutlined;
export const FormOutlined = named.FormOutlined;
export const SafetyCertificateOutlined = named.SafetyCertificateOutlined;
export const DashboardOutlined = named.DashboardOutlined;
export const ScheduleOutlined = named.ScheduleOutlined;
export const AuditOutlined = named.AuditOutlined;
export const UnorderedListOutlined = named.UnorderedListOutlined;
export const HistoryOutlined = named.HistoryOutlined;
export const BarChartOutlined = named.BarChartOutlined;
export const ProfileOutlined = named.ProfileOutlined;
export const ReloadOutlined = named.ReloadOutlined;
export const RightOutlined = named.RightOutlined;
export const LeftOutlined = named.LeftOutlined;
export const DownOutlined = named.DownOutlined;
export const UpOutlined = named.UpOutlined;
export const NodeIndexOutlined = named.NodeIndexOutlined;
export const AimOutlined = named.AimOutlined;
export const ZoomInOutlined = named.ZoomInOutlined;
export const ZoomOutOutlined = named.ZoomOutOutlined;
export const CompressOutlined = named.CompressOutlined;
export const CodeOutlined = named.CodeOutlined;
export const MinusCircleOutlined = named.MinusCircleOutlined;
export const SearchOutlined = named.SearchOutlined;
export const CloseOutlined = named.CloseOutlined;
export const SettingOutlined = named.SettingOutlined;
export const CopyOutlined = named.CopyOutlined;
export const FundOutlined = named.FundOutlined;

export default Fallback;
