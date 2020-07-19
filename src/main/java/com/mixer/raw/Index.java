package com.mixer.raw;

import java.util.HashMap;

public final class Index {

    private static Index index;

    // row number, byte position
    private HashMap<Long,Long> rawIndex;

    private long totalRowNumber = 0;

    private Index(){
        rawIndex = new HashMap<>();
    }

    public static Index getInstance(){
        if(index==null) {
            index = new Index();
        }

        return index;
    }

    public void add(long bytePosition){
        this.rawIndex.put(this.totalRowNumber, bytePosition);
        this.totalRowNumber++;
    }

    public void remove(int row){
        this.rawIndex.remove(row);
        this.totalRowNumber--;
    }

    public long getTotalRowNumber() {
        return totalRowNumber;
    }

    public long getBytesPosition(long rowNumber){
        return this.rawIndex.getOrDefault(rowNumber,-1L);
    }

    public void clear(){
        this.totalRowNumber = 0;
        this.rawIndex.clear();
    }
}
