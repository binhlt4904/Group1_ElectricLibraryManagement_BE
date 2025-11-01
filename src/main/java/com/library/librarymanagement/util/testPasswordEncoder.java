package com.library.librarymanagement.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class testPasswordEncoder {
    public static void main(String[] args) {
        PasswordEncoder pe = new BCryptPasswordEncoder();
        String raw = "staff";
        String hash = pe.encode(raw);
        System.out.println(hash);
    }
}
