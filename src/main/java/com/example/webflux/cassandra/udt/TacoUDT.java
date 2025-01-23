package com.example.webflux.cassandra.udt;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.util.List;

@Data
@UserDefinedType("taco")
public class TacoUDT {
    private final String name;
    private final List<IngredientUDT> ingredients; // 또 다른 타입을 저장하는 컬렉션을 포함함.
}
