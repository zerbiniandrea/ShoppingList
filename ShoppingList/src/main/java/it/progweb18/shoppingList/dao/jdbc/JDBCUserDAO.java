package it.progweb18.shoppingList.dao.jdbc;

import it.progweb18.shoppingList.dao.UserDAO;
import it.progweb18.shoppingList.dao.entities.GoogleMail;
import it.progweb18.shoppingList.dao.entities.User;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.jdbc.JDBCDAO;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The JDBC implementation of the {@link UserDAO} interface.
 */
public class JDBCUserDAO extends JDBCDAO<User, Integer> implements UserDAO {

    /**
     * The default constructor of the class.
     *
     * @param con the connection to the persistence system.
     */
    public JDBCUserDAO(Connection con) {
        super(con);
    }

    /**
     * Returns the number of {@link User users} stored on the persistence system
     * of the application.
     *
     * @return the number of records present into the storage system.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    @Override
    public Long getCount() throws DAOException {
        try (Statement stmt = CON.createStatement()) {
            ResultSet counter = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (counter.next()) {
                return counter.getLong(1);
            }

        } catch (SQLException ex) {
            throw new DAOException("Impossible to count users", ex);
        }

        return 0L;
    }

    /**
     * Returns the {@link User user} with the primary key equals to the one
     * passed as parameter.
     *
     * @param primaryKey the {@code id} of the {@code user} to get.
     * @return the {@code user} with the id equals to the one passed as
     * parameter or {@code null} if no entities with that id are present into
     * the storage system.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    @Override
    public User getByPrimaryKey(Integer primaryKey) throws DAOException {
        if (primaryKey == null) {
            throw new DAOException("primaryKey is null");
        }
        try (PreparedStatement stm = CON.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            stm.setInt(1, primaryKey);
            try (ResultSet rs = stm.executeQuery()) {

                rs.next();
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setFirstName(rs.getString("name"));
                user.setLastName(rs.getString("lastname"));
                user.setAvatarPath(rs.getString("avatar_path"));
                user.setType(rs.getString("type"));

                try (PreparedStatement todoStatement = CON.prepareStatement("SELECT count(*) FROM USERS_SHOPPING_LISTS WHERE id_user = ?")) {
                    todoStatement.setInt(1, user.getId());

                    ResultSet counter = todoStatement.executeQuery();
                    counter.next();
                    user.setShoppingListsCount(counter.getInt(1));
                }

                return user;
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the user for the passed primary key", ex);
        }
    }

    /**
     * Returns the {@link User user} with the given {@code email} and
     * {@code password}.
     *
     * @param email the email of the user to get.
     * @param password the password of the user to get.
     * @return the {@link User user} with the given {@code email} and
     * {@code password}..
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    @Override
    public User getByEmailAndPassword(String email, String password) throws DAOException {
        if ((email == null) || (password == null)) {
            throw new DAOException("Email and password are mandatory fields", new NullPointerException("email or password are null"));
        }

        try (PreparedStatement stm = CON.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?")) {
            stm.setString(1, email);
            stm.setString(2, password);
            try (ResultSet rs = stm.executeQuery()) {
                PreparedStatement shoppingListStatement = CON.prepareStatement("SELECT count(*) FROM users_shopping_lists WHERE id_user = ?");

                int count = 0;
                while (rs.next()) {
                    count++;
                    if (count > 1) {
                        throw new DAOException("Unique constraint violated! There are more than one user with the same email! WHY???");
                    }
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setFirstName(rs.getString("name"));
                    user.setLastName(rs.getString("lastname"));
                    user.setAvatarPath(rs.getString("avatar_path"));
                    user.setType(rs.getString("type"));

                    shoppingListStatement.setInt(1, user.getId());

                    ResultSet counter = shoppingListStatement.executeQuery();
                    counter.next();
                    user.setShoppingListsCount(counter.getInt(1));

                    return user;
                }

                if (!shoppingListStatement.isClosed()) {
                    shoppingListStatement.close();
                }

                return null;
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of users", ex);
        }
    }

    
    @Override
    public int getByEmail(String userEmail) throws DAOException {
        int id;
        
        if (userEmail == null) {
            throw new DAOException("email is mandatory field", new NullPointerException("email is null"));
        }
        
        try (PreparedStatement stm = CON.prepareStatement("SELECT id FROM users WHERE email = ?")) {
            stm.setString(1,userEmail);
            try (ResultSet rs = stm.executeQuery()) {
                rs.next();
                id=rs.getInt("id");
            }
            
            return id;
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of users", ex);
        }
    }
    
    
    @Override
    public User getById(int id) throws DAOException {        
        if (id <= 0) {
            throw new DAOException("id is mandatory field", new NullPointerException("userId is null"));
        }
        
        try (PreparedStatement stm = CON.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            stm.setInt(1,id);
            try (ResultSet rs = stm.executeQuery()) {
                User user=new User();
                rs.next();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setFirstName(rs.getString("name"));
                user.setLastName(rs.getString("lastname"));
                user.setAvatarPath(rs.getString("avatar_path"));
                user.setType(rs.getString("type"));
                return user;
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of users", ex);
        }
    }
    
    @Override
    public boolean alreadyExists(String userEmail) throws DAOException {      
        if (userEmail == null) {
            throw new DAOException("userId is mandatory field", new NullPointerException("userId is null"));
        }
        
        try (PreparedStatement stm = CON.prepareStatement("SELECT id FROM users WHERE email = ?")) {
            stm.setString(1,userEmail);
            try (ResultSet rs = stm.executeQuery()) {
                if(rs.next())
                    return true;
            }
            
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of users", ex);
        }
        
        try (PreparedStatement stm = CON.prepareStatement("SELECT code FROM not_verified_users WHERE email = ?")) {
            stm.setString(1,userEmail);
            try (ResultSet rs = stm.executeQuery()) {
                if(rs.next())
                    return true;
            }
            
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of users", ex);
        }
        return false;
    }
    
    /**
     * Returns the list of all the valid {@link User users} stored by the
     * storage system.
     *
     * @return the list of all the valid {@code users}.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     */
    @Override
    public List<User> getAll() throws DAOException {
        List<User> users = new ArrayList<>();

        try (Statement stm = CON.createStatement()) {
            try (ResultSet rs = stm.executeQuery("SELECT * FROM users ORDER BY lastname")) {

                PreparedStatement shoppingListsStatement = CON.prepareStatement("SELECT count(*) FROM users_shopping_lists WHERE id_user = ?");

                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setFirstName(rs.getString("name"));
                    user.setLastName(rs.getString("lastname"));
                    user.setAvatarPath(rs.getString("avatar_path"));
                    user.setType(rs.getString("type"));

                    shoppingListsStatement.setInt(1, user.getId());

                    ResultSet counter = shoppingListsStatement.executeQuery();
                    counter.next();
                    user.setShoppingListsCount(counter.getInt(1));

                    users.add(user);
                }
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of users", ex);
        }

        return users;
    }
    
