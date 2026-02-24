package com.netflix.clone.controller;

import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.dto.response.PageResponse;
import com.netflix.clone.dto.response.VedioResponse;
import com.netflix.clone.service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    @PostMapping("/{videoId}")
    public ResponseEntity<MessageResponse> addToWatchlist(@PathVariable long videoId , Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(watchlistService.addToWatchlist(email,videoId));
    }

    @DeleteMapping("/{videId}")
    public  ResponseEntity<MessageResponse> removeFromWatchlist(@PathVariable long videId, Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(watchlistService.removeFromWatchList(email,videId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<VedioResponse>> getWatchlsi(
            @RequestParam (defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Authentication authentication
    ){
        String email =authentication.getName();

        PageResponse<VedioResponse> response = watchlistService.getWatchlistPaginated(email,page,size,search);
        return ResponseEntity.ok(response);
    }
}
