package com.connectly.Connectly_member_service.dto;

import java.util.List;

public class UserDto {
    private String email;
    private String firstName;

    public UserDto(String email, String firstName, String phoneNumber, List<String> roles) {
        this.email = email;
        this.firstName = firstName;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
    }

    private String phoneNumber;
    private List<String> roles;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
