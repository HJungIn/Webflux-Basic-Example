package com.example.webflux.mongodb.repository;

// ReactiveMongoRepository : 몽고DB에 매우 특화되어서, 만일 언젠가 다른 DB로 전환하지 않을 것이라면 선택하는 것이 데이터 추가의 최적화에 따른 이익을 얻을 수 있음.
public interface TacoRepository extends ReactiveMongoRepository<Taco, String>{
    // 기존에는 PagingAndSortingRepository였지만 리액티브 리퍼지터리에 적합하지 않음.
    // 리액티브에서 페이징이 필요한 이 때는, Flux<Taco>를 반환하므로 결과의 페이징(한 페이지당 반환할 개수만큼만 Taco 객체를 가져옴)을 신경쓰지 않아도 됨.
    // take() 오퍼레이션을 적용해 Flux에서 발행되는 처음 N개의 Taco 객체만 반환할 수 있도록 한다.
    // 커스텀 쿼리 메서드의 명명 규칙을 따라 아래와 같이 만들 수 있다
    Flux<Taco> findByOrderByCreatedAtDesc(); // Taco객체를 createdAt 속성값을 기준으로 내림차순으로 조회하라

}

    /**
     * take() 오퍼레이션을 적용해 Flux에서 발행되는 처음 12개의 Taco 객체만 반환할 수 있도록 한다.
     *
     *     Flux<Taco> recents = repo.findByOrderByCreatedAtDesc()
     *             .take(12);
     */