package com.example.webflux;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Observable;
import java.util.Optional;

@RestController
@RequestMapping(path = "/order",
        produces = "application/json")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderReactiveRepository orderReactiveRepository;


    /**
     * 스프링 MVC를 사용한 방법
     * */
    @GetMapping("/recent")
    public Iterable<Order> recentOrders(){
        PageRequest pageRequest = PageRequest.of(0,12, Sort.by("createdAt").descending());
        return orderRepository.findAll(pageRequest).getContent();
    }

    // 단일값 반환
    @GetMapping("/{id}")
    public Order orderById(@PathVariable("id") Long id){
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if(optionalOrder.isPresent()){
            return optionalOrder.get();
        }
        return null;
    }

    // 입력 처리하기
    // 2번 블로킹 됨 : postOrder() 진입할 때(payload인 Order 객체가 완전히 분석되어야 진행됨.), postOrder() 내부
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Order postOrder(@RequestBody Order order){
        return orderRepository.save(order); // save()의 블로킹되는 호출이 끝나고 복귀되어야 postOrder()가 끝남
    }


    /**
     * 스프링 WebFlux 사용한 방법 1 : 여러 스레드에 걸쳐 작업을 분할함
     * * .fromIterable() : Iterable<Order>이 Flux<Order>로 변환됨
     * * .take() : 개수만큼 제한 (페이징 코드를 Flux의 take()로 교체)
     * */
    @GetMapping("/reactive/recent")
    public Flux<Order> recentOrdersReactive1(){
        return Flux.fromIterable(orderRepository.findAll()).take(12);
    }

    /**
     * 스프링 WebFlux 사용한 방법 2 : repository에서 Flux 타입을 반환
     * */
    @GetMapping("/reactive/recent")
    public Flux<Order> recentOrdersReactive2(){
        return orderReactiveRepository.findAll().take(12);
    }

    // 단일값 반환
    @GetMapping("/reactive/{id}")
    public Mono<Order> orderByIdReactive(@PathVariable("id") Long id){
        return orderReactiveRepository.findById(id);
    }


    /**
     * 리액티브하게 입력 처리하기
     * * Mono<Order> order 를 인자로 받기
     * * saveAll()은 Flux<Order>를 반환하지만 .next()를 호출하여 Mono<Order>로 받을 수 있음.
     * */
    // 논블록킹 : Mono<Order> 를 인자로 받으므로 request 몸체로부터 Order 객체가 분석되는 것을 기다리지 않고 즉시 호출됨. + 라퍼지터리 또한 리액티브이므로 Mono를 받아 즉시 Flux<Order>를 반환함(이 Flux<Order>를 next()호출에서 Mono<Order>로 반환함.)
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Order> postOrderReactive(@RequestBody Mono<Order> order){
        return orderReactiveRepository.saveAll(order).next();
    }

}
