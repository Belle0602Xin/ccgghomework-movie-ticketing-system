import React, { useState } from 'react';
import axios from 'axios';

const Login = ({ onLoginSuccess, onGoRegister }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleLogin = () => {
        const params = new URLSearchParams();
        params.append('username', username);
        params.append('password', password);

        axios.post('http://localhost:8080/api/login', params, { withCredentials: true })
            .then(res => {
                if (res.data.code === 200) {
                    alert("登录成功！");
                    localStorage.setItem('isLoggedIn', 'true');
                    localStorage.setItem('currentUser', username);
                    window.location.reload();
                }
            })
            .catch(err => alert("服务器连接失败"));
    };

    return (
        <div className="reg-container">
            <div className="reg-form">
                <div className="header-bar">用户登录</div>
                <div className="form-body">
                    <p>账号：<input type="text" onChange={(e) => setUsername(e.target.value)} /></p>
                    <p>密码：<input type="password" onChange={(e) => setPassword(e.target.value)} /></p>
                    <div style={{marginTop: '20px', textAlign: 'center'}}>
                        <button className="btn-blue" onClick={handleLogin}>登录</button>
                        <button className="btn-blue" onClick={onGoRegister} style={{ marginLeft: '10px' }}>注册</button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;