package ru.sergeew.entity.utils;

import lombok.*;
import org.json.JSONException;
import org.json.JSONObject;
import ru.sergeew.entity.enums.Action;

import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallbackData {
    private Action action;
    private int page;
    private Long id;

    public String toString() {
        return String.format("{\"action\":\"%s\", \"page\":%d, \"id\":%d}", action.name(), page, id);
    }

    public static Optional<CallbackData> fromString(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            Action action = Action.valueOf(jsonObject.getString("action"));
            int page = jsonObject.getInt("page");
            Long id;
            try{
                id = jsonObject.getLong("id");
            } catch (JSONException e) {
                id = null;
            }

            CallbackData callbackData = new CallbackData();
            callbackData.setAction(action);
            callbackData.setPage(page);
            callbackData.setId(id);

            return Optional.of(callbackData);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }
}
