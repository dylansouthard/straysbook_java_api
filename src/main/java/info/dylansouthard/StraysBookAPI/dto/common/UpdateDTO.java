package info.dylansouthard.StraysBookAPI.dto.common;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class UpdateDTO {
    @JsonIgnore
    private Map<String, Object> updates = new HashMap<>();

    @JsonAnySetter
    public void addUpdate(String key, Object value) {
        updates.put(key, value);
    }

    @Schema(hidden = true)
    public boolean hasUpdate(String fieldName) {
        return updates.containsKey(fieldName);
    }

    public Object getUpdateValue(String fieldName) {
        return updates.get(fieldName);
    }
}
