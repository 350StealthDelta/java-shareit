package ru.practicum.shareit.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest extends PageRequest {
    int offset;

    /**
     * Creates a new {@link PageRequest} with sort parameters applied.
     *
     * @param offset zero-based page index, must not be negative.
     * @param size the size of the page to be returned, must be greater than 0.
     * @param sort must not be {@literal null}, use {@link Sort#unsorted()} instead.
     */
    protected CustomPageRequest(int offset, int size, Sort sort) {
        super(offset / size, size, sort);
        this.offset = offset;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    public static CustomPageRequest of(int from, int size, Sort sort) {
        return new CustomPageRequest(from, size, sort);
    }
}
