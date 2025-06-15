package com.AlNada.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.AlNada.config.JwtUtil;
import com.AlNada.entity.User;
import com.AlNada.payload.AuthRequest;
import com.AlNada.repository.UserRepository;

import java.util.Collections;
import java.util.Map;

@Service
public class AuthService {

    @Autowired private UserRepository repo;
    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtUtil jwtUtil;

    public User register(User user) throws Exception {
    	//username should have to be unique... Checking with backend..
    	User resUser =  repo.findByUsername(user.getUsername()).orElse(null);
    	if(resUser!=null) {
    		throw new Exception("username already exist");
    	}
    	user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
    	return repo.save(user);    		
    }

    public Map<String, String> login(AuthRequest req) {
    	    authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
    	    String token = jwtUtil.generateToken(req.getUsername());
    	    return Collections.singletonMap("token", token);
    }

    public User getUser(String token) {
        String username = jwtUtil.extractUsername(token);
        return repo.findByUsername(username).orElse(null);
    }
}
