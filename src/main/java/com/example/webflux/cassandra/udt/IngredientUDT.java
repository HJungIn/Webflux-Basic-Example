package com.example.webflux.cassandra.udt;

import com.example.webflux.cassandra.domain.Ingredient;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force=true)
@UserDefinedType("ingredient") // 카산드라의 사용자 정의 타입인 것을 알 수 있도록 함.
public class IngredientUDT{
    private final String name;
    private final Ingredient.Type type;
}