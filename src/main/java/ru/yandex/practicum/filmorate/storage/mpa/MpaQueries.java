package ru.yandex.practicum.filmorate.storage.mpa;

public class MpaQueries {

    private MpaQueries() {
    }

    public static final String FIND_ALL = "SELECT * FROM rating_mpa ORDER BY rating_id";
    public static final String FIND_BY_ID = "SELECT * FROM rating_mpa WHERE rating_id = ?";
    public static final String FIND_ID_BY_NAME = "SELECT rating_id FROM rating_mpa WHERE name = ?";
    public static final String INSERT_MPA = "INSERT INTO rating_mpa (name) VALUES (?)";
}
