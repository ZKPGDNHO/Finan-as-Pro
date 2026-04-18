package com.financas.controller;

import com.financas.dto.AtualizarUsuarioRequest;
import com.financas.dto.CadastroRequest;
import com.financas.dto.LoginRequest;
import com.financas.model.Usuario;
import com.financas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller REST responsavel por ações de Autenticação (Login e Cadastro).
 * Como a aplicação não possui complexidade para tokens complexos (JWT), este controlador
 * manipula diretamente a validação dos dados de Usuario e sinaliza ao frontend via status OK e 401.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Rota POST utilizada para fazer Login. Verifica se o email atrelado
     * no banco de dados cruza corretamente com a senha recebida pela web.
     * @param request Payload DTO contendo email e a senha digitada
     * @return Retorna o objeto Usuario (desprovido de token/senha) se as credenciais baterem.
     *         Caso contrário, retorna statusCode 401.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOp = usuarioRepository.findByEmail(request.getEmail());
        
        if (usuarioOp.isPresent()) {
            Usuario usuario = usuarioOp.get();
            if (usuario.getSenha().equals(request.getSenha())) {
                // In a real app, you'd generate a JWT token.
                // Here, for local/simple usage, we can just return the user object (avoiding password) or their ID.
                return ResponseEntity.ok(usuario);
            }
        }
        return ResponseEntity.status(401).body("Email ou Senha inválidos.");
    }

    /**
     * Rota de Cadastro. Permite o apontamento de um novo usuário para o sistema SQLite
     * garantindo que não duplicaremos contatos com e-mail já existente.
     * @param request Payload DTO de Cadastro requistando nome, email e senha.
     * @return Response status ok contento o usuário novo
     */
    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@RequestBody CadastroRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }

        Usuario novoUsuario = new Usuario(request.getEmail(), request.getSenha(), request.getNome());
        Usuario salvo = usuarioRepository.save(novoUsuario);
        return ResponseEntity.ok(salvo);
    }

    /**
     * Rota de Busca. Retorna as informações do usuário atual pelo seu ID.
     * @param id O identificador do usuário
     * @return Response status ok contento o usuário 
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioInfo(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Rota de Atualização (Perfil). Retorna as novas informações persistidas.
     * @param id O identificador do usuário
     * @param request Dados atualizados do usuário (nome, email, senha, foto)
     * @return Response status ok contento o usuário atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody AtualizarUsuarioRequest request) {
        Optional<Usuario> usuarioOp = usuarioRepository.findById(id);
        if (usuarioOp.isPresent()) {
            Usuario usuario = usuarioOp.get();
            if (request.getNome() != null && !request.getNome().trim().isEmpty()) {
                usuario.setNome(request.getNome());
            }
            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                usuario.setEmail(request.getEmail());
            }
            if (request.getSenha() != null && !request.getSenha().trim().isEmpty()) {
                usuario.setSenha(request.getSenha());
            }
            if (request.getFoto() != null) {
                usuario.setFoto(request.getFoto());
            }
            
            Usuario salvo = usuarioRepository.save(usuario);
            return ResponseEntity.ok(salvo);
        }
        return ResponseEntity.notFound().build();
    }
}
