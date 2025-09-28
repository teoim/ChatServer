package org.mtr.utils;

import org.mtr.web.api.repository.dao.UserDAO;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestDataFactory {

    private static final String[] NICKS = {
            "alpha", "beta", "gamma", "delta", "omega",
            "superNick", "CoolUser", "xXDarkLordXx", "JaneDoe", "johnny",
            "testoasterOne", "QuickBrown", "lazyDog", "emailGuy", "searchable",
            "MixCaseNick", "user123", "friendFinder", "nickname", "LastOne"
    };

    private static final String[] EMAILS = {
            "alpha@test.com", "beta@test.org", "gamma@domain.com", "delta@sample.net", "omega@example.com",
            "nick@example.org", "cool.user@test.com", "darklord@domain.org", "jane.doe@test.com", "johnny123@mail.com",
            "tester.one@sample.org", "quick.brown@fox.com", "lazy.dog@test.net", "email.guy@sample.com", "search@test.org",
            "mix.CASE@test.com", "user123@domain.com", "finder@friends.net", "nick.name@test.org", "last.one@example.com"
    };

    public static List<UserDAO> createUsers(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> new UserDAO(
                        (long) i,                               // id
                        i + NICKS[(i - 1) % NICKS.length],                             // nick
                        "Name" + i,                             // name
                        "Surname" + i,                          // surname
                        Date.valueOf("1990-12-"+(i%30+1)),          // dob ~ Jan (1990-12-i)
                        "555-000" + i,                          // phonenr
                        i + EMAILS[(i - 1) % EMAILS.length],               // email
                        "This is bio of user " + i,             // bio
                        "password" + i,                         // password
                        "/images/user" + i + ".png"             // profile photo
                ))
                .collect(Collectors.toList());
    }

    /**
     * Returns predefined search scenarios:
     * key = search input, value = expected matching users
     */
    public static Map<String, List<UserDAO>> createUserSearchScenarios(List<UserDAO> allUsers) {
        Map<String, List<UserDAO>> scenarios = new LinkedHashMap<>();

        // case-insensitive search for "nick"
        scenarios.put("nick", allUsers.stream()
                .filter(u -> u.getNick().toLowerCase().contains("nick")
                        || u.getEmail().toLowerCase().contains("nick"))
                .collect(Collectors.toList()));

        // partial substring "lord"
        scenarios.put("lord", allUsers.stream()
                .filter(u -> u.getNick().toLowerCase().contains("lord")
                        || u.getEmail().toLowerCase().contains("lord"))
                .collect(Collectors.toList()));

        // search by mixed-case "MIX" (should still match MixCaseNick, mix.CASE@test.com)
        scenarios.put("MIX", allUsers.stream()
                .filter(u -> u.getNick().toLowerCase().contains("mix")
                        || u.getEmail().toLowerCase().contains("mix"))
                .collect(Collectors.toList()));

        // search by email domain "test.com"
        scenarios.put("test.com", allUsers.stream()
                .filter(u -> u.getEmail().toLowerCase().contains("test.com"))
                .collect(Collectors.toList()));

        // search for something nonexistent
        scenarios.put("nonexistent", Collections.emptyList());

        return scenarios;
    }
}
