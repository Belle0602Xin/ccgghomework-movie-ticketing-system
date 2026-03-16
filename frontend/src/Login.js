import React, { useState } from 'react';
import axios from 'axios';

const Login = ({ onLoginSuccess, onGoRegister }) => {
    const [user, setUser] = useState({ username: '', password: '' });

    const handleLogin = () => {
        const params = new URLSearchParams();
        params.append('username', user.username);
        params.append('password', user.password);

        axios.post('http://localhost:8080/api/login', params)
            .then(res => {

                if (res.data === "OK" || (res.data && res.data.code === 200)) {
                    onLoginSuccess(user.username);
                } else {
                    alert("账号或密码错误");
                }
            })
            .catch(() => alert("服务端连接失败"));
    };

    return (
        <div className="reg-container">
            <div className="reg-form">
                <div className="header-bar">用户登录</div>

                <div className="form-body">
                    <p>
                        账号：
                        <input type="text" onChange={e => setUser({...user, username: e.target.value})} />
                    </p>
                    <p>
                        密码：
                        <input type="password" onChange={e => setUser({...user, password: e.target.value})} />
                    </p>
                    <div style={{ marginTop: '20px', textAlign: 'center' }}>
                        <button className="btn-blue" onClick={handleLogin}>登录</button>
                        <button className="btn-blue" onClick={onGoRegister} style={{ marginLeft: '10px', backgroundColor: '#5bc0de', borderColor: '#46b8da' }}>注册</button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;