// Copyright © 2016-2018 Esko Luontola
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.cqrshotel.framework.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.luontola.cqrshotel.framework.Envelope;
import fi.luontola.cqrshotel.framework.Event;
//import org.postgresql.util.PSQLException;
//import org.postgresql.util.ServerErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PsqlEventStore implements EventStore {

    private static final Logger log = LoggerFactory.getLogger(PsqlEventStore.class);

    private static final Pattern OPTIMISTIC_LOCKING_FAILURE_MESSAGE =
            Pattern.compile("^optimistic locking failure, current version is (\\d+)$");

    /*private final DataSource dataSource;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public PsqlEventStore(DataSource dataSource, ObjectMapper objectMapper) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.objectMapper = objectMapper;
    }*/

    @Override
    public long saveEvents(UUID streamId, List<Envelope<Event>> newEvents, int expectedVersion) {
    	return 0;
        /*try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
            Array data = connection.createArrayOf("jsonb", serializeData(newEvents));
            Array metadata = connection.createArrayOf("jsonb", serializeMetadata(newEvents));

            long endPosition = jdbcTemplate.queryForObject(
                    "SELECT save_events(:stream_id, :expected_version, :data, :metadata)",
                    new MapSqlParameterSource()
                            .addValue("stream_id", streamId)
                            .addValue("expected_version", expectedVersion)
                            .addValue("data", data)
                            .addValue("metadata", metadata),
                    Long.class);

            if (log.isTraceEnabled()) {
                for (int i = 0; i < newEvents.size(); i++) {
                    int newVersion = expectedVersion + 1 + i;
                    Envelope<Event> newEvent = newEvents.get(i);
                    log.trace("Saved stream {} version {}: {}", streamId, newVersion, newEvent);
                }
            }
            return endPosition;

        } catch (UncategorizedSQLException e) {
            if (e.getCause() instanceof PSQLException) {
                ServerErrorMessage serverError = ((PSQLException) e.getCause()).getServerErrorMessage();
                Matcher m = OPTIMISTIC_LOCKING_FAILURE_MESSAGE.matcher(serverError.getMessage());
                if (m.matches()) {
                    String currentVersion = m.group(1);
                    throw new OptimisticLockingException("expected version " + expectedVersion +
                            " but was " + currentVersion + " for stream " + streamId, e);
                }
            }
            throw e;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }*/
    }

    @Override
    public List<PersistedEvent> getEventsForStream(UUID streamId, int sinceVersion) {
        /*return jdbcTemplate.query("SELECT e.data, e.metadata, e.stream_id, e.version, s.position " +
                        "FROM event e " +
                        "JOIN event_sequence s USING (stream_id, version) " +
                        "WHERE e.stream_id = :stream_id " +
                        "  AND e.version > :since_version " +
                        "ORDER BY e.version",
                new MapSqlParameterSource()
                        .addValue("stream_id", streamId)
                        .addValue("since_version", sinceVersion),
                this::eventMapping);*/
    	return null;
    }

    @Override
    public List<PersistedEvent> getAllEvents(long sincePosition) {
        return /*jdbcTemplate.query("SELECT e.data, e.metadata, e.stream_id, e.version, s.position " +
                        "FROM event e " +
                        "JOIN event_sequence s USING (stream_id, version) " +
                        "WHERE s.position > :position " +
                        "ORDER BY s.position",
                new MapSqlParameterSource("position", sincePosition),
                this::eventMapping);*/null;
    }

    @Override
    public int getCurrentVersion(UUID streamId) {
        /*List<Integer> version = jdbcTemplate.queryForList(
                "SELECT version FROM stream WHERE stream_id = :stream_id",
                new MapSqlParameterSource("stream_id", streamId),
                Integer.class);
        return version.isEmpty() ? BEGINNING : version.get(0);*/
    	return 0;
    }

    @Override
    public long getCurrentPosition() {
        /*List<Long> position = jdbcTemplate.queryForList(
                "SELECT position FROM event_sequence ORDER BY position DESC  LIMIT 1",
                new MapSqlParameterSource(),
                Long.class);
        return position.isEmpty() ? BEGINNING : position.get(0);*/
    	return 0;
    }

    /*private PersistedEvent eventMapping(ResultSet rs, int rowNum) throws SQLException {
        String data = rs.getString("data");
        String metadata = rs.getString("metadata");
        Envelope<Event> event = deserialize(data, metadata);
        UUID streamId = UUID.fromString(rs.getString("stream_id"));
        int version = rs.getInt("version");
        long position = rs.getLong("position");
        return new PersistedEvent(event, streamId, version, position);
    }

    private String[] serializeData(List<Envelope<Event>> events) {
        return events.stream()
                .map(event -> event.payload)
                .map(this::serialize)
                .toArray(String[]::new);
    }

    private String[] serializeMetadata(List<Envelope<Event>> events) {
        return events.stream()
                .map(PsqlEventStore::getMetadata)
                .map(this::serialize)
                .toArray(String[]::new);
    }

    private static EventMetadata getMetadata(Envelope<Event> event) {
        EventMetadata meta = new EventMetadata();
        meta.messageId = event.messageId;
        meta.correlationId = event.correlationId;
        meta.causationId = event.causationId;
        meta.type = event.payload.getClass().getName();
        meta.version = 1; // TODO: versioning support
        return meta;
    }

    private String serialize(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize: " + event, e);
        }
    }

    private Envelope<Event> deserialize(String dataJson, String metadataJson) {
        try {
            EventMetadata meta = objectMapper.readValue(metadataJson, EventMetadata.class);
            Event data = (Event) objectMapper.readValue(dataJson, Class.forName(meta.type));
            return new Envelope<>(data, meta.messageId, meta.correlationId, meta.causationId);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }*/
}
