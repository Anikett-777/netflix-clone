package com.netflix.clone.dao;

import com.netflix.clone.entity.Vedio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoRepository extends JpaRepository<Vedio,Long> {

    @Query("SELECT v FROM Vedio v " +
            "WHERE LOWER(v.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(v.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Vedio> searchVideos(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(v) FROM Vedio v WHERE v.published = true")
    long countPublishedVideos();

    @Query("SELECT COALESCE(SUM(v.duration),0) FROM Vedio v")
    long totalDuration();



    @Query("""
    SELECT v FROM Vedio v
    WHERE v.published = true
      AND (
           :search IS NULL
        OR :search = ''
        OR LOWER(v.title) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(v.description) LIKE LOWER(CONCAT('%', :search, '%'))
      )
""")
    Page<Vedio> searchPublishedVideos( @Param("search")String trim, Pageable pageable);

    @Query("""
    SELECT v FROM Vedio v
    WHERE v.published = true
    ORDER BY v.createdAt DESC
""")
    Page<Vedio> findPublishedVideos(Pageable pageable);

    @Query("SELECT v FROM Vedio v WHERE v.published = true ORDER BY FUNCTION('RAND')")
    List<Vedio> findRandomPublishedVideos(Pageable pageable);
}
