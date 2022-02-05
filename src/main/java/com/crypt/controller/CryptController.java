package com.crypt.controller;

import com.crypt.entity.Crypt;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Log4j2
@RestController
@RequestMapping("/crypt")
public class CryptController {

    @Autowired
    private JdbcOperations jdbcOperations;

    @GetMapping("/list")
    public ResponseEntity<List<Crypt>> getListCrypt() {
        final String query = "SELECT * FROM crypt";

        return ResponseEntity.ok(this.jdbcOperations
                .query(query, ((rs, rowNum) ->
                        new Crypt(
                                rs.getInt("crypt_id"),
                                rs.getString("symbol"),
                                rs.getDouble("price_usd")
                        ))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Crypt> getCrypt(@PathVariable Integer id) {
        final String query = "SELECT * FROM crypt WHERE crypt_id = ?";

        try {
            return ResponseEntity.ok(this.jdbcOperations
                    .queryForObject(query, new Object[]{id}, ((rs, rowNum) ->
                            new Crypt(
                                    rs.getInt("crypt_id"),
                                    rs.getString("symbol"),
                                    rs.getDouble("price_usd")
                            ))));
        } catch (IncorrectResultSizeDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    public void getActualPriceCrypt() {
        RestTemplate template = new RestTemplate();
        final int[] cryptsId = Objects.requireNonNull(getListCrypt().getBody())
                .stream().mapToInt(Crypt::getId).toArray();

        for (int id : cryptsId) {
            Crypt[] crypt = template.getForObject("https://api.coinlore.net/api/ticker/?id=" + id, Crypt[].class);
            updateCrypt(crypt);
        }

        checkDifferencePrice();
    }

    private void checkDifferencePrice() {
        final String query = "SELECT crypt.price_usd, super_task.user.price_usd " +
                "FROM user INNER JOIN crypt USING(crypt_id)";

        List<Map<String, Object>> prices = jdbcOperations.queryForList(query);
        Double oldPrice = (Double) prices.get(0).get("price_usd");
        Double actualPrice = (Double) prices.get(0).get("price_usd");

        if (actualPrice > (oldPrice * 1.01)) {
            notifyLog((actualPrice - oldPrice) / oldPrice * 100, actualPrice);
        }
    }

    private void updateCrypt(Crypt[] crypt) {
        final String query = "UPDATE crypt SET price_usd = ? WHERE crypt_id = ?";
        jdbcOperations.update(query, crypt[0].getPrice_usd(), crypt[0].getId());
    }

    private void updateUserPrice(Double actualPrice, Integer user_id) {
        final String query = "UPDATE super_task.user SET price_usd = ? WHERE super_task.user.user_id = ?";
        jdbcOperations.update(query, actualPrice, user_id);
    }

    private void notifyLog(double v, double actualPrice) {
        final String query = "SELECT * FROM user ORDER BY user_id DESC LIMIT 1";
        List<Map<String, Object>> user = jdbcOperations.queryForList(query);
        System.out.println(user.size());

        Integer user_id = (Integer) user.get(0).get("user_id");
        Integer crypt_id = (Integer) user.get(0).get("crypt_id");
        String username = (String) user.get(0).get("name");

        log.warn(String.format("%d %s %f", crypt_id, username, v));
        updateUserPrice(actualPrice, user_id);
    }
}
