package model;

public class ShelfItem {
    private int shelfId;
    private String itemName;
    private int quantityOnShelf;

    private ShelfItem(Builder builder) {
        this.shelfId = builder.shelfId;
        this.itemName = builder.itemName;
        this.quantityOnShelf = builder.quantityOnShelf;
    }

    public int getShelfId() {
        return shelfId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantityOnShelf() {
        return quantityOnShelf;
    }

    public static class Builder {
        private int shelfId;
        private String itemName;
        private int quantityOnShelf;

        public Builder shelfId(int shelfId) {
            this.shelfId = shelfId;
            return this;
        }

        public Builder itemName(String itemName) {
            this.itemName = itemName;
            return this;
        }

        public Builder quantityOnShelf(int quantityOnShelf) {
            this.quantityOnShelf = quantityOnShelf;
            return this;
        }

        public ShelfItem build() {
            return new ShelfItem(this);
        }
    }
}
