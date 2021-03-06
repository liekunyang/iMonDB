package inspector.imondb.viewer.view.io;

/*
 * #%L
 * iMonDB Viewer
 * %%
 * Copyright (C) 2014 - 2015 InSPECtor
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import inspector.imondb.model.Event;
import inspector.imondb.model.EventType;
import inspector.imondb.model.Instrument;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class EventsCsvImporter {

    private static DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    private File file;
    private Instrument instrument;

    public EventsCsvImporter(File file, Instrument instrument) {
        this.file = file;
        this.instrument = instrument;
    }

    public List<Event> read() throws IOException {
        List<Event> events = new ArrayList<>();

        for(CSVRecord record : CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.EXCEL.withDelimiter(';').withHeader())) {
            try {
                Event event = new Event(instrument, new Timestamp(df.parse(record.get("Date")).getTime()),
                        EventType.fromString(record.get("Type")), record.get("Problem"),
                        record.get("Solution"), record.get("Additional information"));
                events.add(event);
            } catch(ParseException ignore) {
                // invalid date: ignore event
            }
        }

        return events;
    }
}
