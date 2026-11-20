declare module '@ant-design/icons' {
    import * as React from 'react';
    type IconComponent = React.FC<any>;
    export const MenuUnfoldOutlined: IconComponent;
    export const MenuFoldOutlined: IconComponent;
    export const LogoutOutlined: IconComponent;
    export const PlusOutlined: IconComponent;
    export const EditOutlined: IconComponent;
    export const UserOutlined: IconComponent;
    export const LockOutlined: IconComponent;
    export const ArrowRightOutlined: IconComponent;
    export const ArrowLeftOutlined: IconComponent;
    export const CheckOutlined: IconComponent;
    export const DeleteOutlined: IconComponent;
    export const EllipsisOutlined: IconComponent;
    export const DownloadOutlined: IconComponent;
    export const SyncOutlined: IconComponent;
    export const FullscreenExitOutlined: IconComponent;
    export const LoginOutlined: IconComponent;
    export const IdcardOutlined: IconComponent;
    export const TableOutlined: IconComponent;
    export const DatabaseOutlined: IconComponent;
    export const WarningOutlined: IconComponent;
    export const UsergroupAddOutlined: IconComponent;
    export const CloseCircleOutlined: IconComponent;
    export const TagOutlined: IconComponent;
    export const FormOutlined: IconComponent;
    export const SafetyCertificateOutlined: IconComponent;
    export const DashboardOutlined: IconComponent;
    export const ScheduleOutlined: IconComponent;
    export const AuditOutlined: IconComponent;
    export const UnorderedListOutlined: IconComponent;
    export const HistoryOutlined: IconComponent;
    export const BarChartOutlined: IconComponent;
    export const ProfileOutlined: IconComponent;
    export const ReloadOutlined: IconComponent;
    export const RightOutlined: IconComponent;
    export const LeftOutlined: IconComponent;
    export const DownOutlined: IconComponent;
    export const UpOutlined: IconComponent;
    export const NodeIndexOutlined: IconComponent;
    export const AimOutlined: IconComponent;
    export const ZoomInOutlined: IconComponent;
    export const ZoomOutOutlined: IconComponent;
    export const CompressOutlined: IconComponent;
    export const CodeOutlined: IconComponent;
    export const MinusCircleOutlined: IconComponent;
    export const SearchOutlined: IconComponent;
    export const CloseOutlined: IconComponent;
    export const SettingOutlined: IconComponent;
    const Icons: { [name: string]: IconComponent };
    export default Icons;
}

declare module 'antd' {
    export * from '../src/ui/antdCompat/index';
}

declare module 'antd/*' {
    const mod: any;
    export = mod;
}
