package it.progweb18.shoppingList.dao.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * The entity that describes a {@code shopping-list} entity.
 */
public class ShoppingList {
    private Integer id;
    private String name;
    private String description;
    private Category category;
    private List<Product> products;
    
    /**
     * Initialize products list
     */
    public ShoppingList() {
        this.products = new ArrayList<>();
    }

    /**
     * Returns the primary key of this to-do entity.
     * @return the id of the shopping-list entity.
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * Sets the new primary key of this shopping-list entity.
     * @param id the new id of this shopping-list entity.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the name of this shopping-list entity.
     * @return the name of this shopping-list entity.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the new name of this shopping-list entity.
     * @param name the new name of this shopping-list entity.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the description of this shopping-list entity.
     * @return the description of this shopping-list entity.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the new description of this shopping-list entity.
     * @param description the new description of this shopping-list entity.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Returns the category of this shopping-list entity.
     * @return the category of this shopping-list entity.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets the new category of this shopping-list entity.
     * @param category the new category of this shopping-list entity.
     */
    public void setCategory(Category category) {
        this.category = category;
    }
    
    /**
     * Adds a new product to the shopping-list
     * @param product the product added to the shopping-list
     */
    public void addProduct(Product product) {
        this.products.add(product);
    }
    
    /**
     * Gets the list of all products contained in this shopping-list
     * @return the products contained in this shopping-list
     */
    public List<Product> getProducts() {
        return products;
    }
    
    /**
     * Changes the list of products contained in this shopping-list
     * @param p the new products list
     */
    public void setProducts(List<Product> p) {
        products=p;
    }
}
