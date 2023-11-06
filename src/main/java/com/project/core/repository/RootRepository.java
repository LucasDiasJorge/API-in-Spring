package com.project.core.repository;

import com.project.core.model.AbstractModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RootRepository extends JpaRepository<AbstractModel, Long> {

}
