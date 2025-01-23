package com.example.webflux.mongodb.repository;

// Order 문서는 자주 생성 될 것이기 때문에 insert() 메서드로 제공되는 최적화의 장점을 얻기 위해 ReactiveMongoRepository 사용
public interface OrderRepository extends ReactiveMongoRepository<Order, String>{
}