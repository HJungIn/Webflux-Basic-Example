package com.example.webflux.cassandra.domain;

import com.example.webflux.cassandra.udt.TacoUDT;
import com.example.webflux.cassandra.udt.UserUDT;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Table("tacoorders") // tacoorders 테이블로 매핑한다.
public class CassandraOrder {
    private static final long serialVersionUID = 1L;

    @PrimaryKey // 기본키를 선언함. ==> 파티션 키와 클러스터링 키 모두 이 속성이 사용됨
    private UUID id = UUIDs.timeBased();

    private Date placedAt = new Date();

    @Column("user") // user 열에 사용자 정의 타입을 매핑함.
    private UserUDT user; // 주문한 사용자 데이터를 tacoorders 테이블이 포함함.

        ...

    @Column("tacos") // tacos 열에 사용자 정의 타입을 매핑함. ==> 관계형 DB처럼 다른 테이블의 행들을 외부키를 통해 조인하는 것이 아니고, 주문된 모든 타코의 데이터를 tacoorders 테이블에 포함시킨다(why? 빠른 데이터 검색에 테이블을 최적화하기 위함.)
    private List<TacoUDT> tacos = new ArrayList<>();

    public void addDesign(TacoUTD design){
        this.tacos.add(design);
    }
}
