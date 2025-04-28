package com.example.ec_2024b_back.auth.domain.models;

import com.example.ec_2024b_back.share.domain.models.Email;

public record EmailAuthentication(AuthenticationType type, Email email, String password)
    implements Authentication {}
