package com.example.endavapwj.util;

import com.example.endavapwj.exceptions.NotFoundException;
import com.example.endavapwj.services.UserService.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupListener {

  private final UserService userService;

  @Value("${security.admin.email}")
  private String adminEmail;

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    try {
      userService.promoteToAdmin(adminEmail);
    } catch (NotFoundException e) {
      System.err.println(
          "Admin not configured yet. Check security.admin.email variable and run docker compose up again.");
    }
  }
}
