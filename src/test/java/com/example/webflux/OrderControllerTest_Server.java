package com.example.webflux;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Netty나 톰캣과 같은 서버 환경에서 리퍼지터리나 다른 의존성 모듈을 사용해 WebFlux 컨트롤러 테스트 하기 => 통합 테스트
//@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 무작위 포트로 리스닝됨
public class OrderControllerTest_Server {

    @Autowired
    private WebTestClient testClient;

    @Test
    public void shouldReturnRecentOrders() throws IOException {
        testClient.get().uri("/order/recent")
                .accept(MediaType.APPLICATION_JSON).exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$[?(@.id == 'TAC01')].name") // JSONPath 표현식 사용
                        .isEqualTo("Carnivore")
                    .jsonPath("$[?(@.id == 'TAC02')].name")
                        .isEqualTo("Bovine Bounty")
                    .jsonPath("$[?(@.id == 'TAC03')].name")
                        .isEqualTo("Veg-Out");

    }
}

