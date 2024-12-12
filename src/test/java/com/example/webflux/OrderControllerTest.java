package com.example.webflux;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// 모의 스프링 WebFlux 프레임워크 사용 (실제 서버가 필요하지 x)
public class OrderControllerTest {

    /**
     * GET 요청 테스트
     * */
    @Test
    public void shouldReturnRecentOrders(){
        Order[] orders = {
                testOrder(1L), testOrder(2L),
                testOrder(3L), testOrder(4L),
                testOrder(5L), testOrder(6L),
                testOrder(7L), testOrder(8L),
                testOrder(9L), testOrder(10L),
                testOrder(11L), testOrder(12L),
                testOrder(13L), testOrder(14L),
                testOrder(15L), testOrder(16L),
        };
        Flux<Order> orderFlux = Flux.just(orders); // 1. Flux 타입의 테스트 데이터 생성

        OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
        OrderReactiveRepository orderReactiveRepository = Mockito.mock(OrderReactiveRepository.class);

        when(orderReactiveRepository.findAll()).thenReturn(orderFlux); // 2. 모의 OrderRepository의 반환값으로 Flux 제공

        WebTestClient testClient = WebTestClient // 3. WebTestClient 인스턴스가 생성됨.
                .bindToController(
                        new OrderController(orderRepository, orderReactiveRepository)
                )
                .build();

        testClient.get().uri("/order/recent") // WebTestClient를 생성함.
            .exchange() // 4. 가장 최근 order을 요청함
            .expectStatus().isOk() // 5. 우리가 기대한 응답인지 검사함.
            .expectBody()
                .jsonPath("$").isArray() // jsonPath() : 응답 몸체의 JSON이 기대한 값을 갖는지 검사함
                .jsonPath("$").isNotEmpty()
                .jsonPath("$[0].id").isEqualTo(orders[0].getId().toString())
                .jsonPath("$[0].name").isEqualTo("Order 1")
                .jsonPath("$[1].id").isEqualTo(orders[1].getId().toString())
                .jsonPath("$[1].name").isEqualTo("Order 2")
                // ...
                .jsonPath("$[12]").doesNotExist(); // doesNotExist() : 요소의 존재 여부 검사
    }

    public Order testOrder(Long id){
        return Order.builder().id(id).build();
    }


    // recent-orders.json이라는 파일에 완벽한 응답 JSON을 생성 후 /orders 경로의 classpath에 저장했을 때
    public void testJSON() throws IOException {
        ClassPathResource recentsResource = new ClassPathResource("/orders/recent-orders.json"); // 1. classpath의 리소스를 String 타입으로 로드
        String recentsJson = StreamUtils.copyToString(recentsResource.getInputStream(), Charset.defaultCharset());

        Order[] orders = {
                testOrder(1L), testOrder(2L),
                testOrder(3L), testOrder(4L),
                testOrder(5L), testOrder(6L),
                testOrder(7L), testOrder(8L),
                testOrder(9L), testOrder(10L),
                testOrder(11L), testOrder(12L),
                testOrder(13L), testOrder(14L),
                testOrder(15L), testOrder(16L),
        };
        Flux<Order> orderFlux = Flux.just(orders); // 1. Flux 타입의 테스트 데이터 생성
        OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
        OrderReactiveRepository orderReactiveRepository = Mockito.mock(OrderReactiveRepository.class);
        when(orderReactiveRepository.findAll()).thenReturn(orderFlux); // 2. 모의 OrderRepository의 반환값으로 Flux 제공
        WebTestClient testClient = WebTestClient // 3. WebTestClient 인스턴스가 생성됨.
                .bindToController(
                        new OrderController(orderRepository, orderReactiveRepository)
                )
                .build();

        // 테스트 방법 1
        testClient.get().uri("/order/recent")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json(recentsJson);

        // 테스트 방법 2
        testClient.get().uri("/order/recent")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Order.class) // expectBodyList() : 리스트 형태로 여러 개의 값을 갖는 응답 몸체를 비교할 수 있음.
                .contains(Arrays.copyOf(orders, 12));
    }

    /**
     * POST 요청 테스트
     * */
    @Test
    public void shouldSaveATaco(){
        OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
        OrderReactiveRepository orderReactiveRepository = Mockito.mock(OrderReactiveRepository.class); // 1. 테스트 데이터 설정
        Mono<Order> unsavedOrderMono = Mono.just(testOrder(null));
        Order savedOrder = testOrder(null);
        savedOrder.updateId(1L);
        Mono<Order> savedOrderMono = Mono.just(savedOrder);

        when(orderReactiveRepository.save(any())).thenReturn(savedOrderMono); // 2. 모의 OrderRepository

        WebTestClient testClient = WebTestClient.bindToController(new OrderController(orderRepository,orderReactiveRepository)).build(); // WebTestClient를 생성함.

        testClient.post()
                .uri("/order")
                .contentType(MediaType.APPLICATION_JSON) // 3. 요청 타입 설정
                .body(unsavedOrderMono, Order.class)
                .exchange()
                .expectStatus().isCreated() // 4. 201(created) 상태 코드를 갖는지 확인
                .expectBody(Order.class)
                .isEqualTo(savedOrder);
    }

}

