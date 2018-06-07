// Copyright © 2016-2017 Esko Luontola
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.cqrshotel.capacity.queries;

import java.time.LocalDate;

public class CapacityDto {

    public LocalDate date;
    public Integer capacity;
    public Integer reserved;
}
