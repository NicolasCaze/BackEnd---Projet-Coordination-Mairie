package com.app.controller;

import com.app.dto.DocumentRuleRequest;
import com.app.dto.DocumentRuleResponse;
import com.app.service.DocumentRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/document-rules")
@RequiredArgsConstructor
public class DocumentRuleController {

    private final DocumentRuleService documentRuleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DocumentRuleResponse>> getAllRules() {
        return ResponseEntity.ok(documentRuleService.getAllRules());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DocumentRuleResponse> getRuleById(@PathVariable Long id) {
        return ResponseEntity.ok(documentRuleService.getRuleById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DocumentRuleResponse> createRule(@RequestBody DocumentRuleRequest request) {
        return ResponseEntity.ok(documentRuleService.createRule(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DocumentRuleResponse> updateRule(@PathVariable Long id, @RequestBody DocumentRuleRequest request) {
        return ResponseEntity.ok(documentRuleService.updateRule(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        documentRuleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
