package com.tandem.controllers;

import com.tandem.models.*;
import com.tandem.services.DataStore;
import com.tandem.utils.IDGenerator;
import com.tandem.utils.PasswordUtils;
import com.tandem.utils.Validator;

public class AuthController {

    private DataStore store = DataStore.getInstance();

    public User register(String name, String nim, String email, String rawPassword,
                         String role, String faculty, String major, String contactNumber) {
        if (!Validator.isNotEmpty(name) || !Validator.isNotEmpty(nim)
                || !Validator.isValidEmail(email) || !Validator.isNotEmpty(rawPassword)
                || !Validator.isNotEmpty(role)) {
            return null;
        }
        if (store.emailExists(email)) return null;

        String id = IDGenerator.generateId();
        String hashed = PasswordUtils.hash(rawPassword);
        User user;
        switch (role) {
            case "Hacker":
                user = new Hacker(id, name, nim, email, hashed, faculty, major, contactNumber);
                break;
            case "Hipster":
                user = new Hipster(id, name, nim, email, hashed, faculty, major, contactNumber);
                break;
            case "Hustler":
                user = new Hustler(id, name, nim, email, hashed, faculty, major, contactNumber);
                break;
            default:
                return null;
        }
        store.addUser(user);
        return user;
    }

    public User login(String email, String rawPassword) {
        if (!Validator.isValidEmail(email) || !Validator.isNotEmpty(rawPassword)) return null;
        User user = store.findUserByEmail(email);
        if (user != null && user.verifyPassword(rawPassword)) return user;
        return null;
    }

    public boolean isEmailTaken(String email) {
        return store.emailExists(email);
    }
}
