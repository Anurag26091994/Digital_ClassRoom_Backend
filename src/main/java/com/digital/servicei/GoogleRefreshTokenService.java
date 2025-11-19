package com.digital.servicei;

import com.digital.google_meet_config.GoogleRefreshToken;

public interface GoogleRefreshTokenService {

    void saveRefreshToken(GoogleRefreshToken token);

    String getRefreshToken(Long teacherId);
}
