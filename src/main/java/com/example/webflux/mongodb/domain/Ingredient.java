package com.example.webflux.mongodb.domain;

@Data
@RequiredArgsConstructor
@NoArgsCOnstructor(access=AccessLevel.PRIVATE, force=true)
@Document // 해당 클래스가 몽고DB에 저장되거나 읽을 수 있는 문서 엔티티라는 것을 나타냄, 이름은 클래스 이름과 같고 첫 자만 소문자.
// @Document(collection="ingredients") // 이름 변경 시 colllection 사용
public class Ingredient{
    @Id // 문서의 id로 지정
    private final String id;
    private final String name;
    private final Type type;

    public static enum Type {
        WRAP, PROTEIN
    }
}
