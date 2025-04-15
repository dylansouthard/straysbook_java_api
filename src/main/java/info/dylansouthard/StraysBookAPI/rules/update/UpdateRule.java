package info.dylansouthard.StraysBookAPI.rules.update;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import info.dylansouthard.StraysBookAPI.rules.enums.AccessLevel;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateRule {
    private AccessLevel access;
    private Object condition;
}
