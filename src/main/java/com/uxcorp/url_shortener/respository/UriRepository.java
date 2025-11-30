package com.uxcorp.url_shortener.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uxcorp.url_shortener.model.UriModel;

public interface UriRepository extends JpaRepository<UriModel, Long> {
    UriModel findByMask(String mask);
}
