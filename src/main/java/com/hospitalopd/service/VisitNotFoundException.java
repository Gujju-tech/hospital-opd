package com.hospitalopd.service;

public class VisitNotFoundException extends RuntimeException {

    public VisitNotFoundException(Long id) {
        super("OPD visit not found: " + id);
    }
}
