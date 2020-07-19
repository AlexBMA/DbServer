package com.mixer.raw;

import java.io.*;

public class FileHandler {

    private RandomAccessFile dbFile;

    public FileHandler(final String dbFileName) throws FileNotFoundException {
        this.dbFile = new RandomAccessFile(dbFileName,"rw");


    }

    public void loadAllDataToIndex() throws IOException {

        if (this.dbFile.length() == 0) return;

        long currentPos = 0;
        long rowNum = 0;
        long deletedRows = 0;

        while (currentPos <this.dbFile.length()){
            this.dbFile.seek(currentPos);
            boolean isDeleted = this.dbFile.readBoolean();
            if(!isDeleted){
                Index.getInstance().add(currentPos);
                rowNum++;
            } else {
                deletedRows++;
            }

            currentPos += 1;
            this.dbFile.seek(currentPos);
            int recordLength = this.dbFile.readInt();
            currentPos += 4;
            currentPos += recordLength;
        }

        System.out.println("Total row number in Database "+rowNum);
        System.out.println("Total deleted row number in Database "+deletedRows);

    }

    public boolean add(String name,
                       int age,
                       String address,
                       String carPlateNumber,
                       String description) throws IOException {

        // seek to the end of the file

        long currentPosition = this.dbFile.length();
        this.dbFile.seek(currentPosition);
        // isDeleted byte
        // record length : int
        // name length :int
        // name
        // address length :int
        // address
        // carPlateNumber length :int
        // carPlateNumber
        // description length :int
        // description

        //calculate record
        int length = 4 + // name length
                     name.length() +
                     4 + // age
                     4 + // address length
                     address.length() +
                     4 + // carPlateNumber length
                     carPlateNumber.length() +
                     4 + // description length
                    description.length();

        //it is deleted
        this.dbFile.writeBoolean(false);
        // record length
        this.dbFile.writeInt(length);

        // store the name
        this.dbFile.writeInt(name.length());
        this.dbFile.write(name.getBytes());

        //store age information
        this.dbFile.writeInt(age);

        // store the address
        this.dbFile.writeInt(address.length());
        this.dbFile.write(address.getBytes());

        // store the carPlateNumber
        this.dbFile.writeInt(carPlateNumber.length());
        this.dbFile.write(carPlateNumber.getBytes());

        //store the description
        this.dbFile.writeInt(description.length());
        this.dbFile.write(description.getBytes());

        Index.getInstance().add(currentPosition);
        return true;

    }


    public Person readRow(int rowNumber) throws IOException {

        long bytesPosition = Index.getInstance().getBytesPosition(rowNumber);

        if(bytesPosition == -1){
            return null;
        }

        byte[] row = this.readRowRecord(bytesPosition);
        Person person = new Person();
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(row));

        int nameLength = inputStream.readInt();
        byte[] b = new byte[nameLength];
        inputStream.read(b);
        person.setName(new String(b));

        //age
        person.setAge(inputStream.readInt());

        //address
        b = new byte[inputStream.readInt()];
        inputStream.read(b);
        person.setAddress(new String(b));

        //car plate
        b = new byte[inputStream.readInt()];
        inputStream.read(b);
        person.setCarPlateNumber(new String(b));

        //description
        b = new byte[inputStream.readInt()];
        inputStream.read(b);
        person.setDescription(new String(b));


        return person;
    }

    private byte[] readRowRecord(long bytePositionOfRow) throws IOException {

        this.dbFile.seek(bytePositionOfRow);

        if(this.dbFile.readBoolean()) return new byte[0];
            else {

            this.dbFile.seek(bytePositionOfRow + 1);
            int recordLength = this.dbFile.readInt();
            this.dbFile.seek(bytePositionOfRow + 5);
            byte[] data = new byte[recordLength];
            this.dbFile.read(data);

            return data;
        }


    }

    public void close() throws IOException {
        this.dbFile.close();
    }

    public void deleteRow(int rowNumber) throws IOException {
        long bytePositionOfRecord = Index.getInstance().getBytesPosition(rowNumber);
        if(bytePositionOfRecord == -1){
            throw new IOException("Row does not exits in index");
        }
        this.dbFile.seek(bytePositionOfRecord);
        this.dbFile.writeBoolean(true);

        //update the index
        Index.getInstance().remove(rowNumber);
    }
}
