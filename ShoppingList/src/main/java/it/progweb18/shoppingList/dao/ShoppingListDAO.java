package it.progweb18.shoppingList.dao;

import it.progweb18.shoppingList.dao.entities.Guest;
import it.progweb18.shoppingList.dao.entities.Notification;
import it.progweb18.shoppingList.dao.entities.ShoppingList;
import it.progweb18.shoppingList.dao.entities.User;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.DAO;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;
import java.util.List;

/**
 * All concrete DAOs must implement this interface to handle the persistence 
 * system that interact with {@link ShoppingList shopping-lists}.
 */
public interface ShoppingListDAO extends DAO<ShoppingList, Integer> {
    /**
     * Persists the new {@link ShoppingList shopping-list} passed as parameter
     * to the storage system.
     * @param shoppingList the new {@code shopping-list} to persist.
     * @return the id of the new persisted record.
     * @throws DAOException if an error occurred during the persist action.
     */
    public Integer insert(ShoppingList shoppingList) throws DAOException;
    
    /**
     * Persists the already existing {@link ShoppingList shopping-list} passed
     * as parameter to the storage system.
     * @param shoppingList the {@code shopping-list} to persist.
     * @return the old record.
     * @throws DAOException if an error occurred during the persist action.
     */
    public ShoppingList update(ShoppingList shoppingList) throws DAOException;
    
    /**
     * Links the passed {@code shopping_list} with the passed {@code user}.
     * @param shoppingList the shopping_list to link.
     * @param user the user to link.
     * @return {@code true} if the link has correctly persisted on the storage
     * system.
     * @throws DAOException if an error occurred during the persist action.
     */
    public boolean linkShoppingListToUser(ShoppingList shoppingList, User user) throws DAOException;
    
    /**
     * Links the passed {@code shopping_list} with the passed {@code guest}.
     * @param shoppingList the shopping_list to link.
     * @param guest the guest to link.
     * @return {@code true} if the link has correctly persisted on the storage
     * system.
     * @throws DAOException if an error occurred during the persist action.
     */
    public boolean linkShoppingListToGuest(ShoppingList shoppingList, Guest guest) throws DAOException;
    
    /**
     * Returns the list of {@link ShoppingList shopping-lists} with the
     * {@code id_user} is the one passed as parameter.
     *
     * @param userId the {@code id} of the {@code user} for which retrieve the
     * shopping-lists list.
     * @return the list of {@code shopping-list} with the user id equals to the
     * one passed as parameter or an empty list if user id is not linked to any
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    public List<ShoppingList> getByUserId(Integer userId) throws DAOException;
    
    /**
     * Returns the list of {@link ShoppingList shopping-lists} with the
     * {@code id_user} is the one passed as parameter.
     *
     * @param userId the {@code id} of the {@code user} for which retrieve the
     * shopping-lists list.
     * @param order the order of the results
     * @return the list of {@code shopping-list} with the user id equals to the
     * one passed as parameter or an empty list if user id is not linked to any
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    public List<ShoppingList> getByuserId(Integer userId, Integer order) throws DAOException;
    
    /**
     * Returns the list of {@link ShoppingList shopping-lists} with the
     * {@code id_guest} is the one passed as parameter.
     *
     * @param guestId the {@code id} of the {@code guest} for which retrieve the
     * shopping-lists list.
     * @return the list of {@code shopping-list} with the guest id equals to the
     * one passed as parameter or an empty list if guest id is not linked to any
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    public List<ShoppingList> getByGuestId(Integer guestId) throws DAOException;
    
    /**
     * Returns the list of {@link ShoppingList shopping-lists} with the
     * {@code id_guest} is the one passed as parameter.
     *
     * @param guestId the {@code id} of the {@code guest} for which retrieve the
     * shopping-lists list.
     * @param order the order of the results
     * @return the list of {@code shopping-list} with the guest id equals to the
     * one passed as parameter or an empty list if guest id is not linked to any
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    public List<ShoppingList> getByGuestId(Integer guestId, Integer order) throws DAOException;
    
    /**
     * Deletes the shopping list linked to some user
     *
     * @param shoppingList the {@code shoppingList} that has to be deleted
     * @return true if the list was deleted succesfully
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    public boolean deleteUserList(ShoppingList shoppingList) throws DAOException;

    /**
     * Deletes the shopping list linked to some guest
     *
     * @param shoppingList the {@code shoppingList} that has to be deleted
     * @return true if the list was deleted succesfully
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    public boolean deleteGuestList(ShoppingList shoppingList) throws DAOException;
    
    /**
     * Inserts into the notifications table a message for every user that
     * can see this shopping list
     *
     * @param shoppingList the {@code shoppingList} that has to be notified
     * @param notifyMessage the id of the message
     * @param currentUser the user currently logged in
     * @return true if the notification was succesfully added
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    public boolean notifyUsers(ShoppingList shoppingList, Integer notifyMessage, User currentUser) throws DAOException;
    
    /**
     *  Gets a list containing the users that can see the shopping list 
     * @param shoppingList the shopping list 
     * @return  the list of users
     * @throws DAOException
     */
    public List<User> getShoppingListUsers(ShoppingList shoppingList) throws DAOException;
    
    /**
     *  Gets all the notifications for the user
     * @param user the user 
     * @return a list of all the notifications for the user
     * @throws DAOException
     */
    public List<Notification> getUserNotifications(User user) throws DAOException;
    
    /**
     *  Deletes a notification
     * @param shoppingList the shopping list that sent the notification
     * @param messageId the id of the message
     * @param user  the user that deletes the notification
     * @return true if the notification was successfully deleted
     * @throws DAOException
     */
    public boolean deleteNotification(ShoppingList shoppingList, Integer messageId, User user) throws DAOException;
}
