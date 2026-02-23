package com.netflix.clone.serviceImpl;

import com.netflix.clone.dao.UserRepository;
import com.netflix.clone.dao.VideoRepository;
import com.netflix.clone.dto.request.VedioRequest;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.dto.response.PageResponse;
import com.netflix.clone.dto.response.VedioResponse;
import com.netflix.clone.dto.response.VedioStatsResponse;
import com.netflix.clone.entity.Vedio;
import com.netflix.clone.service.VideoService;
import com.netflix.clone.util.PaginationUtils;
import com.netflix.clone.util.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.support.PageableUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class VideoServiceImpl implements VideoService {


    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceUtils serviceUtils;


    @Override
    public MessageResponse createVideoByAdmin(VedioRequest vedioRequest) {
        Vedio vedio = new Vedio();
        vedio.setTitle(vedioRequest.getTitle());
        vedio.setDescription(vedioRequest.getDescription());
        vedio.setYear(vedioRequest.getYear());
        vedio.setRating(vedioRequest.getRating());
        vedio.setDuration(vedioRequest.getDuration());
        vedio.setSrcUuid(vedioRequest.getSrc());
        vedio.setPosterUuid(vedioRequest.getPoster());
        vedio.setPublished(vedioRequest.isPublished());
        vedio.setCategories(vedioRequest.getCategories()!=null ? vedioRequest.getCategories() : List.of());
        videoRepository.save(vedio);

        return new MessageResponse("Video Created Successfully..");
    }

    @Override
    public PageResponse<VedioResponse> getAllAdminVideos(int page, int size, String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page,size,"id");
        Page<Vedio> vedioPage;

        if(search != null && !search.trim().isEmpty()){
            vedioPage = videoRepository.searchVideos(search.trim(),pageable);
        }else {
            vedioPage = videoRepository.findAll(pageable);
        }
        return PaginationUtils.toPageResponse(vedioPage, VedioResponse::fromEntity);
    }

    @Override
    public MessageResponse updateVedioByAdmin(long id, VedioRequest vedioRequest) {
        Vedio video = new Vedio();
        video.setId(id);
        video.setTitle(vedioRequest.getTitle());
        video.setDescription(vedioRequest.getDescription());
        video.setYear(vedioRequest.getYear());
        video.setRating(vedioRequest.getRating());
        video.setDuration(vedioRequest.getDuration());
        video.setSrcUuid(vedioRequest.getSrc());
        video.setPosterUuid(vedioRequest.getPoster());
        video.setCategories(vedioRequest.getCategories()!= null ? vedioRequest.getCategories() : List.of() );
        videoRepository.save(video);
        return new MessageResponse("Video Updated Successfully");
    }

    @Override
    public MessageResponse deleteVideoByAdmin(long id) {
        if(!videoRepository.existsById(id)){
            throw new IllegalArgumentException("Video Not Found : "+ id);
        }
        videoRepository.deleteById(id);
        return new MessageResponse("Video Deleted Successfully");
    }

    @Override
    public MessageResponse toggleVideoPublishStatusByAdmin(long id, boolean status) {
        Vedio vedio = serviceUtils.getVideoByIdOrThrow(id);
        vedio.setPublished(status);
        videoRepository.save(vedio);

        return new MessageResponse("Video Published Status Updated Successfully..");
    }

    @Override
    public VedioStatsResponse getAdminStats() {
        long totalVideos = videoRepository.count();
        long publishedVideos = videoRepository.countPublishedVideos();
        long totalDuration = videoRepository.totalDuration();

        return new VedioStatsResponse(totalVideos,publishedVideos,totalDuration);
    }

    @Override
    public PageResponse<VedioResponse> getPublishedVideos(int page, int size, String search,String email) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size,"id");
        Page<Vedio> videoPage ;

        if(search != null && !search.trim().isEmpty()){
            videoPage= videoRepository.searchPublishedVideos(search.trim(),pageable);
        }else {
            videoPage = videoRepository.findPublishedVideos(pageable);
        }
        List<Vedio> videos = videoPage.getContent();

        Set<Long> watchListIds = Set.of();
        if(!videos.isEmpty()){
                try {
                    List<Long> videoIds = videos.stream().map(Vedio::getId).toList();
                    watchListIds = userRepository.findWatchlistVideoIds(email,videoIds);

                } catch (Exception e) {
                    watchListIds = Set.of();
                }
        }
        Set<Long> finalWatchListIds = watchListIds;
        videos.forEach(vedio -> vedio.setInWatchlist(finalWatchListIds.contains(vedio.getId())));
        List<VedioResponse> vedioResponses = videos.stream().map(VedioResponse::fromEntity).toList();
         return PaginationUtils.toPageResponse(videoPage,vedioResponses) ;
    }

    @Override
    public List<VedioResponse> getFeaturedVideos() {
        Pageable pageable = PageRequest.of(0,5);
        List<Vedio> videos = videoRepository.findRandomPublishedVideos(pageable);
        return videos.stream().map(VedioResponse::fromEntity).toList();
    }

}
