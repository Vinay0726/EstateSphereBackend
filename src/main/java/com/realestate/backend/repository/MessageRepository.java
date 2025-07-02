package com.realestate.backend.repository;


import com.realestate.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<com.realestate.backend.model.Message, Long> {
}
