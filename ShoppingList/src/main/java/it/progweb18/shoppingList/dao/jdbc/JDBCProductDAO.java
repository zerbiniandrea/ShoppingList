package it.progweb18.shoppingList.dao.jdbc;

import it.progweb18.shoppingList.dao.ProductDAO;
import it.progweb18.shoppingList.dao.entities.ShoppingList;
import it.progweb18.shoppingList.dao.entities.Product;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.jdbc.JDBCDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * The JDBC implementation of the {@link ProductDAO} interface.
 */
public class JDBCProductDAO extends JDBCDAO<Product, Integer> implements ProductDAO{
    
    /**
     * The default constructor of the class.
     *
     * @param con the connection to the persistence system.
     */
    public JDBCProductDAO(Connection con) {
        super(con);
    }
    
    /**
     * Returns the number of {@link Product products} stored on the persistence system
     * of the application.
     *
     * @return the number of records present into the storage system.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    @Override
    public Long getCount() throws DAOException {
        try (Statement stmt = CON.createStatement()) {
            ResultSet counter = stmt.executeQuery("SELECT COUNT(*) FROM products");
            if (counter.next()) {
                return counter.getLong(1);
            }

        } catch (SQLException ex) {
            throw new DAOException("Impossible to count users", ex);
        }

        return 0L;
    }

    /**
     * Returns the {@link Product products} with the primary key equals to the one
     * passed as parameter.
     *
     * @param primaryKey the {@code id} of the {@code product} to get.
     * @return the {@code product} with the id equals to the one passed as
     * parameter or {@code null} if no entities with that id are present into
     * the storage system.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    @Override
    public Product getByPrimaryKey(Integer primaryKey) throws DAOException {
        if (primaryKey == null) {
            throw new DAOException("primaryKey is null");
        }
        
        JDBCCategoryDAO categoryDao=new JDBCCategoryDAO(CON);
        
        try (PreparedStatement stm = CON.prepareStatement("SELECT * FROM products WHERE id = ?")) {
            stm.setInt(1, primaryKey);
            try (ResultSet rs = stm.executeQuery()) {
                rs.next();
                
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setCategory(categoryDao.getByPrimaryKey(rs.getInt("id_category")));
                
                return product;
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the product for the passed primary key", ex);
        }
    }

    /**
     * Returns the list of all the valid {@link Product products}
     * stored by the storage system.
     *
     * @return the list of all the valid {@code products}.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    @Override
    public List<Product> getAll() throws DAOException {
        List<Product> products = new ArrayList<>();
        
        JDBCCategoryDAO categoryDao=new JDBCCategoryDAO(CON);
        
        try (Statement stm = CON.createStatement()) {
            try (ResultSet rs = stm.executeQuery("SELECT * FROM products ORDER BY name")) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setDescription(rs.getString("description"));
                    product.setCategory(categoryDao.getByPrimaryKey(rs.getInt("id_category")));

                    products.add(product);
                }
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of product", ex);
        }

        return products;
    }

    @Override
    public Integer insert(Product product) throws DAOException {
        if (product == null) {
            //TODO: Thinking to return an error instead of null.
            return null;
        }
        try (PreparedStatement ps = CON.prepareStatement("INSERT INTO products (name, description,id_category) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setInt(3, product.getCategory().getId());
            
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                product.setId(rs.getInt(1));
            }
            
            return product.getId();
        } catch (SQLException ex) {
            throw new DAOException("Impossible to insert the new product", ex);
        }
    }

    @Override
    public Product update(Product product) throws DAOException {
        if (product == null) {
            //TODO: Thinking to return an error instead of null.
            return null;
        }
        
        Integer productId = product.getId();
        if (productId == null) {
            //TODO: Thinking to insert it as a new record.
            throw new DAOException("Product is not valid", new NullPointerException("Product id is null"));
        }
        
        Product oldProduct = null;
        try (PreparedStatement ps = CON.prepareStatement("SELECT * FROM products WHERE id = ?")) {
            ps.setInt(1, productId);
            ResultSet oldResultSet = ps.executeQuery();
            
            if (oldResultSet.next()) {
                oldProduct = new Product();
                oldProduct.setId(productId);
                oldProduct.setName(oldResultSet.getString("name"));
                oldProduct.setDescription(oldResultSet.getString("description"));
            }
            
        } catch (SQLException ex) {
            //TODO: log the exception
        }
        
        try (PreparedStatement ps = CON.prepareStatement("UPDATE products SET name = ?, description = ? WHERE id = ?")) {
            
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setInt(3, productId);
            
            int count = ps.executeUpdate();
            if (count != 1) {
                throw new DAOException("Update affected an invalid number of records: " + count);
            }
            
            return oldProduct;
        } catch (SQLException ex) {
            throw new DAOException("Impossible to update the product", ex);
        }
    }

    @Override
    public boolean delete(Product product) throws DAOException {
        if(product == null){
            throw new DAOException("Product is a mandatory field", new NullPointerException("Product is null"));  
        }
        
        try {
            PreparedStatement ps1 = CON.prepareStatement("DELETE FROM products_shopping_lists WHERE id_product= ? ");
            PreparedStatement ps2 = CON.prepareStatement("DELETE FROM products WHERE id= ? ");
            ps1.setInt(1, product.getId());
            ps2.setInt(1, product.getId());
            int intPs1=ps1.executeUpdate();
            int intPs2=ps2.executeUpdate();
            
            if(intPs1 == intPs2)
                return intPs1 == 1;
            else
                return false;
        } catch (SQLException ex) {
            throw new DAOException("Impossible to delete the list", ex);
        }
    }
    
    @Override
    public boolean linkProductToShoppingList(Product product, ShoppingList shoppingList, Integer quantity) throws DAOException {
        if ((shoppingList == null) || (product == null)) {
            throw new DAOException("Shopping_list and product are mandatory fields", new NullPointerException("shopping_list or product are null"));
        }
        
        if(quantity == null || quantity < 0)
            return false;

        try (PreparedStatement ps = CON.prepareStatement("INSERT INTO products_shopping_lists (id_product, id_shopping_list,quantity) VALUES (?, ?, ?)")) {
            ps.setInt(1, product.getId());
            ps.setInt(2, shoppingList.getId());
            ps.setInt(3, quantity);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23") && ex.getSQLState().length() == 5) {
                //TODO: Duplicated key. The link already exists. Update the record data (modification data and user id)
                return false;
            }
            throw new DAOException("Impossible to link the passed shopping_list with the passed product", ex);
        }

    }

    @Override
    public List<Product> getByShoppingListId(Integer shoppingListId) throws DAOException {
        if (shoppingListId == null) {
            throw new DAOException("shoppingListId is mandatory field", new NullPointerException("shoppingListId is null"));
        }
        
        JDBCCategoryDAO categoryDao=new JDBCCategoryDAO(CON);
        
        try (PreparedStatement stm = CON.prepareStatement("SELECT * FROM products INNER JOIN products_shopping_lists ON products.id = products_shopping_lists.id_product AND id_shopping_list = ?")) {
            List<Product> products = new ArrayList<>();
            stm.setInt(1, shoppingListId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setDescription(rs.getString("description"));
                    product.setCategory(categoryDao.getByPrimaryKey(rs.getInt("id_category")));
                    product.setQuantity(rs.getInt("quantity"));
                    products.add(product);
                }

                return products;
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of users", ex);
        }
    }

    @Override
    public boolean deleteProductFromList(Product product, ShoppingList shoppingList) throws DAOException {
        if(shoppingList == null){
            throw new DAOException("Shopping_list is a mandatory fields", new NullPointerException("shopping_list is null"));  
        }
        
        try (PreparedStatement ps = CON.prepareStatement("DELETE FROM products_shopping_lists WHERE id_product = ? AND id_shopping_list = ? ")) {
            ps.setInt(1, product.getId());
            ps.setInt(2, shoppingList.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23") && ex.getSQLState().length() == 5) {
                //TODO: Duplicated key. The link already exists. Update the record data (modification data and user id)
                return false;
            }
            throw new DAOException("Impossible to delete the product from the list", ex);
        }
    }
}
