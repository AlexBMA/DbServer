package com.dbserver;

import com.mixer.raw.Person;

import java.io.IOException;

public interface DB {

    void close() throws IOException;

    void add(String name,
             int age,
             String address,
             String carPlateNumber,
             String description) throws IOException;

    void delete(int rowNumber) throws IOException;

    Person readRow(int rowNumber) throws IOException;

}
