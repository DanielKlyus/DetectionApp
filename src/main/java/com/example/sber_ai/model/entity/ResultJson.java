package com.example.sber_ai.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ResultJson implements Serializable {

    private List<String> labels;

    private List<Double> scores;

    private List<List<Integer>> bboxes;

    private String img;

    private String datetime;

    private Integer animalsCount;
}

