package com.nazmul.finalyearproject.Model;

import java.util.List;

import javax.xml.transform.Result;

public class MyResponse {

    public long multicast_id;
    public int success,failure,canonical_ids;

    public List<Result> results;
}
