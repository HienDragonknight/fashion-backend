package com.fashion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.fashion.repository.OrderRepository;
import com.fashion.repository.UserRepository;
import com.fashion.repository.ProductRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import com.fashion.repository.OrderRepository;
import com.fashion.repository.UserRepository;
import com.fashion.repository.ProductRepository;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class FashionBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(FashionBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner testRunner(
            OrderRepository orderRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            JdbcTemplate jdbcTemplate) {
        return args -> {
            System.out.println("====== DB CHECK START ======");
            System.out.println("Count of orders in DB = " + orderRepository.count());
            System.out.println("Count of users in DB = " + userRepository.count());
            System.out.println("Count of products in DB = " + productRepository.count());
            
            try {
                System.out.println("--- Applied Flyway Migrations ---");
                List<Map<String, Object>> migrations = jdbcTemplate.queryForList(
                    "SELECT version, description, type, script, success FROM flyway_schema_history ORDER BY installed_rank"
                );
                for (Map<String, Object> m : migrations) {
                    System.out.println(String.format("Version: %s | Desc: %s | Success: %s", 
                        m.get("version"), m.get("description"), m.get("success")));
                }
            } catch (Exception e) {
                System.out.println("Could not query flyway_schema_history: " + e.getMessage());
            }

            try {
                System.out.println("--- Table Row Counts ---");
                String[] tables = {"users", "products", "orders", "order_items", "categories", "brands", "banners"};
                for (String table : tables) {
                    try {
                        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
                        System.out.println(table + " count = " + count);
                    } catch (Exception e) {
                        System.out.println(table + " count = ERROR (" + e.getMessage() + ")");
                    }
                }
                
                System.out.println("--- Products in DB ---");
                productRepository.findAll().forEach(p -> {
                    System.out.println(String.format("ID: %s | Name: %s | Active: %s", p.getId(), p.getName(), p.getIsActive()));
                });
            } catch (Exception e) {
                System.out.println("Could not query table row counts or products: " + e.getMessage());
            }
            System.out.println("====== DB CHECK END ======");
        };
    }
}
