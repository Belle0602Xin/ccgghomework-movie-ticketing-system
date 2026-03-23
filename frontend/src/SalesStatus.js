import React from 'react';

const SalesStatus = ({ orders }) => {

    return (
        <div className="sales-container" style={{ padding: '20px' }}>
            <div className="header-bar">5. 销售统计结果</div>
            <table className="order-table" style={{ width: '100%', marginTop: '20px' }}>
                <thead>
                <tr style={{ backgroundColor: '#e3f2fd' }}>
                    <th>#</th>
                    <th>场次 ID</th>
                    <th>电影名称</th>
                    <th>订单时间</th>
                    <th>销售数量</th>
                    <th>销售金额</th>
                </tr>
                </thead>
                <tbody>
                {orders && orders.length > 0 ? orders.map((s, index) => (
                    <tr key={index} style={{ textAlign: 'center', borderBottom: '1px solid #ddd' }}>
                        <td>{index + 1}</td>
                        <td>{s.sessionId || s.schedule_id}</td>
                        <td>{s.movieName || "电影场次: " + (s.sessionId || s.schedule_id)}</td>
                        <td>{s.orderTime || "---"}</td>
                        <td>{s.ticketsCount || s.quality}</td>
                        <td style={{ fontWeight: 'bold', color: '#2c3e50' }}>
                            ${s.totalAmount || s.price}
                        </td>
                    </tr>
                )) : (
                    <tr>
                        <td colSpan="6" style={{ padding: '20px', color: '#999' }}>暂无统计数据</td>
                    </tr>
                )}
                </tbody>
            </table>
        </div>
    );
};

export default SalesStatus;