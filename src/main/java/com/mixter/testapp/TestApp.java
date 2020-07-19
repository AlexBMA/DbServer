package com.mixter.testapp;

import com.dbserver.DB;
import com.dbserver.DBServer;
import com.mixer.raw.Index;

public class TestApp {

    public static void main(String[] args) {


        try {

            String dbFileName = "Dbserver.db";
            DB db = new DBServer(dbFileName);
            db.add("John", 44, "Berlin", "www-404", "This is a description");
            System.out.println("Total number of rows in interface: "+ Index.getInstance().getTotalRowNumber());
            db.delete(0);
            System.out.println(Index.getInstance().getTotalRowNumber());
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
