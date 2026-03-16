import React from 'react';

const SalesStatus = ({ orders }) => {
    const statusMap = orders.reduce((acc, o) => {
        if (!acc[o.sessionId]) {
            acc[o.sessionId] = { id: o.sessionId, name: o.movieName, time: o.movieTime, count: 0, money: 0 };
        }
        acc[o.sessionId].count += o.ticketsCount;
        acc[o.sessionId].money += o.totalAmount;

        return acc;
    }, {});

    const stats = Object.values(statusMap);

    return (
        <div style={{ padding: '20px' }}>
            <div className="header-bar">5. 销售统计结果</div>
            <table className="movie-table">
                <thead>
                <tr>
                    <th>#</th><th>场次</th><th>电影名称</th><th>放映时间</th><th>销售数量</th><th>销售金额</th>
                </tr>
                </thead>
                <tbody>
                {stats.map((s, index) => (
                    <tr key={s.id}>
                        <td>{index + 1}</td>
                        <td>{s.id}</td>
                        <td>{s.name}</td>
                        <td>{s.time}</td>
                        <td>{s.count}</td>
                        <td>{Math.round(s.money)}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default SalesStatus;