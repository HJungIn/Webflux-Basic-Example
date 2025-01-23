package com.example.webflux.mongodb.domain;

import com.example.webflux.cassandra.domain.Taco;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private Date placedAt = new Date();

    @Field("customer") // customenr 열로 문서에 저장
    private User user;

        ...

    private List<Taco> tacos = new ArrayList<>();
    public void addDesign(Taco design){
        this.tacos.add(design);
    }
}