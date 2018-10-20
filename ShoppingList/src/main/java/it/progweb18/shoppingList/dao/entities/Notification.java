package it.progweb18.shoppingList.dao.entities;

/**
 * The entity that describe a {@code category} entity.
 */
public class Notification {
    private Integer messageId;
    private ShoppingList shoppingList;
    private User user;
    
    /**
     * Returns the messageId of this product entity.
     * @return the messageId of the product entity.
     */
    public Integer getMessageId(){
        return messageId;
    }
    
    /**
     * Sets the new messageId of this product entity.
     * @param messageId the new messageId of this product entity.
     */
    public void setMessageId(Integer messageId){
        this.messageId = messageId;
    }
    
    /**
     * Returns the shoppingList of this product entity.
     * @return the shoppingList of the product entity.
     */
    public ShoppingList getShoppingList(){
        return shoppingList;
    }
    
    /**
     * Sets the new shoppingList of this product entity.
     * @param shoppingList the new shoppingList of this product entity.
     */
    public void setShoppingList(ShoppingList shoppingList){
        this.shoppingList = shoppingList;
    }
    
    /**
     * Returns the user of this product entity.
     * @return the user of the product entity.
     */
    public User getUser(){
        return user;
    }
    
    /**
     * Sets the new user of this product entity.
     * @param user the new user of this product entity.
     */
    public void setUser(User user){
        this.user = user;
    }
    
}
