package com.uade.tpo.demo.controllers.config;

import com.uade.tpo.demo.entity.Rol;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Optional<Usuario> adminUser = usuarioRepository.findByEmail("admin@example.com");

        if (adminUser.isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setEmail("admin@example.com");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(Rol.ADMIN);
            usuarioRepository.save(admin);
        }
    }
}