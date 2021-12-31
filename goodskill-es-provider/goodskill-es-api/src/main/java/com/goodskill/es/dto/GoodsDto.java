package com.goodskill.es.dto;


import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * @author heng
 */
public class GoodsDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    private Integer goodsId;

    private String photoUrl;

    private String name;

    private String price;

    private Date createTime;

    private String introduce;

    private String rawName;

    private byte[] photoImage;

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

    public String getRawName() {
        return rawName;
    }

    public void setRawName(String rawName) {
        this.rawName = rawName;
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GoodsDto goodsDto = (GoodsDto) o;
        return id == goodsDto.id &&
                Objects.equals(goodsId, goodsDto.goodsId) &&
                Objects.equals(photoUrl, goodsDto.photoUrl) &&
                Objects.equals(name, goodsDto.name) &&
                Objects.equals(price, goodsDto.price) &&
                Objects.equals(createTime, goodsDto.createTime) &&
                Objects.equals(introduce, goodsDto.introduce) &&
                Arrays.equals(photoImage, goodsDto.photoImage);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, goodsId, photoUrl, name, price, createTime, introduce);
        result = 31 * result + Arrays.hashCode(photoImage);
        return result;
    }
}
