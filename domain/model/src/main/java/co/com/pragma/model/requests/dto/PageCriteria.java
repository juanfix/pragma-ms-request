package co.com.pragma.model.requests.dto;

public record PageCriteria(int page, int size) {
    public PageCriteria {
        if (page < 0){
            page = 0;
        } else {
            page= page - 1;
        };
        if (size <= 0) size = 10;
    }
    public int offset() { return page * size; }
}