package org.crochet.service.impl;

import org.crochet.constant.MessageConstant;
import org.crochet.enumerator.AuthProvider;
import org.crochet.enumerator.RoleType;
import org.crochet.exception.BadRequestException;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.UserMapper;
import org.crochet.model.User;
import org.crochet.payload.request.SignUpRequest;
import org.crochet.payload.request.UserUpdateRequest;
import org.crochet.payload.response.UserPaginationResponse;
import org.crochet.payload.response.UserResponse;
import org.crochet.repository.Filter;
import org.crochet.repository.Specifications;
import org.crochet.repository.UserRepository;
import org.crochet.repository.UserSpecification;
import org.crochet.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;
import static org.crochet.constant.MessageConstant.INCORRECT_PASSWORD_MESSAGE;
import static org.crochet.constant.MessageConstant.USER_NOT_FOUND_WITH_EMAIL_MESSAGE;
import static org.crochet.constant.MessageConstant.USER_NOT_FOUND_WITH_ID_MESSAGE;

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
            throw new BadRequestException(MessageConstant.EMAIL_ADDRESS_ALREADY_IN_USE_MESSAGE,
                    MAP_CODE.get(MessageConstant.EMAIL_ADDRESS_ALREADY_IN_USE_MESSAGE));
        }

        // Creating user's account
        User user = new User()
                .setName(signUpRequest.getName())
                .setEmail(signUpRequest.getEmail())
                .setEmailVerified(false)
                .setPassword(passwordEncoder.encode(signUpRequest.getPassword()))
                .setProvider(AuthProvider.LOCAL)
                .setRole(RoleType.USER);
        // Save the user to the repository
        return userRepository.save(user);
    }

    /**
     * Retrieves all users with pagination and optional filters.
     *
     * @param pageNo     The page number to retrieve. Page numbers start from 0.
     * @param pageSize   The number of records to retrieve per page.
     * @param sortBy     The field by which to sort the records.
     * @param sortDir    The direction of the sort. Can be 'ASC' for ascending or 'DESC' for descending.
     * @param searchText The text to search for in the name or email fields.
     * @param filters    The list of filters.
     * @return A UserPaginationResponse object containing the retrieved records and pagination details.
     */
    @Override
    public UserPaginationResponse getAll(int pageNo, int pageSize, String sortBy, String sortDir,
                                         String searchText, List<Filter> filters) {
        Sort sort = Sort.by(sortBy);
        sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Specification<User> spec = Specifications.getSpecificationFromFilters(filters);
        // Search by search text
        if (searchText != null && !searchText.isEmpty()) {
            spec = spec.and(UserSpecification.searchByNameOrEmail(searchText));
        }
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
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID_MESSAGE + request.getId(),
                        MAP_CODE.get(USER_NOT_FOUND_WITH_ID_MESSAGE)));
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
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_EMAIL_MESSAGE + email,
                        MAP_CODE.get(USER_NOT_FOUND_WITH_EMAIL_MESSAGE)));
    }

    @Override
    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID_MESSAGE + id,
                        MAP_CODE.get(USER_NOT_FOUND_WITH_ID_MESSAGE)));
    }

    @Override
    public UserResponse getDetail(UUID id) {
        return userRepository.getDetail(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID_MESSAGE + id,
                        MAP_CODE.get(USER_NOT_FOUND_WITH_ID_MESSAGE)));
    }

    @Override
    public void updatePassword(String password, String email) {
        userRepository.updatePassword(password, email);
    }

    @Override
    public void verifyEmail(String email) {
        userRepository.verifyEmail(email);
    }

    @Override
    public User checkLogin(String email, String password) {
        var user = this.getByEmail(email);
        var isMatch = passwordEncoder.matches(password, user.getPassword());
        if (!isMatch) {
            throw new BadRequestException(INCORRECT_PASSWORD_MESSAGE,
                    MAP_CODE.get(INCORRECT_PASSWORD_MESSAGE));
        }
        return user;
    }

    private boolean isValidEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
