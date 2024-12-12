package com.example.webflux;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class WebClientExample {

    /**
     * 0-1. 기본 URI로 요청하기
     * * WebClient 빈 생성 시 기본 URI를 포함하여 생성
     * */
    @Bean
    public WebClient webClient() {
        return WebClient.create("http://localhost:8080");
    }

    @Autowired
    WebClient webClient;
    public Mono<Order> getOrderById(Long orderId){
        Mono<Order> orderMono = webClient
                .get()
                .uri("/orders/{id}", orderId)
                .retrieve()
                .bodyToMono(Order.class);

        orderMono.subscribe(i -> {
            //...
        });
        return null;
    }

    /**
     * 0-2. 오래 실행되는 요청 타입아웃 시키기
     * * 요청이 지체되는것을 방지하기 위해 Flux나 Mono의 timeout()을 사용해 데이터를 기다리는 시간을 제한하기
     * */
    public void timeout(){
        Flux<Order> orderFlux = WebClient.create()
                .get()
                .uri("http://localhost:8080/orders")
                .retrieve()
                .bodyToFlux(Order.class);

        orderFlux
                .timeout(Duration.ofSeconds(1)) // 경과 시간 1초
                .subscribe(
                        i -> {
                            //...
                        },
                        e -> {
                            // handle timeout error
                        });
    }



    /**
     * 1. GET : 리소스 얻기
     * * (== RestTemplate의 getForObject())
     * * 요청을 생성하고 응답을 받은 다음에 Order객체를 발행하는 Mono를 추출한다.
     *
     * */
    public void getMono(Long orderId){
        Mono<Order> orderMono = WebClient.create() // 1. create() : 새로운 WebClient 인스턴스 생성
                .get() // 2. 아래 uri에 대한 get요청 정의
                .uri("http://localhost:8080/orders/{id}", orderId)
                .retrieve() // 3. 해당 요청을 실행함.
                .bodyToMono(Order.class); // 4. 응답 몸체의 페이로드를 Mono<Order>로 추출함

        orderMono.subscribe(i -> { // 5. bodyToMono로부터 반환되는 Mono에 추가로 오퍼레이션 적용 시 해당 요청이 전송되기 전에 구독을 해야함
            //...
        });
    }

    public void getFlux(){
        Flux<Order> orderFlux = WebClient.create()
                .get()
                .uri("http://localhost:8080/orders")
                .retrieve()
                .bodyToFlux(Order.class);

        orderFlux.subscribe(i -> {
            //...
        });
    }

    /**
     * 2. POST : 리소스 전송하기
     * * body()를 통해 요청 몸체에 넣는다.
     * * body()를 통해 Mono나 Flux가 아닌 도메인 객체를 전송할 수 있다.
     * */
    public void postMono(){
        Mono<Order> orderMono = Mono.just(Order.builder().name("a").build());
        Mono<Order> result = webClient
                .post()
                .uri("/orders")
                .body(orderMono, Order.class)
                .retrieve()
                .bodyToMono(Order.class);

        result.subscribe(i -> {
            //...
        });
    }

    // 일반적으로 PUT 요청은 비어있는 응답 페이로드를 갖는다. 따라서 Void 타입의 Mono를 반환하도록 bodyToMono()에 요구해야 한다.
    public void putMono(Long orderId){
        Order order = Order.builder().name("a").build();
        Mono<Void> result = webClient
                .put()
                .uri("/orders/{id}", orderId)
                .body(order, Order.class)
                .retrieve()
                .bodyToMono(Void.class);

        result.subscribe(i -> {
            //...
        });
    }

    /**
     * 3. DELETE : 리소스 삭제하기
     * */
    public void delete(Long orderId){
        Mono<Void> result = webClient
                .delete()
                .uri("/orders/{id}", orderId)
                .retrieve()
                .bodyToMono(Void.class);

        result.subscribe(i -> {
            //...
        });
    }

    /**
     * 4. 에러 처리하기
     * * 400번 대나 500번 대의 상태 코드를 갖는 응답 처리하기
     * * onStatus() : 에러를 처리해야 할 때 호출하는 메서드 - 처리해야 할 HTTP 상태 코드를 지정할 수 있다.
     * * onStatus(HTTP 상태와 일치시키는데 사용되는 조건 함수, Mono<Throwable>)
     * */
    public void error(Long orderId){
        Mono<Order> orderMono = webClient
                .get()
                .uri("/orders/{id}", orderId)
                .retrieve()
                .bodyToMono(Order.class);

        orderMono.subscribe( //구독 필수
                order -> { /** orderId와 일치하는 식자재 리소스를 찾았을 때 */
                    // order 데이터 처리
                    // ...
                },
                error -> { /** orderId와 일치하는 식자재 리소스를 못 찾았을 때 : 404(NOT FOUND) */
                    // 에러를 처리 (default : WebClientResponseException 발생)
                    // ...

                }
        );
    }

    /**
     * 4-1.커스텀 에러 처리하기
     * * retrieve() 호출 후 onState() 호출
     * * 커스텀 에러 핸들러 추가 시 HTTP 상태 코드를 우리가 선택한 Throwable로 변환하는 실행코드를 제공할 수 있음.
     * * 응답으로 반환될 수 있는 다양한 HTTP 상태 코드 처리 시 onState() 호출을 여러 번 할 수 있다.
     *  ex ) order 리소스 요청에 실패 시 UnknownOrderException 에러를 포함하는 Mono로 생성하고 싶을 때
     *  ex ) HTTP 404 상태 코드를 검사하도록 onStatus()에서 확인하기
     * */
    public void errorCustom(Long orderId){
        Mono<Order> orderMono = webClient
                .get()
                .uri("/orders/{id}", orderId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), // true일 경우 하위 함수를 거쳐 Throwable 타입의 Mono를 반환함.
                        response -> Mono.just(new UnknownOrderException()))
                .bodyToMono(Order.class);

        orderMono.subscribe( //구독 필수

        );
    }

    public void errorCustom2(Long orderId){
        Mono<Order> orderMono = webClient
                .get()
                .uri("/orders/{id}", orderId)
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND,
                        response -> Mono.just(new UnknownOrderException()))
                .bodyToMono(Order.class);

        orderMono.subscribe( //구독 필수

        );
    }


    /**
     * 5. 요청 교환하기
     * * retrieve() : ResponseSpec 타입의 객체를 반환함. => 간단한 상황에서는 좋지만 응답의 헤더나 쿠키값을 사용할 필요가 있을 떄는 처리할 수 없음.
     * * exchangeToMono() : ClientResponse 타입의 Mono로 반환함. ClientResponse 타입은 리액티브 오페레이션을 적용할 수 있고, 응답의 모든 부분(페이로드, 헤더, 쿠키 등)에서 데이터를 사용할 수 있음.
     * * 공통점 :
     * * 차이점
     *  * retrieve() : bodyToMono()를 사용해 Mono<Order>를 가져옴.
     *  * exchangeToMono() : exchangeToMono()를 사용해 ClientResponse를 통해 바로 Order클래스에 매칭함.
     *
     * */
    public void retrieveAndExchange(Long orderId){
        Mono<Order> orderMonoRetrieve = webClient
                .get()
                .uri("/orders/{id}", orderId)
                .retrieve()
                .bodyToMono(Order.class);

        Mono<Order> orderMonoExchange = webClient
                .get()
                .uri("/orders/{id}", orderId)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Order.class));

        // 구독 생략


        /**
         * * 요청의 응답에 true값을 갖는 X_UNAVAILABLE라는 이름의 헤더가 포함될 때
         */
        Mono<Order> orderMonoExchange2 = webClient
                .get()
                .uri("/orders/{id}", orderId)
                .exchangeToMono(clientResponse -> {
                    if(clientResponse.headers().header("X_UNVAILABLE").contains("true")){
                        return Mono.empty(); // 비어있는 Mono 반환
                    }
                    return clientResponse.bodyToMono(Order.class); // ClientResponse를 포함하는 새로운 Mono를 반환
                });
    }




}

@AllArgsConstructor
@NoArgsConstructor
class UnknownOrderException extends RuntimeException {}