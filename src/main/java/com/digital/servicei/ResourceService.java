package com.digital.servicei;

import com.digital.dto.ResourceRequest;
import com.digital.dto.ResourceResponse;
import org.apache.coyote.BadRequestException;

public interface ResourceService {
    ResourceResponse createResource(Long uploadedBy, ResourceRequest request) throws BadRequestException;
    ResourceResponse getResource(Long resourceId);
    ResourceResponse updateResource(Long resourceId, ResourceRequest request);
    void deleteResource(Long resourceId);
}
