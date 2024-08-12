package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MpaService implements MpaServiceInterface {

    private final MpaDbStorage mpaStorage;

    @Override
    public Mpa getMpaById(int id) {
        return mpaStorage.getMpaById(id);
    }

    @Override
    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    @Override
    public Optional<Integer> findMpaIdByName(String name) {
        return mpaStorage.findMpaIdByName(name);
    }

    @Override
    public Mpa createMpa(Mpa mpa) {
        return mpaStorage.createMpa(mpa);
    }
}

