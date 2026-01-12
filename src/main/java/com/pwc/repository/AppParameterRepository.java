package com.pwc.repository;

import com.pwc.model.AppParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AppParameterRepository extends JpaRepository<AppParameter, Long> {
    Optional<AppParameter> findByParamKey(String paramKey);
}


