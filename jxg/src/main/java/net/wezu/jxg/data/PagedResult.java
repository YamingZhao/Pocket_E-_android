package net.wezu.jxg.data;

import java.util.List;

public class PagedResult<T> {
    public int total;

    public int start;
    public int end;

    public List<T> rows;
}
