package app.oengus.adapter.rest.dto.misc;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageDto<T> {
    private final List<T> content;
    private final int totalPages;
    private final int currentPage;
    private final boolean first;
    private final boolean last;
    private final boolean empty;

    public PageDto(Page<T> page) {
        this.content = page.getContent();
        this.totalPages = page.getTotalPages();
        this.currentPage = page.getNumber() + 1;
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
    }

    public List<T> getContent() {
        return content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isEmpty() {
        return empty;
    }
}
