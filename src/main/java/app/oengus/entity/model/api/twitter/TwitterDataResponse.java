package app.oengus.entity.model.api.twitter;

public class TwitterDataResponse<T> {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
