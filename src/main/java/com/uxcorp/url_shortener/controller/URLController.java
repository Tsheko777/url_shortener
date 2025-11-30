package com.uxcorp.url_shortener.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.uxcorp.url_shortener.dto.uri.NewUrIDTO;
import com.uxcorp.url_shortener.dto.uri.UpdateMaskDTO;
import com.uxcorp.url_shortener.dto.uri.UpdateUriDTO;
import com.uxcorp.url_shortener.model.UriAnalyticsModel;
import com.uxcorp.url_shortener.model.UriModel;
import com.uxcorp.url_shortener.service.URIService;
import com.uxcorp.url_shortener.service.UriAnalyticsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/url")
@RequiredArgsConstructor
public class URLController {

    private final URIService uriService;
    private final UriAnalyticsService uriAnalyticsService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUri(@PathVariable String id) {
        UriModel uri = uriService.getUri(id);
        if (uri != null) {
            this.uriAnalyticsService.saveAnalytics(uri.getMask());
            return ResponseEntity.ok(uri);
        }
        return ResponseEntity.status(404).body(Map.of("message", "Uri not found"));
    }

    @GetMapping("/analytics/{mask}")
    public ResponseEntity<?> getUriAnalytics(@PathVariable String mask) {
        List<Map<String, Object>> analytics = uriAnalyticsService.getUriAnalytics(mask);
        if (analytics != null) {
            return ResponseEntity.ok(analytics);
        }
        return ResponseEntity.status(404).body(Map.of("message", "No analytics found"));
    }

    @PostMapping("/new")
    public ResponseEntity<?> newUri(@RequestBody @Valid NewUrIDTO uri) {
        UriModel createdUri = uriService.newUri(uri);
        return ResponseEntity.ok(createdUri);
    }

    @PatchMapping("/update/mask/{id}")
    public ResponseEntity<?> updateUriMask(@PathVariable String id, @RequestBody @Valid UpdateMaskDTO newMask) {
        List<UriAnalyticsModel> analytics = uriService.updateUriMask(id, newMask);
        if (analytics != null) {
            return ResponseEntity.ok(analytics);
        }
        return ResponseEntity.status(404).body(Map.of("message", "Uri not found"));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateUri(@PathVariable String id, @RequestBody @Valid UpdateUriDTO uri) {
        UriModel updatedUri = uriService.updateUri(id, uri);
        if (updatedUri != null) {
            return ResponseEntity.ok(updatedUri);
        }
        return ResponseEntity.status(404).body(Map.of("message", "Uri not found"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUri(@PathVariable String id) {
        UriModel deletedUri = uriService.deleteUri(id);
        if (deletedUri != null) {
            return ResponseEntity.ok(deletedUri);
        }
        return ResponseEntity.status(404).body(Map.of("message", "Uri not found"));
    }
}
