package com.example.endavapwj.controllers;

import com.example.endavapwj.util.JwtUtil;
import com.example.endavapwj.util.LoginThrottle;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
public abstract class BaseControllerTest {

  @Autowired protected MockMvc mvc;
  @Autowired protected ObjectMapper objectMapper;

  @MockitoBean protected JwtUtil jwtUtil;
  @MockitoBean protected BCryptPasswordEncoder passwordEncoder;
  @MockitoBean protected LoginThrottle loginThrottle;
}
