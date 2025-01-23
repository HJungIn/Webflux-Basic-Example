package com.example.webflux.cassandra.repository;

import org.springframework.data.cassandra.repository.AllowFiltering;

import java.util.UUID;

public interface UserRepository extends ReactiveCassandraRepository<User, UUID>{
    @AllowFiltering
    Mono<User> findByUsername(String username); // Mono<User>를 반환하도록 함.
}

/**
 * * 카산드라의 특성상 관계형 DB에서 SQL로 하듯이 테이블을 단순하게 where절로 쿼리할 수 없다.
 * * 카산드라는 데이터 읽기에 최적화 된다.
 * * 그러나 where절을 사용한 필터링 결과는 빠른 쿼리와는 달리 너무 느리게 처리될 수 있다. 그렇지만 결과가 하나 이상의 열로 필터링되는 테이블 쿼리에는 매우 유용하므로 where절을 사용할 필요가 있다.
 * * => 이 때 사용하는 애노테이션! : @AllowFiltering
 *
 * * @AllowFiltering을 지정하지 않은 findByUsername() : select * from users where username='검색할 사용자 이름';
 *     * 이런 단순한 where 절은 카산드라에서 허용되지 않음.
 * * @AllowFiltering을 지정한 findByUsername() : select * from users where username='검색할 사용자 이름' allow filtering;
 *     * allow filtering절은 '쿼리 성능에 잠재적인 영향을 준다는 것을 알고 있지만, 어쨌든 수행해야 한다'는 것을 카산드라에 알려준다.
 *     * 이 경우 카산드라는 where절을 허용하고 결과 데이터를 필터링한다.
 * */

/**
 * // Flux 형태로 return 될 때, PagingAndSortRepository 인터페이스의 확장이나 결과 페이지 처리에 관해 더이상 신경쓰지 않아도 됨.
 *     // 대신 자신이 반환하는 Flux에 take()를 호출하여 결과의 한 페이지를 채울 Taco객체의 수를 제한해야 한다.
 * */