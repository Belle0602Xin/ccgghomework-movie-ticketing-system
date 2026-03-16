import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';
import MovieTable from './MovieTable';
import OrderList from './OrderList';
import SalesStatus from './SalesStatus';
import Login from './Login';
import Register from './Register';

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [isRegistering, setIsRegistering] = useState(false);
    const [activeTab, setActiveTab] = useState('场次列表');
    const [orders, setOrders] = useState([]);

    const fetchOrders = () => {
        axios.get('http://localhost:8080/api/orders').then(res => setOrders(res.data));
    };

    const handleSave = () => {
        axios.post('http://localhost:8080/api/save')
            .then(res => {
                alert(res.data.message || "备份成功");
            })
            .catch(err => alert("备份请求失败"));
    };

    const handleLoad = () => {
        axios.post('http://localhost:8080/api/load')
            .then(res => {
                alert(res.data.message || "加载成功");
                fetchOrders();
            })
            .catch(err => alert("加载请求失败"));
    };

    if (isRegistering) {
        return <Register onBack={() => setIsRegistering(false)} />;
    }

    if (!isLoggedIn) {
        return <Login
            onLoginSuccess={(name) => { setIsLoggedIn(true); fetchOrders(); }}
            onGoRegister={() => setIsRegistering(true)}
        />;
    }

    return (
        <div className="App">
            <nav className="nav-menu">
                <button className={activeTab === '我的订单' ? 'active' : ''} onClick={() => {fetchOrders(); setActiveTab('我的订单');}}>我的订单</button>
                <button className={activeTab === '场次列表' ? 'active' : ''} onClick={() => setActiveTab('场次列表')}>场次列表</button>
                <button className={activeTab === '销售统计' ? 'active' : ''} onClick={() => {fetchOrders(); setActiveTab('销售统计');}}>销售统计</button>
                <button onClick={handleSave}>备份订单</button>
                <button onClick={handleLoad}>加载订单</button>
                <button onClick={() => window.location.reload()}>退出</button>
            </nav>

            <div className="content-area">
                {activeTab === '场次列表' && <MovieTable onBookSuccess={fetchOrders} />}
                {activeTab === '我的订单' && <OrderList orders={orders} />}
                {activeTab === '销售统计' && <SalesStatus orders={orders} />}
            </div>
        </div>
    );
}

export default App;