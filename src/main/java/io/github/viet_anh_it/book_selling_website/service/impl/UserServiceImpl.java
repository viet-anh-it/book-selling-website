package io.github.viet_anh_it.book_selling_website.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import io.github.viet_anh_it.book_selling_website.dto.PaginationMetadataDTO;
import io.github.viet_anh_it.book_selling_website.dto.UserDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.model.User;
import io.github.viet_anh_it.book_selling_website.repository.UserRepository;
import io.github.viet_anh_it.book_selling_website.service.UserService;
import io.github.viet_anh_it.book_selling_website.specification.UserSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public SuccessResponse<List<UserDTO>> getAllUsers(Pageable pageable) {
        Specification<User> combinedSpec = Specification.where(null);
        Specification<User> notHaveRoleAdmin = UserSpecification.notHaveRoleAdmin();
        combinedSpec = combinedSpec.and(notHaveRoleAdmin);
        Page<User> userPage = this.userRepository.findAll(combinedSpec, pageable);
        List<User> users = userPage.getContent();
        List<UserDTO> userDtos = users.stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .role(new UserDTO.Role(user.getRole().getName().name()))
                        .active(user.isActive())
                        .locked(user.isLocked())
                        .build())
                .toList();
        PaginationMetadataDTO paginationMetadata = PaginationMetadataDTO.builder()
                .currentPosition(userPage.getNumber())
                .numberOfElementsPerPage(userPage.getSize())
                .totalNumberOfElements(userPage.getTotalElements())
                .totalNumberOfPages(userPage.getTotalPages())
                .hasNextPage(userPage.hasNext())
                .hasPreviousPage(userPage.hasPrevious())
                .nextPosition(userPage.hasNext() ? userPage.getNumber() + 1 : -1)
                .previousPosition(userPage.hasPrevious() ? userPage.getNumber() - 1 : -1)
                .build();
        return SuccessResponse.<List<UserDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy tất cả tài khoản thành công!")
                .data(userDtos)
                .paginationMetadata(paginationMetadata)
                .build();
    }

}
