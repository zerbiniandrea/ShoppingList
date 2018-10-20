package it.progweb18.shoppingList.dao.jdbc;

import it.progweb18.shoppingList.dao.CategoryDAO;
import it.progweb18.shoppingList.dao.entities.Category;
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
 * The JDBC implementation of the {@link CategoryDAO} interface.
 */
public class JDBCCategoryDAO extends JDBCDAO<Category, Integer> implements CategoryDAO{
    
    /**
     * The default constructor of the class.
     *
     * @param con the connection to the persistence system.
     */
    public JDBCCategoryDAO(Connection con) {
        super(con);
    }
    
    /**
     * Returns the number of {@link Category categories} stored on the persistence system
     * of the application.
     *
     * @return the number of records present into the storage system.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    @Override
    public Long getCount() throws DAOException {
        try (Statement stmt = CON.createStatement()) {
            ResultSet counter = stmt.executeQuery("SELECT COUNT(*) FROM categories");
            if (counter.next()) {
                return counter.getLong(1);
            }

        } catch (SQLException ex) {
            throw new DAOException("Impossible to count users", ex);
        }

        return 0L;
    }

    /**
     * Returns the {@link Category category} with the primary key equals to the one
     * passed as parameter.
     *
     * @param primaryKey the {@code id} of the {@code category} to get.
     * @return the {@code category} with the id equals to the one passed as
     * parameter or {@code null} if no entities with that id are present into
     * the storage system.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    @Override
    public Category getByPrimaryKey(Integer primaryKey) throws DAOException {
        if (primaryKey == null) {
            throw new DAOException("primaryKey is null");
        }
        try (PreparedStatement stm = CON.prepareStatement("SELECT * FROM categories WHERE id = ?")) {
            stm.setInt(1, primaryKey);
            try (ResultSet rs = stm.executeQuery()) {
                rs.next();
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));

                return category;
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the category for the passed primary key", ex);
        }
    }

    /**
     * Returns the list of all the valid {@link Category categories} stored by the
     * storage system.
     *
     * @return the list of all the valid {@code categories}.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    @Override
    public List<Category> getAll() throws DAOException {
        List<Category> categories = new ArrayList<>();

        try (Statement stm = CON.createStatement()) {
            try (ResultSet rs = stm.executeQuery("SELECT * FROM categories WHERE NOT id=1 ORDER BY name")) {

                while (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setName(rs.getString("name"));
                    category.setDescription(rs.getString("description"));

                    categories.add(category);
                }
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of categories", ex);
        }

        return categories;
    }

    @Override
    public Integer insert(Category category) throws DAOException {
        if (category == null) {
            //TODO: Thinking to return an error instead of null.
            return null;
        }
        try (PreparedStatement ps = CON.prepareStatement("INSERT INTO categories (name, description) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());

            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                category.setId(rs.getInt(1));
            }
            
            return category.getId();
        } catch (SQLException ex) {
            throw new DAOException("Impossible to insert the new category", ex);
        }
    }

    @Override
    public Category update(Category category) throws DAOException {
        if (category == null) {
            throw new DAOException("Impossible to update the category");
        }
        
        Integer categoryId = category.getId();
        if (categoryId == null) {
            throw new DAOException("Category is not valid", new NullPointerException("Category id is null"));
        }
        
        Category oldCategory = null;
        try (PreparedStatement ps = CON.prepareStatement("SELECT * FROM categories WHERE id = ?")) {
            ps.setInt(1, categoryId);
            ResultSet oldResultSet = ps.executeQuery();
            
            if (oldResultSet.next()) {
                oldCategory = new Category();
                oldCategory.setId(categoryId);
                oldCategory.setName(oldResultSet.getString("name"));
                oldCategory.setDescription(oldResultSet.getString("description"));
            }
            
        } catch (SQLException ex) {
            throw new DAOException("Impossible to select the category", ex);
        }
        
        try (PreparedStatement ps = CON.prepareStatement("UPDATE categories SET name = ?, description = ? WHERE id = ?")) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, categoryId);
            
            int count = ps.executeUpdate();
            if (count != 1) {
                throw new DAOException("Update affected an invalid number of records: " + count);
            }
            
            return oldCategory;
        } catch (SQLException ex) {
            throw new DAOException("Impossible to update the category", ex);
        }
    }
    
    @Override
    public boolean delete(Category category) throws DAOException {
        if(category == null){
            throw new DAOException("Category is a mandatory field", new NullPointerException("Category is null"));  
        }
        
        try {
            PreparedStatement ps_products = CON.prepareStatement("UPDATE products SET  id_category=1 WHERE id_category = ?");
            PreparedStatement ps_shoppinglist = CON.prepareStatement("UPDATE shopping_lists SET  id_category=1 WHERE id_category = ?");
            PreparedStatement ps_category = CON.prepareStatement("DELETE FROM categories WHERE id= ? ");
            ps_products.setInt(1, category.getId());
            ps_shoppinglist.setInt(1, category.getId());
            ps_category.setInt(1, category.getId());
            int intProducts=ps_products.executeUpdate();
            int intShoppingLists=ps_shoppinglist.executeUpdate();
            int intCategory=ps_category.executeUpdate();
            
            if((intProducts == intShoppingLists) && (intShoppingLists == intCategory))
                return intProducts == 1;
            else
                return false;
        } catch (SQLException ex) {
            throw new DAOException("Impossible to delete the list", ex);
        }
    }
}
