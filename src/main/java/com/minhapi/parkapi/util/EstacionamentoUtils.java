package com.minhapi.parkapi.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EstacionamentoUtils {

    // 2023-03-16T15:23:48.6164635000
    // 20230316-152348
    
    public static String gerarRecibo() {
        LocalDateTime date = LocalDateTime.now();
        String recibo = date.toString().substring(0, 19);
        return recibo.replace("-", "")
            .replace(":", "")
            .replace("T", "-");
    }

    private static final double PRIMEIROS_15_MINUTOS = 5.00;
    private static final double PRIMEIROS_60_MINUTOS = 9.25;
    private static final double ADICIONAL_15_MINUTOS = 1.75;

    public static BigDecimal calcularCusto(LocalDateTime entrada, LocalDateTime saida) {
        long minutes = entrada.until(saida, ChronoUnit.MINUTES);
        double total = 0.0;

        if (minutes <= 15) {
            
            total += PRIMEIROS_15_MINUTOS;
            
        } else if (minutes <= 60) {
            
            total += PRIMEIROS_60_MINUTOS;
            
        } else {
            
            long acr = minutes - 60;
            total += PRIMEIROS_60_MINUTOS;
            while (acr > 0) {
                total += ADICIONAL_15_MINUTOS;
                acr -= 15;
            }
            
        }

        return new BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN);
    }

    private static final double DESCONTO_PERCENTUAL = 0.30;
    
    public static BigDecimal calcularDesconto(BigDecimal custo, long numeroDeVezes) {
       
        BigDecimal desconto = ((numeroDeVezes > 0) && (numeroDeVezes % 10 == 0))
        ? custo.multiply(new BigDecimal(DESCONTO_PERCENTUAL)) : new BigDecimal(0);
        
        return desconto.setScale(2, RoundingMode.HALF_EVEN);
    }

}
