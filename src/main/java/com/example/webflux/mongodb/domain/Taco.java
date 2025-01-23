package com.example.webflux.mongodb.domain;

import java.util.List;

@Data
@RestResource(rel="tacos", path="tacos")
@Document
public class Taco {
    @Id // id가 String일 경우 데이터베이스가 저장될 떄 몽고 DB가 자동으로 ID값을 지정해준다(null 일 때)
    private String id;

    @NotNull
    @Size(min=5, message="Name must be at least 5 characters long")
    private String name;

    private Date createdAt = new Date();

    @Size(min=1, message="choose 1 ingredient")
    private List<Ingredient> ingredients;
    // JPA : Ingredient객체를 저장한 컬렉션인 List<Ingredient>
    // mongodb : 컬렉션에 저장되지 않으며, 비정규화된 상태로 타코 문서에 직접 저장한다. but 카산드라와 달리 사용자정의타입을 만들 필요 x. @Document가 지정된 다른 클래스는 단순 POJO 모두 가능함.
}