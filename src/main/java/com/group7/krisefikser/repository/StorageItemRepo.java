package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.StorageItem;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
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
 * This class is a repository for managing storage items in the database.
 * It provides methods to get, add, update, and delete storage items.
 * It uses JdbcTemplate to interact with the database.
 */
@Repository
public class StorageItemRepo {
  private final JdbcTemplate jdbcTemplate;

  /**
   * RowMapper to map the result set to a StorageItem object.
   */
  private final RowMapper<StorageItem> storageItemRowMapper = (rs, rowNum) -> new StorageItem(
          rs.getInt("id"),
          rs.getTimestamp("expiration_date").toLocalDateTime(),
          rs.getInt("quantity"),
          rs.getInt("household_id"),
          rs.getInt("item_id"),
          rs.getBoolean("is_shared")
  );

  /**
   * Constructor for StorageItemRepo.
   *
   * @param jdbcTemplate The JdbcTemplate used to interact with the database.
   */
  @Autowired
  public StorageItemRepo(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * This method retrieves all storage items for a specific household from the database.
   *
   * @param householdId The ID of the household to retrieve storage items for
   * @return A list of StorageItem objects.
   */
  public List<StorageItem> getAllStorageItems(int householdId) {
    String sql = "SELECT id, expiration_date, quantity, household_id, "
            + "item_id, is_shared FROM storage_items WHERE household_id = ?";
    return jdbcTemplate.query(sql, storageItemRowMapper, householdId);
  }

  /**
   * This method retrieves all shared storage items for a specific emergency group from the
   * database.
   *
   * @param emergencyGroupId The ID of the emergency group to retrieve storage items for
   * @return A list of StorageItem objects.
   */
  public List<StorageItem> getAllSharedStorageItemsInGroup(long emergencyGroupId) {
    String sql = "SELECT si.id, si.expiration_date, si.quantity, si.household_id, "
            + "si.item_id, si.is_shared FROM storage_items si "
            + "JOIN households h ON si.household_id = h.id "
            + "WHERE h.emergency_group_id = ? AND si.is_shared = TRUE";
    return jdbcTemplate.query(sql, storageItemRowMapper, emergencyGroupId);
  }

  /**
   * This method retrieves storage items for a specific group based on item id
   * and group ID.
   *
   * @param groupId The ID of the group to retrieve storage items for
   * @return A list of StorageItem objects.
   */
  public List<StorageItem> getSharedStorageItemsInGroupByItemId(long groupId, long itemId) {
    String sql = "SELECT si.id, si.expiration_date, si.quantity, si.household_id, "
            + "si.item_id, si.is_shared FROM storage_items si "
            + "JOIN households h ON si.household_id = h.id "
            + "WHERE h.emergency_group_id = ? AND si.is_shared = TRUE AND si.item_id = ?";
    return jdbcTemplate.query(sql, storageItemRowMapper, groupId, itemId);
  }

  /**
   * This method retrieves a storage item by its ID.
   *
   * @param id          The ID of the storage item to retrieve.
   * @return An Optional containing the StorageItem object if found, or empty if not found.
   */
  public Optional<StorageItem> findById(int id) {
    try {
      String sql = "SELECT id, expiration_date, quantity, household_id, "
              + "item_id, is_shared FROM storage_items WHERE id = ?";
      StorageItem storageItem = jdbcTemplate.queryForObject(sql,
              storageItemRowMapper, id);
      return Optional.ofNullable(storageItem);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  /**
   * This method retrieves storage items by their item ID for a specific household.
   *
   * @param itemId      The item ID of the storage items to retrieve.
   * @param householdId The ID of the household the storage items belong to.
   * @return A list of StorageItem objects of the specified item.
   */
  public List<StorageItem> findByItemId(int itemId, int householdId) {
    String sql = "SELECT id, expiration_date, quantity, household_id, item_id, is_shared "
            + "FROM storage_items "
            + "WHERE item_id = ? AND household_id = ?";
    return jdbcTemplate.query(sql, storageItemRowMapper, itemId, householdId);
  }

  /**
   * Adds a new storage item to the database.
   * This method inserts the storage item's details into the database and sets the generated ID
   * on the provided storageItem object.
   *
   * @param storageItem The storage item to be added. It must contain expiration date,
   *                    quantity, household ID, and item ID.
   * @return The added storage item with the generated ID set.
   */
  public StorageItem add(StorageItem storageItem) {
    String sql = "INSERT INTO storage_items (expiration_date, quantity, "
            + "household_id, item_id) VALUES (?, ?, ?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setTimestamp(1, Timestamp.valueOf(storageItem.getExpirationDate()));
      ps.setDouble(2, storageItem.getQuantity());
      ps.setInt(3, storageItem.getHouseholdId());
      ps.setInt(4, storageItem.getItemId());
      return ps;
    }, keyHolder);

    storageItem.setId(keyHolder.getKey().intValue());
    return storageItem;
  }

  /**
   * Updates an existing storage item in the database.
   * Ensures the storage item belongs to the specified household.
   *
   * @param storageItem The storage item to be updated. It must contain the ID and the new details.
   * @return The updated storage item.
   * @throws EmptyResultDataAccessException if no storage item is found with the
   *                                        given ID in the specified household.
   */
  public StorageItem update(StorageItem storageItem) {
    String sql = "UPDATE storage_items SET expiration_date = ?, quantity = ?, item_id = ? "
            + "WHERE id = ? AND household_id = ?";
    int rowsAffected = jdbcTemplate.update(sql,
            Timestamp.valueOf(storageItem.getExpirationDate()),
            storageItem.getQuantity(),
            storageItem.getItemId(),
            storageItem.getId(),
            storageItem.getHouseholdId());

    if (rowsAffected == 0) {
      throw new EmptyResultDataAccessException(
              "No storage item found with id: " + storageItem.getId()
                      + " in household: " + storageItem.getHouseholdId(), 1);
    }
    return storageItem;
  }

  /**
   * Deletes a storage item from the database by its ID,
   * ensuring it belongs to the specified household.
   *
   * @param id          The ID of the storage item to be deleted.
   * @param householdId The ID of the household the storage item belongs to.
   * @return true if the storage item was deleted, false otherwise.
   */
  public boolean deleteById(int id, int householdId) {
    String sql = "DELETE FROM storage_items WHERE id = ? AND household_id = ?";
    return jdbcTemplate.update(sql, id, householdId) > 0;
  }

  /**
   * Deletes all storage items of a specific household from the database.
   *
   * @param householdId The household ID of the storage items to be deleted.
   * @return The number of storage items deleted.
   */
  public int deleteByHouseholdId(int householdId) {
    String sql = "DELETE FROM storage_items WHERE household_id = ?";
    return jdbcTemplate.update(sql, householdId);
  }

  /**
   * Retrieves storage items that are about to expire for a specific household.
   *
   * @param days        The number of days within which items will expire.
   * @param householdId The ID of the household to retrieve expiring items for.
   * @return A list of storage items that will expire within the specified number of days.
   */
  public List<StorageItem> findExpiringItems(int days, int householdId) {
    // Calculate current date and future date using Java
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime futureDate = now.plusDays(days);

    String sql = "SELECT id, expiration_date, quantity, household_id, item_id, is_shared "
            + "FROM storage_items "
            + "WHERE expiration_date <= ? "
            + "AND expiration_date >= ? "
            + "AND household_id = ?";

    return jdbcTemplate.query(sql, storageItemRowMapper,
            Timestamp.valueOf(futureDate),
            Timestamp.valueOf(now),
            householdId);
  }

  /**
   * Retrieves storage items by item type for a specific household.
   * Uses a JOIN query to filter at the database level.
   *
   * @param householdId     The household ID of the storage items to retrieve.
   * @param itemTypeStrings The types of items to filter by (lowercase strings).
   * @return A list of StorageItem objects matching the criteria.
   */
  public List<StorageItem> findByItemTypes(int householdId, List<String> itemTypeStrings) {
    if (itemTypeStrings == null || itemTypeStrings.isEmpty()) {
      return getAllStorageItems(householdId);
    }

    String placeholders = String.join(",", Collections.nCopies(itemTypeStrings.size(), "?"));

    String sql = "SELECT si.id, si.expiration_date, si.quantity, si.household_id, si.item_id, "
            + "si.is_shared "
            + "FROM storage_items si "
            + "JOIN items i ON si.item_id = i.id "
            + "WHERE si.household_id = ? "
            + "AND i.type IN (" + placeholders + ")";

    Object[] params = new Object[itemTypeStrings.size() + 1];
    params[0] = householdId;
    for (int i = 0; i < itemTypeStrings.size(); i++) {
      params[i + 1] = itemTypeStrings.get(i);
    }

    return jdbcTemplate.query(sql, storageItemRowMapper, params);
  }
}