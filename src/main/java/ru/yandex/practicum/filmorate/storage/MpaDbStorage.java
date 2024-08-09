package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseDbStorage<Mpa> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM rating_mpa ORDER BY rating_id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM rating_mpa WHERE rating_id = ?";
    private static final String FIND_ID_BY_NAME_QUERY = "SELECT rating_id FROM rating_mpa WHERE name = ?";
    private static final String INSERT_MPA_QUERY = "INSERT INTO rating_mpa (name) VALUES (?)";

    public MpaDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Mpa> mapper) {
        super(jdbcTemplate, mapper);
    }

    public Mpa getMpaById(int id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new EntityNotFoundException("Rating MPA with id=" + id + " not found"));
    }

    public List<Mpa> getAllMpa() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Integer> findMpaIdByName(String name) {
        return this.jdbc.query(FIND_ID_BY_NAME_QUERY, (rs, rowNum) -> rs.getInt("rating_id"), name)
                .stream().findFirst();
    }

    public Mpa createMpa(Mpa mpa) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_MPA_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, mpa.getName());
            return ps;
        }, keyHolder);
        mpa.setId(keyHolder.getKey().intValue());
        return mpa;
    }
}


