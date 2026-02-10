package com.netflix.clone.dao;

import com.netflix.clone.entity.Vedio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Vedio,Long> {
}
