package com.example.endavapwj.services.ProblemService;

import com.example.endavapwj.DTOs.CreateProblemDTO;
import com.example.endavapwj.collection.Problem;
import com.example.endavapwj.collection.User;
import com.example.endavapwj.enums.Role;
import com.example.endavapwj.exceptions.NotFoundException;
import com.example.endavapwj.exceptions.NotPermittedException;
import com.example.endavapwj.repositories.ProblemRepository;
import com.example.endavapwj.repositories.UserRepository;
import com.example.endavapwj.util.JwtUtil;
import jakarta.transaction.Transactional;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ProblemServiceImpl implements ProblemService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final ProblemRepository problemRepository;

    public ProblemServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, ProblemRepository problemRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.problemRepository = problemRepository;
    }
    @Override
    @Transactional
    public CompletableFuture<Map<String, String>> create(CreateProblemDTO createProblemDTO) {
        User u = this.userRepository.findByUsername(jwtUtil.extractUsername()).orElseThrow(()->new NotFoundException("User not found"));
        if(u.getRole()!= Role.Admin)
            throw new NotPermittedException("You do not have permission to perform this operation");
        Problem problem = Problem.builder()
                .title(createProblemDTO.getTitle())
                .description(createProblemDTO.getDescription())
                .difficulty(createProblemDTO.getDifficulty())
                .timeLimit(createProblemDTO.getTimeLimit())
                .memoryLimit(createProblemDTO.getMemoryLimit())
                .admin(u)
                .build();

        problemRepository.save(problem);
        return CompletableFuture.completedFuture(Map.of("message","Problem created successfully"));
    }


}
