package com.project.core.service;

import com.project.core.model.functional.Imagem;
import com.project.core.repository.ImagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImagemService {

    @Autowired
    private ImagemRepository imagemRepository;

    public void salvarImagem(byte[] conteudo) {
        Imagem imagem = new Imagem();
        imagem.setConteudo(conteudo);
        imagemRepository.save(imagem);
    }

    public Imagem carregarImagem(Long id) {
        return imagemRepository.findById(id).orElse(null);
    }
}
