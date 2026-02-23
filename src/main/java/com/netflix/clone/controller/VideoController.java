package com.netflix.clone.controller;

import com.netflix.clone.dto.request.VedioRequest;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.dto.response.PageResponse;
import com.netflix.clone.dto.response.VedioResponse;
import com.netflix.clone.dto.response.VedioStatsResponse;
import com.netflix.clone.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<MessageResponse> createVideoByAdmin(@Valid @RequestBody VedioRequest vedioRequest){
        return ResponseEntity.ok(videoService.createVideoByAdmin(vedioRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<PageResponse<VedioResponse>> getAllAdminVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search

    ){
        return ResponseEntity.ok(videoService.getAllAdminVideos(page,size,search));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<MessageResponse> updateVideoByAdmin(@PathVariable long id,@Valid @RequestBody VedioRequest vedioRequest){
        return ResponseEntity.ok(videoService.updateVedioByAdmin(id,vedioRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public  ResponseEntity<MessageResponse> deleteVideoByAdmin(@PathVariable long id){
        return ResponseEntity.ok(videoService.deleteVideoByAdmin(id));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/{id}/publish")
    public ResponseEntity<MessageResponse> toggleVideoPublishStatusByAdmin(@PathVariable long id, @RequestParam boolean value){
        return ResponseEntity.ok(videoService.toggleVideoPublishStatusByAdmin(id,value));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/stats")
    public ResponseEntity<VedioStatsResponse> getAdminStats(){
        return ResponseEntity.ok(videoService.getAdminStats());
    }


    @GetMapping("/published")
    public ResponseEntity<PageResponse<VedioResponse>> getPublishedVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10")int size,
            @RequestParam (required = false) String search,
            Authentication authentication
    ) {
        String email = authentication.getName();
        PageResponse<VedioResponse> response = videoService.getPublishedVideos(page,size,search,email);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/featured")
    public ResponseEntity<List<VedioResponse>>getFeaturedVideos(){
        List<VedioResponse> responses = videoService.getFeaturedVideos();
        return ResponseEntity.ok(responses);
    }
}
