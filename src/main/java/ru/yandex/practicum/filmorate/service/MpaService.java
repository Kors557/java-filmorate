package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaDbStorage mpaStorage;

    public Mpa getMpaById(int id) {
        return mpaStorage.getMpaById(id);
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public Optional<Integer> findMpaIdByName(String name) {
        return mpaStorage.findMpaIdByName(name);
    }

    public Mpa createMpa(Mpa mpa) {
        return mpaStorage.createMpa(mpa);
    }
}

