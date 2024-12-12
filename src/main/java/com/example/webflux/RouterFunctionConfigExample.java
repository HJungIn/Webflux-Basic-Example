package com.example.webflux;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

// static import : 함수형 타입을 생성하는데 사용할 수 있음
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static reactor.core.publisher.Mono.just;

/**
 * 스프링 5의 리액티브 API를 정의하기 위한 **새로운 함수형 프로그래밍 모델**.
 * 프레임워크보다는 라이브러리 형태로 사용되며 애노테이션을 사용하지 않고 요청을 핸들러 코드에 연관 시킴.
 * WebFlux의 대안으로 사용됨
 *
 * * 스프링 함수형 프로그래밍 모델을 사용한 API작성의 4가지 타입
 *     1. RequestPredicate : 처리될 요청의 종류 선언
 *     2. RouterFunction : 일치하는 요청이 어떻게 핸들러에게 전달되어야 하는지를 선언
 *     3. ServerRequest : HTTP 요청을 나타내며, 헤더와 몸체 정보를 사용할 수 있음.
 *     4. ServerResponse : HTTP 응답을 나타내며, 헤더와 몸체 정보를 포함함.
 * */

@Configuration
public class RouterFunctionConfigExample {

    @Bean
    public RouterFunction<?> helloRouterFunction(){ // RouterFunction : 요청을 나타내는 RequestPredicate 객체가 어떤 요청 처리 함수와 연관되는지를 선언함.
        return route(GET("/hello"),
                request -> ok().body(just("Hello World!"), String.class)) // route(RequestPredicate 객체, 일치하는 요청을 처리하는 함수(ServerRequest를 인자로 받으며, ServerResponse의 ok()메서드와 이 매서드에서 반환된 BodyBuilder의 body()를 사용해 ServerResponse를 반환))
                .andRoute(GET("/bye"), // andRoute() : 또 다른 RouterFunction을 연결해 추가할 수 있다
                        request -> ok().body(just("See ya!"), String.class));
    }
}
