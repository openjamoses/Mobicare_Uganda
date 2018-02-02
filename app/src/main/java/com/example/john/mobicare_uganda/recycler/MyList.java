package com.example.john.mobicare_uganda.recycler;

/**
 * Created by john on 12/3/17.
 */

public class MyList {
    private String head;
    private String desc;
    private String status;
    private Integer id;

    //constructor initializing values
    public MyList(String head, String desc, String status, Integer id) {
        this.head = head;
        this.desc = desc;
        this.status = status;
        this.id = id;
    }

    //getters
    public String getHead() {
        return head;
    }

    public String getDesc() {
        return desc;
    }

    public String getStatus() {
        return status;
    }
    public Integer getID() {
        return id;
    }
}
