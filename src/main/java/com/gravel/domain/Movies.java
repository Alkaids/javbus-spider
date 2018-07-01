package com.gravel.domain;

import lombok.Data;

import java.io.Serializable;
/**
 * Created by gravel on 2018/06/26.
 */
@Data
public class Movies implements Serializable {

    private Long id;

    private String avCode;

    private String publishDate;

    private String footage;

    private String director;

    private String publisher;

    private String manufacturer;

    private String series;

    private String categories;

    private String tag;

    private String downloadUrl;

    private String picUrl;

}
