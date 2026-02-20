package com.netflix.clone.serviceImpl;

import com.netflix.clone.dao.UserRepository;
import com.netflix.clone.dto.request.UserRequest;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.dto.response.PageResponse;
import com.netflix.clone.dto.response.UserResponse;
import com.netflix.clone.entity.User;
import com.netflix.clone.enums.Role;
import com.netflix.clone.exceptions.EmailAlreadyExistsException;
import com.netflix.clone.exceptions.InvalidRoleException;
import com.netflix.clone.service.EmailService;
import com.netflix.clone.service.UserService;
import com.netflix.clone.util.PaginationUtils;
import com.netflix.clone.util.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ServiceUtils serviceUtils;

    @Autowired
    private EmailService emailService;


    @Override
    public MessageResponse createUser(UserRequest userRequest) {
        if(userRepository.findByEmail(userRequest.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException("Email Already Exists");
        }
        validateRole(userRequest.getRole());
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFullName(userRequest.getFullName());
        user.setRole(Role.valueOf(userRequest.getRole().toUpperCase()));
        user.setActive(true);
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));
        userRepository.save(user);
        emailService.sendVerificationEmail(userRequest.getEmail(),verificationToken);
        return new MessageResponse("User Created Successfully..");
    }

    private void validateRole(String role) {
        if(Arrays.stream(Role.values()).noneMatch(r->r.name().equalsIgnoreCase(role))){
            throw new InvalidRoleException("Invalid Role : "+ role);
        }
    }

    @Override
    public MessageResponse updateUser(Long id, UserRequest userRequest) {
        User user = serviceUtils.getUserByIdOrThrow(id);
        ensureNotLastActiveAdmin(user);
        validateRole(userRequest.getRole());

        user.setFullName(userRequest.getFullName());
        user.setRole(Role.valueOf(userRequest.getRole().toUpperCase()));
        userRepository.save(user);
        return new MessageResponse("User Updated Successfully..");
    }

    private void ensureNotLastActiveAdmin(User user) {
        if(user.isActive() && user.getRole()==Role.ADMIN){
            long activeAdminCount = userRepository.countByRoleAndActive(Role.ADMIN,true);
            if(activeAdminCount<=1){
                throw new RuntimeException("Cannot Deactivate The Last active admin user..");
            }
        }
    }

    @Override
    public PageResponse<UserResponse> getUsers(int page, int size, String search) {

        Pageable pageable = PaginationUtils.createPageRequest(page, size,"id");
        Page<User> userPage;
        if(search!= null && search.trim().isEmpty()){
            userPage =userRepository.searchUser(search.trim(),pageable);
        }else {
            userPage= userRepository.findAll(pageable);
        }
        return PaginationUtils.toPageResponse(userPage,UserResponse :: fromEntity);
    }

    @Override
    public MessageResponse deleteUser(Long id, String currentUserEmail) {
        User user = serviceUtils.getUserByIdOrThrow(id);
        if(user.getEmail().equals(currentUserEmail)){
            throw new RuntimeException("You cannot Delete Your own Account..");
        }
        ensureNotLastAdmin(user,"delete");
        userRepository.deleteById(id);

        return new MessageResponse("User Deleted Successfully..");
    }

    private void ensureNotLastAdmin(User user, String operation) {
        if(user.getRole()==Role.ADMIN){
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if(adminCount<=1){
                throw new RuntimeException("Cannot "+ operation +"the last admin user");
            }
        }
    }

    @Override
    public MessageResponse toggleUserStatus(long id, String currentUserEmail) {
        User user = serviceUtils.getUserByIdOrThrow(id);
        if(user.getEmail().equals(currentUserEmail)){
            throw new RuntimeException("You cannot be Activate Your own account");
        }
        ensureNotLastActiveAdmin(user);
        user.setActive(!user.isActive());
        userRepository.save(user);
        return new MessageResponse("User Status Update Successfully..");
    }

    @Override
    public MessageResponse changeUserRole(Long id, UserRequest userRequest) {
        User user = serviceUtils.getUserByIdOrThrow(id);
        validateRole(userRequest.getRole());

        Role newRole = Role.valueOf(userRequest.getRole().toUpperCase());
        if (user.getRole()==Role.ADMIN && newRole == Role.USER){
            ensureNotLastAdmin(user,"Change the Role of");

        }
        user.setRole(newRole);
        userRepository.save(user);
        return new MessageResponse("User Role Updated Successfully..");
    }


}
