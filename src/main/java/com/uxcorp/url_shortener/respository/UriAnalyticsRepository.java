package com.uxcorp.url_shortener.respository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.uxcorp.url_shortener.model.UriAnalyticsModel;

public interface UriAnalyticsRepository extends JpaRepository<UriAnalyticsModel, Long> {
    List<UriAnalyticsModel> findAllByMask(String mask);

    @Query("SELECT b.uri, a.country, COUNT(a) FROM UriModel b,UriAnalyticsModel a WHERE a.mask = b.mask and b.mask = :mask GROUP BY a.country order by COUNT(a) desc")
    List<Object[]> countByCountryWithMask(@Param("mask") String mask);

}
