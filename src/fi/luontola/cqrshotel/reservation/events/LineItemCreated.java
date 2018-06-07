// Copyright © 2016-2018 Esko Luontola
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.cqrshotel.reservation.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.luontola.cqrshotel.framework.Event;
import fi.luontola.cqrshotel.framework.util.Struct;
import org.javamoney.moneta.Money;

import java.time.LocalDate;
import java.util.UUID;

public class LineItemCreated extends Struct implements Event {

    public final UUID reservationId;
    public final int lineItemId;
    public final LocalDate date;
    public final Money price;

    @JsonCreator
    public LineItemCreated(@JsonProperty("reservationId") UUID reservationId,
                           @JsonProperty("lineItemId") int lineItemId,
                           @JsonProperty("date") LocalDate date,
                           @JsonProperty("price") Money price) {
        this.reservationId = reservationId;
        this.lineItemId = lineItemId;
        this.date = date;
        this.price = price;
    }
}
