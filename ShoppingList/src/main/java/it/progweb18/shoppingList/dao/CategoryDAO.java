package it.progweb18.shoppingList.dao;

import it.progweb18.shoppingList.dao.entities.Category;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.DAO;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;

/**
 * All concrete DAOs must implement this interface to handle the persistence 
 * system that interact with {@link Category categories}.
 */
public interface CategoryDAO extends DAO<Category, Integer>{
    /**
     * Persists the new {@link Category category} passed as parameter
     * to the storage system.
     * @param category the new {@code category} to persist.
     * @return the id of the new persisted record.
     * @throws DAOException if an error occurred during the persist action.
     */
    public Integer insert(Category category) throws DAOException;
    
    /**
     * Persists the already existing {@link Category category} passed
     * as parameter to the storage system.
     * @param category the (@code category) to persist.
     * @return the old record.
     * @throws DAOException if an error occurred during the persist action.
     */
    public Category update(Category category) throws DAOException;
    
    /**
     * Deletes the already existing {@link Category category} passed
     * as parameter to the storage system.
     * @param category the {@code category} to persist.
     * @return true if the category was succesfully deleted
     * @throws DAOException if an error occurred during the persist action.
     */
    public boolean delete(Category category) throws DAOException;
}
