package it.progweb18.shoppingList.dao.entities;

/**
 * The entity that describes a {@code product} entity.
 */
public class Product {
    private Integer id;
    private String name;
    private String description;
    private Category category;
    private Integer quantity = 2;
    
    /**
     * Returns the primary key of this product entity.
     * @return the id of the product entity.
     */
    public Integer getId(){
        return id;
    }
    
    /**
     * Sets the new primary key of this product entity.
     * @param id the new id of this product entity.
     */
    public void setId(Integer id){
        this.id = id;
    }
    
    /**
     * Returns the name of this product entity.
     * @return the name of this product entity.
     */
    public String getName(){
        return name;
    }
    
    /**
     * Sets the new name of this product entity.
     * @param name the new name of this product entity.
     */
    public void setName(String name){
        this.name=name;
    }
    
    /**
     * Returns the description of this product entity.
     * @return the description of this product entity.
     */
    public String getDescription(){
        return description;
    }
    
    /**
     * Sets the new description of this product entity.
     * @param description the new description of this product entity.
     */
    public void setDescription(String description){
        this.description=description;
    }
    
    /**
     * Returns the category of this product entity.
     * @return the category of this product entity.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets the new category of this product entity.
     * @param category the new category of this product entity.
     */
    public void setCategory(Category category) {
        this.category = category;
    }
    
    /**
     * Returns the quantity of this product entity.
     * @return the quantity of this product entity.
     */
    public Integer getQuantity(){
        return quantity;
    }
    
    /**
     * Sets the new quantity of this product entity.
     * @param quantity the new quantity of this product entity.
     */
    public void setQuantity(Integer quantity){
        this.quantity=quantity;
    }
}
