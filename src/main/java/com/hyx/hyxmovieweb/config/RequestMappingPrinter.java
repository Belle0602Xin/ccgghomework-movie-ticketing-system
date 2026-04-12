package com.hyx.hyxmovieweb.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestMappingPrinter {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        System.out.println("\n--- 启动自检：所有 API 映射列表 ---");

        Map<String, List<Map.Entry<RequestMappingInfo, HandlerMethod>>> groupedMethods =
                requestMappingHandlerMapping.getHandlerMethods().entrySet().stream()
                        .collect(Collectors.groupingBy(e -> e.getValue().getBeanType().getSimpleName()));

        groupedMethods.forEach((className, methods) -> {
            System.out.println("\n📍 Controller类: [" + className + "]");

            for (Map.Entry<RequestMappingInfo, HandlerMethod> methodEntry : methods) {
                System.out.println("URL: " + methodEntry.getKey() + " -> 方法: " + methodEntry.getValue().getMethod().getName());
            }
        });

        System.out.println("\n--- 结束自检 ---\n");
    }
}


//    public void onApplicationEvent(ContextRefreshedEvent event) {
//        System.out.println("\n--- 启动自检：所有 API 映射列表 ---");
//
//        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
//
//        String currentClassName = "";
//
//        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
//            HandlerMethod method = entry.getValue();
//            String className = method.getBeanType().getSimpleName();
//
//            // 如果换了一个类，打印类名标题
//            if (!className.equals(currentClassName)) {
//                currentClassName = className;
//                System.out.println("\n📍 Controller类: [" + className + "]");
//            }
//
//            System.out.println(" URL: " + entry.getKey() + " -> 方法: " + method.getMethod().getName());
//        }
//
//        System.out.println("\n--- 结束自检 ---\n");
//    }