import React, { useEffect, useState } from 'react';
import { Card, Form, Input, Button, Upload, message } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import { getSystemConfig, updateSystemConfig, type SysConfig } from '../../api/systemConfig';
import type { UploadFile, RcFile } from 'antd/es/upload/interface';

const SystemConfigPage: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [logoFileList, setLogoFileList] = useState<UploadFile[]>([]);
  const [iconFileList, setIconFileList] = useState<UploadFile[]>([]);

  const getBase64 = (file: RcFile): Promise<string> =>
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = (error) => reject(error);
    });

  const loadData = async () => {
    try {
      const res = await getSystemConfig();
      if (res) {
        form.setFieldsValue({
          systemName: res.systemName,
        });
        if (res.logo) {
            setLogoFileList([{
                uid: '-1',
                name: 'logo.png',
                status: 'done',
                url: res.logo,
            }]);
        }
        if (res.icon) {
            setIconFileList([{
                uid: '-1',
                name: 'icon.png',
                status: 'done',
                url: res.icon,
            }]);
        }
      }
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const onFinish = async (values: any) => {
    setLoading(true);
    try {
      let logoBase64 = logoFileList.length > 0 ? logoFileList[0].url : '';
      if (logoFileList.length > 0 && !logoFileList[0].url && logoFileList[0].originFileObj) {
        logoBase64 = await getBase64(logoFileList[0].originFileObj as RcFile);
      } else if (logoFileList.length === 0) {
          logoBase64 = '';
      }

      let iconBase64 = iconFileList.length > 0 ? iconFileList[0].url : '';
      if (iconFileList.length > 0 && !iconFileList[0].url && iconFileList[0].originFileObj) {
        iconBase64 = await getBase64(iconFileList[0].originFileObj as RcFile);
      } else if (iconFileList.length === 0) {
          iconBase64 = '';
      }

      const config: SysConfig = {
        systemName: values.systemName,
        logo: logoBase64,
        icon: iconBase64,
      };

      await updateSystemConfig(config);
      message.success('保存成功，刷新页面生效');
      
      // Update local storage to reflect changes immediately if needed, 
      // or rely on next fetch. But user asked for "default request this interface when entering page"
      // So changes will be visible on reload.
      
    } catch (error) {
      console.error(error);
      message.error('保存失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card title="系统配置" bordered={false} style={{ borderRadius: 8 }}>
      <Form
        form={form}
        layout="vertical"
        onFinish={onFinish}
        style={{ maxWidth: 600 }}
      >
        <Form.Item
          name="systemName"
          label="系统名称"
          rules={[{ required: true, message: '请输入系统名称' }]}
        >
          <Input placeholder="请输入系统名称" />
        </Form.Item>

        <Form.Item label="系统Logo">
            <Upload
                listType="picture-card"
                fileList={logoFileList}
                onChange={({ fileList }) => setLogoFileList(fileList)}
                beforeUpload={() => false} // Prevent auto upload
                maxCount={1}
            >
                {logoFileList.length < 1 && (
                    <div>
                        <UploadOutlined />
                        <div style={{ marginTop: 8 }}>上传</div>
                    </div>
                )}
            </Upload>
        </Form.Item>

        <Form.Item label="系统图标 (Icon)">
            <Upload
                listType="picture-card"
                fileList={iconFileList}
                onChange={({ fileList }) => setIconFileList(fileList)}
                beforeUpload={() => false} // Prevent auto upload
                maxCount={1}
            >
                {iconFileList.length < 1 && (
                    <div>
                        <UploadOutlined />
                        <div style={{ marginTop: 8 }}>上传</div>
                    </div>
                )}
            </Upload>
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading}>
            保存配置
          </Button>
        </Form.Item>
      </Form>
    </Card>
  );
};

export default SystemConfigPage;
