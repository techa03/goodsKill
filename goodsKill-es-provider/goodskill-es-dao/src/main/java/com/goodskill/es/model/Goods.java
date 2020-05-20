package com.goodskill.es.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * @author heng
 */
@Document(indexName = "goods", shards = 3)
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer goodsId;

    private String photoUrl;

    @Field(type = FieldType.Text)
    private String name;

    private String price;

    private Date createTime;

    @Field(type = FieldType.Text)
    private String introduce;

    private byte[] photoImage;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce == null ? null : introduce.trim();
    }

    public byte[] getPhotoImage() {
        return photoImage;
    }

    public void setPhotoImage(byte[] photoImage) {
        this.photoImage = photoImage;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", goodsId=").append(goodsId);
        sb.append(", photoUrl=").append(photoUrl);
        sb.append(", name=").append(name);
        sb.append(", price=").append(price);
        sb.append(", createTime=").append(createTime);
        sb.append(", introduce=").append(introduce);
        sb.append(", photoImage=").append(photoImage);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goods goods = (Goods) o;
        return Objects.equals(goodsId, goods.goodsId) &&
                Objects.equals(photoUrl, goods.photoUrl) &&
                Objects.equals(name, goods.name) &&
                Objects.equals(price, goods.price) &&
                Objects.equals(createTime, goods.createTime) &&
                Objects.equals(introduce, goods.introduce) &&
                Arrays.equals(photoImage, goods.photoImage);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(goodsId, photoUrl, name, price, createTime, introduce);
        result = 31 * result + Arrays.hashCode(photoImage);
        return result;
    }
}
