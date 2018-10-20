package it.progweb18.shoppingList.dao.entities;

/**
 * The entity that describe a {@code user} entity.
 */
public class User {
    private Integer id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String avatarPath;
    private String type;
    
    private Integer shoppingListsCount;

    /**
     * Returns the primary key of this user entity.
     * @return the id of the user entity.
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * Sets the new primary key of this user entity.
     * @param id the new id of this user entity.
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * Returns the unique email of this user entity.
     * @return the email of this user entity.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the new email of this user entity.
     * @param email the new email of this user entity.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the password of this user entity.
     * @return the password of this user entity.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the new password of this user entity.
     * @param password the new password of this user entity.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the first name of this user entity.
     * @return the first name of this user entity.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the new first name of this user entity.
     * @param firstName the new first name of this user entity.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name of this user entity.
     * @return the last name of this user entity.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the new last name of this user entity.
     * @param lastName the new last name of this user entity.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Returns the number of shopping-lists shared with this user entity.
     * @return the number of shopping-lists shared with this user entity.
     */
    public Integer getShoppingListsCount() {
        return shoppingListsCount;
    }
    
    /**
     * Returns the avatar path of this user.
     * @return the avatar path of this user.
     */
    public String getAvatarPath() {
        return avatarPath;
    }
    
    /**
     * Sets the avatar path of this user.
     * @param avatarPath the avatar path of this user.
     */
    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
    
    /**
     * Returns the type of this user.
     * @return the type of this user.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of this user.
     * @param type the type of this user.
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * Sets the new amount of shopping-lists shared with this user entity.
     * @param shoppingListsCount the new amount of shopping-lists shared with this user entity.
     */
    public void setShoppingListsCount(Integer shoppingListsCount) {
        this.shoppingListsCount = shoppingListsCount;
    }
}