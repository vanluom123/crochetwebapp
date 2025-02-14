package org.crochet.service.impl;

import com.turkraft.springfilter.converter.FilterSpecification;
import org.crochet.enums.AuthProvider;
import org.crochet.enums.ResultCode;
import org.crochet.enums.RoleType;
import org.crochet.exception.BadRequestException;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.PaginationMapper;
import org.crochet.mapper.UserMapper;
import org.crochet.model.User;
import org.crochet.payload.request.SignUpRequest;
import org.crochet.payload.request.UserUpdateRequest;
import org.crochet.payload.response.PaginationResponse;
import org.crochet.payload.response.UserResponse;
import org.crochet.repository.UserRepository;
import org.crochet.service.UserService;
import org.crochet.util.ObjectUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor for the UserServiceImpl class.
     *
     * @param userRepository  The repository for the User entity.
     * @param passwordEncoder The password encoder.
     */
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new user account.
     *
     * @param signUpRequest The request object containing the user's details.
     * @return The created user object.
     */
    @Override
    public User createUser(SignUpRequest signUpRequest) {
        // Check if the email address is already in use
        if (isValidEmail(signUpRequest.getEmail())) {
            throw new BadRequestException(
                    ResultCode.MSG_EMAIL_ALREADY_IN_USE.message(),
                    ResultCode.MSG_EMAIL_ALREADY_IN_USE.code()
            );
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
     * @param offset  The page number to retrieve. Page numbers start from 0.
     * @param limit   The number of records to retrieve per page.
     * @param sortBy  The field by which to sort the records.
     * @param sortDir The direction of the sort. Can be 'ASC' for ascending or
     *                'DESC' for descending.
     * @param spec     The specification object containing the filter criteria.
     * @return A UserPaginationResponse object containing the retrieved records and
     * pagination details.
     */
    @SuppressWarnings("ConstantValue")
    @Override
    public PaginationResponse<UserResponse> getAll(int offset, int limit, String sortBy, String sortDir, Specification<User> spec) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(offset, limit, sort);
        var filter = ((FilterSpecification<User>) spec).getFilter();
        Specification<User> userSpec = Specification.where(null);
        if (filter != null && ObjectUtils.isNotEmpty(filter.getChildren())) {
            userSpec = userSpec.and(spec);
        }
        var page = userRepository.findAll(userSpec, pageable);
        List<UserResponse> users = UserMapper.INSTANCE.toResponses(page.getContent());
        return PaginationMapper.getInstance().toPagination(page, users);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param request The request object containing the user's ID.
     */
    @Transactional
    @Override
    public void updateUser(UserUpdateRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ResultCode.MSG_USER_NOT_FOUND_WITH_ID.message(),
                        ResultCode.MSG_USER_NOT_FOUND_WITH_ID.code()
                ));
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        userRepository.save(user);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     */
    @Transactional
    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    /**
     * Deletes multiple users by their IDs.
     *
     * @param ids The list of IDs of the users to delete.
     */
    @Transactional
    @Override
    public void deleteMultipleUsers(List<String> ids) {
        userRepository.deleteMultipleUsers(ids);
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email The email address of the user to retrieve.
     * @return The retrieved user object.
     */
    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ResultCode.MSG_USER_NOT_FOUND_WITH_EMAIL.message(),
                        ResultCode.MSG_USER_NOT_FOUND_WITH_EMAIL.code()
                ));
    }

    @Override
    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ResultCode.MSG_USER_NOT_FOUND.message(),
                        ResultCode.MSG_USER_NOT_FOUND.code()
                ));
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The retrieved user object.
     */
    @Override
    public UserResponse getDetail(String id) {
        return userRepository.getDetail(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ResultCode.MSG_USER_NOT_FOUND_WITH_ID.message(),
                        ResultCode.MSG_USER_NOT_FOUND_WITH_ID.code()
                ));
    }

    /**
     * Updates a user's password.
     *
     * @param password The new password.
     * @param email    The email address of the user.
     */
    @Override
    public void updatePassword(String password, String email) {
        var user = getByEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    /**
     * Updates a user's email address.
     *
     * @param email The new email address.
     */
    @Override
    public void verifyEmail(String email) {
        var user = getByEmail(email);
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    /**
     * Validates a user's credentials.
     *
     * @param email    The email address of the user.
     * @param password The password of the user.
     * @return The user object if the credentials are valid.
     */
    @Override
    public User validateUserCredentials(String email, String password) {
        var user = this.getByEmail(email);
        var isMatch = passwordEncoder.matches(password, user.getPassword());
        if (!isMatch) {
            throw new BadRequestException(
                    ResultCode.MSG_INCORRECT_PASSWORD.message(),
                    ResultCode.MSG_INCORRECT_PASSWORD.code()
            );
        }
        return user;
    }

    /**
     * Checks if an email address is already in use.
     *
     * @param email The email address to check.
     * @return True if the email address is already in use, false otherwise.
     */
    private boolean isValidEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
