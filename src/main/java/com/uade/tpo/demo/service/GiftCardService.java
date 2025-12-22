package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.GiftCardCode;
import com.uade.tpo.demo.entity.ItemPedido;
import com.uade.tpo.demo.entity.Pedido;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.repository.GiftCardCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class GiftCardService {

    @Autowired
    private GiftCardCodeRepository giftCardCodeRepository;

    @Autowired
    private EmailService emailService;

    private static final SecureRandom RNG = new SecureRandom();

    public List<GiftCardCode> generarCodigosParaPedido(Pedido pedido) {
        List<GiftCardCode> generados = new ArrayList<>();
        Usuario destinatario = pedido.getComprador();

        for (ItemPedido item : pedido.getProductosAdquiridos()) {
            if (item.getVideojuego() != null && item.getVideojuego().isGiftCard()) {
                int qty = item.getCantidad() != null ? item.getCantidad() : 1;
                for (int i = 0; i < qty; i++) {
                    GiftCardCode code = new GiftCardCode();
                    code.setCode(generarCodigo());
                    code.setValor(item.getPrecio());
                    code.setUsuario(destinatario);
                    code.setPedido(pedido);
                    generados.add(giftCardCodeRepository.save(code));
                }
            }
        }
        return generados;
    }

    public void enviarCodigos(Pedido pedido, List<GiftCardCode> codes) {
        String email = pedido.getComprador() != null ? pedido.getComprador().getEmail() : null;
        if (email != null && !codes.isEmpty()) {
            emailService.enviarGiftCards(email, codes, pedido);
        }
    }

    private String generarCodigo() {
        byte[] raw = new byte[12];
        RNG.nextBytes(raw);
        String base = Base64.getUrlEncoder().withoutPadding().encodeToString(raw).toUpperCase();
        // formatear XXXX-XXXX-XXXX-XXXX
        String clean = base.replaceAll("[^A-Z0-9]", "");
        clean = clean.length() > 16 ? clean.substring(0,16) : String.format("%-16s", clean).replace(' ', 'X');
        return clean.substring(0,4) + "-" + clean.substring(4,8) + "-" + clean.substring(8,12) + "-" + clean.substring(12,16);
    }
}
