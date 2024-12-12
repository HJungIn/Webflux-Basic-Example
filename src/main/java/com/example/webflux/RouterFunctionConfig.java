package com.example.webflux;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static reactor.core.publisher.Mono.just;

// 함수형 웹 프로그래밍 모델. OrderController과 동일한 기능을 함수형 방식으로 작성함.
@Configuration
@RequiredArgsConstructor
public class RouterFunctionConfig {

    private final OrderReactiveRepository orderReactiveRepository;

    @Bean
    public RouterFunction<?> routerFunction(){ // 요청 처리
        return route(GET("/order/recent"), this::recents) // 메서드 참조로 경로가 처리됨
                .andRoute(POST("/order"), this::postOrder);
    }

    public Mono<ServerResponse> recents(ServerRequest request){
        return ServerResponse.ok()
                .body(orderReactiveRepository.findAll().take(12), Order.class);
    }

    public Mono<ServerResponse> postOrder(ServerRequest request){
        Mono<Order> order = request.bodyToMono(Order.class);
        Mono<Order> savedOrder = orderReactiveRepository.saveAll(order).next();
        return ServerResponse
                .created(URI.create("http://localhost:8080/order/"+savedOrder.block().getId()))
                .body(savedOrder, Order.class);
    }
}
