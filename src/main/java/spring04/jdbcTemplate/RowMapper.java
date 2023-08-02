package spring04.jdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {
    public T mapper(ResultSet rs, int i) throws SQLException;
}
