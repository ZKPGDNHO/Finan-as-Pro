package com.financas.dto;

import com.financas.model.Lancamento;
import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {
    private BigDecimal saldo;
    private BigDecimal totalReceitas;
    private BigDecimal totalDespesas;
    private List<Lancamento> ultimosLancamentos;

    public DashboardResponse(BigDecimal saldo, BigDecimal totalReceitas, BigDecimal totalDespesas, List<Lancamento> ultimosLancamentos) {
        this.saldo = saldo;
        this.totalReceitas = totalReceitas;
        this.totalDespesas = totalDespesas;
        this.ultimosLancamentos = ultimosLancamentos;
    }

    public BigDecimal getSaldo() { return saldo; }
    public BigDecimal getTotalReceitas() { return totalReceitas; }
    public BigDecimal getTotalDespesas() { return totalDespesas; }
    public List<Lancamento> getUltimosLancamentos() { return ultimosLancamentos; }
}
