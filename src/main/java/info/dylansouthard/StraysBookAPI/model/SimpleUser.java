package info.dylansouthard.StraysBookAPI.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SimpleUser {
    @Getter
    @Setter
    private String username;
}
