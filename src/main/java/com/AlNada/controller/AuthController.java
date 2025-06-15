package com.AlNada.controller;
//Controller: AuthController.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.AlNada.entity.User;
import com.AlNada.payload.AuthRequest;
import com.AlNada.service.AuthService;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

 @Autowired private AuthService authService;

 @PostMapping("/register")
 public ResponseEntity<?> register(@RequestBody User user) {
     try {
		return ResponseEntity.ok(authService.register(user));
	 } catch (Exception e) {
		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	 }
 }

 @PostMapping("/login")
 public ResponseEntity<?> login(@RequestBody AuthRequest req) {
	 try {
		 return ResponseEntity.ok(authService.login(req));		 
	 }catch(Exception e) {
		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User credential doesn't match !!");
	 }
 }

 @GetMapping("/user")
 public ResponseEntity<?> getUser(@RequestHeader("Authorization") String authHeader) {
	 try {
	     String token = authHeader.replace("Bearer ", "");
	     return ResponseEntity.ok(authService.getUser(token));
	 }catch(Exception e) {
		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User credential doesn't match !!");
	 }
	 
 }
}
