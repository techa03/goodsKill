package com.goodskill.es.dto;


import java.io.Serializable;
import java.util.Objects;

/**
 * @author heng
 */
public class GoodsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    private Integer goodsId;

    private String photoUrl;

    private String name;

    private String price;

    private String introduce;

    private String rawName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl == null ? null : photoUrl.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price == null ? null : price.trim();
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce == null ? null : introduce.trim();
    }

    public String getRawName() {
        return rawName;
    }

    public void setRawName(String rawName) {
        this.rawName = rawName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoodsDTO goodsDto = (GoodsDTO) o;
        return id == goodsDto.id && Objects.equals(goodsId, goodsDto.goodsId) && Objects.equals(photoUrl, goodsDto.photoUrl) && Objects.equals(name, goodsDto.name) && Objects.equals(price, goodsDto.price) && Objects.equals(introduce, goodsDto.introduce) && Objects.equals(rawName, goodsDto.rawName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, goodsId, photoUrl, name, price, introduce, rawName);
    }

    @Override
    public String toString() {
        return "GoodsDto{" +
                "id=" + id +
                ", goodsId=" + goodsId +
                ", photoUrl='" + photoUrl + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", introduce='" + introduce + '\'' +
                ", rawName='" + rawName + '\'' +
                '}';
    }
}
