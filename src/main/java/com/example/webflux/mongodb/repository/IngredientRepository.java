package com.example.webflux.mongodb.repository;

// ReactiveCrudRepository : 초기에 추가할 때 생성되므로 ReactiveCrudRepository 사용
@CrossOrigin(origins="*")
public interface IngredientRepository extends ReactiveCrudRepository<Ingredient, String>{
    // ex) Flux나 Mono 타입으로 도메인 객체를 return
    Flux<Ingredient> findAll();
    Mono<Ingredient> findById();
}
