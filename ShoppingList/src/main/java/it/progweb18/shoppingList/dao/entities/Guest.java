package it.progweb18.shoppingList.dao.entities;

/**
 * The entity that describes a {@code guest} entity.
 */
public class Guest {
    private Integer id=-1;
    private Integer shoppingListsCount;

    /**
     * Returns the primary key of this guest entity.
     * @return the id of the guest entity.
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * Sets the new primary key of this guest entity.
     * @param id the new id of this guest entity.
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * Returns the number of shopping-lists shared with this guest entity.
     * @return the number of shopping-lists shared with this guest entity.
     */
    public Integer getShoppingListsCount() {
        return shoppingListsCount;
    }
    
        
    /**
     * Sets the new amount of shopping-lists shared with this guest entity.
     * @param shoppingListsCount the new amount of shopping-lists shared with this guest entity.
     */
    public void setShoppingListsCount(Integer shoppingListsCount) {
        this.shoppingListsCount = shoppingListsCount;
    }
    
}