package com.library.librarymanagement.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class testPasswordEncoder {
    public static void main(String[] args) {
        PasswordEncoder pe = new BCryptPasswordEncoder();
        String raw = "admin123";
        String hash = pe.encode(raw);
        System.out.println(hash);
    }
}
