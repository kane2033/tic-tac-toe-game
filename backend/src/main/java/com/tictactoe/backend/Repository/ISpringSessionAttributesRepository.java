package com.tictactoe.backend.Repository;

import com.tictactoe.backend.Entity.SpringSessionAttributesEntity;
import org.springframework.data.repository.CrudRepository;

public interface ISpringSessionAttributesRepository extends CrudRepository<SpringSessionAttributesEntity, Long> {
    boolean existsSpringSessionAttributesEntityByAttributeBytes(byte[] bytes);
}
