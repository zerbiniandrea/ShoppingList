package it.progweb18.shoppingList.dao;

import it.progweb18.shoppingList.dao.entities.ShoppingList;
import it.progweb18.shoppingList.dao.entities.Product;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.DAO;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;
import java.util.List;

/**
 * All concrete DAOs must implement this interface to handle the persistence 
 * system that interact with {@link Product products}.
 */
public interface ProductDAO extends DAO<Product, Integer>{
    /**
     * Persists the new {@link Product product} passed as parameter
     * to the storage system.
     * @param product the new {@code product} to persist.
     * @return the id of the new persisted record.
     * @throws DAOException if an error occurred during the persist action.
     */
    public Integer insert(Product product) throws DAOException;
    
    /**
     * Persists the already existing {@link Product product} passed
     * as parameter to the storage system.
     * @param product the {@code product} to persist.
     * @return the old record.
     * @throws DAOException if an error occurred during the persist action.
     */
    public Product update(Product product) throws DAOException;
    
    /**
     * Deletes the already existing {@link Product product} passed
     * as parameter to the storage system.
     * @param product the {@code product} to persist.
     * @return true if the product was succesfully deleted
     * @throws DAOException if an error occurred during the persist action.
     */
    public boolean delete(Product product) throws DAOException;
    
    /**
     * Links the passed {@code product} with the passed {@code shopping-list}.
     * @param product the product to link
     * @param shoppingList the shopping_list to link.
     * @param quantity how many products are added to the shopping_list
     * @return {@code true} if the link has correctly persisted on the storage
     * system.
     * @throws DAOException if an error occurred during the persist action.
     */
    public boolean linkProductToShoppingList(Product product, ShoppingList shoppingList, Integer quantity) throws DAOException;
    
    /**
     * Returns the list of {@link ShoppingList shopping-lists} with the
     * {@code id_user} is the one passed as parameter.
     *
     * @param shoppingListId
     * @return the list of {@code shopping-list} with the user id equals to the
     * one passed as parameter or an empty list if user id is not linked to any
     * to-dos.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    public List<Product> getByShoppingListId(Integer shoppingListId) throws DAOException;
     
    /**
     * Deletes the shopping list passed as the parameter
     *
     * @param product
     * @param shoppingList the {@code shoppingList} that has to be deleted
     * @return the list of {@code shopping-list} with the user id equals to the
     * one passed as parameter or an empty list if user id is not linked to any
     * to-dos.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    public boolean deleteProductFromList(Product product, ShoppingList shoppingList) throws DAOException;
}
