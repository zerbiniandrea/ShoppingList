package it.progweb18.shoppingList.dao;

import it.progweb18.shoppingList.dao.entities.Guest;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.DAO;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;
import javax.servlet.http.Cookie;

/**
 * All concrete DAOs must implement this interface to handle the persistence 
 * system that interact with {@link Guest guests}.
 */
public interface GuestDAO extends DAO<Guest, Integer> {
    /**
     * Create the guest
     * @return the created guest
     * @throws DAOException if an error occurred during the action.
     */
    public Guest create() throws DAOException;
    
    /**
     * Checks for guest cookies
     * @param cookies the array of all cookies
     * @return the guest
     * @throws DAOException if an error occurred during the action.
     */
    public Guest getGuest(Cookie[] cookies) throws DAOException;
    
    /**
     * Sets the last login of a guest to the current moment
     * @param guest the guest that has logged in
     * @return true if the last login has been updated
     * @throws DAOException if an error occurred during the persist action.
     */
    public boolean setLastLogin(Guest guest) throws DAOException;
    
    /**
     * Get the number of shopping lists created by the guest
     * @param guest the guest that has logged in
     * @return the number of shopping lists
     * @throws DAOException if an error occurred during the persist action.
     */
    public Integer countShoppingLists(Guest guest) throws DAOException;
}
