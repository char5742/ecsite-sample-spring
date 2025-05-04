package com.example.ec_2024b_back.share.domain.services;

import java.time.LocalDateTime;

/** 時刻を取得するためのインターフェース */
public interface TimeProvider {
  LocalDateTime now();
}
