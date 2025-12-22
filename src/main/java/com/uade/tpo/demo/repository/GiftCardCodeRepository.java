package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.GiftCardCode;
import com.uade.tpo.demo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GiftCardCodeRepository extends JpaRepository<GiftCardCode, Long> {
    Optional<GiftCardCode> findByCode(String code);
    List<GiftCardCode> findByUsuario(Usuario usuario);
}
