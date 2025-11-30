package com.uxcorp.url_shortener.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.uxcorp.url_shortener.model.UriAnalyticsModel;
import com.uxcorp.url_shortener.respository.UriAnalyticsRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UriAnalyticsService {

    @Value("${app.countryApi}")
    private String countryApiUrl;

    private final UriAnalyticsRepository uriAnalyticsRepository;

    @Async
    @CacheEvict(value = "URI_INFO_ANALYTICS", key = "#mask")
    public void saveAnalytics(String mask) {
        String country = this.getCountry();
        UriAnalyticsModel analytics = new UriAnalyticsModel();
        analytics.setCountry(country);
        analytics.setMask(mask);
        uriAnalyticsRepository.save(analytics);
    }

    @Cacheable(value = "URI_INFO_ANALYTICS", key = "#mask", unless = "#result == null")
    public List<Map<String, Object>> getUriAnalytics(@NotBlank String mask) {
        List<Object[]> analytics = uriAnalyticsRepository.countByCountryWithMask(mask);
        if (!analytics.isEmpty()) {
            List<Map<String, Object>> response = analytics.stream()
                    .map(row -> Map.of("uri", row[0], "country", row[1], "count", row[2]))
                    .collect(Collectors.toList());
            return response;
        }
        return null;
    }

    String getCountry() {
        RestTemplate restTemplate = new RestTemplate();
        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.getForObject(this.countryApiUrl, Map.class);
        String country = (String) response.get("country");
        return country != null ? country : "Unknown";
    }
}
