package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataOperationException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

// Это не репозиторий, это общий инструмент, поэтому не аннотация Repository
@Component
@RequiredArgsConstructor
public class BaseRepository<T> {
    private final JdbcOperations jdbc;
    private final RowMapper<T> mapper;
    //private final Class<T> entityType;

    protected Optional<Long> findCount(String query, Object... params) {
        try {
            Long count = jdbc.queryForObject(query, Long.class, params);
            return Optional.ofNullable(count);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        //jdbc.queryForList(query, entityType, params);
        return jdbc.query(query, mapper, params);
    }

    public boolean delete(String query, Object... params) {
        return 0 < jdbc.update(query, params);
    }

    protected long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            return id;
        } else {
            throw new DataOperationException("Не удалось сохранить данные");
        }
    }

    protected int[] simpleBatchInsert(String query, List<Object[]> param) {
        return jdbc.batchUpdate(query, param);
    }

    protected int[] batchInsert(String query, List<Object[]> param) {
        return jdbc.batchUpdate(query,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        for (int j = 0; j < param.get(0).length; j++) {
                            ps.setObject(j + 1, param.get(i)[j]);
                        }
                    }

                    public int getBatchSize() {
                        return param.size();
                    }
                });
    }

    protected boolean update(String query, Object... params) {
        return 0 < jdbc.update(query, params);
    }
}
