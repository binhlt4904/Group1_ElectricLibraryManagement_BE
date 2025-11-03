package com.library.librarymanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class LibraryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementApplication.class, args);
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "ngoctuan123"; // <-- đổi thành mật khẩu bạn muốn mã hóa
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("Raw: " + rawPassword);
        System.out.println("Encoded: " + encodedPassword);
    }

}
