package com.crypt.controller;

import com.crypt.entity.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JdbcOperations jdbcOperations;

    @PostMapping("/reg")
    private ResponseEntity<User> register(@RequestParam String name, @RequestParam String symbol) {
        String query = "INSERT INTO super_task.user (name, crypt_id, reg_date, price_usd) VALUES " +
                "(?, (SELECT crypt_id FROM super_task.crypt WHERE symbol = ?), NOW()," +
                "(SELECT price_usd FROM super_task.crypt WHERE symbol = ?))";
        User user = new User(name, symbol);

        jdbcOperations.update(query, name, symbol, symbol);
        return ResponseEntity.ok(user);
    }
}