    /**
     * Update the user passed as parameter and returns it.
     *
     * @param user the user used to update the persistence system.
     * @return the updated user.
     * @throws DAOException if an error occurred during the action.
     */
    @Override
    public User update(User user) throws DAOException {
        if (user == null) {
            throw new DAOException("parameter not valid", new IllegalArgumentException("The passed user is null"));
        }

        try (PreparedStatement std = CON.prepareStatement("UPDATE app.users SET email = ?, password = ?, name = ?, lastname = ?, avatar_path = ?, type = ? WHERE id = ?")) {
            std.setString(1, user.getEmail());
            std.setString(2, user.getPassword());
            std.setString(3, user.getFirstName());
            std.setString(4, user.getLastName());
            std.setString(5, user.getAvatarPath());
            std.setString(6, user.getType());
            std.setInt(7, user.getId());
            
            if (std.executeUpdate() == 1) {
                return user;
            } else {
                throw new DAOException("Impossible to update the user");
            }
        } catch (SQLException ex) {
            throw new DAOException("Impossible to update the user", ex);
        }
    }
    
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
    @Override
    public User create(String email, String password, String name, String lastname, String avatarpath) throws DAOException {
        if (email == null || password == null || name == null || lastname == null) {
            throw new DAOException("parameter not valid", new IllegalArgumentException("The passed user is null"));
        }
        
        try (PreparedStatement ps = CON.prepareStatement("INSERT INTO users (email, password, name, lastname, avatar_path, type) VALUES (?, ?, ?, ?, ?, ?)")) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setFirstName(name);
            user.setLastName(lastname);
            user.setAvatarPath("null");
            user.setType("user");
            
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, name);
            ps.setString(4, lastname);
            ps.setString(5, avatarpath);
            ps.setString(6, "user");
            
