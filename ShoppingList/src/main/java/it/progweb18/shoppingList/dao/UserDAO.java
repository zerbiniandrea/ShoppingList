package it.progweb18.shoppingList.dao;

import it.progweb18.shoppingList.dao.entities.User;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.DAO;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;

/**
 * All concrete DAOs must implement this interface to handle the persistence 
 * system that interact with {@link User users}.
 */
public interface UserDAO extends DAO<User, Integer> {
    /**
     * Returns the {@link User user} with the given {@code email} and
     * {@code password}.
     * @param email the email of the user to get.
     * @param password the password of the user to get.
     * @return the {@link User user} with the given {@code username} and
     * {@code password}..
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    public User getByEmailAndPassword(String email, String password) throws DAOException;
    
    /**
     * Returns the list id of the user with the email passed as the parameter.
     *
     * @param userEmail the {@code email} of the {@code user} for which retrieve 
     * the id.
     * @return the id for the user with that email.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    public int getByEmail(String userEmail) throws DAOException;
    
    /**
     * Returns the user with the id passed as the parameter.
     * @param id the id of the user
     * @return the user with that id.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    public User getById(int id) throws DAOException;
    
    /**
     * Returns true if the email has already been used.
     *
     * @param userEmail the {@code email} of the {@code user} to check
     * @return true if the email has already been used, false otherwise.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    public boolean alreadyExists(String userEmail) throws DAOException;
    
    /**
     * Update the user passed as parameter and returns it.
     * @param user the user used to update the persistence system.
     * @return the updated user.
     * @throws DAOException if an error occurred during the action.
     */
    public User update(User user) throws DAOException;
    
    /**
     * Create the user
     * @param email user's email
     * @param password user's password
     * @param name user's name
     * @param lastname user's last name
     * @param avatarpath user's avatar
     * @return the updated user.
     * @throws DAOException if an error occurred during the action.
     */
    public User create(String email, String password, String name, String lastname, String avatarpath) throws DAOException;
    
    /**
     * Add the user to the list of not verified users
     * @param email user's email
     * @param password user's password
     * @param name user's name
     * @param lastname user's last name
     * @param avatarpath user's avatar
     * @return the string that the user must use to get verified.
     * @throws DAOException if an error occurred during the action.
     */
    public String createNotVerified(String email, String password, String name, String lastname, String avatarpath) throws DAOException;
    
    /**
     * Creates a random alpha-numeric string
     * @param count the length of the string
     * @return the string that the user must use to get verified.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    public String buildRandomAlphaNumericString(int count) throws DAOException;
    
    /**
     * Verifies the user associated with that code
     * @param code the code for the verification
     * @return true if the user was verified
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    public User deleteByVerificationCode(String code)  throws DAOException;
    
    /**
     * Deletes the user from the list of non verified users
     * @param code the code for the user
     * @return true if the user was deleted
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    public boolean deleteNotVerified(String code)  throws DAOException;
    
    /**
     * Resets the password of the user passed as a parameter
     * @param email the user's email
     * @return true if the user's password was changed successfully
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    public boolean resetPassword(String email)  throws DAOException;
}
