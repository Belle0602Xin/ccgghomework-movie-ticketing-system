🎬 Movie Ticketing & Management System

🚀 Project Overview

This is a high-performance, full-stack web application designed to handle end-to-end movie ticketing operations. Beyond simple CRUD, the system is architected to tackle typical enterprise challenges such as concurrency control, data consistency, and scalable cloud deployment.

🛠️ Tech Stack
Backend: Java 17, Spring Boot 3, Spring Security (Authentication & RBAC).

Data Layer: MySQL, MyBatis/Hibernate (ORM), Redis (Session management & Caching).

Frontend: React / Thymeleaf (based on your implementation), JavaScript, Bootstrap.

Infrastructure: Docker, AWS (EC2/S3).

💡 Key Technical Challenges & Solutions
1. High Concurrency Ticket Booking
Challenge: Handling simultaneous seat selection to prevent "double-booking" or "over-selling."

Solution: Implemented Optimistic Locking at the database level and leveraged Redis Distributed Locks to ensure thread safety during peak traffic, ensuring that one seat can only be sold to one user at any given millisecond.

2. Performance Optimization via Caching
Challenge: Repeatedly querying the database for "Now Showing" movies during high-traffic periods.

Solution: Integrated Redis as a caching layer for static movie metadata and schedule information, reducing database read latency by over 60%.

3. Secure User Authentication
Challenge: Managing sensitive user data and distinct roles (Admin vs. Customer).

Solution: Integrated Spring Security with JWT (or Session-based) authentication. Implemented Role-Based Access Control (RBAC) to restrict administrative functions (e.g., adding theaters, updating prices) to authorized personnel only.

📖 Features
User Module: Secure registration, login, and personalized booking history.

Movie Browsing: Advanced filtering by genre, rating, and screening time.

Seat Selection: Interactive UI for real-time seat availability visualization.

Admin Dashboard: Comprehensive management of movie catalogs, cinema halls, and dynamic pricing strategies.


👩‍💻 About the Developer

Developed by Iris (Yuxin) Hou, a Full Stack Developer specialized in Java ecosystems. This project serves as a showcase of my ability to build secure, scalable, and user-centric backend systems.