            if(ps.executeUpdate() == 1)
                return user;
            else
                return null;
        } catch (SQLException ex) {
            throw new DAOException("Impossible to link the passed shopping_list with the passed user", ex);
        }
    }

    @Override
    public String createNotVerified(String email, String password, String name, String lastname, String avatarpath) throws DAOException {
        if (email == null || password == null || name == null || lastname == null) {
            throw new DAOException("parameter not valid", new IllegalArgumentException("The passed user is null"));
        }
        
        boolean isDuplicate=false;
        String code=null;
        
        try {
            PreparedStatement stm = CON.prepareStatement("SELECT code FROM not_verified_users");

            do{
                code=buildRandomAlphaNumericString(10);
                ResultSet rs = stm.executeQuery();
                while(rs.next()){
                    if(rs.getString(1).equals(code)){
                        isDuplicate=true;
                    }
                }
            }while(isDuplicate);

            PreparedStatement ps = CON.prepareStatement("INSERT INTO not_verified_users (email, password, name, lastname, avatar_path, code,created) VALUES (?, ?, ?, ?, ?, ?,?)");
            
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, name);
            ps.setString(4, lastname);
            ps.setString(5, avatarpath);
            ps.setString(6, code);
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); 
            ps.setString(7, timeStamp);
            
            if(ps.executeUpdate() == 1)
                return code;
            else
                return null;
        } catch (SQLException ex) {
            throw new DAOException("Impossible to link create the user", ex);
        }
    }

    @Override
    public String buildRandomAlphaNumericString(int count) throws DAOException {
        final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder;

        builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }

        return builder.toString();
    }

    @Override
    public User deleteByVerificationCode(String code) throws DAOException {
        String email;
        String password;
        String name;
        String lastname;
        String avatarpath="null";
        
        if (code == null) {
            throw new DAOException("code is a mandatory field", new NullPointerException("userId is null"));
        }
        
        try (PreparedStatement stm = CON.prepareStatement("SELECT * FROM not_verified_users WHERE code = ?")) {
            stm.setString(1,code);
            try (ResultSet rs = stm.executeQuery()) {
                rs.next();
                email=rs.getString("email");
                password=rs.getString("password");
                name=rs.getString("name");
                lastname=rs.getString("lastname");
                //avatarpath=rs.getString("avatar_path");
                
                User user=create(email,password,name,lastname,avatarpath);
                
                if(user != null){
                    boolean deleted=deleteNotVerified(code);
                    if(deleted)
                        return user;
                }
            }

            return null;
        } catch (SQLException ex) {
            throw new DAOException("Impossible to get the list of users", ex);
        }
    }

    @Override
    public boolean deleteNotVerified(String code) throws DAOException {
        if(code == null){
            throw new DAOException("Code is a mandatory fields", new NullPointerException("code is null"));  
        }
        
        try (PreparedStatement ps = CON.prepareStatement("DELETE FROM not_verified_users WHERE code= ? ")) {
            ps.setString(1, code);

            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new DAOException("Impossible to delete the given not_verified_user", ex);
        }
    }

    @Override
    public boolean resetPassword(String email) throws DAOException {
        final String subject="ShoppingList: Reset your password!";
        final String message1="This is your new password: ";
        final String message2="<br> If you want you can change your password, once you've logged in our website with your new password, under account settings!";
        
        User user=getById(getByEmail(email));
        user.setPassword(buildRandomAlphaNumericString(20));
        user=update(user);
        
        if(user!=null){
            try {
                GoogleMail.sendEmail(user.getEmail(),subject, message1+user.getPassword()+message2);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(JDBCUserDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return true;
        }
        
        return false;
    }
}
