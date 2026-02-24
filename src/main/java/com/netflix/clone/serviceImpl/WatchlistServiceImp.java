package com.netflix.clone.serviceImpl;

import com.netflix.clone.dao.UserRepository;
import com.netflix.clone.dao.VideoRepository;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.dto.response.PageResponse;
import com.netflix.clone.dto.response.VedioResponse;
import com.netflix.clone.entity.User;
import com.netflix.clone.entity.Vedio;
import com.netflix.clone.service.WatchlistService;
import com.netflix.clone.util.PaginationUtils;
import com.netflix.clone.util.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WatchlistServiceImp implements WatchlistService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ServiceUtils serviceUtils;


    @Override
    public MessageResponse addToWatchlist(String email, long videoId) {
        User user = serviceUtils.getUserByEmailOrThrow(email);
        Vedio video = serviceUtils.getVideoByIdOrThrow(videoId);

        user.addToWatchList(video);
        userRepository.save(user);
        return new MessageResponse("Video Added To watchlist successfully");
    }

    @Override
    public MessageResponse removeFromWatchList(String email, long videId) {
        User user =serviceUtils.getUserByEmailOrThrow(email);
        Vedio vedio = serviceUtils.getVideoByIdOrThrow(videId);

        user.removeFromWatchList(vedio);
        userRepository.save(user);
        return new MessageResponse("Video Removed From WatchList.");
    }

    @Override
    public PageResponse<VedioResponse> getWatchlistPaginated(String email, int page, int size, String search) {
        User user =serviceUtils.getUserByEmailOrThrow(email);

        Pageable pageable = PaginationUtils.createPageRequest(page,size);
        Page<Vedio> videoPage ;
        if(search!= null && !search.trim().isEmpty()){
            videoPage=userRepository.searchWatchlistByUserId(user.getId(),search.trim(),pageable);
        }else{
            videoPage=userRepository.findWatchlistByUserId(user.getId(),pageable);
        }
        return PaginationUtils.toPageResponse(videoPage,VedioResponse::fromEntity);
    }
}
