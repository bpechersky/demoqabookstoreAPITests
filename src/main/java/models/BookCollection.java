package models;

import java.util.List;

public class BookCollection {
    public String userId;
    public List<Isbn> collectionOfIsbns;

    public BookCollection(String userId, List<Isbn> collectionOfIsbns) {
        this.userId = userId;
        this.collectionOfIsbns = collectionOfIsbns;
    }
}
