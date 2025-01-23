package com.example.webflux.cassandra.udt;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@Data
@UserDefinedType("user")
public class UserUDT{
    private final String username;
    private final String fullname;
    private final String phoneNumber;
}
