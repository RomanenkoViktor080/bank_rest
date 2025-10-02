package com.example.bankcards.util.masker;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class PanMaskerWithLastNumbersImpl implements PanMasker {
    public String mask(String pan) {
        return "**** **** **** " + normalize(pan);
    }

    private String normalize(String lastNumbers) {
        return lastNumbers.replaceAll("\\s+", "");
    }
}
