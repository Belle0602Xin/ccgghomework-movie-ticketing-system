import React from 'react';

const OrderList = ({ orders }) => {
    return (
        <div style={{ padding: '20px' }}>
            <div className="header-bar">3. 我的订单列表</div>
            <table className="movie-table">
                <thead>
                <tr>
                    <th>#</th><th>订单编号</th><th>电影名称</th><th>放映时间</th><th>购票时间</th><th>购票数量</th><th>金额</th>
                </tr>
                </thead>
                <tbody>
                {orders.map((o, index) => (
                    <tr key={o.orderId}>
                        <td>{index + 1}</td>
                        <td>{o.orderId}</td>
                        <td>{o.movieName}</td>
                        <td>{o.movieTime}</td>
                        <td>{o.orderTime}</td>
                        <td>{o.ticketsCount}</td>
                        <td>{o.totalAmount}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default OrderList;