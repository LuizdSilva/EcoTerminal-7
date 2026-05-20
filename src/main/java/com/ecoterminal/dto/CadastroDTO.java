package com.ecoterminal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroDTO(

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    String nome,

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    String email,

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 50, message = "Senha deve ter no mínimo 6 caracteres")
    String senha,

    @NotBlank(message = "Confirmação de senha é obrigatória")
    String confirmarSenha

) {
    public boolean senhasConferem() {
        return senha != null && senha.equals(confirmarSenha);
    }
}
