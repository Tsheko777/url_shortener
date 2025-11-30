package com.uxcorp.url_shortener.service;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.uxcorp.url_shortener.dto.uri.NewUrIDTO;
import com.uxcorp.url_shortener.dto.uri.UpdateMaskDTO;
import com.uxcorp.url_shortener.dto.uri.UpdateUriDTO;
import com.uxcorp.url_shortener.model.UriAnalyticsModel;
import com.uxcorp.url_shortener.model.UriModel;
import com.uxcorp.url_shortener.respository.UriAnalyticsRepository;
import com.uxcorp.url_shortener.respository.UriRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class URIService {
    private final UriRepository uriRepository;
    private final UriAnalyticsRepository uriAnalyticsRepository;

    @Cacheable(value = "URI_INFO", key = "#mask", unless = "#result == null")
    public UriModel getUri(String mask) {
        UriModel uri = uriRepository.findByMask(mask);
        if (uri != null) {
            return uri;
        }
        return null;
    }

    @CachePut(value = "URI_INFO", key = "#result.mask", unless = "#result == null")
    public UriModel newUri(NewUrIDTO uriReq) {
        UriModel uri = new UriModel();
        uri.setMask(generateMask());
        uri.setUri(uriReq.getUri());
        uriRepository.save(uri);
        return uri;
    }

    public List<UriAnalyticsModel> updateUriMask(@NotBlank String oldMask, UpdateMaskDTO newMask) {
        UriModel uri = uriRepository.findByMask(oldMask);
        List<UriAnalyticsModel> analytics = uriAnalyticsRepository.findAllByMask(oldMask);

        if (uri != null) {
            UriModel taken = uriRepository.findByMask(newMask.getMask());
            if (taken != null)
                return null;

            if (analytics != null) {
                for (UriAnalyticsModel analytic : analytics) {
                    analytic.setMask(newMask.getMask());
                }
                uriAnalyticsRepository.saveAll(analytics);
            }
            uri.setMask(newMask.getMask());
            uriRepository.save(uri);
            evictCache(oldMask);
            putCache(uri);
            return analytics;
        }
        return null;
    }

    @CachePut(value = "URI_INFO", key = "#mask", unless = "#result == null")
    public UriModel updateUri(@NotBlank String mask, UpdateUriDTO uriDto) {
        UriModel uri = uriRepository.findByMask(mask);
        if (uri != null) {
            uri.setUri(uriDto.getUri());
            uriRepository.save(uri);
            return uri;
        }
        return null;
    }

    @CacheEvict(value = "URI_INFO", key = "#mask")
    public UriModel deleteUri(@NotBlank String mask) {
        UriModel uri = uriRepository.findByMask(mask);
        if (uri != null) {
            uriRepository.delete(uri);
            return uri;
        }
        return null;
    }

    public String generateMask() {
        final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        final int LENGTH = 8;
        while (true) {
            StringBuilder mask = new StringBuilder(LENGTH);
            for (int i = 0; i < LENGTH; i++) {
                mask.append(chars.charAt(random.nextInt(chars.length())));
            }
            String candidate = mask.toString();
            if (uriRepository.findByMask(candidate) == null) {
                return candidate;
            }
        }
    }

    @CacheEvict(value = "URI_INFO", key = "#mask")
    public void evictCache(String mask) {
    }

    @CachePut(value = "URI_INFO", key = "#uri.mask")
    public UriModel putCache(UriModel uri) {
        return uri;
    }

}
