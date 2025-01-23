package com.example.webflux.cassandra.domain;

import com.example.webflux.cassandra.udt.IngredientUDT;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Data
@RestResource(rel="tacos", path="tacos")
@Table("tacos") // tacos 테이블에 저장, 유지한다.
public class Taco{
    @PrimaryKeyColumn(type= PrimaryKeyType.PARTITIONED) // 파티션 키를 정의한다. // 2개의 키본 키 열 중 하나
    private UUID id = UUIDs.timeBased(); // UUID : 자동 생성되는 ID값을 저장하는 속성에 흔히 사용하는 타입이다. 새로운 Taco  객체가 생성될 때 시간 기반의 UUID 값으로 초기화됨.

    @NotNull
    @Size(min=5, message="Name must be at least 5 characters long")
    private String name;

    @PrimaryKeyColumn(type=PrimaryKeyType.CLUSTERED, // 클러스터링 키를 정의한다.
            ordering= Ordering.DESCENDING)
    private Date createdAt = new Date();

    @Size(min=1, message="You must choose at least 1 ingredient")
    @Column("ingredients") // List를 ingredients열에 매핑한다.
    private List<IngredientUDT> ingredients;
}