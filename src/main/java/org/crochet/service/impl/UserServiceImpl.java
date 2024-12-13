package org.crochet.service.impl;

import org.crochet.constant.MessageConstant;
import org.crochet.enumerator.AuthProvider;
import org.crochet.enumerator.RoleType;
import org.crochet.exception.BadRequestException;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.UserMapper;
import org.crochet.model.User;
import org.crochet.payload.request.Filter;
import org.crochet.payload.request.ProfileUserUpdateRequest;
import org.crochet.payload.request.SignUpRequest;
import org.crochet.payload.request.UserUpdateRequest;
import org.crochet.payload.response.UserPaginationResponse;
import org.crochet.payload.response.UserResponse;
import org.crochet.repository.GenericFilter;
import org.crochet.repository.UserRepository;
import org.crochet.service.UserService;
import org.crochet.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;
import static org.crochet.constant.MessageConstant.MSG_INCORRECT_PASSWORD;
import static org.crochet.constant.MessageConstant.MSG_USER_NOT_FOUND_WITH_EMAIL;
import static org.crochet.constant.MessageConstant.MSG_USER_NOT_FOUND_WITH_ID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(SignUpRequest signUpRequest) {
        // Check if the email address is already in use
        if (isValidEmail(signUpRequest.getEmail())) {
            throw new BadRequestException(MessageConstant.MSG_EMAIL_ALREADY_IN_USE,
                    MAP_CODE.get(MessageConstant.MSG_EMAIL_ALREADY_IN_USE));
        }

        // Creating user's account
        User user = User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .emailVerified(false)
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .provider(AuthProvider.LOCAL)
                .role(RoleType.USER)
                .build();
        // Save the user to the repository
        return userRepository.save(user);
    }

    /**
     * Retrieves all users with pagination and optional filters.
     *
     * @param pageNo   The page number to retrieve. Page numbers start from 0.
     * @param pageSize The number of records to retrieve per page.
     * @param sortBy   The field by which to sort the records.
     * @param sortDir  The direction of the sort. Can be 'ASC' for ascending or
     *                 'DESC' for descending.
     * @param filters  The list of filters.
     * @return A UserPaginationResponse object containing the retrieved records and
     *         pagination details.
     */
    @Override
    public UserPaginationResponse getAll(int pageNo, int pageSize, String sortBy, String sortDir, Filter[] filters) {
        Specification<User> spec = Specification.where(null);
        if (filters != null && filters.length > 0) {
            GenericFilter<User> filter = GenericFilter.create(filters);
            spec = filter.build();
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<User> page = userRepository.findAll(spec, pageable);
        List<UserResponse> users = UserMapper.INSTANCE.toResponses(page.getContent());

        return UserPaginationResponse.builder()
                .contents(users)
                .pageNo(pageNo)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .last(page.isLast())
                .build();
    }

    @Transactional
    @Override
    public void updateUser(UserUpdateRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MSG_USER_NOT_FOUND_WITH_ID + request.getId(),
                        MAP_CODE.get(MSG_USER_NOT_FOUND_WITH_ID)));
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_USER_NOT_FOUND_WITH_EMAIL + email,
                        MAP_CODE.get(MSG_USER_NOT_FOUND_WITH_EMAIL)));
    }

    @Override
    public UserResponse getDetail(String id) {
        return userRepository.getDetail(id)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_USER_NOT_FOUND_WITH_ID + id,
                        MAP_CODE.get(MSG_USER_NOT_FOUND_WITH_ID)));
    }

    @Override
    public void updatePassword(String password, String email) {
        var user = getByEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public void verifyEmail(String email) {
        var user = getByEmail(email);
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    @Override
    public User validateUserCredentials(String email, String password) {
        var user = this.getByEmail(email);
        var isMatch = passwordEncoder.matches(password, user.getPassword());
        if (!isMatch) {
            throw new BadRequestException(MSG_INCORRECT_PASSWORD,
                    MAP_CODE.get(MSG_INCORRECT_PASSWORD));
        }
        return user;
    }

    @Transactional
    @Override
    public String updateInfo(ProfileUserUpdateRequest request) {
        var currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new ResourceNotFoundException(MessageConstant.MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MessageConstant.MSG_USER_NOT_FOUND));
        }

        if (request.getName() != null) {
            currentUser.setName(request.getName());
        }

        if (request.getImageUrl() != null) {
            currentUser.setImageUrl(request.getImageUrl());
        }

        userRepository.save(currentUser);

        return "Update user info successfully";
    }

    private boolean isValidEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
