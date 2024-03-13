package ru.practicum.shareit.pageRequest;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestChangePageToFrom extends PageRequest {
    private final int from;

    public PageRequestChangePageToFrom(int from, int size, Sort sort) {
        super(from / size, size, sort);
        this.from = from;
    }

    @Override
    public long getOffset() {
        return from;
    }
}
