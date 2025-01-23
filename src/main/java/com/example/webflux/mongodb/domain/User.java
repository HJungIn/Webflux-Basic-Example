package com.example.webflux.mongodb.domain;

@Data
@RequiredArgsConstructor
@NoArgsCOnstructor(access=AccessLevel.PRIVATE, force=true)
@Document
public class User implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private final String username;
    private final String password;
    private final String fullname;
    private final String street;
    private final String city;
    private final String state;
    private final String zip;
    private final String phoneNumber;
        ...
}