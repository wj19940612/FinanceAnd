package com.sbai.finance.model.mine;

/**
 * Created by ${wangJie} on 2017/6/20.
 * 兑换元宝或积分的model
 */

public class ExchangeProductModel {

    //0 元宝 1 积分
    private int type;
    private int product;
    private int price;

    public ExchangeProductModel() {
    }

    public ExchangeProductModel(int type, int product, int price) {
        this.type = type;
        this.product = product;
        this.price = price;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
