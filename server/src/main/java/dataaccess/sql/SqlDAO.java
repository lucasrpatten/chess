package dataaccess.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.Gson;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

public abstract class SqlDAO {
    private static boolean isConfigured = false;

    public SqlDAO() throws DataAccessException {
        configure();
    }

    protected <T> T query(String queryStatement, Parser<T> parser, Object... args) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement statement = conn.prepareStatement(queryStatement)) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof ChessGame) {
                    statement.setString(i + 1, new Gson().toJson(args[i]));
                }
                else {
                    statement.setObject(i + 1, args[i]);
                }
            }

            try (ResultSet res = statement.executeQuery()) {
                return parser.parse(res);
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    protected int update(String queryStatement, Object... args) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement statement = conn.prepareStatement(queryStatement, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof ChessGame) {
                    statement.setString(i + 1, new Gson().toJson(args[i]));
                }
                else {
                    statement.setObject(i + 1, args[i]);
                }
            }

            statement.executeUpdate();

            try (ResultSet res = statement.getGeneratedKeys()) {
                if (res.next()) {
                    return res.getInt(1);
                }
                return 0;
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private void configure() throws DataAccessException {
        if (!isConfigured) {
            DatabaseManager.createDatabase();
            isConfigured = true;
        }
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createQuery()) {
                conn.createStatement().execute(statement);
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @FunctionalInterface
    protected interface Parser<T> {
        T parse(ResultSet resultSet) throws SQLException;
    }

    protected abstract String[] createQuery();
}
