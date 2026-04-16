package com.hyx.hyxmovieweb.controller;

import com.hyx.hyxmovieweb.entity.*;
import com.hyx.hyxmovieweb.service.MovieService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@CrossOrigin(
        origins = "http://localhost:3000",
        allowCredentials = "true",
        allowedHeaders = "*",
        exposedHeaders = "Set-Cookie"
)
@RestController
@RequestMapping("/api")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/movies")
    public Result getMovies(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(required = false) String user,
                            HttpSession session) {
        String loginUser = (String) session.getAttribute("currentUser");

        if (loginUser == null) {
            loginUser = user;
        }

        if (loginUser == null) {
            return Result.error("未登录");
        }

        Page<Movie> moviePage = movieService.getMoviesPage(page);

        return Result.ok("查询成功", moviePage);
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user, HttpSession session) {
        if (user.username == null || user.username.length() < 3) {
            return Result.error("账号过短");
        }

        if (user.password == null || user.password.length() < 6 || user.password.length() > 20) {
            return Result.error("密码长度6~20");
        }

        if (user.gender == null || user.gender.isEmpty()) {
            return Result.error("性别不能为空");
        }

        if (user.alias == null || user.alias.length() < 2 || user.alias.length() > 20) {
            return Result.error("昵称长度2~20");
        }

        if (user.email == null || user.email.length() < 10 || user.email.length() > 30) {
            return Result.error("邮箱在10到30个字符");
        }

        movieService.addUser(user);
        session.setAttribute("currentUser", user.username);

        return Result.ok("注册成功");
    }

    @PostMapping("/ticket-booking")
    public ResponseEntity<String> book(@RequestParam String sid,
                                       @RequestParam int count,
                                       @RequestParam(required = false) String user,
                                       HttpSession session) {

        String loginUser = (user != null) ? user : "testUser";
        int movieSid = Integer.parseInt(sid);

        String result = movieService.bookTicket(movieSid, count, loginUser);

        if (result.contains("Success")) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @GetMapping("/orders")
    public Result getOrders(@RequestParam(required = false) String user, HttpSession session) {
        String loginUser = (String) session.getAttribute("currentUser");

        if (loginUser == null) {
            loginUser = user;
        }

        if (loginUser == null) {
            return Result.error("未登录，无法查看订单");
        }

        return Result.ok("获取成功", movieService.getOrdersByUsername(loginUser));
    }

    @GetMapping("/sales")
    public Result getSales(@RequestParam(required = false) String user, HttpSession session) {
        String loginUser = (String) session.getAttribute("currentUser");

        if (loginUser == null) {
            loginUser = user;
        }

        if (loginUser == null) {
            return Result.error("未登录");
        }

        return Result.ok("获取成功", movieService.getSalesStatistics());
    }

    @PostMapping("/login")
    public Result login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        String token = movieService.login(username, password);

        if (token != null) {
            return Result.success(token);
        }
        return Result.error("账号或密码错误");
    }

    @PostMapping("/save")
    public Result save() {
        return Result.ok("数据库版本自动实时保存");
    }

    @PostMapping("/load")
    public Result load() {
        return Result.ok("数据库数据已是最新");
    }
}

//    @PostMapping("/test")
//    public ResponseEntity test() {
//        return ResponseEntity.ok("数据库版本自动实时保存");
//    }
//
//    @PostMapping("/test1")
//    @ResponseStatus(HttpStatus.OK)
//    public String test1() {
//        return "数据库版本自动实时保存";
//    }
//
//    @PostMapping("/load")
//    public Result load() {
//        return Result.ok("数据库数据已是最新");
//    }


//    public Result getMovies(@RequestParam(defaultValue = "0") int page, HttpSession session) {
//        if (session.getAttribute("currentUser") == null) {
//            return Result.error("未登录");
//        }
//
//        Page<Movie> moviePage = movieService.getMoviesPage(page);
//
//        return Result.ok("查询成功", moviePage);
//    }


//        String loginUser = (String) session.getAttribute("currentUser");
//        if (loginUser == null) {
//            loginUser = user;
//        }
//
//        if (loginUser == null) {
//            return Result.error("请登录后再进行购票操作");
//        }
//
//        int movieSid = Integer.parseInt(sid);
//
//        try {
//            String res = movieService.bookTicket(movieSid, count, loginUser);
//            return "SUCCESS".equals(res) ? Result.ok("购票成功") : Result.error("购票失败");
//        } catch (Exception e) {
//            return Result.error(e.getMessage());
//        }

