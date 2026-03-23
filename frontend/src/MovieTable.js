import React, { useState, useEffect } from 'react';
import axios from 'axios';

const MovieTable = ({ onBookSuccess }) => {
    const [movies, setMovies] = useState([]);
    const [loading, setLoading] = useState(false);
    const [booking, setBooking] = useState({ sid: '', count: '' });
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const loadData = (page = 0) => {
        if (loading) return;
        setLoading(true);

        const user = localStorage.getItem('currentUser');
        axios.get(`http://localhost:8080/api/movies?page=${page}&user=${user}`, { withCredentials: true })
            .then(res => {
                console.log("后端返回原始数据:", res.data);
                if (res.data.code === 200) {
                    const movieContent = res.data.data.content || [];
                    setMovies(movieContent);
                    setTotalPages(res.data.data.totalPages || 0);
                    setCurrentPage(page);
                }
            })
            .catch(err => {
                console.error("加载失败", err);
            })
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        loadData(0);
    }, []);

    const handleBook = () => {
        if (!booking.sid || !booking.count) {
            alert("请完整填写场次ID和张数");
            return;
        }

        const user = localStorage.getItem('currentUser');
        const params = new URLSearchParams();
        params.append('sid', booking.sid);
        params.append('count', booking.count);
        params.append('user', user);

        axios.post('http://localhost:8080/api/book', params, { withCredentials: true })
            .then(res => {
                alert(res.data.message);
                if (res.data.code === 200) {
                    loadData(currentPage);
                    if(onBookSuccess) {
                        onBookSuccess();
                    }
                }
            })
            .catch(err => alert("购票请求失败，请检查网络"));
    };

    return (
        <div className="movie-container" style={{ padding: '20px' }}>
            <div className="header-bar">--- 可售电影场次安排如下 ---</div>

            <div className="movie-list">
                {movies.length > 0 ? movies.map(m => (
                    <div key={m.id || m.movieId} className="movie-item" style={{
                        marginBottom: '15px', padding: '15px', borderBottom: '2px solid #eee',
                        backgroundColor: '#fff', borderRadius: '8px', color: '#333'
                    }}>
                        <p>
                            <strong style={{color: '#007bff'}}>场次 ID: {m.id || m.movieId}</strong> |
                            电影编号: {m.filmId || m.f_id}
                        </p>
                        <p>
                            票价: <span
                            style={{color: '#e4393c', fontWeight: 'bold'}}>${m.moviePrice || m.price}</span> |
                            上映时间: {m.movieTime || m.show_time}
                        </p>
                        <p>
                            当前余票: <strong style={{color: m.ticketsAvailable > 0 ? 'green' : 'red'}}>
                            {m.ticketsAvailable}
                        </strong> 张
                        </p>
                    </div>
                )) : (
                    <div style={{textAlign: 'center', padding: '50px', color: '#999'}}>
                    {loading ? "加载中..." : "暂无排片数据，请确保数据库 t_schedule 有值"}
                    </div>
                )}
            </div>

            <div className="pagination" style={{ marginTop: '20px', textAlign: 'center' }}>
                <button disabled={currentPage === 0 || loading} onClick={() => loadData(currentPage - 1)}>上一页</button>
                <span style={{ margin: '0 15px' }}> 第 {currentPage + 1} 页 / 共 {totalPages} 页 </span>
                <button disabled={currentPage >= totalPages - 1 || loading} onClick={() => loadData(currentPage + 1)}>下一页</button>
            </div>

            <div className="header-bar" style={{ marginTop: '40px' }}>购票登记界面</div>
            <div className="booking-form" style={{ backgroundColor: '#2c3e50', color: '#ecf0f1', padding: '20px', borderRadius: '8px' }}>
                <div style={{ marginBottom: '10px' }}>
                    <label>场次 ID: </label>
                    <input type="text" value={booking.sid} onChange={e => setBooking({...booking, sid: e.target.value})} style={{ color: '#000' }} />
                </div>
                <div style={{ marginBottom: '10px' }}>
                    <label>购票张数: </label>
                    <input type="number" value={booking.count} onChange={e => setBooking({...booking, count: e.target.value})} style={{ color: '#000' }} />
                </div>
                <button className="btn-book" onClick={handleBook} style={{
                    backgroundColor: '#e67e22', color: 'white', border: 'none', padding: '10px 20px', cursor: 'pointer', borderRadius: '4px'
                }}>确认购票</button>
            </div>
        </div>
    );
};

export default MovieTable;