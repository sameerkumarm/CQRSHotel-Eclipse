// Copyright © 2016-2018 Esko Luontola
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.cqrshotel.framework;

import java.util.UUID;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(UUID streamId) {
        super(String.valueOf(streamId));
    }
}
