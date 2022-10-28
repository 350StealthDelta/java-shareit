package ru.practicum.shareit.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeAdapterGsonBuilder {

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }

    // Адаптеры
    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            jsonWriter.value(localDateTime.format(formatter));
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            String stringOfTime = jsonReader.nextString();
            if (stringOfTime.equals("null")) {
                return LocalDateTime.MAX;
            } else {
                return LocalDateTime.parse(stringOfTime, formatter);
            }
        }
    }
}
