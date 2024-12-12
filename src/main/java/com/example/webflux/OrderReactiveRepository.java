package com.example.webflux;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

// 리퍼지터리로부터 Flux<Order>와 같은 리액티브 타입을 받을 때 subscribe()을 프레임워크가 알아서 호출해주기 때문에 직접 호출할 필요 x
public interface OrderReactiveRepository extends ReactiveCrudRepository<Order, Long> {
}
