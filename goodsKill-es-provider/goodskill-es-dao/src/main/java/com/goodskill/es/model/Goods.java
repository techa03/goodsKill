package com.goodskill.es.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author heng
 */
@Document(indexName = "goods", shards = 2, replicas = 2)
@Data
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer goodsId;

    private String photoUrl;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;

    private String price;

    private Date createTime;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String introduce;


}
