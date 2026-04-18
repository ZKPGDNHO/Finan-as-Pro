package com.financas.dto;

import com.financas.model.TipoLancamento;
import java.math.BigDecimal;
import java.time.LocalDate;

public class LancamentoRequest {
    private TipoLancamento tipo;
    private BigDecimal valor;
    private String descricao;
    private LocalDate data;

    // Getters and Setters
    public TipoLancamento getTipo() { return tipo; }
    public void setTipo(TipoLancamento tipo) { this.tipo = tipo; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
}
