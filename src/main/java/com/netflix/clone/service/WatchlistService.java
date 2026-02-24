package com.netflix.clone.service;

import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.dto.response.PageResponse;
import com.netflix.clone.dto.response.VedioResponse;

public interface WatchlistService {
    MessageResponse addToWatchlist(String email, long videoId);

    MessageResponse removeFromWatchList(String email, long videId);

    PageResponse<VedioResponse> getWatchlistPaginated(String email, int page, int size, String search);
}
