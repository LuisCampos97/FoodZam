package pt.ipleiria.estg.foodzam;

public class FavItem {

    private int itemImage;
    private String item_title;
    private String key_id;

    public FavItem() {
    }

    public FavItem(int itemImage, String item_title, String key_id) {
        this.itemImage = itemImage;
        this.item_title = item_title;
        this.key_id = key_id;
    }

    public int getItemImage() {
        return itemImage;
    }

    public void setItemImage(int itemImage) {
        this.itemImage = itemImage;
    }

    public String getItem_title() {
        return item_title;
    }

    public void setItem_title(String item_title) {
        this.item_title = item_title;
    }

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }
}
