package org.onecx.tenantsvc.control.services;

import java.time.LocalDateTime;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TimeProvider {

    public LocalDateTime localDateTimeNow() {

        return LocalDateTime.now();
    }
}
