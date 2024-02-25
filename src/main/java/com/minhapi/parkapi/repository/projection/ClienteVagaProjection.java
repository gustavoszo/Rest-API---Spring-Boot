package com.minhapi.parkapi.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ClienteVagaProjection {
    
    String getPlaca();
    String getMarca();
    String getModelo();
    String getCor();
    String getClienteCpf();
    String getRecibo();
    @JsonFormat(pattern = "yyyy-mm-dd hh:mm:dd")
    LocalDateTime getDataEntrada();
    @JsonFormat(pattern = "yyyy-mm-dd hh:mm:dd")
    LocalDateTime getDataSaida();
    String getVagaCodigo();
    BigDecimal getValor();
    BigDecimal getDesconto();

}
