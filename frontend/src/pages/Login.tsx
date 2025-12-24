import React from 'react';
import { Form, Input, Button, message, Checkbox, Row, Col, Typography, Space, theme } from 'antd';
import { UserOutlined, LockOutlined, RocketOutlined, SafetyCertificateOutlined, BarChartOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { login } from '../api/auth';
import logo from '../assets/logo.svg';

const { Title, Text, Paragraph } = Typography;
const { useToken } = theme;

const Login: React.FC = () => {
  const navigate = useNavigate();
  const { token } = useToken();

  const onFinish = async (values: any) => {
    try {
      const data = await login(values);
      localStorage.setItem('uni-message-token', data.token);
      localStorage.setItem('user', JSON.stringify(data.user));
      message.success('登录成功');
      navigate('/');
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <Row style={{ height: '100vh', width: '100%', overflow: 'hidden' }}>
      {/* Left Side - Brand & Info */}
      <Col 
        xs={0} md={12} lg={14} xl={16}
        style={{
          background: `linear-gradient(135deg, ${token.colorPrimary} 0%, #001529 100%)`,
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          padding: '8%',
          position: 'relative',
          color: '#fff'
        }}
      >
        {/* Decorative Elements */}
        <div style={{ 
          position: 'absolute', 
          top: '-10%', 
          left: '-10%', 
          width: '50%', 
          height: '50%', 
          borderRadius: '50%', 
          background: 'radial-gradient(circle, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0) 70%)',
          pointerEvents: 'none'
        }} />
        <div style={{ 
          position: 'absolute', 
          bottom: '-10%', 
          right: '-10%', 
          width: '60%', 
          height: '60%', 
          borderRadius: '50%', 
          background: 'radial-gradient(circle, rgba(255,255,255,0.08) 0%, rgba(255,255,255,0) 70%)',
          pointerEvents: 'none'
        }} />
        
        <div style={{ zIndex: 1 }}>
          <div style={{ display: 'flex', alignItems: 'center', marginBottom: 32 }}>
             <div style={{
                width: 48,
                height: 48,
                background: 'rgba(255,255,255,0.2)',
                borderRadius: '12px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                marginRight: 16,
                backdropFilter: 'blur(10px)'
             }}>
                <img src={logo} alt="logo" style={{ width: 32, height: 32 }} />
             </div>
             <Title level={2} style={{ color: '#fff', margin: 0, fontWeight: 600 }}>UniMessage</Title>
          </div>
          
          <Title level={1} style={{ color: '#fff', fontSize: 'clamp(32px, 4vw, 48px)', marginBottom: 24, lineHeight: 1.2 }}>
            企业级统一<br/>消息推送平台
          </Title>
          
          <Paragraph style={{ color: 'rgba(255,255,255,0.85)', fontSize: 18, marginBottom: 64, maxWidth: 600 }}>
            提供高效、稳定、安全的一站式消息发送解决方案，支持多渠道聚合、全链路追踪与数据分析。
          </Paragraph>

          <Space direction="vertical" size="large" style={{ marginLeft: 8 }}>
            <Space align="center" size="middle">
              <div style={{ width: 40, height: 40, borderRadius: '50%', background: 'rgba(255,255,255,0.1)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <RocketOutlined style={{ fontSize: 20, color: '#69b1ff' }} />
              </div>
              <Text style={{ color: '#fff', fontSize: 16 }}>多渠道聚合发送，触达更高效</Text>
            </Space>
            <Space align="center" size="middle">
              <div style={{ width: 40, height: 40, borderRadius: '50%', background: 'rgba(255,255,255,0.1)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <SafetyCertificateOutlined style={{ fontSize: 20, color: '#69b1ff' }} />
              </div>
              <Text style={{ color: '#fff', fontSize: 16 }}>企业级安全保障，数据更放心</Text>
            </Space>
            <Space align="center" size="middle">
              <div style={{ width: 40, height: 40, borderRadius: '50%', background: 'rgba(255,255,255,0.1)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <BarChartOutlined style={{ fontSize: 20, color: '#69b1ff' }} />
              </div>
              <Text style={{ color: '#fff', fontSize: 16 }}>全维度数据监控，效果更清晰</Text>
            </Space>
          </Space>
        </div>
      </Col>

      {/* Right Side - Login Form */}
      <Col 
        xs={24} md={12} lg={10} xl={8} 
        style={{ 
          background: '#fff', 
          display: 'flex', 
          alignItems: 'center', 
          justifyContent: 'center',
          padding: '40px',
          boxShadow: '-4px 0 16px rgba(0,0,0,0.05)',
          zIndex: 2
        }}
      >
        <div style={{ width: '100%', maxWidth: 380 }}>
          <div style={{ marginBottom: 48, textAlign: 'left' }}>
            <Title level={2} style={{ marginBottom: 12, color: '#1f1f1f' }}>欢迎登录</Title>
            <Text type="secondary" style={{ fontSize: 16 }}>请输入您的账号和密码开始使用</Text>
          </div>

          <Form
            name="login"
            initialValues={{ remember: true }}
            onFinish={onFinish}
            size="large"
            layout="vertical"
          >
            <Form.Item
              name="username"
              rules={[{ required: true, message: '请输入用户名!' }]}
              style={{ marginBottom: 24 }}
            >
              <Input 
                prefix={<UserOutlined style={{ color: token.colorTextQuaternary, fontSize: 18 }} />} 
                placeholder="用户名" 
                style={{ 
                  borderRadius: '8px', 
                  padding: '12px 16px',
                  backgroundColor: '#f8f9fa',
                  border: '1px solid #e9ecef'
                }}
                variant="borderless"
              />
            </Form.Item>

            <Form.Item
              name="password"
              rules={[{ required: true, message: '请输入密码!' }]}
              style={{ marginBottom: 24 }}
            >
              <Input.Password 
                prefix={<LockOutlined style={{ color: token.colorTextQuaternary, fontSize: 18 }} />} 
                placeholder="密码"
                style={{ 
                  borderRadius: '8px', 
                  padding: '12px 16px',
                  backgroundColor: '#f8f9fa',
                  border: '1px solid #e9ecef'
                }}
                variant="borderless"
              />
            </Form.Item>

            <Form.Item style={{ marginBottom: 24 }}>
               <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                 <Form.Item name="remember" valuePropName="checked" noStyle>
                   <Checkbox>记住我</Checkbox>
                 </Form.Item>
                 <a style={{ color: token.colorPrimary, fontWeight: 500 }}>忘记密码？</a>
               </div>
            </Form.Item>

            <Form.Item style={{ marginBottom: 24 }}>
              <Button 
                type="primary" 
                htmlType="submit" 
                block
                style={{ 
                  height: '52px', 
                  borderRadius: '8px',
                  fontSize: '18px',
                  fontWeight: 600,
                  background: `linear-gradient(90deg, ${token.colorPrimary} 0%, ${token.colorPrimaryActive} 100%)`,
                  border: 'none',
                  boxShadow: '0 8px 20px rgba(22, 119, 255, 0.2)'
                }}
              >
                登 录
              </Button>
            </Form.Item>
            
            <div style={{ textAlign: 'center', marginTop: 32 }}>
               <Text type="secondary">还没有账号？ </Text>
               <a style={{ color: token.colorPrimary, fontWeight: 600 }}>立即注册</a>
            </div>
            
            <div style={{ marginTop: 64, textAlign: 'center' }}>
              <Text type="secondary" style={{ fontSize: 12, color: '#bfbfbf' }}>
                UniMessage ©2025 Created by <a href="https://github.com/Estelle925" target="_blank" rel="noopener noreferrer" style={{ color: 'inherit', textDecoration: 'underline', cursor: 'pointer' }}>Estelle925</a>
              </Text>
            </div>
          </Form>
        </div>
      </Col>
    </Row>
  );
};

export default Login;
