package it.progweb18.shoppingList.dao.entities;

/**
 * The entity that describe a {@code category} entity.
 */
public class Category {
    private Integer id;
    private String name;
    private String description;
    
    /**
     * Returns the primary key of this category entity.
     * @return the id of the category entity.
     */
    public Integer getId(){
        return id;
    }
    
    /**
     * Sets the new primary key of this category entity.
     * @param id the new id of this category entity.
     */
    public void setId(Integer id){
        this.id = id;
    }
    
    /**
     * Returns the name of this category entity.
     * @return the name of this category entity.
     */
    public String getName(){
        return name;
    }
    
    /**
     * Sets the new name of this category entity.
     * @param name the new name of this category entity.
     */
    public void setName(String name){
        this.name=name;
    }
    
    /**
     * Returns the description of this category entity.
     * @return the description of this category entity.
     */
    public String getDescription(){
        return description;
    }
    
    /**
     * Sets the new description of this category entity.
     * @param description the new description of this category entity.
     */
    public void setDescription(String description){
        this.description=description;
    }
}
