import React, { useState, useEffect } from 'react';
import axios from 'axios';

const MovieTable = ({ onBookSuccess }) => {
    const [movies, setMovies] = useState([]);
    const [booking, setBooking] = useState({ sid: '', uid: '', count: '' });

    const loadData = () => {
        axios.get('http://localhost:8080/api/movies').then(res => setMovies(res.data));
    };

    useEffect(() => { loadData(); }, []);

    const handleBook = () => {
        if (!booking.sid || !booking.count) {
            alert("请完整填写场次和票数");
            return;
        }

        const params = new URLSearchParams();
        params.append('sid', booking.sid);
        params.append('uid', booking.uid || "Guest");
        params.append('count', booking.count);

        axios.post('http://localhost:8080/api/book', params)
            .then(res => {
                if (res.data.code === 200) {
                    alert("购票成功！");
                    loadData();
                } else {
                    alert(res.data.message);
                }
            })
            .catch(err => {
                alert("系统异常，请稍后再试");
            });
    };

    return (
        <div style={{ padding: '20px' }}>
            <div className="header-bar">--- 可售电影场次安排如下 ---</div>
            {movies.map(m => (
                <div key={m.movieId} style={{ marginBottom: '15px' }}>
                    <p>场次:{m.movieId}</p>
                    <p>电影：{m.movieName}</p>
                    <p>放映时间：{m.movieTime}</p>
                    <p>余票数量:{m.ticketsAvailable}</p>
                </div>
            ))}

            <div className="header-bar" style={{ marginTop: '30px' }}>购票登记界面</div>
            <div style={{ backgroundColor: '#333', color: '#fff', padding: '15px', borderRadius: '5px' }}>
                <p>请输入订票信息(0 返回):</p>
                <label>请输入放映场次:</label>
                <input type="text" onChange={e => setBooking({...booking, sid: e.target.value})} /><br/>
                <label>请输入用户ID:</label>
                <input type="text" onChange={e => setBooking({...booking, uid: e.target.value})} /><br/>
                <label>请输入订购票数:</label>
                <input type="text" onChange={e => setBooking({...booking, count: e.target.value})} /><br/>
                <button onClick={handleBook} style={{ marginTop: '10px' }}>确认购票</button>
            </div>
        </div>
    );
};

export default MovieTable;