package com.example.bankcards.util.masker;

import org.mapstruct.Named;

public interface PanMasker {
    @Named("maskPan")
    String mask(String pan);
}
