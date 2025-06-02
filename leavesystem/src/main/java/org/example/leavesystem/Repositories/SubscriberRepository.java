package org.example.leavesystem.Repositories;

import org.example.leavesystem.Models.SubscriberModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriberRepository extends JpaRepository<SubscriberModel,Long> {
    boolean existsByEmail(String email);
}
