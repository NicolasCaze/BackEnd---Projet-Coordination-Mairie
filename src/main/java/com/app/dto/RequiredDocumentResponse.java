package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequiredDocumentResponse {
    private UUID groupId;
    private String groupName;
    private String groupType;
    private String exonerationType;
    private List<String> requiredDocuments;
}
