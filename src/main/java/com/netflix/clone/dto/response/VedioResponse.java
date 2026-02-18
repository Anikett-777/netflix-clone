package com.netflix.clone.dto.response;

import com.netflix.clone.entity.Vedio;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VedioResponse {

    private long id;
    private String title;
    private String description;
    private Integer year;
    private String rating;
    private Integer duration;
    private String src;
    private String poster;
    private boolean published;
    private List<String> categories;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isInWatchList;

    public VedioResponse(Long id,
                         String title,
                         String description,
                         Integer year,
                         String rating,
                         Integer duration,
                         String src,
                         String poster,
                         boolean published,
                         List<String> categories,
                         Instant createdAt,
                         Instant updatedAt,
                         boolean isInWatchList){

        this.id=id;
        this.title=title;
        this.description=description;
        this.year=year;
        this.rating=rating;
        this.duration=duration;
        this.src=src;
        this.poster=poster;
        this.published=published;
        this.categories=categories;
        this.createdAt=createdAt;
        this.updatedAt=updatedAt;
        this.isInWatchList=isInWatchList;
    }

    public static VedioResponse fromEntity(Vedio vedio){

        VedioResponse response = new VedioResponse(
                vedio.getId(),
                vedio.getTitle(),
                vedio.getDescription(),
                vedio.getYear(),
                vedio.getRating(),
                vedio.getDuration(),
                vedio.getSrc(),
                vedio.getPoster(),
                vedio.isPublished(),
                vedio.getCategories(),
                vedio.getCreatedAt(),
                vedio.getUpdatedAt(),
                vedio.isInWatchlist()
        );

        if(vedio.isInWatchlist()){
            response.setInWatchList(vedio.isInWatchlist());
        }
        return response;
    }
}
