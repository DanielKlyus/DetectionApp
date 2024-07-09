package com.example.sber_ai.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServerResponse {
    private List<String> labels = new ArrayList<>();

    private List<Double> scores = new ArrayList<>();

//    private List<List<>>
}
