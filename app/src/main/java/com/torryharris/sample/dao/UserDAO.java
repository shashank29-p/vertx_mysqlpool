package com.torryharris.sample.dao;

import com.torryharris.sample.Utils.Configkeys;
import com.torryharris.sample.Utils.PropertyFileUtils;
import com.torryharris.sample.Utils.Queries;
import com.torryharris.sample.model.User;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final Logger LOGGER = LogManager.getLogger(UserDAO.class);
    Vertx vertx = Vertx.vertx();
    private MySQLPool pool;

    public UserDAO(Vertx vertx) {
        pool = MySQLPool.pool(vertx, getsqlConnectionOptions(), getPoolOptions());
    }

    public Future<List<User>> getuser() {
        Promise<List<User>> promise = Promise.promise();
        List<User> user = new ArrayList<>();
        pool.preparedQuery(PropertyFileUtils.getQuery(Queries.GETALL))
                .execute().onSuccess(rows -> {
                    for (Row row : rows) {
                        User users = new User();
                        users.setid(row.getLong("id"));
                        users.setfirstname(row.getString("firstname"));
                        users.setlastname(row.getString("lastname"));
                        users.setemail(row.getString("email"));
                        user.add(users);
                    }
                    promise.tryComplete(user);
                });
        return promise.future();
    }

    public List<User> updateUser(long id){
        List<User> user = new ArrayList<>();
        pool.preparedQuery(PropertyFileUtils.getQuery(Queries.UPDATE_USER))
                .execute(Tuple.of(id)).onSuccess(rows -> {
                    for (Row row : rows) {
                        User users = new User();
                        users.setfirstname(row.getString("firstname"));
                        users.setlastname(row.getString("lastname"));
                        users.setemail(row.getString("email"));
                        user.add(users);
                        LOGGER.info(users);
                    }
                    LOGGER.info(user);
                });
        return user;
    }

    public List<User> insertuser(User user) {
        List<User> Users = new ArrayList<>();
        pool.preparedQuery(PropertyFileUtils.getQuery(Queries.INSERT))
                .execute(Tuple.of(user.getid(), user.getfirstname(), user.getlastname(), user.getemail())).onSuccess(rows -> {
                    for (Row row : rows) {
                        user.setid(row.getLong("id"));
                        user.setfirstname(row.getString("firstname"));
                        user.setlastname(row.getString("lastname"));
                        user.setemail(row.getString("email"));
                        Users.add(user);
                        LOGGER.info(Users);
                    }
                });
        return Users;
    }

    private MySQLConnectOptions getsqlConnectionOptions() {
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setHost("localhost")
                .setDatabase(PropertyFileUtils.getProperty(Configkeys.DATABASE))
                .setUser(PropertyFileUtils.getProperty(Configkeys.USERNAME))
                .setPassword(PropertyFileUtils.getProperty(Configkeys.PASSWORD));
        return connectOptions;
    }

    private PoolOptions getPoolOptions() {
        return new PoolOptions()
                .setMaxSize(Integer.parseInt(PropertyFileUtils.getProperty(Configkeys.POOL_SIZE)));
    }
}
