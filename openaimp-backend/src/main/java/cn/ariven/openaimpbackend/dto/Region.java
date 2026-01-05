package cn.ariven.openaimpbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class Region {
    private String id;
    private List<List<List<List<Double>>>> coordinates;
}
