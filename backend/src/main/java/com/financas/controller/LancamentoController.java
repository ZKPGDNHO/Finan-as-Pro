package com.financas.controller;

import com.financas.dto.DashboardResponse;
import com.financas.dto.LancamentoRequest;
import com.financas.model.Lancamento;
import com.financas.model.TipoLancamento;
import com.financas.model.Usuario;
import com.financas.repository.LancamentoRepository;
import com.financas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller REST focado em processar lógica de Lançamentos Financeiros (Receitas e Despesas).
 * Todos os endpoints ficam expostos sob o path '/api/lancamentos'.
 */
@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Busca os dados da Dashboard de um usuário, calculando ativamente
     * o saldo atual, o total de receitas e o total de despesas, baseado no seu histórico.
     * @param usuarioId ID unvívoco do usuário.
     * @return Um objeto DashboardResponse em formato JSON para p frontend.
     */
    @GetMapping("/dashboard/{usuarioId}")
    public ResponseEntity<DashboardResponse> getDashboard(@PathVariable Long usuarioId) {
        List<Lancamento> lancamentos = lancamentoRepository.findByUsuarioId(usuarioId);
        
        BigDecimal totalReceitas = BigDecimal.ZERO;
        BigDecimal totalDespesas = BigDecimal.ZERO;

        for (Lancamento l : lancamentos) {
            if (l.getTipo() == TipoLancamento.RECEITA) {
                totalReceitas = totalReceitas.add(l.getValor());
            } else {
                totalDespesas = totalDespesas.add(l.getValor());
            }
        }

        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        DashboardResponse response = new DashboardResponse(saldo, totalReceitas, totalDespesas, lancamentos);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint responsável por cadastrar (salvar) um novo lançamento.
     * @param usuarioId ID que diz de quem é o lançamento
     * @param request Payload DTO contendo o tipo, valor, descricao e a data.
     * @return A Entidade salva (com ID gerado). Retorna 400 em caso de erro na lógica.
     */
    @PostMapping("/{usuarioId}")
    public ResponseEntity<?> criarLancamento(@PathVariable Long usuarioId, @RequestBody LancamentoRequest request) {
        Optional<Usuario> usuarioOp = usuarioRepository.findById(usuarioId);
        if (usuarioOp.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário não encontrado.");
        }

        Lancamento novoLancamento = new Lancamento(
                request.getTipo(),
                request.getValor(),
                request.getDescricao(),
                request.getData(),
                usuarioOp.get()
        );

        Lancamento salvo = lancamentoRepository.save(novoLancamento);
        return ResponseEntity.ok(salvo);
    }

    /**
     * Atualiza um lançamento existente.
     * @param id ID do lançamento a ser atualizado
     * @param request Novos dados do lançamento
     * @return O lançamento atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarLancamento(@PathVariable Long id, @RequestBody LancamentoRequest request) {
        Optional<Lancamento> lancamentoOp = lancamentoRepository.findById(id);
        if (lancamentoOp.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Lancamento lancamento = lancamentoOp.get();
        lancamento.setTipo(request.getTipo());
        lancamento.setValor(request.getValor());
        lancamento.setDescricao(request.getDescricao());
        lancamento.setData(request.getData());

        Lancamento salvo = lancamentoRepository.save(lancamento);
        return ResponseEntity.ok(salvo);
    }

    /**
     * Deleta um lançamento existente pelo ID.
     * @param id ID do lançamento
     * @return 204 No Content se sucesso, ou 404 se não encontrado.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarLancamento(@PathVariable Long id) {
        if (!lancamentoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lancamentoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retorna o Extrato/Relatório detalhado, listando todos os lançamentos
     * que um usuário possui atrelado à sua sessão.
     * @param usuarioId O ID do usuario na sessao local
     * @return Uma lista de objetos tipo Lancamento
     */
    @GetMapping("/relatorio/{usuarioId}")
    public ResponseEntity<List<Lancamento>> getRelatorio(@PathVariable Long usuarioId) {
        List<Lancamento> lancamentos = lancamentoRepository.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(lancamentos);
    }
}
