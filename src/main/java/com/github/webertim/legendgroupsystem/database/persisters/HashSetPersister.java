package com.github.webertim.legendgroupsystem.database.persisters;

import com.google.gson.Gson;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import java.sql.SQLException;
import java.util.HashSet;

public class HashSetPersister extends BaseDataType {

    private static final HashSetPersister instance = new HashSetPersister();
    private final Gson parser = new Gson();

    private HashSetPersister() {
        super(SqlType.STRING);
    }

    public static HashSetPersister getSingleton() {
        return instance;
    }

    @Override
    public Object parseDefaultString(FieldType fieldType, String s) throws SQLException {
        return new HashSet<String>();
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults databaseResults, int i) throws SQLException {
        return databaseResults.getString(i);
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        return parser.toJson(javaObject);
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        return parser.fromJson((String) sqlArg, HashSet.class);
    }

    @Override
    public int getDefaultWidth() {
        return 255;
    }
}
