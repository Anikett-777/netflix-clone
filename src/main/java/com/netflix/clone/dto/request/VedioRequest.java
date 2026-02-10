package com.netflix.clone.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class VedioRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @Size(max=4000,message = "Description must not Exceed 4000 character ")
    private String Description;

    private Integer year;
    private Integer duration;
    private String rating;
    private String src;
    private String poster;
    private boolean published;
    private List<String> categories;
}
