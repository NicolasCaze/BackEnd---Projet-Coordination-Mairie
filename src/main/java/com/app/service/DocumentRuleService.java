package com.app.service;

import com.app.dto.DocumentRuleRequest;
import com.app.dto.DocumentRuleResponse;
import com.app.dto.RequiredDocumentResponse;
import com.app.entity.DocumentRule;
import com.app.entity.Groupe;
import com.app.repository.DocumentRuleRepository;
import com.app.repository.GroupeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentRuleService {

    private final DocumentRuleRepository documentRuleRepository;
    private final GroupeRepository groupeRepository;

    public List<DocumentRuleResponse> getAllRules() {
        return documentRuleRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public DocumentRuleResponse getRuleById(Long id) {
        DocumentRule rule = documentRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document rule not found with id: " + id));
        return convertToResponse(rule);
    }

    public DocumentRuleResponse createRule(DocumentRuleRequest request) {
        DocumentRule rule = DocumentRule.builder()
                .typeGroupe(request.getTypeGroupe())
                .typeExoneration(request.getTypeExoneration())
                .document(request.getDocument())
                .description(request.getDescription())
                .build();
        
        DocumentRule savedRule = documentRuleRepository.save(rule);
        log.info("Created document rule: {} for typeGroupe: {}, typeExoneration: {}", 
                savedRule.getDocument(), savedRule.getTypeGroupe(), savedRule.getTypeExoneration());
        
        return convertToResponse(savedRule);
    }

    public DocumentRuleResponse updateRule(Long id, DocumentRuleRequest request) {
        DocumentRule existingRule = documentRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document rule not found with id: " + id));
        
        existingRule.setTypeGroupe(request.getTypeGroupe());
        existingRule.setTypeExoneration(request.getTypeExoneration());
        existingRule.setDocument(request.getDocument());
        existingRule.setDescription(request.getDescription());
        
        DocumentRule updatedRule = documentRuleRepository.save(existingRule);
        log.info("Updated document rule: {}", updatedRule.getId());
        
        return convertToResponse(updatedRule);
    }

    public void deleteRule(Long id) {
        if (!documentRuleRepository.existsById(id)) {
            throw new RuntimeException("Document rule not found with id: " + id);
        }
        
        documentRuleRepository.deleteById(id);
        log.info("Deleted document rule: {}", id);
    }

    public RequiredDocumentResponse getRequiredDocumentsForGroup(UUID groupId) {
        Groupe groupe = groupeRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        
        List<DocumentRule> rules = documentRuleRepository.findByTypeGroupeAndTypeExoneration(
                groupe.getType_groupe(), groupe.getType_exoneration());
        
        List<String> requiredDocuments = rules.stream()
                .map(DocumentRule::getDocument)
                .collect(Collectors.toList());
        
        return RequiredDocumentResponse.builder()
                .groupId(groupId)
                .groupName(groupe.getNom())
                .groupType(groupe.getType_groupe() != null ? groupe.getType_groupe().toString() : null)
                .exonerationType(groupe.getType_exoneration() != null ? groupe.getType_exoneration().toString() : null)
                .requiredDocuments(requiredDocuments)
                .build();
    }

    private DocumentRuleResponse convertToResponse(DocumentRule rule) {
        return DocumentRuleResponse.builder()
                .id(rule.getId())
                .typeGroupe(rule.getTypeGroupe())
                .typeExoneration(rule.getTypeExoneration())
                .document(rule.getDocument())
                .description(rule.getDescription())
                .creer_le(rule.getCreer_le())
                .build();
    }
}
