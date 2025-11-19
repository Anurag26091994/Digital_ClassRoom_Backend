package com.digital.servicei;


import com.digital.dto.PTMRequest;
import com.digital.dto.PTMResponse;

import java.util.List;

public interface PTMService {
    PTMResponse schedulePTM(PTMRequest request);
    List<PTMResponse> getAllPTMs();
}
