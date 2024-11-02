package com.denise.cleaningservices.exception;

public class RoleAlreadyExistsException extends RuntimeException{

    public RoleAlreadyExistsException(String message){
        super(message);
    }
}
