// Copyright © 2016-2018 Esko Luontola
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.cqrshotel.room.queries;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.luontola.cqrshotel.framework.Query;
import fi.luontola.cqrshotel.framework.util.Struct;

import java.util.UUID;

public class GetRoomById extends Struct implements Query {

    public final UUID roomId;

    @JsonCreator
    public GetRoomById(@JsonProperty("roomId") UUID roomId) {
        this.roomId = roomId;
    }
}
