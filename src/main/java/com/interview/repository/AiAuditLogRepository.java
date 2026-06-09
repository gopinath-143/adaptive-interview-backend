package com.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interview.entity.AiAuditLog;

public interface AiAuditLogRepository
extends JpaRepository<AiAuditLog, Long> {
}
