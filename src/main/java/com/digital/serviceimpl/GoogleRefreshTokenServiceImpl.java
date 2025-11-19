package com.digital.serviceimpl;

import com.digital.exception.ResourceNotFoundException;
import com.digital.google_meet_config.GoogleRefreshToken;
import com.digital.repository.GoogleRefreshTokenRepository;
import com.digital.servicei.GoogleRefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GoogleRefreshTokenServiceImpl implements GoogleRefreshTokenService {

    private final GoogleRefreshTokenRepository repository;

    @Override
    public void saveRefreshToken(GoogleRefreshToken token) {
        repository.save(token);
    }

    @Override
    public String getRefreshToken(Long teacherId) {

        GoogleRefreshToken token = repository.findByTeacher_Id(teacherId).orElseThrow(() ->
                new ResourceNotFoundException("Refresh token with teacherId: " + " not found in database."));

        return token.getRefreshToken();
    }
}
