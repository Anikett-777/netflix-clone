package com.netflix.clone.util;


import com.netflix.clone.dao.UserRepository;
import com.netflix.clone.dao.VideoRepository;
import com.netflix.clone.entity.User;
import com.netflix.clone.entity.Vedio;
import com.netflix.clone.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceUtils {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    public User getUserByEmailOrThrow(String email){
        return userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User Not Found With Email : "+email));

    }

    public User getUserByIdOrThrow(long id){
       return userRepository
               .findById(id)
               .orElseThrow(()->new ResourceNotFoundException("User Not Found with id" + id));
    }

    public Vedio getVideoByIdOrThrow(long id){
        return videoRepository
                .findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Vedio Not Found with id" + id));
    }
}
