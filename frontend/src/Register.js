import React, { useState } from 'react';
import axios from 'axios';

const Register = ({ onBack }) => {
    const [formData, setFormData] = useState({
        username: '',
        password: '',
        rePassword: '',
        nickname: '',
        gender: '',
        email: '',
        phone: ''
    });

    const handleRegister = () => {
        let frontendErrors = [];

        if (formData.password !== formData.rePassword) {
            frontendErrors.push("两次输入的密码不一致");
        }

        axios.post('http://localhost:8080/api/register', formData)
            .then(res => {
                if (res.data.code === 200 && frontendErrors.length === 0) {
                    alert("注册成功！已为您自动登录");
                    localStorage.setItem('isLoggedIn', 'true');
                    localStorage.setItem('currentUser', formData.username);
                    window.location.href = "/";
                }
            })
            .catch(() => alert("服务端连接失败，请检查后端程序"));
    };

    return (
        <div className="reg-container">
            <div className="reg-form">
                <div className="header-bar">用户注册</div>

                <div className="form-body">
                    <div className="form-row">
                        <label>账号：</label>
                        <input type="text" placeholder="请输入账号" onChange={e => setFormData({...formData, username: e.target.value})} />
                    </div>
                    <div className="form-row">
                        <label>密码：</label>
                        <input type="password" placeholder="请输入密码" onChange={e => setFormData({...formData, password: e.target.value})} />
                    </div>
                    <div className="form-row">
                        <label>重复密码：</label>
                        <input type="password" placeholder="请再次输入密码" onChange={e => setFormData({...formData, rePassword: e.target.value})} />
                    </div>
                    <div className="form-row">
                        <label>昵称：</label>
                        <input type="text" placeholder="请输入昵称" onChange={e => setFormData({...formData, nickname: e.target.value})} />
                    </div>
                    <div className="form-row">
                        <label>性别：</label>
                        <div className="gender-group">
                            <label style={{width:'auto', fontWeight:'normal'}}>
                                <input type="radio" name="gender" value="男" onChange={e => setFormData({...formData, gender: e.target.value})} /> 男
                            </label>
                            <label style={{width:'auto', fontWeight:'normal'}}>
                                <input type="radio" name="gender" value="女" onChange={e => setFormData({...formData, gender: e.target.value})} /> 女
                            </label>
                        </div>
                    </div>
                    <div className="form-row">
                        <label>邮箱：</label>
                        <input type="text" placeholder="请输入邮箱" onChange={e => setFormData({...formData, email: e.target.value})} />
                    </div>
                    <div className="form-row">
                        <label>电话：</label>
                        <input type="text" placeholder="请输入电话" onChange={e => setFormData({...formData, phone: e.target.value})} />
                    </div>

                    <div style={{ textAlign: 'center', marginTop: '30px' }}>
                        <button className="btn-blue" onClick={onBack} style={{backgroundColor:'#666', borderColor:'#444'}}>返回</button>
                        <button className="btn-blue" style={{marginLeft: '20px'}} onClick={handleRegister}>注册</button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Register;