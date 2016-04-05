package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class Daogenerator {

    public static void main(String[] args) throws Exception {
        int version = 1;
        String defaultJavaPackage = "de.greenrobot.daoexample";
        Schema schema = new Schema(version, defaultJavaPackage);
        //addTable(schema);
        addSearch(schema);
        // /表示根目录， ./表示当前路径， ../表示上一级父目录
        new DaoGenerator().generateAll(schema, "./friends/src/main/java");
    }
private static void addSearch(Schema schema){
    Entity item = schema.addEntity("QuickSearch");
    item.addIdProperty();
    item.addDateProperty("add_time");
    item.addStringProperty("content").notNull();
}
    private static void addTable(Schema schema) {
        Entity note = schema.addEntity("NewsChannelTable");
        //        note.addIdProperty();
        /**
         * 频道名称
         */
        note.addStringProperty("newsChannelName").notNull().primaryKey().index();
        /**
         * 频道id
         */
        note.addStringProperty("newsChannelId").notNull();
        /**
         * 频道类型
         */
        note.addStringProperty("newsChannelType").notNull();
        /**
         * 选中的频道
         */
        note.addBooleanProperty("newsChannelSelect").notNull();
        /**
         * 频道的排序位置
         */
        note.addIntProperty("newsChannelIndex").notNull();
        /**
         * 频道是否固定的
         */
        note.addBooleanProperty("newsChannelFixed");
    }

}
