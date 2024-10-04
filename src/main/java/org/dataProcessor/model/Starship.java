package org.dataProcessor.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class Starship {
    // assuming potentially have duplicated data in the source table ignore id
    @EqualsAndHashCode.Exclude
    private Integer id;

    private String name;
    private String shipClass;
    private String captain;
    private Integer launcheYear;

}
