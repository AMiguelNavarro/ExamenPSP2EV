package com.svalero.examen.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Job {

    private String id;
    private String type;
    private String title;
    private String location;
    private String description;
    private String company;

    @Override
    public String toString() {
        return title;
    }
}
