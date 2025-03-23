package info.dylansouthard.StraysBookAPI.config;

import info.dylansouthard.StraysBookAPI.model.CareEvent;
import info.dylansouthard.StraysBookAPI.model.enums.AnimalType;
import info.dylansouthard.StraysBookAPI.model.enums.CareEventType;
import info.dylansouthard.StraysBookAPI.model.enums.SexType;
import info.dylansouthard.StraysBookAPI.model.friendo.Animal;
import info.dylansouthard.StraysBookAPI.model.user.User;

import java.time.LocalDateTime;

public class DummyTestData {

   public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setDisplayName("Joe Schmo");
        return user;
    }

    public static Animal createAnimal() {
        return new Animal(AnimalType.CAT, SexType.FEMALE,"AmeChan");
    }

    public static CareEvent createCareEvent() {
        CareEvent careEvent = new CareEvent();
        careEvent.setId(1L);
        careEvent.setType(CareEventType.FED);
        careEvent.setDate(LocalDateTime.now());
        return careEvent;
    }

}
