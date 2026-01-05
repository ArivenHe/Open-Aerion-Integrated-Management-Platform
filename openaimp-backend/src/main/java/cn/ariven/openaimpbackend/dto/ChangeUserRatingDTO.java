package cn.ariven.openaimpbackend.dto;

import lombok.Data;

@Data
public class ChangeUserRatingDTO {
    private int userId;
    private int rating;
}
