package com.vti.springdatajpa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;

// @Component
// Tạm thời vô hiệu hóa để Hibernate tự quản lý schema
public class DatabaseMigrationRunner implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- Starting Database Migration Runner ---");

        // 1. Fix product image column
        try {
            jdbcTemplate.execute("ALTER TABLE products MODIFY image_base64 LONGTEXT");
            System.out.println("✅ Successfully migrated products.image_base64 to LONGTEXT.");
        } catch (Exception e) {
            System.out.println("❌ Could not migrate image_base64: " + e.getMessage());
        }

        // 2. Fix wallet_id type mismatches (CHAR(36) -> INT)
        String[] tables = { "transactions", "qr_codes", "balance_change_logs", "transfer_details" };
        for (String table : tables) {
            try {
                // Determine if we need to different column names for some tables
                String col = "wallet_id";
                if (table.equals("transfer_details")) {
                    // transfer_details might have counterparty_wallet_id too
                    try {
                        jdbcTemplate.execute("ALTER TABLE " + table + " MODIFY counterparty_wallet_id INT");
                    } catch (Exception ignored) {
                    }
                }

                // Attempt to modify the column. If there's a foreign key, this might fail,
                // but setting ddl-auto=none and running this first might help.
                jdbcTemplate.execute("ALTER TABLE " + table + " MODIFY " + col + " INT");
                System.out.println("✅ Successfully migrated " + table + "." + col + " to INT.");
            } catch (Exception e) {
                System.out.println("⚠️ Note for " + table + ": " + e.getMessage());
            }
        }

        System.out.println("--- Database Migration Runner Completed ---");
    }
}
