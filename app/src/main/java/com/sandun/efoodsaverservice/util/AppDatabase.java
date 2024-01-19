package com.sandun.efoodsaverservice.util;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.sandun.efoodsaverservice.dao.UserDAO;
import com.sandun.efoodsaverservice.entities.User;

@Database(version = 1, entities = {User.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
}
