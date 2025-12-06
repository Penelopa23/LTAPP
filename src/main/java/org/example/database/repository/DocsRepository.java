package org.example.database.repository;

import io.micrometer.core.annotation.Timed;
import org.example.database.entity.DocEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocsRepository extends CrudRepository<DocEntity, Integer> {
    @Timed("findDoc")
    List<DocEntity> findByName(String name);
}
