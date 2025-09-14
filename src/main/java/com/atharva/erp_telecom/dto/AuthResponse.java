package com.atharva.erp_telecom.dto;

public class AuthResponse {
    private String jwt;

    public AuthResponse(String jwt){
        this.jwt = jwt;
    }

    public String getJwt(){
        return jwt;
    }
}
