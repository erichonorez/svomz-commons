package org.svomz.commons.persistence;

import java.util.List;

/**
 * Exposes a basic set of methods to store and retrieve domain objects from a persistence system like
 * traditional relational databases or NoSQL.
 *
 * Generally this interface will be extended by more specific repositories to offer domain specific
 * operations.
 *
 * This interface exposes CRUD operations on an entity.
 *
 * @param <T> the type of entity the repository will store and retrieve
 * @param <PK> the type of the entity primary key
 */
public interface Repository<T, PK> {

  /**
   * Finds an entity by its primary key.
   *
   * @param primaryKey the primary key of the entity to retrieve
   * @return the entity
   * @throws EntityNotFoundException if no entity is found
   */
  public T find(PK primaryKey) throws EntityNotFoundException;

  public List<T> findAll();

  /**
   * Persists an entity in the persistence system.
   *
   * The primary of the supplied entity must be null.
   *
   * The returned instance of the entity is the same as the one supplied in parameter. The entity's
   * primary key is updated with the primary allocated by the persistence system.
   *
   * @param entity the entity to persist.
   * @return the persisted entity with its primary key.
   */
  public T create(T entity);

  /**
   * Updates an entity in the persistence system.
   *
   * The returned instance may be the same as the same supplied in parameter depending on the implementation.
   *
   * @param entity the entity to update.
   * @return the updated entity
   */
  //TODO the returned entity should be the same instance as the one supplied in parameter
  //TODO should throws entity not found if the entity does not exists yet
  public T update(T entity);

  public void delete(T entity);

}

