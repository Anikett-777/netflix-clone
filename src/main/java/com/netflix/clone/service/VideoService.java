package com.netflix.clone.service;

import com.netflix.clone.dto.request.VedioRequest;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.dto.response.PageResponse;
import com.netflix.clone.dto.response.VedioResponse;
import com.netflix.clone.dto.response.VedioStatsResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface VideoService {
    MessageResponse createVideoByAdmin(@Valid VedioRequest vedioRequest);

    PageResponse<VedioResponse> getAllAdminVideos(int page, int size, String search);

    MessageResponse updateVedioByAdmin(long id, @Valid VedioRequest vedioRequest);

    MessageResponse deleteVideoByAdmin(long id);

    MessageResponse toggleVideoPublishStatusByAdmin(long id, boolean value);

    VedioStatsResponse getAdminStats();

    PageResponse<VedioResponse> getPublishedVideos(int page, int size, String search,String email);

    List<VedioResponse> getFeaturedVideos();
}
