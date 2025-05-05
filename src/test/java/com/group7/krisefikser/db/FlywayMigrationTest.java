package com.group7.krisefikser.db;

import com.group7.krisefikser.KrisefikserApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = KrisefikserApplication.class)
@ActiveProfiles("test")
class FlywayMigrationTest {

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public FlywayMigrationTest(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Test
  void migrationShouldCreateExpectedTables() {
    Integer tablesCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = CURRENT_SCHEMA()",
            Integer.class);

    assertTrue(tablesCount > 1, "Database should contain multiple tables");

  }

  @Test
  void usersTableShouldHaveExpectedColumns() {
    Integer columnsCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'households'",
            Integer.class);

    assertEquals(4, columnsCount);
  }
}