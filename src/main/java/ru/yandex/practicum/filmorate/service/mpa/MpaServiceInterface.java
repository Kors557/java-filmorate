package ru.yandex.practicum.filmorate.service.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaServiceInterface {

    Mpa getMpaById(int id);

    List<Mpa> getAllMpa();

    Optional<Integer> findMpaIdByName(String name);

    Mpa createMpa(Mpa mpa);
}
