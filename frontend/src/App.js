import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';
import MovieTable from './MovieTable';
import OrderList from './OrderList';
import SalesStatus from './SalesStatus';
import Login from './Login';
import Register from './Register';

axios.defaults.withCredentials = true;

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(localStorage.getItem('isLoggedIn') === 'true');
    const [isRegistering, setIsRegistering] = useState(false);
    const [activeTab, setActiveTab] = useState('场次列表');
    const [orders, setOrders] = useState([]);
    const [isOrdersLoading, setIsOrdersLoading] = useState(false);

    const fetchOrders = () => {
        const user = localStorage.getItem('currentUser');
        if (!user) return;

        setIsOrdersLoading(true);

        axios.get(`http://localhost:8080/api/orders?user=${user}`)
            .then(res => {
                if (res.data.code === 200) {
                    setOrders(res.data.data || []);
                }
            })
            .catch(err => console.error("获取订单失败", err))
            .finally(() => {
                setTimeout(() => {
                    setIsOrdersLoading(false);
                }, 500);
            });
    };

    useEffect(() => {
        if (isLoggedIn) {
            fetchOrders();
        }
    }, [isLoggedIn]);

    if (isRegistering) {
        return <Register onBack={() => setIsRegistering(false)} />;
    }

    if (!isLoggedIn) {
        return (
            <Login
                onLoginSuccess={() => {
                    localStorage.setItem('isLoggedIn', 'true');
                    setIsLoggedIn(true);
                }}
                onGoRegister={() => setIsRegistering(true)}
            />
        );
    }

    return (
        <div className="App">
            <nav className="nav-menu">
                <button className={activeTab === '场次列表' ? 'active' : ''}
                        onClick={() => setActiveTab('场次列表')}>场次列表
                </button>
                <button className={activeTab === '我的订单' ? 'active' : ''} onClick={() => {
                    fetchOrders();
                    setActiveTab('我的订单');
                }}>我的订单
                </button>
                <button className={activeTab === '销售统计' ? 'active' : ''} onClick={() => {
                    fetchOrders();
                    setActiveTab('销售统计');
                }}>销售统计
                </button>

                <button
                    onClick={() => axios.post('http://localhost:8080/api/save').then(res => alert(res.data.message))}>备份订单
                </button>
                <button onClick={() => axios.post('http://localhost:8080/api/load').then(res => {
                    alert(res.data.message);
                    fetchOrders();
                })}>加载订单
                </button>

                <button onClick={() => {
                    localStorage.removeItem('isLoggedIn');
                    window.location.reload();
                }}>退出
                </button>
            </nav>

            <div className="content-area" style={{marginTop: '20px'}}>
                {activeTab === '场次列表' && <MovieTable onBookSuccess={fetchOrders}/>}
                {activeTab === '我的订单' && <OrderList orders={orders} isLoading={isOrdersLoading} />}
                {activeTab === '销售统计' && <SalesStatus orders={orders} isLoading={isOrdersLoading} />}
            </div>
        </div>
    );
}

export default App;