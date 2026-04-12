package com.hyx.hyxmovieweb.component;

import com.mongodb.lang.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RequestMappingInspector implements CommandLineRunner {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public void run(@NonNull String... args) {
        System.out.println("\n--- API Self Inspection Start Scanning Mappings ---");

        Map<RequestMappingInfo, HandlerMethod> methods = requestMappingHandlerMapping.getHandlerMethods();

        methods.forEach((info, method) -> {
            String className = method.getBeanType().getSimpleName();
            String methodName = method.getMethod().getName();
            String patterns = info.getDirectPaths().toString();
            System.out.printf("📍 Controller: [%s] -> URL: %s -> Method: %s\n",
                    className, patterns, methodName);
        });

        System.out.println("--- API Self Inspection End Scanning Mappings ---\n");
    }
}