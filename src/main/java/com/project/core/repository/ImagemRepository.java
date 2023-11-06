package com.project.core.repository;

import com.project.core.model.functional.Imagem;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

@Primary
public interface ImagemRepository extends JpaRepository<Imagem, Long> {
}

