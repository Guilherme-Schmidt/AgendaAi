package com.agendai.service;
import com.agendai.dto.PagedResponse;
import com.agendai.dto.UserResponse;
import com.agendai.exception.ResourceNotFoundException;
import com.agendai.mapper.UserMapper;
import com.agendai.model.User;
import com.agendai.model.UserType;
import com.agendai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return userMapper.toResponse(user);
    }

    public PagedResponse<UserResponse> getUsers(int page, int size, UserType type, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));

        Page<User> userPage;
        if (search != null && !search.trim().isEmpty()) {
            userPage = userRepository.searchActiveUsers(search.trim(), pageable);
        } else if (type != null) {
            userPage = userRepository.findByTypeAndActiveTrue(type, pageable);
        } else {
            userPage = userRepository.findByActiveTrue(pageable);
        }

        List<UserResponse> content = userPage.getContent()
                .stream()
                .map(userMapper::toResponse)
                .toList();

        return PagedResponse.<UserResponse>builder()
                .content(content)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .hasNext(userPage.hasNext())
                .hasPrevious(userPage.hasPrevious())
                .build();
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        user.setActive(false);
        userRepository.save(user);

        log.info("User deactivated: {}", id);
    }
}}