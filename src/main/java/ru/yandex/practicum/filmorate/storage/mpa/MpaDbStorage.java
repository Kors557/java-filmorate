package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseDbStorage<Mpa> {

    MpaQueries mpaQueries;

    public MpaDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Mpa> mapper) {
        super(jdbcTemplate, mapper);
    }

    public Mpa getMpaById(int id) {
        return findOne(mpaQueries.FIND_BY_ID, id)
                .orElseThrow(() -> new EntityNotFoundException("Rating MPA with id=" + id + " not found"));
    }

    public List<Mpa> getAllMpa() {
        return findMany(mpaQueries.FIND_ALL);
    }

    public Optional<Integer> findMpaIdByName(String name) {
        return this.jdbc.query(mpaQueries.FIND_ID_BY_NAME, (rs, rowNum) -> rs.getInt("rating_id"), name)
                .stream().findFirst();
    }

    public Mpa createMpa(Mpa mpa) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(mpaQueries.INSERT_MPA, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, mpa.getName());
            return ps;
        }, keyHolder);
        mpa.setId(keyHolder.getKey().intValue());
        return mpa;
    }
}


