package com.group7.krisefikser.repository;

import com.group7.krisefikser.enums.ItemType;
import com.group7.krisefikser.model.Item;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;


/**
 * This class is a repository for managing items in the database.
 * It provides methods to get, add, update, and delete items.
 * It uses JdbcTemplate to interact with the database.
 */
@Repository
public class ItemRepo {
  private final JdbcTemplate jdbcTemplate;

  /**
   * RowMapper to map the result set to an Item object.
   */
  private final RowMapper<Item> itemRowMapper = (rs, rowNum) -> {
    return new Item(
      rs.getInt("id"),
      rs.getString("name"),
      rs.getString("unit"),
      rs.getInt("calories"),
      ItemType.fromString(rs.getString("type"))
    );
  };

  /**
   * Constructor for ItemRepo.
   *
   * @param jdbcTemplate The JdbcTemplate used to interact with the database.
   */
  @Autowired
  public ItemRepo(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * This method retrieves all items from the database.
   *
   * @return A list of Item objects.
   */
  public List<Item> getAllItems() {
    String sql = "SELECT id, name, unit, calories, type FROM items";
    return jdbcTemplate.query(sql, itemRowMapper);
  }

  /**
   * This method retrieves an item by its ID.
   *
   * @param id The ID of the item to retrieve.
   * @return An Optional containing the Item object if found, or empty if not found.
   */
  public Optional<Item> findById(int id) {
    try {
      String sql = "SELECT id, name, unit, calories, type FROM items WHERE id = ?";
      Item item = jdbcTemplate.queryForObject(sql, itemRowMapper, id);
      return Optional.ofNullable(item);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  /**
   * This method retrieves items by their type.
   *
   * @param type The type of items to retrieve.
   * @return A list of Item objects of the specified type.
   */
  public List<Item> findByType(ItemType type) {
    String sql = "SELECT id, name, unit, calories, type FROM items WHERE type = ?";
    return jdbcTemplate.query(sql, itemRowMapper, type.name().toLowerCase());
  }

  /**
   * Adds a new item to the database.
   * This method inserts the item's details into the database and sets the generated ID
   * on the provided item object.
   *
   * @param item The item to be added. It must contain the name, unit, calories, and type.
   * @return The added item with the generated ID set.
   */
  public Item add(Item item) {
    String sql = "INSERT INTO items (name, unit, calories, type) VALUES (?, ?, ?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, item.getName());
      ps.setString(2, item.getUnit());
      ps.setInt(3, item.getCalories());
      ps.setString(4, item.getType().name().toLowerCase());
      return ps;
    }, keyHolder);

    item.setId(keyHolder.getKey().intValue());
    return item;
  }

  /**
   * Updates an existing item in the database.
   *
   * @param item The item to be updated. It must contain the ID and the new details.
   * @return The updated item.
   */
  public Item update(Item item) {
    String sql = "UPDATE items SET name = ?, unit = ?, calories = ?, type = ? WHERE id = ?";
    int rowsAffected = jdbcTemplate.update(sql,
        item.getName(),
        item.getUnit(),
        item.getCalories(),
        item.getType().name().toLowerCase(),
        item.getId());

    if (rowsAffected == 0) {
      throw new EmptyResultDataAccessException("No item found with id: " + item.getId(), 1);
    }
    return item;
  }

  /**
   * Deletes an item from the database by its ID.
   *
   * @param id The ID of the item to be deleted.
   * @return true if the item was deleted, false otherwise.
   */
  public boolean deleteById(int id) {
    String sql = "DELETE FROM items WHERE id = ?";
    return jdbcTemplate.update(sql, id) > 0;
  }
}
