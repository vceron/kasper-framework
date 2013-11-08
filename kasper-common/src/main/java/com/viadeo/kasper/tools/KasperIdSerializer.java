// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.cqrs.command.CommandResponse;

import java.io.IOException;

public class KasperIdSerializer extends JsonSerializer<KasperID> {

    @Override
    public void serialize(final KasperID value, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException {

        jgen.writeString(value.toString());
    }

}
