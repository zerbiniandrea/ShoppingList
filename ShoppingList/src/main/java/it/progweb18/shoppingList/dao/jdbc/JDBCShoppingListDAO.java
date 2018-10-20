package it.progweb18.shoppingList.dao.jdbc;

import it.progweb18.shoppingList.dao.ShoppingListDAO;
import it.progweb18.shoppingList.dao.entities.Guest;
import it.progweb18.shoppingList.dao.entities.Notification;
import it.progweb18.shoppingList.dao.entities.ShoppingList;
import it.progweb18.shoppingList.dao.entities.User;
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
 * The JDBC implementation of the {@link ShoppingListDAO} interface.
 */
public class JDBCShoppingListDAO extends JDBCDAO<ShoppingList, Integer> implements ShoppingListDAO {
    
    /**
     * The default constructor of the class.
     * @param con the connection to the persistence system.
     */
    public JDBCShoppingListDAO(Connection con) {
        super(con);
    }

    /**
     * Returns the number of {@link ShoppingList shopping_lists} stored on the
     * persistence system of the application.
     *
     * @return the number of records present into the storage system.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    @Override
    public Long getCount() throws DAOException {
        try (Statement stmt = CON.createStatement()) {
            ResultSet counter = stmt.executeQuery("SELECT COUNT(*) FROM shopping_lists");
            if (counter.next()) {
                return counter.getLong(1);
            }

        } catch (SQLException ex) {
            throw new DAOException("Impossible to count users", ex);
        }

        return 0L;
    }

    /**
     * Returns the {@link ShoppingList shopping_lists} with the primary key
     * equals to the one passed as parameter.
     *
     * @param primaryKey the {@code id} of the {@code shopping_list} to get.
     * @return the {@code shopping_list} with the id equals to the one passed as
     * parameter or {@code null} if no entities with that id are present into
     * the storage system.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    @Override
    public ShoppingList getByPrimaryKey(Integer primaryKey) throws DAOException {
        if (primaryKey == null) {
            throw new DAOException("primaryKey is null");
        }
        
        JDBCCategoryDAO categoryDao=new JDBCCategoryDAO(CON);
        
        try (PreparedStatement stm = CON.prepareStatement("SELECT * FROM shopping_lists WHERE id = ?")) {
            stm.setInt(1, primaryKey);
            try (ResultSet rs = stm.executeQuery()) {

                rs.next();
                ShoppingList shoppingList = new ShoppingList();
                shoppingList.setId(rs.getInt("id"));
                shoppingList.setName(rs.getString("name"));
                shoppingList.setDescription(rs.getString("description"));
                shoppingList.setCategory(categoryDao.getByPrimaryKey(rs.getInt("id_category")));

                return shoppingList;
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the shopping_list for the passed primary key", ex);
        }
    }

    /**
     * Returns the list of all the valid {@link ShoppingList shopping_lists}
     * stored by the storage system.
     *
     * @return the list of all the valid {@code shopping_lists}.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    @Override
    public List<ShoppingList> getAll() throws DAOException {
        List<ShoppingList> shoppingLists = new ArrayList<>();
        
        JDBCCategoryDAO categoryDao=new JDBCCategoryDAO(CON);
        
        try (Statement stm = CON.createStatement()) {
            try (ResultSet rs = stm.executeQuery("SELECT * FROM shopping_lists ORDER BY name")) {

                while (rs.next()) {
                    ShoppingList shoppingList = new ShoppingList();
                    shoppingList.setId(rs.getInt("id"));
                    shoppingList.setName(rs.getString("name"));
                    shoppingList.setDescription(rs.getString("description"));
                    shoppingList.setCategory(categoryDao.getByPrimaryKey(rs.getInt("id_category")));

                    shoppingLists.add(shoppingList);
                }
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of shopping_lists", ex);
        }

        return shoppingLists;
    }

    @Override
    public Integer insert(ShoppingList shoppingList) throws DAOException {
        if (shoppingList == null) {
            //TODO: Thinking to return an error instead of null.
            return null;
        }
        try (PreparedStatement ps = CON.prepareStatement("INSERT INTO shopping_lists (name, description,id_category) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, shoppingList.getName());
            ps.setString(2, shoppingList.getDescription());
            ps.setInt(3, shoppingList.getCategory().getId());
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                shoppingList.setId(rs.getInt(1));
            }
            
            return shoppingList.getId();
        } catch (SQLException ex) {
            throw new DAOException("Impossible to insert the new shopping_list", ex);
        }
    }
    
    @Override
    public ShoppingList update(ShoppingList shoppingList) throws DAOException {
        if (shoppingList == null) {
            //TODO: Thinking to return an error instead of null.
            return null;
        }
        
        Integer shoppingListId = shoppingList.getId();
        if (shoppingListId == null) {
            //TODO: Thinking to insert it as a new record.
            throw new DAOException("ShppiingList is not valid", new NullPointerException("ShoppingList id is null"));
        }
        
        ShoppingList oldShoppingList = null;
        try (PreparedStatement ps = CON.prepareStatement("SELECT * FROM shopping_lists WHERE id = ?")) {
            ps.setInt(1, shoppingListId);
            ResultSet oldResultSet = ps.executeQuery();
            
            if (oldResultSet.next()) {
                oldShoppingList = new ShoppingList();
                oldShoppingList.setId(shoppingListId);
                oldShoppingList.setName(oldResultSet.getString("name"));
                oldShoppingList.setDescription(oldResultSet.getString("description"));
            }
            
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the shopping_lists", ex);
        }
        
        try (PreparedStatement ps = CON.prepareStatement("UPDATE shopping_lists SET name = ?, description = ? WHERE id = ?")) {
            ps.setString(1, shoppingList.getName());
            ps.setString(2, shoppingList.getDescription());
            ps.setInt(3, shoppingListId);
            
            int count = ps.executeUpdate();
            if (count != 1) {
                throw new DAOException("Update affected an invalid number of records: " + count);
            }
            
            return oldShoppingList;
        } catch (SQLException ex) {
            throw new DAOException("Impossible to update the shopping_list", ex);
        }
    }
    
    @Override
    public boolean linkShoppingListToUser(ShoppingList shoppingList, User user) throws DAOException {
        if ((shoppingList == null) || (user == null)) {
            throw new DAOException("Shopping_list and user are mandatory fields", new NullPointerException("shopping_list or user are null"));
        }
        
        try (PreparedStatement ps = CON.prepareStatement("INSERT INTO users_shopping_lists (id_user, id_shopping_list) VALUES (?, ?)")) {
            ps.setInt(1, user.getId());
            ps.setInt(2, shoppingList.getId());

            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23") && ex.getSQLState().length() == 5) {
                //TODO: Duplicated key. The link already exists. Update the record data (modification data and user id)
                return false;
            }
            throw new DAOException("Impossible to link the passed shopping_list with the passed user", ex);
        }
    }
    
    @Override
    public boolean linkShoppingListToGuest(ShoppingList shoppingList, Guest guest) throws DAOException {
        if ((shoppingList == null) || (guest == null)) {
            throw new DAOException("Shopping_list and user are mandatory fields", new NullPointerException("shopping_list or user are null"));
        }
        
        try (PreparedStatement ps = CON.prepareStatement("INSERT INTO guests_shopping_lists (id_guest, id_shopping_list) VALUES (?, ?)")) {
            ps.setInt(1, guest.getId());
            ps.setInt(2, shoppingList.getId());

            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23") && ex.getSQLState().length() == 5) {
                //TODO: Duplicated key. The link already exists. Update the record data (modification data and user id)
                return false;
            }
            throw new DAOException("Impossible to link the passed shopping_list with the passed user", ex);
        }
    }
    
    @Override
    public List<ShoppingList> getByUserId(Integer userId) throws DAOException {
        if (userId == null) {
            throw new DAOException("userId is mandatory field", new NullPointerException("userId is null"));
        }
        
        JDBCCategoryDAO categoryDao=new JDBCCategoryDAO(CON);
        
        try (PreparedStatement stm = CON.prepareStatement("SELECT * FROM shopping_lists WHERE id IN (SELECT id_shopping_list FROM users_shopping_lists WHERE id_user = ?) ORDER BY name")) {
            List<ShoppingList> shoppingLists = new ArrayList<>();
            stm.setInt(1, userId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    ShoppingList shoppingList = new ShoppingList();
                    shoppingList.setId(rs.getInt("id"));
                    shoppingList.setName(rs.getString("name"));
                    shoppingList.setDescription(rs.getString("description"));
                    shoppingList.setCategory(categoryDao.getByPrimaryKey(rs.getInt("id_category")));

                    shoppingLists.add(shoppingList);
                }

                return shoppingLists;
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of users", ex);
        }
    }
    
    @Override
    public List<ShoppingList> getByuserId(Integer userId, Integer order) throws DAOException {
        if (userId == null) {
            throw new DAOException("userId is mandatory field", new NullPointerException("userId is null"));
        }
        
        JDBCCategoryDAO categoryDao=new JDBCCategoryDAO(CON);
        List<ShoppingList> shoppingLists = new ArrayList<>();
        PreparedStatement stm = null;
        
        try{
            switch(order){
                case 1:
                    stm = CON.prepareStatement("SELECT * FROM shopping_lists WHERE id IN (SELECT id_shopping_list FROM users_shopping_lists WHERE id_user = ?) ORDER BY name");
                    break;
                case 2:
                    stm = CON.prepareStatement("SELECT * FROM shopping_lists WHERE id IN (SELECT id_shopping_list FROM users_shopping_lists WHERE id_user = ?) ORDER BY name DESC");
                    break;
                case 3:
                    stm = CON.prepareStatement("SELECT shopping_lists.ID,shopping_lists.\"NAME\",shopping_lists.DESCRIPTION,shopping_lists.ID_CATEGORY FROM \n" +
                                                "shopping_lists INNER JOIN users_shopping_lists \n" +
                                                "ON shopping_lists.id=users_shopping_lists.id_shopping_list\n" +
                                                "AND users_shopping_lists.id_user = ? \n" +
                                                "INNER JOIN categories \n" +
                                                "ON shopping_lists.ID_CATEGORY=categories.ID\n" +
                                                "ORDER BY categories.\"NAME\"");
                    break;
                case 4:
                    stm = CON.prepareStatement("SELECT shopping_lists.ID,shopping_lists.\"NAME\",shopping_lists.DESCRIPTION,shopping_lists.ID_CATEGORY,categories.\"NAME\" FROM \n" +
                                                "shopping_lists INNER JOIN users_shopping_lists \n" +
                                                "ON shopping_lists.id=users_shopping_lists.id_shopping_list\n" +
                                                "AND users_shopping_lists.id_user = ? \n" +
                                                "INNER JOIN categories \n" +
                                                "ON shopping_lists.ID_CATEGORY=categories.ID\n" +
                                                "ORDER BY categories.\"NAME\" DESC");
                    break;
                case 5:
                    stm = CON.prepareStatement("SELECT shopping_lists.ID,shopping_lists.\"NAME\",shopping_lists.DESCRIPTION,shopping_lists.ID_CATEGORY FROM \n" +
                                                "shopping_lists INNER JOIN users_shopping_lists \n" +
                                                "ON shopping_lists.id=users_shopping_lists.id_shopping_list\n" +
                                                "AND users_shopping_lists.id_user = ? \n" +
                                                "INNER JOIN categories \n" +
                                                "ON shopping_lists.ID_CATEGORY=categories.ID\n" +
                                                "ORDER BY shopping_lists.id");
                    break;
                case 6:
                    stm = CON.prepareStatement("SELECT shopping_lists.ID,shopping_lists.\"NAME\",shopping_lists.DESCRIPTION,shopping_lists.ID_CATEGORY FROM \n" +
                                                "shopping_lists INNER JOIN users_shopping_lists \n" +
                                                "ON shopping_lists.id=users_shopping_lists.id_shopping_list\n" +
                                                "AND users_shopping_lists.id_user = ? \n" +
                                                "INNER JOIN categories \n" +
                                                "ON shopping_lists.ID_CATEGORY=categories.ID\n" +
                                                "ORDER BY shopping_lists.id DESC");
                    break;
                default: 
                    stm = CON.prepareStatement("SELECT * FROM shopping_lists WHERE id IN (SELECT id_shopping_list FROM users_shopping_lists WHERE id_user = ?) ORDER BY name");
            }
        
            stm.setInt(1, userId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    ShoppingList shoppingList = new ShoppingList();
                    shoppingList.setId(rs.getInt("id"));
                    shoppingList.setName(rs.getString("name"));
                    shoppingList.setDescription(rs.getString("description"));
                    shoppingList.setCategory(categoryDao.getByPrimaryKey(rs.getInt("id_category")));

                    shoppingLists.add(shoppingList);
                }

                return shoppingLists;
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of users", ex);
        }
    }
    
    @Override
    public List<ShoppingList> getByGuestId(Integer guestId) throws DAOException {
        if (guestId == null) {
            throw new DAOException("guestId is mandatory field", new NullPointerException("guestId is null"));
        }
        
        JDBCCategoryDAO categoryDao=new JDBCCategoryDAO(CON);
        
        try (PreparedStatement stm = CON.prepareStatement("SELECT * FROM shopping_lists WHERE id IN (SELECT id_shopping_list FROM guests_shopping_lists WHERE id_guest = ?) ORDER BY name")) {
            List<ShoppingList> shoppingLists = new ArrayList<>();
            stm.setInt(1, guestId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    ShoppingList shoppingList = new ShoppingList();
                    shoppingList.setId(rs.getInt("id"));
                    shoppingList.setName(rs.getString("name"));
                    shoppingList.setDescription(rs.getString("description"));
                    shoppingList.setCategory(categoryDao.getByPrimaryKey(rs.getInt("id_category")));

                    shoppingLists.add(shoppingList);
                }

                return shoppingLists;
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of users", ex);
        }
    }
    
    @Override
    public List<ShoppingList> getByGuestId(Integer guestId, Integer order) throws DAOException {
        if (guestId == null) {
            throw new DAOException("guestId is mandatory field", new NullPointerException("guestId is null"));
        }
        
        JDBCCategoryDAO categoryDao=new JDBCCategoryDAO(CON);
        List<ShoppingList> shoppingLists = new ArrayList<>();
        PreparedStatement stm = null;
        
        try{
            switch(order){
                case 1:
                    stm = CON.prepareStatement("SELECT * FROM shopping_lists WHERE id IN (SELECT id_shopping_list FROM guests_shopping_lists WHERE id_guest = ?) ORDER BY name");
                    //shoppingLists = getByGuestId(guestId);
                    break;
                case 2:
                    stm = CON.prepareStatement("SELECT * FROM shopping_lists WHERE id IN (SELECT id_shopping_list FROM guests_shopping_lists WHERE id_guest = ?) ORDER BY name DESC");
                    break;
                case 3:
                    stm = CON.prepareStatement("SELECT shopping_lists.ID,shopping_lists.\"NAME\",shopping_lists.DESCRIPTION,shopping_lists.ID_CATEGORY FROM \n" +
                                                "shopping_lists INNER JOIN guests_shopping_lists \n" +
                                                "ON shopping_lists.id=guests_shopping_lists.id_shopping_list\n" +
                                                "AND guests_shopping_lists.ID_GUEST = ? \n" +
                                                "INNER JOIN categories \n" +
                                                "ON shopping_lists.ID_CATEGORY=categories.ID\n" +
                                                "ORDER BY categories.\"NAME\"");
                    break;
                case 4:
                    stm = CON.prepareStatement("SELECT shopping_lists.ID,shopping_lists.\"NAME\",shopping_lists.DESCRIPTION,shopping_lists.ID_CATEGORY,categories.\"NAME\" FROM \n" +
                                                "shopping_lists INNER JOIN guests_shopping_lists \n" +
                                                "ON shopping_lists.id=guests_shopping_lists.id_shopping_list\n" +
                                                "AND guests_shopping_lists.ID_GUEST = ? \n" +
                                                "INNER JOIN categories \n" +
                                                "ON shopping_lists.ID_CATEGORY=categories.ID\n" +
                                                "ORDER BY categories.\"NAME\" DESC");
                    break;
                case 5:
                    stm = CON.prepareStatement("SELECT shopping_lists.ID,shopping_lists.\"NAME\",shopping_lists.DESCRIPTION,shopping_lists.ID_CATEGORY FROM \n" +
                                                "shopping_lists INNER JOIN guests_shopping_lists \n" +
                                                "ON shopping_lists.id=guests_shopping_lists.id_shopping_list\n" +
                                                "AND guests_shopping_lists.ID_GUEST = ? \n" +
                                                "INNER JOIN categories \n" +
                                                "ON shopping_lists.ID_CATEGORY=categories.ID\n" +
                                                "ORDER BY shopping_lists.id");
                    break;
                case 6:
                    stm = CON.prepareStatement("SELECT shopping_lists.ID,shopping_lists.\"NAME\",shopping_lists.DESCRIPTION,shopping_lists.ID_CATEGORY FROM \n" +
                                                "shopping_lists INNER JOIN guests_shopping_lists \n" +
                                                "ON shopping_lists.id=guests_shopping_lists.id_shopping_list\n" +
                                                "AND guests_shopping_lists.ID_GUEST = ? \n" +
                                                "INNER JOIN categories \n" +
                                                "ON shopping_lists.ID_CATEGORY=categories.ID\n" +
                                                "ORDER BY shopping_lists.id DESC");
                    break;
                default: 
                    stm = CON.prepareStatement("SELECT * FROM shopping_lists WHERE id IN (SELECT id_shopping_list FROM guests_shopping_lists WHERE id_guest = ?) ORDER BY name");
            }
        
            stm.setInt(1, guestId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    ShoppingList shoppingList = new ShoppingList();
                    shoppingList.setId(rs.getInt("id"));
                    shoppingList.setName(rs.getString("name"));
                    shoppingList.setDescription(rs.getString("description"));
                    shoppingList.setCategory(categoryDao.getByPrimaryKey(rs.getInt("id_category")));

                    shoppingLists.add(shoppingList);
                }

                return shoppingLists;
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of users", ex);
        }
    }
    
    @Override
    public boolean deleteUserList(ShoppingList shoppingList) throws DAOException {
        if(shoppingList == null){
            throw new DAOException("Shopping_list is a mandatory field", new NullPointerException("shopping_list is null"));  
        }
        
        try {
            PreparedStatement ps1 = CON.prepareStatement("DELETE FROM users_shopping_lists WHERE id_shopping_list= ? ");
            PreparedStatement ps2 = CON.prepareStatement("DELETE FROM shopping_lists WHERE id= ? ");
            ps1.setInt(1, shoppingList.getId());
            ps2.setInt(1, shoppingList.getId());
            int intPs1=ps1.executeUpdate();
            int intPs2=ps2.executeUpdate();
            
            if(intPs1 == intPs2)
                return intPs1 == 1;
            else
                return false;
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23") && ex.getSQLState().length() == 5) {
                //TODO: Duplicated key. The link already exists. Update the record data (modification data and user id)
                return false;
            }
            throw new DAOException("Impossible to delete the list", ex);
        }
    }

    @Override
    public boolean deleteGuestList(ShoppingList shoppingList) throws DAOException {
        if(shoppingList == null){
            throw new DAOException("Shopping_list is a mandatory field", new NullPointerException("shopping_list is null"));  
        }
        
        try {
            PreparedStatement ps1 = CON.prepareStatement("DELETE FROM guests_shopping_lists WHERE id_shopping_list= ? ");
            PreparedStatement ps2 = CON.prepareStatement("DELETE FROM shopping_lists WHERE id= ? ");
            ps1.setInt(1, shoppingList.getId());
            ps2.setInt(1, shoppingList.getId());
            int intPs1=ps1.executeUpdate();
            int intPs2=ps2.executeUpdate();
            
            if(intPs1 == intPs2)
                return intPs1 == 1;
            else
                return false;
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23") && ex.getSQLState().length() == 5) {
                //TODO: Duplicated key. The link already exists. Update the record data (modification data and user id)
                return false;
            }
            throw new DAOException("Impossible to delete the list", ex);
        }
    }

    @Override
    public boolean notifyUsers(ShoppingList shoppingList, Integer notifyMessage, User currentUser) throws DAOException {
        if(shoppingList == null || shoppingList.getId() == null || notifyMessage == null){
            throw new DAOException("Shopping_list and notifyMessage is a mandatory field", new NullPointerException("shopping_list or notifyMessage is null"));
        }
        
        boolean retValue = true;
        
        List<User> users = getShoppingListUsers(shoppingList);
        for(User user : users){
            if(user.getId() != currentUser.getId()){
                try (PreparedStatement ps = CON.prepareStatement("INSERT INTO notifications VALUES (?, ?, ?)")) {
                    ps.setInt(1, notifyMessage);
                    ps.setInt(2, user.getId());
                    ps.setInt(3, shoppingList.getId());

                    retValue=(retValue && (ps.executeUpdate() == 1));
                } catch (SQLException ex) {
                    if (ex.getSQLState().startsWith("23") && ex.getSQLState().length() == 5) {
                        //TODO: Duplicated key. The link already exists. Update the record data (modification data and user id)
                        return false;
                    }
                    throw new DAOException("Impossible to add notifications for this shopping list", ex);
                }
            }
        }
        
        return retValue;
    }

    
    @Override
    public List<User> getShoppingListUsers(ShoppingList shoppingList) throws DAOException {
        if (shoppingList == null || shoppingList.getId() == null) {
            throw new DAOException("shoppingList is mandatory field", new NullPointerException("shoppingList is null"));
        }
        
        try (PreparedStatement stm = CON.prepareStatement("SELECT id_user FROM users_shopping_lists WHERE id_shopping_list = ?")) {
            List<User> users = new ArrayList<>();
            stm.setInt(1, shoppingList.getId());
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt(1));
                    users.add(user);
                }

                return users;
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of users", ex);
        }
    }

    @Override
    public List<Notification> getUserNotifications(User user) throws DAOException {
        if (user == null || user.getId() == null) {
            throw new DAOException("user is null");
        }
        
        try (PreparedStatement stm = CON.prepareStatement("SELECT * FROM notifications WHERE id_user = ?")) {
            List<Notification> notifications = new ArrayList<>();
            stm.setInt(1, user.getId());
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Notification notification = new Notification();
                    notification.setMessageId(rs.getInt(1));
                    notification.setShoppingList(getByPrimaryKey(rs.getInt(3)));

                    notifications.add(notification);
                }

                return notifications;
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of users", ex);
        }
    }

    @Override
    public boolean deleteNotification(ShoppingList shoppingList, Integer messageId, User user) throws DAOException {
        if(shoppingList == null || shoppingList.getId() == null){
            throw new DAOException("Shopping_list is a mandatory field", new NullPointerException("shopping_list is null"));  
        }
        if(messageId == null){
            throw new DAOException("messageId is a mandatory field", new NullPointerException("messageId is null"));  
        }
        if(user == null || user.getId() == null){
            throw new DAOException("user is a mandatory field", new NullPointerException("user is null"));  
        }
        
        try {
            PreparedStatement ps = CON.prepareStatement("DELETE FROM notifications WHERE id_shopping_list= ? AND id_message = ? AND id_user = ? ");
            ps.setInt(1, shoppingList.getId());
            ps.setInt(2, messageId);
            ps.setInt(3, user.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23") && ex.getSQLState().length() == 5) {
                //TODO: Duplicated key. The link already exists. Update the record data (modification data and user id)
                return false;
            }
            throw new DAOException("Impossible to delete the list", ex);
        }
    }
}
