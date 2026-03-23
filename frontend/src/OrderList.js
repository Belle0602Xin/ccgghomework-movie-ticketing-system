import React, { useState, useEffect } from 'react';

const OrderList = ({ orders, isLoading }) => {
    return (
        <div className="order-container" style={{ padding: '20px' }}>
            <div className="header-bar">3. 我的订单列表</div>
            <table className="order-table" style={{ width: '100%', marginTop: '20px' }}>
                <thead>
                <tr>
                    <th>#</th><th>订单编号</th><th>电影名称</th><th>放映时间</th><th>购票数量</th><th>金额</th>
                </tr>
                </thead>
                <tbody>
                {isLoading ? (
                    <tr>
                        <td colSpan="6" style={{ textAlign: 'center', padding: '50px', color: '#666' }}>
                            <div className="loading-spinner">数据加载中，请稍候...</div>
                        </td>
                    </tr>
                ) : (
                    orders && orders.length > 0 ? orders.map((o, index) => (
                        <tr key={index} style={{ textAlign: 'center', borderBottom: '1px solid #ddd' }}>
                            <td>{index + 1}</td>
                            <td>{o.id}</td>
                            <td>{o.movieName || "场次: " + o.sessionId}</td>
                            <td>{o.orderTime}</td>
                            <td>{o.ticketsCount}</td>
                            <td style={{ color: 'red', fontWeight: 'bold' }}>${o.totalAmount}</td>
                        </tr>
                    )) : (
                        <tr><td colSpan="6" style={{ textAlign: 'center', padding: '20px' }}>暂无订单记录</td></tr>
                    )
                )}
                </tbody>
            </table>
        </div>
    );
};

export default OrderList;

// import React from 'react';
//
// const OrderList = () => {
//     const [orders, setOrders] = useState([]);
//
//     const loadOrders = () => {
//         const user = localStorage.getItem('currentUser');
//
//         axios.get(`http://localhost:8080/api/orders?user=${user}`, { withCredentials: true })
//             .then(res => {
//                 if (res.data.code === 200) {
//                     setOrders(res.data.data || []);
//                 }
//             })
//             .catch(err => console.error("订单加载失败", err));
//     };
//
//     useEffect(() => {
//         loadOrders();
//     }, []);
//
//     return (
//         <div style={{ padding: '20px' }}>
//             <div className="header-bar">3. 我的订单列表</div>
//             <table className="movie-table">
//                 <thead>
//                 <tr>
//                     <th>#</th><th>订单编号</th><th>电影名称</th><th>放映时间</th><th>购票时间</th><th>购票数量</th><th>金额</th>
//                 </tr>
//                 </thead>
//                 <tbody>
//                 {orders.map((o, index) => (
//                     <tr key={o.orderId}>
//                         <td>{index + 1}</td>
//                         <td>{o.orderId}</td>
//                         <td>{o.movieName}</td>
//                         <td>{o.movieTime}</td>
//                         <td>{o.orderTime}</td>
//                         <td>{o.ticketsCount}</td>
//                         <td>{o.totalAmount}</td>
//                     </tr>
//                 ))}
//                 </tbody>
//             </table>
//         </div>
//     );
// };
//
// export default OrderList;